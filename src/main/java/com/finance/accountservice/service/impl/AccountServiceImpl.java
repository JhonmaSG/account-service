package com.finance.accountservice.service.impl;

import com.finance.accountservice.dto.common.PageResponse;
import com.finance.accountservice.dto.request.CreateAccountRequest;
import com.finance.accountservice.dto.response.AccountResponse;
import com.finance.accountservice.entity.Account;
import com.finance.accountservice.entity.AccountStatus;
import com.finance.accountservice.exception.UserNotFoundException;
import com.finance.accountservice.mapper.AccountMapper;
import com.finance.accountservice.repository.AccountRepository;
import com.finance.accountservice.security.authorization.AccountAuthorizationService;
import com.finance.accountservice.security.currentuser.CurrentUserService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


/**
 * Implementation of {@link AccountService}. Handles account CRUD with
 * ownership validation, automatic account number generation, and
 * audit event logging.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final CurrentUserService currentUserService;
    private final AccountAuthorizationService accountAuthorizationService;

    @Transactional
    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

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
                currentUserService.getCurrentUsername(),
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
        Page<Account> accountPage =
                accountAuthorizationService.getAccessibleAccounts(
                        pageable,
                        username,
                        status
                );

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

        Account account = accountAuthorizationService.getAccessibleAccount(id);

        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional
    public void deleteAccount(UUID id) {

        Account account = accountAuthorizationService.getAccessibleAccount(id);

        accountRepository.delete(account);

        auditService.log(
                "DELETE_ACCOUNT",
                currentUserService.getCurrentUsername(),
                "Account",
                id.toString(),
                "AccountNumber: " + account.getAccountNumber(),
                "SUCCESS"
        );
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(UUID id, UpdateAccountRequest request) {

        Account account = accountAuthorizationService.getAccessibleAccount(id);

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
}
