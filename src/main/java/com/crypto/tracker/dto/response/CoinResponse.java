package com.crypto.tracker.dto.response;

import java.math.BigDecimal;

public class CoinResponse {

    private String symbol;
    private String logo;
    private BigDecimal price;

    public CoinResponse(String symbol, String logo, BigDecimal price) {
        this.symbol = symbol;
        this.logo = logo;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getLogo() {
        return logo;
    }

    public BigDecimal getPrice() {
        return price;
    }
}

