package com.crypto.tracker.controller;

import com.crypto.tracker.dto.request.CreateWalletRequest;
import com.crypto.tracker.dto.response.HoldingResponse;
import com.crypto.tracker.model.Holding;
import com.crypto.tracker.model.Wallet;
import com.crypto.tracker.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
@CrossOrigin(origins = {"http://localhost:3000", "https://portfolio-tracker-react-production-4f59.up.railway.app"})
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public List<Wallet> getAllWallets() {
        return walletService.getAllWallets();
    }
    
 // Add wallet
    @PostMapping("/add")
    public ResponseEntity<Wallet> createWallet(@RequestBody CreateWalletRequest request,
            @RequestParam Long userId) {

        Wallet wallet = walletService.createWallet(request, userId);
        return ResponseEntity.ok(wallet);
    }
    
    @GetMapping("/user/holdings")
    public List<HoldingResponse> getWalletsAndHoldings(@RequestHeader("Authorization") String authHeader) {
        return walletService.getUserWalletsAndHoldings(authHeader);
    }
    
 // Get all wallets of user
    @GetMapping("/list")
    public ResponseEntity<List<Wallet>> getWallets(@RequestParam Long userId) {
        return ResponseEntity.ok(walletService.getUserWallets(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable Long id) {
        return walletService.getWalletById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteWallet(@PathVariable Long id) {
        walletService.deleteWallet(id);
    }
}
