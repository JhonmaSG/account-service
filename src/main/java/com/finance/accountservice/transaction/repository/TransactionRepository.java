package com.finance.accountservice.transaction.repository;

import com.finance.accountservice.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findAll(Pageable pageable);

    Page<Transaction> findByAccountUserUsername(String username,  Pageable pageable);

    Optional<Transaction> findByIdAndAccountUserUsername(UUID id, String username);

    Page<Transaction> findByAccountId(UUID id, Pageable pageable);

    Page<Transaction> findByAccountIdAndAccountUserUsername(UUID accountId, String username, Pageable pageable);
}
