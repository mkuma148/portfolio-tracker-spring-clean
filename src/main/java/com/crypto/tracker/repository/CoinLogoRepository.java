package com.crypto.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crypto.tracker.model.CoinLogo;

import java.util.Optional;

public interface CoinLogoRepository extends JpaRepository<CoinLogo, Long> {
    Optional<CoinLogo> findBySymbol(String symbol);
}
