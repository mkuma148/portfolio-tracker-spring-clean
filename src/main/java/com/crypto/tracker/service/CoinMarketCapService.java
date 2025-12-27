package com.crypto.tracker.service;

import com.crypto.tracker.model.Price;
import com.crypto.tracker.repository.PriceRepository;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
public class CoinMarketCapService {
	
	@Value("${cmc.api.key}")
    private String apiKey;

	private final PriceRepository priceRepository;
	
	private final WebClient webClient;
    
	public CoinMarketCapService(PriceRepository priceRepository, WebClient.Builder webClientBuilder) {
		this.priceRepository = priceRepository;
		this.webClient = webClientBuilder.build();
	}

	public List<Price> fetchAndSavePrice(String symbols) {
		List<Price> savedPrices = new ArrayList<>();

		String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + symbols;

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-CMC_PRO_API_KEY", apiKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		JSONObject json = new JSONObject(response.getBody());
		JSONObject data = json.getJSONObject("data");

		for (String symbol : symbols.split(",")) {
			JSONObject coin = data.getJSONObject(symbol);
			double price = coin.getJSONObject("quote").getJSONObject("USD").getDouble("price");

			Price p = priceRepository.findBySymbol(symbol)
	                .orElse(new Price());
			p.setSymbol(symbol);
			p.setPrice(new BigDecimal(String.valueOf(price)));
			p.setTimestamp(LocalDateTime.now());

			priceRepository.save(p);
			savedPrices.add(p);

		}

		return savedPrices;
	}
	
	public JsonNode fetchCMCDataBlocking(String symbol) {
	    System.out.println("symbol " + symbol);
	    return webClient.get()
	            .uri(uriBuilder -> uriBuilder
	                    .scheme("https")
	                    .host("pro-api.coinmarketcap.com")
	                    .path("/v1/cryptocurrency/quotes/latest")
	                    .queryParam("symbol", symbol.toUpperCase())
	                    .queryParam("convert", "USD")
	                    .build())
	            .header("User-Agent", "KaspaRiskCalculator")
	            .header("X-CMC_PRO_API_KEY", apiKey)
	            .retrieve()
	            .bodyToMono(JsonNode.class)
	            .block(Duration.ofSeconds(60)); // blocking call
	}

	public JsonNode fetchGitHubCommitsBlocking(String repo) {
		return webClient.get().uri("https://api.github.com/repos/" + repo + "/commits").retrieve()
				.bodyToMono(JsonNode.class).block(Duration.ofSeconds(60)); // blocking call
	}
	
	public double calculateRisk(JsonNode cmcData, JsonNode githubData) {
	    Map<String, Object> riskMap = new HashMap<>();
	    
	    double change1h = cmcData.at("/data/KAS/quote/USD/percent_change_1h").asDouble();
	    riskMap.put("change1h", change1h);

	    // 1️⃣ Price Volatility
	    double change24h = cmcData.at("/data/KAS/quote/USD/percent_change_24h").asDouble();
	    riskMap.put("volatility24h", change24h);
	    
	    double change7d = cmcData.at("/data/KAS/quote/USD/percent_change_7d").asDouble();
	    riskMap.put("volatility7d", change7d);
	    
	    double change30d = cmcData.at("/data/KAS/quote/USD/percent_change_30d").asDouble();
	    riskMap.put("volatility30d", change30d);

	    // 2️⃣ Developer Activity
	    int devActivity = githubData.size();
	    riskMap.put("devActivity", devActivity);

	    // Commits in last 30 days
	    int commitsLastMonth = (int) StreamSupport.stream(githubData.spliterator(), false)
	            .filter(commit -> {
	                String date = commit.at("/commit/committer/date").asText();
	                return LocalDate.parse(date.substring(0, 10))
	                        .isAfter(LocalDate.now().minusDays(30));
	            })
	            .count();
	    riskMap.put("recentCommits", commitsLastMonth);

	    // 3️⃣ Liquidity
	    double marketCap = cmcData.at("/data/KAS/quote/USD/market_cap").asDouble();
	    double volume24h = cmcData.at("/data/KAS/quote/USD/volume_24h").asDouble();
	    double liquidityScore = volume24h / marketCap;  // 0-1 scale (higher = safer)
	    riskMap.put("liquidity", liquidityScore);

	    // 4️⃣ Tokenomics
	    double circulatingSupply = cmcData.at("/data/KAS/circulating_supply").asDouble();
	    double totalSupply = cmcData.at("/data/KAS/total_supply").asDouble();
	    double tokenomicsScore = circulatingSupply / totalSupply; // closer to 1 = safer
	    riskMap.put("tokenomics", tokenomicsScore);

	    // 5️⃣ Social Sentiment
	    double socialVolume = cmcData.at("/data/KAS/quote/USD/social_volume_24h").asDouble();
	    double socialSentimentScore = socialVolume / marketCap;
	    riskMap.put("socialSentiment", socialSentimentScore);

	    // 6️⃣ Exchange Coverage
	    int numExchanges = cmcData.at("/data/KAS/num_market_pairs").asInt();
	    riskMap.put("exchangeCoverage", numExchanges);

	    int githubStars = cmcData.at("/data/KAS/github_stars").asInt(); // example path
	    int githubForks = cmcData.at("/data/KAS/github_forks").asInt();
	    riskMap.put("githubStars", githubStars);
	    riskMap.put("githubForks", githubForks);
	    
	    double top10Holdings = cmcData.at("/data/KAS/top_10_holders_percent").asDouble();
	    riskMap.put("top10HoldingsPercent", top10Holdings);
	    
	    return calculateLongTermRisk(riskMap);
	}
	
	public double calculateLongTermRisk(Map<String, Object> riskMap) {

	    double totalWeightedRisk = 0;
	    double totalWeightUsed = 0;

	    // 🔴 1️⃣ Volatility Risk (40%)
	    double vol7d = (double) riskMap.get("volatility7d");
	    double vol30d = (double) riskMap.get("volatility30d");

	    if (vol7d != 0 || vol30d != 0) {
	        double volRisk =
	                (Math.abs(vol30d) * 0.6) +
	                (Math.abs(vol7d) * 0.4);

	        volRisk = clamp(volRisk * 4, 0, 100); // scale to 0–100

	        totalWeightedRisk += volRisk * 0.40;
	        totalWeightUsed += 0.40;
	    }

	    // 🟡 2️⃣ Liquidity + Concentration Risk (35%)
	    double liquidity = (double) riskMap.get("liquidity");
	    double top10 = (double) riskMap.get("top10HoldingsPercent");
	    int exchanges = (int) riskMap.get("exchangeCoverage");

	    if (liquidity != 0) {
	        double liqRisk = clamp((1 / liquidity) * 10, 0, 100);

	        if (top10 != 0)
	            liqRisk += clamp(top10, 0, 30);

	        if (exchanges < 50)
	            liqRisk += 10;

	        liqRisk = clamp(liqRisk, 0, 100);

	        totalWeightedRisk += liqRisk * 0.35;
	        totalWeightUsed += 0.35;
	    }

	    // 🟢 3️⃣ Fundamental Risk (25%)
	    int devActivity = (int) riskMap.get("devActivity");
	    int recentCommits = (int) riskMap.get("recentCommits");
	    double tokenomics = (double) riskMap.get("tokenomics");
	    int stars = (int) riskMap.get("githubStars");

	    double fundRisk = 0;
	    boolean fundUsed = false;

	    if (devActivity != 0) {
	        fundRisk += devActivity < 20 ? 20 : 5;
	        fundUsed = true;
	    }

	    if (recentCommits != 0) {
	        fundRisk += recentCommits < 5 ? 20 : 5;
	        fundUsed = true;
	    }

	    if (tokenomics != 0) {
	        fundRisk += tokenomics < 0.5 ? 25 : 5;
	        fundUsed = true;
	    }

	    if (stars != 0) {
	        fundRisk += stars < 10 ? 10 : 0;
	        fundUsed = true;
	    }

	    if (fundUsed) {
	        fundRisk = clamp(fundRisk, 0, 100);
	        totalWeightedRisk += fundRisk * 0.25;
	        totalWeightUsed += 0.25;
	    }

	    // 🧮 Final Risk
	    if (totalWeightUsed == 0)
	        return 0;

	    return clamp(totalWeightedRisk / totalWeightUsed, 0, 100);
	}
	
	private double clamp(double value, double min, double max) {
	    return Math.max(min, Math.min(max, value));
	}

}
