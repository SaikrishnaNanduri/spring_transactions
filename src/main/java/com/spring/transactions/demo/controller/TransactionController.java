package com.spring.transactions.demo.controller;


import com.spring.transactions.demo.dto.TransferRequest;
import com.spring.transactions.demo.model.TransactionRecord;
import com.spring.transactions.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/transactions")
public class TransactionController {

    private  final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequest transferRequest) {
        transactionService.transferMoney(transferRequest);
        return ResponseEntity.ok("Transfer completed successfully");
    }

    @PostMapping("/recordTransaction")
    public ResponseEntity<String> recordTransaction(@RequestBody TransferRequest transferRequest) {
        transactionService.transferMoney(transferRequest);
        return ResponseEntity.ok("Transfer completed successfully");
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionRecord>> getAccountTransactions(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getAccountTransactions(accountId));
    }
}
