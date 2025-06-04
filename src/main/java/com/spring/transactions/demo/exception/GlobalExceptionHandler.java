package com.spring.transactions.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Object>handleAccountNotFoundException(AccountNotFoundException accountNotFoundException){
        Map<String,Object>body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message",accountNotFoundException.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Object>handleInsufficientBalanceException(InsufficientBalanceException insufficientBalanceException){
        Map<String,Object>body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message",insufficientBalanceException.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionFailedException.class)
    public ResponseEntity<Object>handleTransactionFailedException(TransactionFailedException transactionFailedException){
        Map<String,Object>body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message",transactionFailedException.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object>handleIllegalArgumentException(IllegalArgumentException illegalArgumentException){
        Map<String,Object>body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message",illegalArgumentException.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object>handleAllExceptions(Exception exception){
        Map<String,Object>body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message",exception.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
