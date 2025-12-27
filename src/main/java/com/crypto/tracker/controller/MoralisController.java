package com.crypto.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.crypto.tracker.service.MoralisService;

@RestController
@RequestMapping("/api/moralis")
public class MoralisController {

    @Autowired
    private MoralisService moralisService;

    // Multi-chain fetch endpoint
    @GetMapping("/token/multichain/{address}")
    public String getMultiChainTokens(@PathVariable String address) {
        try {
            moralisService.fetchAndSaveWalletMultiChain(address);
            return "Wallet and holdings fetched successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching wallet: " + e.getMessage();
        }
    }
}