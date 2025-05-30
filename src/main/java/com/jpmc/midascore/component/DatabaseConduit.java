package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConduit {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConduit.class);
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        logger.info("DatabaseConduit initialized with userRepository: {}", userRepository);
    }

    public void save(UserRecord userRecord) {
        logger.info("Attempting to save user: {}", userRecord);
        try {
            userRepository.save(userRecord);
            logger.info("Successfully saved user: {}", userRecord);
        } catch (Exception e) {
            logger.error("Error saving user: {}", userRecord, e);
            throw e;
        }
    }

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        logger.info("Processing transaction: {}", transaction);
        
        try {
            UserRecord sender = userRepository.findById(transaction.getSenderId());
            UserRecord recipient = userRepository.findById(transaction.getRecipientId());

            logger.info("Sender found: {}", sender);
            logger.info("Recipient found: {}", recipient);

            if (sender == null || recipient == null) {
                logger.warn("Transaction failed - sender or recipient not found");
                return false;
            }

            if (sender.getBalance() < transaction.getAmount()) {
                logger.warn("Transaction failed - insufficient funds");
                return false;
            }

            // Update balances
            float originalSenderBalance = sender.getBalance();
            float originalRecipientBalance = recipient.getBalance();

            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + transaction.getIncentiveAmount());

            logger.info("Sender balance update: {} -> {}", originalSenderBalance, sender.getBalance());
            logger.info("Recipient balance update: {} -> {}", originalRecipientBalance, recipient.getBalance());

            // Record transaction
            TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount());

            // Save everything
            userRepository.save(sender);
            userRepository.save(recipient);
            transactionRepository.save(record);

            logger.info("Transaction processed successfully");
            return true;
        } catch (Exception e) {
            logger.error("Error processing transaction: {}", transaction, e);
            throw e;
        }
    }

    public float getUserBalance(long userId) {
        try {
            UserRecord user = userRepository.findById(userId);
            float balance = user != null ? user.getBalance() : 0;
            logger.info("Balance for user {}: {}", userId, balance);
            return balance;
        } catch (Exception e) {
            logger.error("Error getting balance for user {}", userId, e);
            throw e;
        }
    }
}