package com.finance.accountservice.service;

import com.finance.accountservice.dto.request.CreateAccountRequest;
import com.finance.accountservice.dto.request.UpdateAccountRequest;
import com.finance.accountservice.dto.response.AccountResponse;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

    List<AccountResponse> getAllAccounts();

    AccountResponse getAccountById(UUID id);

    void deleteAccount(UUID id);

    AccountResponse updateAccount(UUID id, UpdateAccountRequest request);
}