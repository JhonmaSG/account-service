package com.finance.accountservice.service.impl;

import com.finance.accountservice.entity.Account;
import com.finance.accountservice.repository.AccountRepository;
import com.finance.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional  // Allow write (insert, update, delete)
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteAccount(UUID id) {
        Account account = getAccountById(id);   // Valida que exista
        accountRepository.delete(account);
    }
}