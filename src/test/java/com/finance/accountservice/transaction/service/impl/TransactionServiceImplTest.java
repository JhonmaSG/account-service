package com.finance.accountservice.transaction.service.impl;

import com.finance.accountservice.entity.Account;
import com.finance.accountservice.exception.AccessDeniedException;
import com.finance.accountservice.exception.AccountNotFoundException;
import com.finance.accountservice.exception.InsufficientBalanceException;
import com.finance.accountservice.repository.AccountRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.util.List;
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



        Transaction transaction = Transaction.builder()
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

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(() -> "ROLE_ADMIN")
                );
        SecurityContext securityContext =
                mock(SecurityContext.class);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


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
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient(){
        UUID accountId = UUID.randomUUID();

        CreateTransactionRequest request =
                new CreateTransactionRequest();
        request.setAccountId(accountId);

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

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(() -> "ROLE_ADMIN")
                );

        SecurityContext securityContext =
                mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

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

        request.setAccountId(accountId);
        request.setType(TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(10000));
        request.setDescription("Ingreso");

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        mockAutenticatedUser("admin", "ROLE_ADMIN");

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

        when(accountRepository.findByIdAndUserUsername(
                accountId,
                "paula"
        )).thenReturn(Optional.empty());

        mockAutenticatedUser("paula", "ROLE_USER");

        assertThrows(
                AccessDeniedException.class,
                () -> transactionService.createTransaction(request)
        );

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    private void mockAutenticatedUser(String username, String role) {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(() -> role)
                );
        SecurityContext securityContext =
                mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
