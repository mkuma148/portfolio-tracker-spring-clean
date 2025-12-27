package com.crypto.tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.tracker.model.Direction;
import com.crypto.tracker.model.Holding;
import com.crypto.tracker.model.Wallet;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
	
	Optional<Holding> findByWalletAndCoin(Wallet wallet, String coin);
	List<Holding> findByWallet(Wallet wallet);
	List<Holding> findByWalletUserId(Long userId);
	List<Holding> findByWalletId(Long walletId);

}
