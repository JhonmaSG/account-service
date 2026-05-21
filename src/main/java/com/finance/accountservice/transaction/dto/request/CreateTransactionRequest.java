package com.finance.accountservice.transaction.dto.request;

import com.finance.accountservice.transaction.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionRequest {

    @NotNull(message = "Account id is required")
    private UUID accountId;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater that zero")
    private BigDecimal amount;

    private String description;
}
