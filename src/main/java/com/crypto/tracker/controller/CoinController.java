package com.crypto.tracker.controller;

import com.crypto.tracker.dto.response.CoinResponse;
import com.crypto.tracker.service.CoinService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coin")
@CrossOrigin(origins = "*") // React ke liye
public class CoinController {

    private final CoinService coinService;

    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }

    @GetMapping("/{symbol}")
    public CoinResponse getCoin(@PathVariable String symbol) {
        return coinService.getCoinData(symbol.toUpperCase());
    }
}

