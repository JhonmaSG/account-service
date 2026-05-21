package com.finance.accountservice.transaction.controller;

import com.finance.accountservice.dto.common.PageResponse;
import com.finance.accountservice.transaction.dto.request.CreateTransactionRequest;
import com.finance.accountservice.transaction.dto.response.TransactionResponse;
import com.finance.accountservice.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>>
    getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(
                transactionService.getAllTransactions(page, size)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse>
    getTransactionById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                transactionService.getTransactionById(id)
        );
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<PageResponse<TransactionResponse>>
    getTransactionByAccountId(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(
                transactionService.getTransactionsByAccountId(
                        accountId,
                        page,
                        size
                )
        );
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        transactionService.createTransaction(request)
                );
    }
}
