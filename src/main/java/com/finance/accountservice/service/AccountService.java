package com.finance.accountservice.service;

import com.finance.accountservice.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    Account createAccount(Account account);

    List<Account> getAllAccounts();

    Account getAccountById(UUID id);

    void deleteAccount(UUID id);
}