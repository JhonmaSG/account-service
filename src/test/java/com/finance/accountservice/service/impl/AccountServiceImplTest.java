package com.finance.accountservice.service.impl;

import com.finance.accountservice.audit.service.AuditService;
import com.finance.accountservice.dto.common.PageResponse;
import com.finance.accountservice.dto.request.CreateAccountRequest;
import com.finance.accountservice.dto.request.UpdateAccountRequest;
import com.finance.accountservice.dto.response.AccountResponse;
import com.finance.accountservice.entity.Account;
import com.finance.accountservice.entity.AccountStatus;
import com.finance.accountservice.exception.AccountNotFoundException;
import com.finance.accountservice.exception.UserNotFoundException;
import com.finance.accountservice.mapper.AccountMapper;
import com.finance.accountservice.repository.AccountRepository;
import com.finance.accountservice.security.authorization.AccountAuthorizationService;
import com.finance.accountservice.security.currentuser.CurrentUserService;
import com.finance.accountservice.security.user.entity.UserEntity;
import com.finance.accountservice.security.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private AccountAuthorizationService accountAuthorizationService;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void shouldCreateAccountSuccessfully() {

        UUID userId = UUID.randomUUID();

        CreateAccountRequest request =
                new CreateAccountRequest();

        request.setUserId(userId);
        request.setInitialBalance(
                BigDecimal.valueOf(100000)
        );

        UserEntity user = UserEntity.builder()
                .id(userId)
                .username("juan")
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Account savedAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("ACC-123456")
                .balance(BigDecimal.valueOf(100000))
                .user(user)
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountRepository.save(any(Account.class)))
                .thenReturn(savedAccount);

        when(currentUserService.getCurrentUsername())
                .thenReturn("admin");

        AccountResponse response =
                AccountResponse.builder()
                        .id(savedAccount.getId())
                        .accountNumber("ACC-123456")
                        .build();

        when(accountMapper.toResponse(savedAccount))
                .thenReturn(response);

        AccountResponse result =
                accountService.createAccount(request);

        assertNotNull(result);

        assertEquals(
                "ACC-123456",
                result.getAccountNumber()
        );

        verify(accountRepository)
                .save(any(Account.class));

        verify(auditService)
                .log(
                        eq("CREATE_ACCOUNT"),
                        eq("admin"),
                        eq("Account"),
                        anyString(),
                        anyString(),
                        eq("SUCCESS")
                );
    }

    @Test
    void shouldCreateAccountWithZeroBalanceWhenInitialBalanceIsNull() {

        UUID userId = UUID.randomUUID();
        CreateAccountRequest request = CreateAccountRequest.builder()
                .userId(userId)
                .build();

        UserEntity user = UserEntity.builder()
                .id(userId)
                .username("laura")
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Account savedAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("ACC-654321")
                .balance(BigDecimal.ZERO)
                .user(user)
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountRepository.save(any(Account.class)))
                .thenReturn(savedAccount);

        when(currentUserService.getCurrentUsername())
                .thenReturn("admin");

        AccountResponse response = AccountResponse.builder()
                .id(savedAccount.getId())
                .accountNumber(savedAccount.getAccountNumber())
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountMapper.toResponse(savedAccount))
                .thenReturn(response);

        AccountResponse result = accountService.createAccount(request);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getBalance());

        verify(accountRepository).save(argThat(account ->
                BigDecimal.ZERO.equals(account.getBalance())
                        && AccountStatus.ACTIVE.equals(account.getStatus())
                        && user.equals(account.getUser())
        ));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExistOnCreateAccount() {

        UUID userId = UUID.randomUUID();
        CreateAccountRequest request = CreateAccountRequest.builder()
                .userId(userId)
                .initialBalance(BigDecimal.valueOf(50000))
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> accountService.createAccount(request)
        );

        verify(accountRepository, never()).save(any(Account.class));
        verify(auditService, never()).log(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()
        );
    }

    @Test
    void shouldReturnAccountByIdWhenAccountIsAccessible() {

        UUID accountId = UUID.randomUUID();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber("ACC-000001")
                .balance(BigDecimal.valueOf(75000))
                .status(AccountStatus.ACTIVE)
                .build();

        AccountResponse response = AccountResponse.builder()
                .id(accountId)
                .accountNumber("ACC-000001")
                .balance(BigDecimal.valueOf(75000))
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountAuthorizationService.getAccessibleAccount(accountId))
                .thenReturn(account);

        when(accountMapper.toResponse(account))
                .thenReturn(response);

        AccountResponse result = accountService.getAccountById(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals("ACC-000001", result.getAccountNumber());

        verify(accountAuthorizationService)
                .getAccessibleAccount(accountId);
    }

    @Test
    void shouldThrowExceptionWhenAccountByIdDoesNotExist() {

        UUID accountId = UUID.randomUUID();

        when(accountAuthorizationService.getAccessibleAccount(accountId))
                .thenThrow(new AccountNotFoundException(
                        "Account not found with id " + accountId
                ));

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccountById(accountId)
        );

        verify(accountMapper, never())
                .toResponse(any(Account.class));
    }

    @Test
    void shouldReturnPagedAccounts() {

        Account firstAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("ACC-100001")
                .balance(BigDecimal.valueOf(120000))
                .status(AccountStatus.ACTIVE)
                .build();

        Account secondAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("ACC-100002")
                .balance(BigDecimal.valueOf(90000))
                .status(AccountStatus.INACTIVE)
                .build();

        Page<Account> accountPage = new PageImpl<>(
                List.of(firstAccount, secondAccount)
        );

        when(accountAuthorizationService.getAccessibleAccounts(
                any(Pageable.class),
                eq("juan"),
                eq(AccountStatus.ACTIVE)
        )).thenReturn(accountPage);

        when(accountMapper.toResponse(firstAccount))
                .thenReturn(AccountResponse.builder()
                        .id(firstAccount.getId())
                        .accountNumber(firstAccount.getAccountNumber())
                        .build());

        when(accountMapper.toResponse(secondAccount))
                .thenReturn(AccountResponse.builder()
                        .id(secondAccount.getId())
                        .accountNumber(secondAccount.getAccountNumber())
                        .build());

        PageResponse<AccountResponse> result =
                accountService.getAllAccounts(
                        0,
                        10,
                        "createdAt",
                        "desc",
                        "juan",
                        AccountStatus.ACTIVE
                );

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(2, result.getTotalElements());

        verify(accountAuthorizationService)
                .getAccessibleAccounts(
                        any(Pageable.class),
                        eq("juan"),
                        eq(AccountStatus.ACTIVE)
                );
    }

    @Test
    void shouldUpdateAccountSuccessfully() {

        UUID accountId = UUID.randomUUID();
        UpdateAccountRequest request = UpdateAccountRequest.builder()
                .balance(BigDecimal.valueOf(200000))
                .status(AccountStatus.INACTIVE)
                .build();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber("ACC-200001")
                .balance(BigDecimal.valueOf(100000))
                .status(AccountStatus.ACTIVE)
                .build();

        AccountResponse response = AccountResponse.builder()
                .id(accountId)
                .accountNumber("ACC-200001")
                .balance(BigDecimal.valueOf(200000))
                .status(AccountStatus.INACTIVE)
                .build();

        when(accountAuthorizationService.getAccessibleAccount(accountId))
                .thenReturn(account);

        when(accountMapper.toResponse(account))
                .thenReturn(response);

        AccountResponse result =
                accountService.updateAccount(accountId, request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(200000), account.getBalance());
        assertEquals(AccountStatus.INACTIVE, account.getStatus());
        assertEquals(AccountStatus.INACTIVE, result.getStatus());

        verify(accountAuthorizationService)
                .getAccessibleAccount(accountId);
        verify(accountMapper)
                .toResponse(account);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAccountThatDoesNotExist() {

        UUID accountId = UUID.randomUUID();
        UpdateAccountRequest request = UpdateAccountRequest.builder()
                .balance(BigDecimal.valueOf(200000))
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountAuthorizationService.getAccessibleAccount(accountId))
                .thenThrow(new AccountNotFoundException(
                        "Account not found with id " + accountId
                ));

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.updateAccount(accountId, request)
        );

        verify(accountMapper, never())
                .toResponse(any(Account.class));
    }

    @Test
    void shouldDeleteAccountSuccessfully() {

        UUID accountId = UUID.randomUUID();
        Account account = Account.builder()
                .id(accountId)
                .accountNumber("ACC-300001")
                .balance(BigDecimal.valueOf(100000))
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountAuthorizationService.getAccessibleAccount(accountId))
                .thenReturn(account);

        when(currentUserService.getCurrentUsername())
                .thenReturn("admin");

        accountService.deleteAccount(accountId);

        verify(accountRepository)
                .delete(account);

        verify(auditService)
                .log(
                        eq("DELETE_ACCOUNT"),
                        eq("admin"),
                        eq("Account"),
                        eq(accountId.toString()),
                        eq("AccountNumber: ACC-300001"),
                        eq("SUCCESS")
                );
    }

    @Test
    void shouldThrowExceptionWhenDeletingAccountThatDoesNotExist() {

        UUID accountId = UUID.randomUUID();

        when(accountAuthorizationService.getAccessibleAccount(accountId))
                .thenThrow(new AccountNotFoundException(
                        "Account not found with id " + accountId
                ));

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.deleteAccount(accountId)
        );

        verify(accountRepository, never())
                .delete(any(Account.class));
        verify(auditService, never()).log(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()
        );
    }
}
