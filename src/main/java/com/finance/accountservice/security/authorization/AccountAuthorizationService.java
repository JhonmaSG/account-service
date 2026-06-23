package com.finance.accountservice.security.authorization;

import com.finance.accountservice.entity.Account;
import com.finance.accountservice.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AccountAuthorizationService {

    Account getAccessibleAccount(UUID accountId);

    Page<Account> getAccessibleAccounts(
            Pageable pageable,
            String username,
            AccountStatus status
    );
}
