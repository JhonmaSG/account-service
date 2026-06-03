package com.finance.accountservice.service.impl;

import com.finance.accountservice.dto.common.PageResponse;
import com.finance.accountservice.dto.request.CreateAccountRequest;
import com.finance.accountservice.dto.response.AccountResponse;
import com.finance.accountservice.entity.Account;
import com.finance.accountservice.entity.AccountStatus;
import com.finance.accountservice.exception.AccountNotFoundException;
import com.finance.accountservice.mapper.AccountMapper;
import com.finance.accountservice.repository.AccountRepository;
import com.finance.accountservice.security.user.entity.UserEntity;
import com.finance.accountservice.security.user.repository.UserRepository;
import com.finance.accountservice.service.AccountService;
import com.finance.accountservice.audit.service.AuditService;
import com.finance.accountservice.dto.request.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional
    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Account account = Account.builder()
                .accountNumber(generateUniqueAccountNumber())
                .user(user)
                .balance(
                        request.getInitialBalance() != null
                                ? request.getInitialBalance()
                                : BigDecimal.ZERO
                )
                .status(AccountStatus.ACTIVE)
                .build();

        Account savedAccount = accountRepository.save(account);

        auditService.log(
                "CREATE_ACCOUNT",
                getCurrentUsername(),
                "Account",
                savedAccount.getId().toString(),
                "AccountNumber: " + savedAccount.getAccountNumber(),
                "SUCCESS"
        );

        return accountMapper.toResponse(savedAccount);
    }

    @Override
    public PageResponse<AccountResponse> getAllAccounts(
            int page,
            int size,
            String sortBy,
            String direction,
            String username,
            AccountStatus status
    ) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Account> accountPage;

        if (isAdmin()) {
            if (status != null) {

                accountPage = accountRepository.findByStatus(status, pageable);

            } else if (username != null && !username.isBlank()) {

                accountPage = accountRepository
                        .findByUserUsernameContainingIgnoreCase(username, pageable);

            } else {

                accountPage = accountRepository.findAll(pageable);
            }
        }
        else {
            String currentUsername = getCurrentUsername();
            accountPage = accountRepository
                    .findByUserUsername(currentUsername, pageable);
        }

        List<AccountResponse> content = accountPage.getContent()
                .stream()
                .map(accountMapper::toResponse)
                .toList();

        return PageResponse.<AccountResponse>builder()
                .content(content)
                .page(accountPage.getNumber())
                .size(accountPage.getSize())
                .totalElements(accountPage.getTotalElements())
                .totalPages(accountPage.getTotalPages())
                .first(accountPage.isFirst())
                .last(accountPage.isLast())
                .build();
    }

    @Override
    public AccountResponse getAccountById(UUID id) {
        Account account;

        if(isAdmin()) {
            account = accountRepository.findById(id)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
        } else {
            String currentUsername = getCurrentUsername();

            account = accountRepository
                    .findByIdAndUserUsername(id, currentUsername)
                    .orElseThrow(() ->
                            new AccountNotFoundException(
                                    "Account not found with id: " + id
                            ));
        }
        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional
    public void deleteAccount(UUID id) {
        Account account = accountRepository.findById(id)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
        accountRepository.delete(account);

        auditService.log(
                "DELETE_ACCOUNT",
                getCurrentUsername(),
                "Account",
                id.toString(),
                "AccountNumber: " + account.getAccountNumber(),
                "SUCCESS"
        );
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(UUID id, UpdateAccountRequest request) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found with id: " + id
                        ));
        account.setBalance(request.getBalance());
        account.setStatus(request.getStatus());

        return accountMapper.toResponse(account);
    }

    private String generateUniqueAccountNumber() {

        String accountNumber;

        do {
            accountNumber = "ACC-" +
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();

        } while(accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String getCurrentUsername() {
        return getAuthentication().getName();
    }

    private boolean isAdmin() {
        return getAuthentication().getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));
    }
}