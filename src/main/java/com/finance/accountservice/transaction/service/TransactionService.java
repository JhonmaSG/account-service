package com.finance.accountservice.transaction.service;

import com.finance.accountservice.dto.common.PageResponse;
import com.finance.accountservice.transaction.dto.request.CreateTransactionRequest;
import com.finance.accountservice.transaction.dto.response.TransactionResponse;

import java.util.UUID;

/**
 * Service interface for transaction business logic. Defines operations
 * for creating deposits/withdrawals and querying transaction history.
 */
public interface TransactionService {

    TransactionResponse createTransaction(
            CreateTransactionRequest request);

    PageResponse<TransactionResponse> getAllTransactions(int page, int size);

    TransactionResponse getTransactionById(UUID id);

    PageResponse<TransactionResponse> getTransactionsByAccountId(UUID id, int page, int size);
}
