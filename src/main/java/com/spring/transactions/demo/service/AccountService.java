package com.spring.transactions.demo.service;

import com.spring.transactions.demo.dto.AccountDTO;
import com.spring.transactions.demo.exception.InsufficientBalanceException;
import com.spring.transactions.demo.model.Account;
import com.spring.transactions.demo.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private  final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository){
        this.accountRepository=accountRepository;
    }

    @Transactional(readOnly =true)
    public List<AccountDTO> getAllAccounts(){
        return accountRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long id) throws AccountNotFoundException {
        Account account = accountRepository.findById(id)
                .orElseThrow(()->new AccountNotFoundException("Account not found with id: " + id));

        return convertToDTO(account);
    }

    @Transactional(readOnly = true)
    public AccountDTO getAccountByNumber(String accountNumber) throws AccountNotFoundException {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number: " + accountNumber));
        return convertToDTO(account);
    }

    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setAccountNumber(accountDTO.getAccountNumber());
        account.setOwnerName(accountDTO.getOwnerName());
        account.setBalance(accountDTO.getBalance());
        Account savedAccount = accountRepository.save(account);
        return convertToDTO(savedAccount);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AccountDTO deposit(String accountNumber, BigDecimal amount) throws AccountNotFoundException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = accountRepository.findByAccountNumberWithLock(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number: " + accountNumber));

        account.setBalance(account.getBalance().add(amount));
        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AccountDTO withdraw(String accountNumber, BigDecimal amount) throws AccountNotFoundException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        Account account = accountRepository.findByAccountNumberWithLock(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number: " + accountNumber));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }

        account.setBalance(account.getBalance().subtract(amount));
        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }
    // Helper method to convert Entity to DTO
    private AccountDTO convertToDTO(Account account){
        return new AccountDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getOwnerName(),
                account.getBalance()
        );
    }
}
