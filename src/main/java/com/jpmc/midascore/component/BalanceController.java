package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Balance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class BalanceController {
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);
    private final DatabaseConduit databaseConduit;

    public BalanceController(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") Long userId) {
        logger.debug("Received balance request for userId: {}", userId);
        float balance = databaseConduit.getUserBalance(userId);
        logger.debug("Returning balance for userId {}: {}", userId, balance);
        return new Balance(balance);
    }
}