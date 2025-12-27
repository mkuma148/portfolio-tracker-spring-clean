package com.crypto.tracker.controller;

import com.crypto.tracker.model.Price;
import com.crypto.tracker.service.CoinMarketCapService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = {"http://localhost:3000", "https://portfolio-tracker-react-production-4f59.up.railway.app"})
public class CryptoController {

    private final CoinMarketCapService cmcService;

    public CryptoController(CoinMarketCapService cmcService) {
        this.cmcService = cmcService;
    }

    @GetMapping("/fetch/{symbol}")
    public List<Price> fetchPrice(@PathVariable String symbol) {
        return cmcService.fetchAndSavePrice(symbol.toUpperCase());
    }
    
    @GetMapping("/risk/{symbol}")
    public ResponseEntity<Double> getRisk(@PathVariable String symbol) {

    	Double defaultRisk = 0.0;

        try {
            // Blocking / synchronous HTTP calls
            JsonNode cmcData = cmcService.fetchCMCDataBlocking(symbol);
            JsonNode githubData = cmcService.fetchGitHubCommitsBlocking("kaspanet/rusty-kaspa");

            // Risk calculation
            Double risk = cmcService.calculateRisk(cmcData, githubData);

            return ResponseEntity.ok(risk);

        } catch (Exception e) {
            System.out.println("Error fetching data: " + e.getMessage());
            return ResponseEntity.ok(defaultRisk);
        }
    }

}
