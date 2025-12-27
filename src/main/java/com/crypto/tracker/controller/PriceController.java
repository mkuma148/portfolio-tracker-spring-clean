package com.crypto.tracker.controller;

import com.crypto.tracker.model.Price;
import com.crypto.tracker.service.PriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping
    public Price createPrice(@RequestBody Price price) {
        return priceService.savePrice(price);
    }

    @GetMapping
    public List<Price> getAllPrices() {
        return priceService.getAllPrices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Price> getPriceById(@PathVariable Long id) {
        return priceService.getPriceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deletePrice(@PathVariable Long id) {
        priceService.deletePrice(id);
    }
}
