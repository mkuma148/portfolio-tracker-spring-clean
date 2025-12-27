package com.crypto.tracker.dto.request;

import java.math.BigDecimal;

public class AddHoldingRequest {

    private Long walletId;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal buyFee;

    public Long getWalletId() {
        return walletId;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

	public BigDecimal getBuyFee() {
		return buyFee;
	}

	public void setBuyFee(BigDecimal buyFee) {
		this.buyFee = buyFee;
	}
}

