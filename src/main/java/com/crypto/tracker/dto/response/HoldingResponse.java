package com.crypto.tracker.dto.response;

import java.math.BigDecimal;

public class HoldingResponse {

    private Long id;
    private String coin;
    private BigDecimal quantity;
    private BigDecimal currentPrice;
    private BigDecimal totalValue;   // quantity * currentPrice
    private BigDecimal investedValue;   // quantity * lastPrice
    private BigDecimal profitLoss; // totalValue - investedValue

    private String walletLabel;

    private String logoUrl;

	// ✅ REQUIRED CONSTRUCTOR
	public HoldingResponse(Long id, String coin, BigDecimal quantity, BigDecimal currentPrice, BigDecimal totalValue,
			BigDecimal investedValue, // quantity * lastPrice
			BigDecimal profitLoss, String walletLabel, String logoUrl) {
		this.id = id;
		this.coin = coin;
		this.quantity = quantity;
		this.currentPrice = currentPrice;
		this.totalValue = totalValue;
		this.investedValue = investedValue;
		this.profitLoss = profitLoss;
		this.walletLabel = walletLabel;
		this.logoUrl = logoUrl;
	}

    // ✅ GETTERS (MANDATORY for JSON)
    public Long getId() {
        return id;
    }

    public String getCoin() {
        return coin;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}

	public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public BigDecimal getInvestedValue() {
		return investedValue;
	}

	public BigDecimal getProfitLoss() {
		return profitLoss;
	}

	public void setInvestedValue(BigDecimal investedValue) {
		this.investedValue = investedValue;
	}

	public void setProfitLoss(BigDecimal profitLoss) {
		this.profitLoss = profitLoss;
	}

	public String getWalletLabel() {
        return walletLabel;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}

