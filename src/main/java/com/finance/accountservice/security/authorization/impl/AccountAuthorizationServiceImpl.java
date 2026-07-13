package com.finance.accountservice.security.authorization.impl;

import com.finance.accountservice.entity.Account;
import com.finance.accountservice.entity.AccountStatus;
import com.finance.accountservice.exception.AccountNotFoundException;
import com.finance.accountservice.repository.AccountRepository;
import com.finance.accountservice.security.authorization.AccountAuthorizationService;
import com.finance.accountservice.security.currentuser.CurrentUserService;
import com.finance.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link AccountAuthorizationService}. Enforces
 * ownership-based access: ADMIN sees all accounts, USER sees only
 * their own accounts.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountAuthorizationServiceImpl implements AccountAuthorizationService {
    private final AccountRepository accountRepository;
    private final CurrentUserService currentUserService;

    @Override
    public Account getAccessibleAccount(UUID accountId) {

        if(currentUserService.isAdmin()) {
            return accountRepository.findById(accountId)
                    .orElseThrow(() ->
                            new AccountNotFoundException(
                                    "Account not found with id " + accountId));
        }
        return accountRepository.findByIdAndUserUsername(
                        accountId,
                        currentUserService.getCurrentUsername())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found with id: " + accountId));
    }

    @Override
    public Page<Account> getAccessibleAccounts(
            Pageable pageable,
            String username,
            AccountStatus status) {

        if (currentUserService.isAdmin()) {

            if (status != null) {
                return accountRepository.findByStatus(status, pageable);
            }

            if (username != null && !username.isBlank()) {
                return accountRepository.findByUserUsernameContainingIgnoreCase(
                        username,
                        pageable
                );
            }

            return accountRepository.findAll(pageable);
        }

        return accountRepository.findByUserUsername(
                currentUserService.getCurrentUsername(),
                pageable
        );
    }
}
