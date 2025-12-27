package com.crypto.tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.tracker.model.Price;

public interface PriceRepository extends JpaRepository<Price, Long> {
	Optional<Price> findBySymbol(String symbol);
}
