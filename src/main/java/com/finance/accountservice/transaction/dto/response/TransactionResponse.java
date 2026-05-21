package com.finance.accountservice.transaction.dto.response;

import com.finance.accountservice.transaction.entity.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private UUID id;

    private TransactionType type;

    private BigDecimal amount;

    private String description;

    private LocalDate createdAt;

    private UUID accountId;

    private String accountNumber;
}
