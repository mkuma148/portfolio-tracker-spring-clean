package com.crypto.tracker.controller;

import com.crypto.tracker.dto.request.AddHoldingRequest;
import com.crypto.tracker.dto.response.HoldingResponse;
import com.crypto.tracker.model.Holding;
import com.crypto.tracker.service.HoldingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holdings")
@CrossOrigin(origins = {"http://localhost:3000", "https://portfolio-tracker-react-production-4f59.up.railway.app"})
public class HoldingController {

    private final HoldingService holdingService;

    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @PostMapping
    public Holding createHolding(@RequestBody Holding holding) {
        return holdingService.saveHolding(holding);
    }

    @GetMapping
    public List<Holding> getAllHoldings() {
        return holdingService.getAllHoldings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Holding> getHoldingById(@PathVariable Long id) {
        return holdingService.getHoldingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteHolding(@PathVariable Long id) {
        holdingService.deleteHolding(id);
    }
    
//    @GetMapping("/user/{userId}")
//    public List<HoldingResponse> getHoldingsByUser(@PathVariable Long userId) {
//        return holdingService.getHoldingsByUser(userId);
//    }
    
	@PostMapping("/add")
	public void addHolding(@RequestBody AddHoldingRequest request, @RequestHeader("Authorization") String authHeader) {
		holdingService.addHolding(request, authHeader);
	}

//    // ✅ FETCH HOLDINGS
//    @GetMapping("/{walletId}")
//    public List<HoldingResponse> getHoldings(@PathVariable Long walletId) {
//        return holdingService.getHoldings(walletId);
//    }

}
