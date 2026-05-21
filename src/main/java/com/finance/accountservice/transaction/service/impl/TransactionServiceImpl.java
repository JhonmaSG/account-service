package com.finance.accountservice.transaction.service.impl;

import com.finance.accountservice.dto.common.PageResponse;
import com.finance.accountservice.entity.Account;
import com.finance.accountservice.exception.AccountNotFoundException;
import com.finance.accountservice.exception.InsufficientBalanceException;
import com.finance.accountservice.repository.AccountRepository;
import com.finance.accountservice.transaction.dto.request.CreateTransactionRequest;
import com.finance.accountservice.transaction.dto.response.TransactionResponse;
import com.finance.accountservice.transaction.entity.Transaction;
import com.finance.accountservice.transaction.entity.TransactionType;
import com.finance.accountservice.transaction.mapper.TransactionMapper;
import com.finance.accountservice.transaction.repository.TransactionRepository;
import com.finance.accountservice.transaction.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponse createTransaction (
            CreateTransactionRequest request)
    {
        Account account = accountRepository.findById(
                request.getAccountId()
        )
        .orElseThrow(() ->
                new AccountNotFoundException(
                        "Account not found"
                ));

        BigDecimal currentBalance = account.getBalance();

        if(request.getType() == TransactionType.DEPOSIT) {
            account.setBalance(
                    currentBalance.add(request.getAmount()
                    ));

        } else if (request.getType() == TransactionType.WITHDRAW) {
            if( currentBalance.compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }
            account.setBalance(
                    currentBalance.subtract(request.getAmount())
            );
        }

        Transaction transaction = Transaction.builder()
                .type(request.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .account(account)
                .build();

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    public PageResponse<TransactionResponse> getAllTransactions(
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactionPage;

        if( isAdmin() ) {
            transactionPage = transactionRepository.findAll(pageable);
        } else {
            transactionPage = transactionRepository.findByAccountUserUsername(
                    getCurrentUsername(),
                    pageable
            );
        }
        List<TransactionResponse> content =
                transactionPage.getContent()
                        .stream()
                        .map(transactionMapper::toResponse)
                        .toList();
        return PageResponse.<TransactionResponse>builder()
                .content(content)
                .page(transactionPage.getNumber())
                .size(transactionPage.getSize())
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .first(transactionPage.isFirst())
                .last(transactionPage.isLast())
                .build();
    }

    @Override
    public TransactionResponse getTransactionById(UUID id) {
        Transaction transaction;

        if ( isAdmin() ) {
            transaction = transactionRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Transaction not found"));
        } else {
            transaction =
                    transactionRepository
                            .findByIdAndAccountUserUsername(
                                    id,
                                    getCurrentUsername()
                            )
                            .orElseThrow(() ->
                                    new AccountNotFoundException("Transaction not found"));
        }
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public PageResponse<TransactionResponse> getTransactionsByAccountId(
            UUID accountId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactionPage;

        if ( isAdmin() ) {
            transactionPage =
                    transactionRepository.findByAccountId(
                            accountId,
                            pageable
                    );
        } else {
            transactionPage =
                    transactionRepository.findByAccountIdAndAccountUserUsername(
                            accountId,
                            getCurrentUsername(),
                            pageable
                    );
        }

        List<TransactionResponse> content =
                transactionPage.getContent()
                        .stream()
                        .map(transactionMapper::toResponse)
                        .toList();

        return PageResponse.<TransactionResponse>builder()
                .content(content)
                .page(transactionPage.getNumber())
                .size(transactionPage.getSize())
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .first(transactionPage.isFirst())
                .last(transactionPage.isLast())
                .build();
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
