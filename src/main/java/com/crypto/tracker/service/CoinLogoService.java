package com.crypto.tracker.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.crypto.tracker.model.CoinLogo;
import com.crypto.tracker.repository.CoinLogoRepository;

import jakarta.transaction.Transactional;

@Service
public class CoinLogoService {

    @Value("${cmc.api.key}")
    private String apiKey;

    private final CoinLogoRepository coinLogoRepository;

    public CoinLogoService(CoinLogoRepository coinLogoRepository) {
        this.coinLogoRepository = coinLogoRepository;
    }

    public void fetchAndStoreTopLogos() {
        try {
            String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/info?symbol=BTC,ETH,KAS";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-CMC_PRO_API_KEY", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JSONObject json = new JSONObject(response.getBody());
            JSONObject data = json.getJSONObject("data"); // JSONObject, not JSONArray

            for (String symbol : data.keySet()) {
                JSONObject coin = data.getJSONObject(symbol);
                String logoUrl = coin.has("logo") ? coin.getString("logo") : "";

                coinLogoRepository.findBySymbol(symbol)
                        .orElseGet(() -> {
                            CoinLogo cl = new CoinLogo();
                            cl.setSymbol(symbol);
                            cl.setLogoUrl(logoUrl);
                            return coinLogoRepository.save(cl);
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Transactional
    public void deleteAllLogos() {
        coinLogoRepository.deleteAll();
    }
}
