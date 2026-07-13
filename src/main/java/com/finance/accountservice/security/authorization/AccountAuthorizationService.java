package com.finance.accountservice.security.authorization;

import com.finance.accountservice.entity.Account;
import com.finance.accountservice.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for ownership-based authorization on accounts.
 * Provides methods to retrieve accounts that the current user is
 * allowed to access.
 */
public interface AccountAuthorizationService {

    Account getAccessibleAccount(UUID accountId);

    Page<Account> getAccessibleAccounts(
            Pageable pageable,
            String username,
            AccountStatus status
    );
}
