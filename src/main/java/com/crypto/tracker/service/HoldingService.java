package com.crypto.tracker.service;

import com.crypto.tracker.auth.util.JwtUtil;
import com.crypto.tracker.dto.request.AddHoldingRequest;
import com.crypto.tracker.dto.request.CreateWalletRequest;
import com.crypto.tracker.dto.response.HoldingResponse;
import com.crypto.tracker.model.CoinLogo;
import com.crypto.tracker.model.Direction;
import com.crypto.tracker.model.Holding;
import com.crypto.tracker.model.Price;
import com.crypto.tracker.model.Transaction;
import com.crypto.tracker.model.User;
import com.crypto.tracker.model.Wallet;
import com.crypto.tracker.repository.CoinLogoRepository;
import com.crypto.tracker.repository.HoldingRepository;
import com.crypto.tracker.repository.PriceRepository;
import com.crypto.tracker.repository.UserRepository;
import com.crypto.tracker.repository.WalletRepository;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class HoldingService {

	private final UserRepository userRepository;
	private final HoldingRepository holdingRepository;
    private final PriceRepository priceRepository;
    private final CoinLogoRepository coinLogoRepository;
    private final JwtUtil jwtUtil;

	public HoldingService(HoldingRepository holdingRepository, PriceRepository priceRepository,
			CoinLogoRepository coinLogoRepository, UserRepository userRepository, JwtUtil jwtUtil) {
		this.holdingRepository = holdingRepository;
		this.priceRepository = priceRepository;
		this.coinLogoRepository = coinLogoRepository;
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
	}
    
    @Autowired
    private WalletService walletService;

	public Holding saveHolding(Holding holding) {
		return holdingRepository.save(holding);
	}

	public List<Holding> getAllHoldings() {
		return holdingRepository.findAll();
	}

	public Optional<Holding> getHoldingById(Long id) {
		return holdingRepository.findById(id);
	}

	public void deleteHolding(Long id) {
		holdingRepository.deleteById(id);
	}

//	public List<HoldingResponse> getHoldingsByUser(Long userId) {
//
//		return holdingRepository.findByWalletUserId(userId).stream().map(h -> new HoldingResponse(h.getId(), // ✅ id
//				h.getCoin(), // ✅ coin
//				h.getQuantity(), // ✅ quantity
//				h.getLastPrice(), // ✅ lastPrice
//				h.getTotalValue(), // ✅ totalValue
//				h.getWallet().getLabel(), // ✅ wallet label
//				h.getCoinLogo() != null ? h.getCoinLogo().getLogoUrl() : null // ✅ logo (null safe)
//		)).toList();
//	}
	
	// ✅ ADD COIN
    public void addHolding(AddHoldingRequest request, String authHeader) {
    	
    	String email = null;
    	
    	if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();

            try {
                Claims claims = jwtUtil.getClaims(token);
                email = claims.getSubject();
            }catch (Exception ex) {
            	ex.printStackTrace();
            }
    	}            
    	
    	Optional<User> user = userRepository.findByEmail(email);
    	
    	CreateWalletRequest createWalletRequest = new CreateWalletRequest();
    	createWalletRequest.setLabel("MANUAL ENTRY");

    	Wallet wallet = walletService.createWallet(createWalletRequest, user.get().getId());

        CoinLogo logo = coinLogoRepository.findBySymbol(request.getSymbol())
                .orElseThrow(() -> new RuntimeException("Logo not found"));

        Price price = priceRepository.findBySymbol(request.getSymbol())
                .orElseThrow(() -> new RuntimeException("Price not found"));

        BigDecimal totalValue =
                request.getQuantity().multiply(price.getPrice());

        Holding holding = new Holding();
        holding.setWallet(wallet);
        holding.setCoin(request.getSymbol());
        holding.setQuantity(request.getQuantity());
        holding.setLastPrice(price.getPrice());
        holding.setBuyFee(request.getBuyFee());
        holding.setTotalValue(totalValue);
        holding.setCoinLogo(logo);

        holdingRepository.save(holding);
    }

//    // ✅ FETCH HOLDINGS
//    public List<HoldingResponse> getHoldings(Long walletId) {
//
//        return holdingRepository.findByWalletId(walletId)
//                .stream()
//                .map(h -> new HoldingResponse(h.getId(), // ✅ id
//        				h.getCoin(), // ✅ coin
//        				h.getQuantity(), // ✅ quantity
//						h.getLastPrice(), // ✅ lastPrice
//        				h.getTotalValue(), // ✅ totalValue
//        				h.getWallet().getLabel(), // ✅ wallet label
//        				h.getCoinLogo() != null ? h.getCoinLogo().getLogoUrl() : null
//                ))
//                .toList();
//    }

}
