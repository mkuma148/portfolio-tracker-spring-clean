package com.crypto.tracker.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "holdings")
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Column(nullable = false)
    private String coin; // e.g., "KAS", "BTC"

    @Column(precision = 30, scale = 18)
    private BigDecimal quantity;

    @Column(precision = 30, scale = 18)
    private BigDecimal lastPrice;
    
    @Column(precision = 30, scale = 18)
    private BigDecimal buyFee;

    @Column(precision = 30, scale = 18)
    private BigDecimal totalValue;

    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "coin_logo_id")
    private CoinLogo coinLogo;

	public Long getId() {
		return id;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public String getCoin() {
		return coin;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	public void setCoin(String coin2) {
		this.coin = coin2;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public CoinLogo getCoinLogo() {
		return coinLogo;
	}

	public void setCoinLogo(CoinLogo coinLogo) {
		this.coinLogo = coinLogo;
	}

	public BigDecimal getBuyFee() {
		return buyFee;
	}

	public void setBuyFee(BigDecimal buyFee) {
		this.buyFee = buyFee;
	}
}
