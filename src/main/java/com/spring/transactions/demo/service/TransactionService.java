package com.spring.transactions.demo.service;


import com.spring.transactions.demo.dto.TransferRequest;
import com.spring.transactions.demo.exception.AccountNotFoundException;
import com.spring.transactions.demo.exception.InsufficientBalanceException;
import com.spring.transactions.demo.exception.TransactionFailedException;
import com.spring.transactions.demo.model.Account;
import com.spring.transactions.demo.model.TransactionRecord;
import com.spring.transactions.demo.model.enums.TransactionType;
import com.spring.transactions.demo.repository.AccountRepository;
import com.spring.transactions.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Transfer money between accounts with REQUIRED transaction propagation
     * This ensures that the transfer is executed within a transaction
     * If a transaction already exists, it will use that transaction
     * If no transaction exists, it will create a new one
     */
    public void transferMoney(TransferRequest transferRequest){
        String fromAccountNumber =transferRequest.getFromAccountNumber();
        String toAccountNumber =transferRequest.getToAccountNumber();
        BigDecimal amount =transferRequest.getAmount();
        String description =transferRequest.getDescription();
        // Validate amount
        if(amount.compareTo(BigDecimal.ZERO) <=0){
            throw  new IllegalArgumentException("Transfer amount must be positive ");
        }

    // Retrieve accounts with pessimistic lock to prevent concurrent modifications
    Account fromAccount = accountRepository.findByAccountNumberWithLock(fromAccountNumber)
            .orElseThrow(() -> new AccountNotFoundException("Source account not found: " + fromAccountNumber));

    Account toAccount = accountRepository.findByAccountNumberWithLock(toAccountNumber)
            .orElseThrow(() -> new AccountNotFoundException("Destination account not found: " + toAccountNumber));

    // Check if same account
        if (fromAccount.getId().equals(toAccount.getId())) {
        throw new IllegalArgumentException("Cannot transfer to the same account");
    }

    // Check sufficient balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
        throw new InsufficientBalanceException("Insufficient balance for transfer");
    }

        try {
        // Update account balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Save updated accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Record the transaction
        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setTransactionType(TransactionType.TRANSFER);
        transactionRecord.setAmount(amount);
        transactionRecord.setFromAccount(fromAccount);
        transactionRecord.setToAccount(toAccount);
        transactionRecord.setTimeStamp(LocalDateTime.now());
        transactionRecord.setDescription(description);

        transactionRepository.save(transactionRecord);
    } catch (Exception e) {
        // This will trigger transaction rollback due to rollbackFor attribute
        throw new TransactionFailedException("Failed to process transfer: " + e.getMessage(), e);
    }
}

/**
 * Demonstrates REQUIRES_NEW propagation by creating a separate transaction
 */
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
public void recordTransaction(Account fromAccount, Account toAccount, BigDecimal amount, String description) {
    TransactionRecord transactionRecord = new TransactionRecord();
    transactionRecord.setTransactionType(TransactionType.TRANSFER);
    transactionRecord.setAmount(amount);
    transactionRecord.setFromAccount(fromAccount);
    transactionRecord.setToAccount(toAccount);
    transactionRecord.setTimeStamp(LocalDateTime.now());
    transactionRecord.setDescription(description);

    transactionRepository.save(transactionRecord);
}

/**
 * Demonstrates transaction with timeout
 */
@Transactional(timeout = 5)
public List<TransactionRecord> getAccountTransactions(Long accountId) {
    Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

    return transactionRepository.findAllByAccount(account);
}
}
