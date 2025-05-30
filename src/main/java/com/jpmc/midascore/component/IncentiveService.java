package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {
    private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);
    private final RestTemplate restTemplate;
    private final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    public IncentiveService() {
        this.restTemplate = new RestTemplate();
    }

    public float getIncentiveAmount(Transaction transaction) {
        try {
            Incentive incentive = restTemplate.postForObject(
                INCENTIVE_API_URL,
                transaction,
                Incentive.class
            );
            return incentive != null ? incentive.getAmount() : 0;
        } catch (Exception e) {
            logger.error("Error calling incentive API", e);
            return 0;
        }
    }
}