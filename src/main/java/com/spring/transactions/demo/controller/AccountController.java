package com.spring.transactions.demo.controller;
import com.spring.transactions.demo.service.AccountService;
import com.spring.transactions.demo.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) throws AccountNotFoundException {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountDTO> getAccountByNumber(@PathVariable String accountNumber) throws AccountNotFoundException {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        return new ResponseEntity<>(accountService.createAccount(accountDTO), HttpStatus.CREATED);
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<AccountDTO> deposit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount) throws AccountNotFoundException {
        return ResponseEntity.ok(accountService.deposit(accountNumber, amount));
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<AccountDTO> withdraw(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount) throws AccountNotFoundException {
        return ResponseEntity.ok(accountService.withdraw(accountNumber, amount));
    }
}
