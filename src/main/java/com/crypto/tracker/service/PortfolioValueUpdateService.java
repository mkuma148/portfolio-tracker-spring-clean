package com.crypto.tracker.service;

import com.crypto.tracker.model.Holding;
import com.crypto.tracker.repository.HoldingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioValueUpdateService {

    @Value("${cmc.api.key}")
    private String apiKey;

    private final HoldingRepository holdingRepository;

    public PortfolioValueUpdateService(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    public List<Holding> updatePortfolioValues() {

        // 1️⃣ Load all holdings
        List<Holding> holdings = holdingRepository.findAll();

        if (holdings.isEmpty()) {
            return Collections.emptyList();
        }

        // 2️⃣ Extract unique coin symbols: e.g. BTC, ETH, KAS
        String symbols = holdings.stream()
                .map(Holding::getCoin)
                .distinct()
                .collect(Collectors.joining(","));

        // 3️⃣ Call CMC API once for all coins
        Map<String, BigDecimal> priceMap = fetchPrices(symbols);

        // 4️⃣ Update each holding
        for (Holding h : holdings) {
            BigDecimal price = priceMap.get(h.getCoin());

            if (price != null) {
                h.setLastPrice(price);
                h.setTotalValue(h.getQuantity().multiply(price));
                h.setUpdatedAt(LocalDateTime.now());
            }
        }

        // 5️⃣ Save updates
        holdingRepository.saveAll(holdings);

        return holdings;
    }

    // 🔥 Price fetcher (single API call for multiple coins)
    private Map<String, BigDecimal> fetchPrices(String symbols) {
        Map<String, BigDecimal> map = new HashMap<>();

        try {
            String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + symbols;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-CMC_PRO_API_KEY", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JSONObject data = new JSONObject(response.getBody()).getJSONObject("data");

            for (String sym : symbols.split(",")) {
                if (data.has(sym)) {
                    double price = data.getJSONObject(sym)
                            .getJSONObject("quote")
                            .getJSONObject("USD")
                            .getDouble("price");

                    map.put(sym, new BigDecimal(String.valueOf(price)));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
