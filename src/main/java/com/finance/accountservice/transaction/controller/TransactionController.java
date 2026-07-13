package com.finance.accountservice.transaction.controller;

import com.finance.accountservice.dto.common.PageResponse;
import com.finance.accountservice.transaction.dto.request.CreateTransactionRequest;
import com.finance.accountservice.transaction.dto.response.TransactionResponse;
import com.finance.accountservice.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Transactions", description = "Endpoints for deposits, withdrawals and transaction history")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "List all transactions", description = "Returns paginated transactions. ADMIN sees all, USER sees own.")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved")
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

    @Operation(summary = "Get transaction by ID", description = "Returns a single transaction. ADMIN sees any, USER sees own.")
    @ApiResponse(responseCode = "200", description = "Transaction found")
    @ApiResponse(responseCode = "404", description = "Transaction not found")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse>
    getTransactionById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                transactionService.getTransactionById(id)
        );
    }

    @Operation(summary = "Get transactions by account", description = "Returns paginated transactions for a specific account.")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved")
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

    @Operation(summary = "Create a transaction", description = "Creates a deposit or withdrawal. Validates balance for withdrawals.")
    @ApiResponse(responseCode = "201", description = "Transaction created")
    @ApiResponse(responseCode = "400", description = "Insufficient balance or invalid request")
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
