package com.spring.transactions.demo.repository;

import com.spring.transactions.demo.model.Account;
import com.spring.transactions.demo.model.TransactionRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionRecord,Long> {

    List<TransactionRecord> findByFromAccountOrderByTimestampDesc(Account account);

    List<TransactionRecord>findByToAccountOrderByTimeStampDesc(Account account);

    @Query("SELECT t FROM TransactionRecord t WHERE t.fromAccount =:account OR t.toAccount = :account ORDER BY t.timestamp DESC")
    List<TransactionRecord>findAllByAccount(@Param("account") Account account);





}
