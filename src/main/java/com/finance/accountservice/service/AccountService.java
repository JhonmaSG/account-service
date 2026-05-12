package com.finance.accountservice.service;

import com.finance.accountservice.dto.request.CreateAccountRequest;
import com.finance.accountservice.dto.response.AccountResponse;
import com.finance.accountservice.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

    List<AccountResponse> getAllAccounts();

    AccountResponse getAccountById(UUID id);

    void deleteAccount(UUID id);
}