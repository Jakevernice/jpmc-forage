package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import jakarta.annotation.PostConstruct;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaskFourTests {
    static final Logger logger = LoggerFactory.getLogger(TaskFourTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private DatabaseConduit databaseConduit;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        logger.info("UserRepository status: {}", userRepository != null ? "initialized" : "null");
        logger.info("DatabaseConduit status: {}", databaseConduit != null ? "initialized" : "null");
    }

    @Test
    void task_four_verifier() throws InterruptedException {
        logger.info("Starting test execution...");
        
        // Populate users
        userPopulator.populate();
        logger.info("Users populated");

        // Get initial balance
        float initialBalance = databaseConduit.getUserBalance(9);
        logger.info("Wilbur's initial balance: {}", initialBalance);

        // Process transactions
        String[] transactionLines = fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");
        logger.info("Loaded {} transactions", transactionLines.length);
        
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
            logger.info("Sent transaction: {}", transactionLine);
        }
        
        // Wait for processing
        Thread.sleep(2000);

        // Check wilbur's balance
        float wilburBalance = databaseConduit.getUserBalance(9);
        logger.info("Wilbur's balance after transactions: {}", wilburBalance);

        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("use your debugger to find out what wilbur's balance is after all transactions are processed");
        logger.info("kill this test once you find the answer");
        
        while (true) {
            Thread.sleep(20000);
            wilburBalance = databaseConduit.getUserBalance(9);
            logger.info("Current Wilbur's balance: {}", wilburBalance);
            logger.info("...");
        }
    }
}