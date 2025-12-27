package com.crypto.tracker.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crypto.tracker.model.Holding;
import com.crypto.tracker.model.Wallet;
import com.crypto.tracker.repository.WalletRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MoralisService {

	@Value("${mri.api.key}")
	private String apiKey;

	private final WalletRepository walletRepository;

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final String API_URL_TEMPLATE = "https://deep-index.moralis.io/api/v2.2/wallets/%s/tokens?chain=%s";

	private static final String[] SUPPORTED_CHAINS = { "eth", // Ethereum Mainnet
			"goerli", // Ethereum Goerli
			"sepolia", // Ethereum Sepolia

			"bsc", // BNB Smart Chain
			"bsc-testnet", // BNB Testnet

			"polygon", // Polygon
			"mumbai", // Polygon Mumbai

			"avalanche", // Avalanche C-Chain
			"avalanche-testnet",

			"fantom", // Fantom Opera
			"fantom-testnet",

			"cronos", // Cronos Chain
			"cronos-testnet",

			"arbitrum", // Arbitrum One
			"arbitrum-goerli",

			"optimism", // Optimism
			"optimism-goerli",

			"pulsechain", // Pulse Chain
			"pulsechain-testnet",

			"base", // Coinbase Base Chain
			"base-goerli",

			"linea", // Linea Chain
			"linea-testnet",

			"zksync", // zkSync Era Mainnet
			"zksync-testnet",

			"mantle", // Mantle Mainnet
			"mantle-testnet",

			"klaytn", // Klaytn
			"klaytn-testnet",

			"celo", // Celo
			"celo-testnet",

			"moonbeam", // Moonbeam
			"moonriver" // Moonriver
	};

	public MoralisService(WalletRepository walletRepository) {
		this.walletRepository = walletRepository;
	}

	public void fetchAndSaveWalletMultiChain(String walletAddress) throws Exception {

		// 1️⃣ Find or create wallet
		Wallet wallet = walletRepository.findByWalletAddress(walletAddress);
		if (wallet == null) {
			wallet = new Wallet();
			wallet.setWalletAddress(walletAddress);
			wallet.setLabel("Wallet_" + walletAddress.substring(0, 6));
			wallet.setHoldings(new ArrayList<>());
			wallet = walletRepository.save(wallet);
		}

		// Clear old holdings
		if (wallet.getHoldings() == null)
			wallet.setHoldings(new ArrayList<>());
		wallet.getHoldings().clear();

		// 2️⃣ ExecutorService for parallel fetch
		ExecutorService executor = Executors.newFixedThreadPool(SUPPORTED_CHAINS.length);
		List<Future<List<Holding>>> futures = new ArrayList<>();

		final Wallet walletRef = wallet;
		for (String chain : SUPPORTED_CHAINS) {
			futures.add(executor.submit(() -> fetchTokensForChain(walletRef, chain)));
		}

		// 3️⃣ Aggregate all holdings
		for (Future<List<Holding>> future : futures) {
			try {
				wallet.getHoldings().addAll(future.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		executor.shutdown();

		// 4️⃣ Save wallet with all holdings
		walletRepository.save(wallet);
	}

	// ✅ Fetch tokens for a single chain
	private List<Holding> fetchTokensForChain(Wallet wallet, String chain) throws Exception {
		List<Holding> holdings = new ArrayList<>();

		String url = String.format(API_URL_TEMPLATE, wallet.getWalletAddress(), chain);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json")
				.header("X-API-Key", apiKey).GET().build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			return holdings; // empty list
		}

		JsonNode root = objectMapper.readTree(response.body());
		JsonNode results = root.get("result");
		if (results == null || !results.isArray())
			return holdings;

		for (JsonNode token : results) {
			try {
				String symbol = token.get("symbol").asText();
				String balanceRaw = token.get("balance").asText();
				int decimals = token.get("decimals").asInt();
				BigDecimal balance = new BigDecimal(balanceRaw).divide(BigDecimal.TEN.pow(decimals));

				BigDecimal usdPrice = token.has("usd_price") ? new BigDecimal(token.get("usd_price").asText())
						: BigDecimal.ZERO;
				BigDecimal totalValue = balance.multiply(usdPrice);

				Holding h = new Holding();
				h.setWallet(wallet);
				h.setCoin(symbol);
				h.setQuantity(balance);
				h.setLastPrice(usdPrice);
				h.setTotalValue(totalValue);
				h.setUpdatedAt(LocalDateTime.now());

				holdings.add(h);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return holdings;
	}
}