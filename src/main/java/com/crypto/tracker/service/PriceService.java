package com.crypto.tracker.service;

import com.crypto.tracker.model.Price;
import com.crypto.tracker.repository.PriceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PriceService {

    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Price savePrice(Price price) {
        return priceRepository.save(price);
    }

    public List<Price> getAllPrices() {
        return priceRepository.findAll();
    }

    public Optional<Price> getPriceById(Long id) {
        return priceRepository.findById(id);
    }

    public void deletePrice(Long id) {
        priceRepository.deleteById(id);
    }
}
