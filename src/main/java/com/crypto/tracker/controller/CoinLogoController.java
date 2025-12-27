package com.crypto.tracker.controller;

import com.crypto.tracker.service.CoinLogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logo")
@CrossOrigin(origins = {"http://localhost:3000", "https://portfolio-tracker-react-production-4f59.up.railway.app"})
public class CoinLogoController {

    @Autowired
    private CoinLogoService coinLogoService;

    // GET request to fetch & save top 500 coin logos
    @GetMapping("/fetch")
    public String fetchTopLogos() {
        try {
            coinLogoService.fetchAndStoreTopLogos(); // ye service me implement hoga
            return "logos saved successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    @DeleteMapping("/delete-all")
    public String deleteAllLogos() {
        coinLogoService.deleteAllLogos();
        return "All coin logos deleted successfully!";
    }
}

