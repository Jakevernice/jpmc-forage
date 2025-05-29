package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionKafkaListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionKafkaListener.class);
    private final DatabaseConduit databaseConduit;

    public TransactionKafkaListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-consumer")
    public void receiveTransaction(Transaction transaction) {
        try {
            databaseConduit.processTransaction(transaction);
        } catch (Exception e) {
            logger.error("Error processing transaction: {}", transaction, e);
        }
    }
}