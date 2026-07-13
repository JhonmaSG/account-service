package com.finance.accountservice.service;

import com.finance.accountservice.dto.common.PageResponse;
import com.finance.accountservice.dto.request.CreateAccountRequest;
import com.finance.accountservice.dto.request.UpdateAccountRequest;
import com.finance.accountservice.dto.response.AccountResponse;
import com.finance.accountservice.entity.AccountStatus;

import java.util.UUID;

/**
 * Service interface for account business logic. Defines operations
 * for creating, querying, updating, and deleting bank accounts.
 */
public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

    PageResponse<AccountResponse> getAllAccounts(
            int page,
            int size,
            String sortBy,
            String direction,
            String username,
            AccountStatus status
            );

    AccountResponse getAccountById(UUID id);

    void deleteAccount(UUID id);

    AccountResponse updateAccount(UUID id, UpdateAccountRequest request);
}