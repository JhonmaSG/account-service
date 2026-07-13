package com.finance.accountservice.transaction.service.impl;

import com.finance.accountservice.entity.Account;
import com.finance.accountservice.audit.service.AuditService;
import com.finance.accountservice.exception.AccessDeniedException;
import com.finance.accountservice.exception.AccountNotFoundException;
import com.finance.accountservice.exception.InsufficientBalanceException;
import com.finance.accountservice.repository.AccountRepository;
import com.finance.accountservice.security.currentuser.CurrentUserService;
import com.finance.accountservice.transaction.dto.request.CreateTransactionRequest;
import com.finance.accountservice.transaction.dto.response.TransactionResponse;
import com.finance.accountservice.transaction.entity.Transaction;
import com.finance.accountservice.transaction.entity.TransactionType;
import com.finance.accountservice.transaction.mapper.TransactionMapper;
import com.finance.accountservice.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {
    /**
     *  Case 1: Deposito Exitoso
     *  Case 2: Retiro Exitoso
     *  Case 3: Saldo Insuficiente
     *  Case 4: Cuenta inexistente
     *  Case 5: Acceso denegado
     */

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AuditService auditService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private TransactionServiceImpl transactionService;


    @Test
    void shouldIncreaseBalanceWhenDepositMade() {
        UUID accountId = UUID.randomUUID();

        CreateTransactionRequest request =
                new CreateTransactionRequest();
        request.setAccountId(accountId);
        request.setType(TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(50000));
        request.setDescription("Ingreso inicial");

        Account account = Account.builder()
                .id(accountId)
                .balance(BigDecimal.valueOf(100000))
                .build();
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(currentUserService.isAdmin())
                .thenReturn(true);

        when(currentUserService.getCurrentUsername())
                .thenReturn("admin");



        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(50000))
                .type(TransactionType.DEPOSIT)
                .account(account)
                .build();
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);


        TransactionResponse response =
                TransactionResponse.builder()
                        .amount(BigDecimal.valueOf(50000))
                        .type(TransactionType.DEPOSIT)
                        .build();
        when(transactionMapper.toResponse(transaction))
                .thenReturn(response);

        TransactionResponse result =
                transactionService.createTransaction(request);

        assertNotNull(result);
        assertEquals(
                TransactionType.DEPOSIT,
                result.getType()
        );

        assertEquals(
                BigDecimal.valueOf(150000),
                account.getBalance()
        );

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));

        verify(auditService)
                .log(
                        eq("DEPOSIT"),
                        eq("admin"),
                        eq("Transaction"),
                        anyString(),
                        anyString(),
                        eq("SUCCESS")
                );
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient(){
        UUID accountId = UUID.randomUUID();

        CreateTransactionRequest request =
                new CreateTransactionRequest();
        request.setAccountId(accountId);
        request.setType(TransactionType.WITHDRAW);
        request.setAmount(BigDecimal.valueOf(50000));
        request.setDescription("Retiro");

        Account account = Account.builder()
                .id(accountId)
                .balance(BigDecimal.valueOf(10000))
                .build();

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(currentUserService.isAdmin())
                .thenReturn(true);

        assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.createTransaction(request)
        );

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();

        CreateTransactionRequest request =
                new CreateTransactionRequest();
        request.setAccountId(accountId);
        request.setType(TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(10000));
        request.setDescription("Ingreso");

        when(currentUserService.isAdmin())
                .thenReturn(true);

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.createTransaction(request)
        );

        verify(transactionRepository, never())
                .save(any(Transaction.class));

    }

    @Test
    void shouldThrowExceptionWhenUserTriesToAccessAnotherAccount() {
        UUID accountId = UUID.randomUUID();

        CreateTransactionRequest request =
                new CreateTransactionRequest();
        request.setAccountId(accountId);
        request.setType(TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(10000));
        request.setDescription("Ingreso");

        when(currentUserService.isAdmin())
                .thenReturn(false);

        when(currentUserService.getCurrentUsername())
                .thenReturn("paula");

        when(accountRepository.findByIdAndUserUsername(
                accountId,
                "paula"
        )).thenReturn(Optional.empty());

        assertThrows(
                AccessDeniedException.class,
                () -> transactionService.createTransaction(request)
        );

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }
}
