package com.finance.accountservice.repository;

import com.finance.accountservice.entity.Account;
import com.finance.accountservice.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Account> findByStatus(AccountStatus status);

    List<Account> findByBalanceGreaterThan(BigDecimal amount);

    Page<Account> findByStatus(AccountStatus status, Pageable pageable);

    Page<Account> findByOwnerNameContainingIgnoreCase(
            String ownerName,
            Pageable pageable
    );
}
