package com.crypto.tracker.cron;

import com.crypto.tracker.service.CoinMarketCapService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CryptoScheduler {

    private final CoinMarketCapService cmcService;

    public CryptoScheduler(CoinMarketCapService cmcService) {
        this.cmcService = cmcService;
    }

//    // Runs every 1 hour
//    @Scheduled(cron = "0 0 * * * *")
    // Runs every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    public void runCryptoTasks() {

        try {
            cmcService.fetchAndSavePrice("BTC,ETH,KAS,SOL,XRP,ADA,DOT,DOGE,BNB,LTC");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
