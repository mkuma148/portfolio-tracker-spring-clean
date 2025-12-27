package com.crypto.tracker.service;

import com.crypto.tracker.dto.response.CoinResponse;
import com.crypto.tracker.model.CoinLogo;
import com.crypto.tracker.model.Price;
import com.crypto.tracker.repository.CoinLogoRepository;
import com.crypto.tracker.repository.PriceRepository;
import org.springframework.stereotype.Service;

@Service
public class CoinService {

    private final CoinLogoRepository coinLogoRepository;
    private final PriceRepository priceRepository;

    public CoinService(
            CoinLogoRepository coinLogoRepository,
            PriceRepository priceRepository
    ) {
        this.coinLogoRepository = coinLogoRepository;
        this.priceRepository = priceRepository;
    }

    public CoinResponse getCoinData(String symbol) {

        CoinLogo logo = coinLogoRepository
                .findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Logo not found for " + symbol));

        Price price = priceRepository
                .findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Price not found for " + symbol));

        return new CoinResponse(
                symbol,
                logo.getLogoUrl(),
                price.getPrice()
        );
    }
}

