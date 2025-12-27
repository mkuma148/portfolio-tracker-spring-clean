package com.crypto.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.tracker.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
