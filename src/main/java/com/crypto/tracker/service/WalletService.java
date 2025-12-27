package com.crypto.tracker.service;

import com.crypto.tracker.auth.util.JwtUtil;
import com.crypto.tracker.dto.request.CreateWalletRequest;
import com.crypto.tracker.dto.response.HoldingResponse;
import com.crypto.tracker.model.Holding;
import com.crypto.tracker.model.Price;
import com.crypto.tracker.model.User;
import com.crypto.tracker.model.Wallet;
import com.crypto.tracker.repository.HoldingRepository;
import com.crypto.tracker.repository.PriceRepository;
import com.crypto.tracker.repository.UserRepository;
import com.crypto.tracker.repository.WalletRepository;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WalletService {

	private final WalletRepository walletRepository;
	
	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HoldingRepository holdingRepository;
	
	@Autowired
	private JwtUtil jwtUtil;

	public WalletService(WalletRepository walletRepository) {
		this.walletRepository = walletRepository;
	}

	public Wallet saveWallet(Wallet wallet) {
		return walletRepository.save(wallet);
	}

	public Wallet createWallet(CreateWalletRequest request, Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		Wallet wallet = new Wallet();
		wallet.setLabel(request.getLabel());
		wallet.setUser(user);

		// 🔥 KEY PART
		if (request.getWalletAddress() == null || request.getWalletAddress().isBlank()) {
			wallet.setWalletAddress(generateTrackingId(userId));
		} else {
			wallet.setWalletAddress(request.getWalletAddress());
		}

		return walletRepository.save(wallet);
	}

	public List<HoldingResponse> getUserWalletsAndHoldings(String authHeader) {

		String email = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7).trim();

			try {
				Claims claims = jwtUtil.getClaims(token);
				email = claims.getSubject();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		Optional<User> user = Optional.ofNullable(
				userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")));

		List<Price> prices = priceRepository.findAll();
		Map<String, BigDecimal> priceMap = prices.stream().collect(Collectors.toMap(Price::getSymbol, Price::getPrice));

		// 1️⃣ Get all holdings from all wallets
		List<HoldingResponse> allHoldings = walletRepository.findByUserId(user.get().getId()).stream()
				.flatMap(wallet -> holdingRepository.findByWalletId(wallet.getId()).stream().map(h -> {
					BigDecimal currentPrice = priceMap.getOrDefault(h.getCoin(), BigDecimal.ZERO);
					BigDecimal totalValue = h.getQuantity().multiply(currentPrice);
					BigDecimal investedValue = h.getQuantity().multiply(h.getLastPrice());

					BigDecimal profitLoss = totalValue.subtract(investedValue);
					return new HoldingResponse(null, h.getCoin(), h.getQuantity(), currentPrice, totalValue,
							investedValue, profitLoss, null, // wallet label optional
							h.getCoinLogo() != null ? h.getCoinLogo().getLogoUrl() : null);
				})).toList();

		// Group by coin & sum values
		return allHoldings.stream().collect(Collectors.toMap(HoldingResponse::getCoin, // key = coin
				h -> h, // value = holding
				(h1, h2) -> { // merge logic
					h1.setQuantity(h1.getQuantity().add(h2.getQuantity()));
					h1.setTotalValue(h1.getTotalValue().add(h2.getTotalValue()));
					h1.setInvestedValue(h1.getInvestedValue().add(h2.getInvestedValue()));
		            h1.setProfitLoss(h1.getProfitLoss().add(h2.getProfitLoss()));

					return h1;
				})).values().stream().toList();
	}

	public List<Wallet> getUserWallets(Long userId) {
		return walletRepository.findByUserId(userId);
	}

	public List<Wallet> getAllWallets() {
		return walletRepository.findAll();
	}

	public Optional<Wallet> getWalletById(Long id) {
		return walletRepository.findById(id);
	}

	public void deleteWallet(Long id) {
		walletRepository.deleteById(id);
	}

	private String generateTrackingId(Long userId) {
		return "TRACK-" + userId + "-" + UUID.randomUUID().toString().substring(0, 8);
	}
}
