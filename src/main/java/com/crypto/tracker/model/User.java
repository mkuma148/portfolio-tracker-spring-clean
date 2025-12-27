package com.crypto.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = true)
    private String password; // hashed password
    
    @Column(nullable = false)
    private String provider; // LOCAL or GOOGLE

    private String providerId; // google sub

    @Column(nullable = true)
    private String name;
    
    private String avatar;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wallet> wallets = new ArrayList<>();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProvider() {
		return provider;
	}
	public String getProviderId() {
		return providerId;
	}
	public String getName() {
		return name;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Wallet> getWallets() { return wallets; }
    public void setWallets(List<Wallet> wallets) { this.wallets = wallets; }
}

