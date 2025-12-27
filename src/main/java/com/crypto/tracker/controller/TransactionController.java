package com.crypto.tracker.controller;

import com.crypto.tracker.model.Transaction;
import com.crypto.tracker.service.HoldingService;
import com.crypto.tracker.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final HoldingService holdingService;
    
    // Constructor injection
    public TransactionController(TransactionService transactionService, HoldingService holdingService) {
        this.transactionService = transactionService;
        this.holdingService = holdingService;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
    	Transaction savedTx = transactionService.saveTransaction(transaction);
//        holdingService.applyTransaction(savedTx); // Update holdings automatically
        return savedTx;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}
