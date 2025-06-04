package com.spring.transactions.demo.repository;

import com.spring.transactions.demo.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface AccountRepository  extends JpaRepository<Account,Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    // Use pessimistic locking to prevent concurrent modifications
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id=:id")
    Optional<Account>findByIdWithLock(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a where a.accountNumber =:accountNumber")
    Optional<Account>findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);



}
