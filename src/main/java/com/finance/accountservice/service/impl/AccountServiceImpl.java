package com.finance.accountservice.service.impl;

import com.finance.accountservice.dto.request.CreateAccountRequest;
import com.finance.accountservice.dto.response.AccountResponse;
import com.finance.accountservice.entity.Account;
import com.finance.accountservice.exception.AccountNotFoundException;
import com.finance.accountservice.mapper.AccountMapper;
import com.finance.accountservice.repository.AccountRepository;
import com.finance.accountservice.service.AccountService;
import com.finance.accountservice.dto.request.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional  // Allow write (insert, update, delete)
    public AccountResponse createAccount(CreateAccountRequest request) {
        Account account = accountMapper.toEntity(request);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    @Override
    public Page<AccountResponse> getAllAccounts(int page, int size) {
        Pageable pegeable = PageRequest.of(page, size);

        return accountRepository.findAll(pegeable)
                .map(accountMapper::toResponse);
    }

    @Override
    public AccountResponse getAccountById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional
    public void deleteAccount(UUID id) {
        Account account = accountRepository.findById(id)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
        accountRepository.delete(account);
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(UUID id, UpdateAccountRequest request) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found with id: " + id
                        ));

        account.setOwnerName(request.getOwnerName());
        account.setEmail(request.getEmail());
        account.setBalance(request.getBalance());
        account.setStatus(request.getStatus());

        return accountMapper.toResponse(account);
    }
}