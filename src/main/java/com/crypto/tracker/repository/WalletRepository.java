package com.crypto.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.tracker.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

	Wallet findByWalletAddress(String walletAddress);
	
	List<Wallet> findByUserId(Long userId);

}
