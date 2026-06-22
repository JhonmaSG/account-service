package com.finance.accountservice.transaction.service.impl;

import com.finance.accountservice.dto.common.PageResponse;
import com.finance.accountservice.entity.Account;
import com.finance.accountservice.exception.AccessDeniedException;
import com.finance.accountservice.exception.AccountNotFoundException;
import com.finance.accountservice.exception.InsufficientBalanceException;
import com.finance.accountservice.exception.TransactionNotFoundException;
import com.finance.accountservice.repository.AccountRepository;
import com.finance.accountservice.security.currentuser.CurrentUserService;
import com.finance.accountservice.transaction.dto.request.CreateTransactionRequest;
import com.finance.accountservice.transaction.dto.response.TransactionResponse;
import com.finance.accountservice.transaction.entity.Transaction;
import com.finance.accountservice.transaction.entity.TransactionType;
import com.finance.accountservice.transaction.mapper.TransactionMapper;
import com.finance.accountservice.transaction.repository.TransactionRepository;
import com.finance.accountservice.transaction.service.TransactionService;
import com.finance.accountservice.audit.service.AuditService;
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
    private final AuditService auditService;
    private final CurrentUserService currentUserService;

    @Override
    public TransactionResponse createTransaction(
            CreateTransactionRequest request
    ) {

        Account account;

        if (currentUserService.isAdmin()) {
            account = accountRepository.findById(
                            request.getAccountId()
                    )
                    .orElseThrow(() ->
                            new AccountNotFoundException(
                                    "Account not found"
                            ));
        } else {
            account = accountRepository
                    .findByIdAndUserUsername(
                            request.getAccountId(),
                            currentUserService.getUserName()
                    )
                    .orElseThrow(() ->
                            new AccessDeniedException(
                                    "Access denied"
                            ));
        }

        BigDecimal currentBalance = account.getBalance();

        if (request.getType() == TransactionType.DEPOSIT) {
            account.setBalance(
                    currentBalance.add(request.getAmount())
            );

        } else if (request.getType() == TransactionType.WITHDRAW) {
            if (currentBalance.compareTo(request.getAmount()) < 0) {

                throw new InsufficientBalanceException(
                        "Insufficient balance"
                );
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

        auditService.log(
                request.getType().name(),
                currentUserService.getUserName(),
                "Transaction",
                savedTransaction.getId().toString(),
                "Amount: " + request.getAmount() + " | Account: " + account.getAccountNumber(),
                "SUCCESS"
        );

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    public PageResponse<TransactionResponse> getAllTransactions(
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactionPage;

        if( currentUserService.isAdmin() ) {
            transactionPage = transactionRepository.findAll(pageable);
        } else {
            transactionPage = transactionRepository.findByAccountUserUsername(
                    currentUserService.getUserName(),
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

        if ( currentUserService.isAdmin() ) {
            transaction = transactionRepository.findById(id)
                    .orElseThrow(() ->
                            new TransactionNotFoundException("Transaction not found"));
        } else {
            transaction =
                    transactionRepository
                            .findByIdAndAccountUserUsername(
                                    id,
                                    currentUserService.getUserName()
                            )
                            .orElseThrow(() ->
                                    new TransactionNotFoundException("Transaction not found"));
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

        if ( currentUserService.isAdmin() ) {
            transactionPage =
                    transactionRepository.findByAccountId(
                            accountId,
                            pageable
                    );
        } else {
            transactionPage =
                    transactionRepository.findByAccountIdAndAccountUserUsername(
                            accountId,
                            currentUserService.getUserName(),
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
}
