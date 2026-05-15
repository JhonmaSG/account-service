package com.finance.accountservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {

    @NotNull(message = "User id is required")
    private UUID userId;

    @PositiveOrZero(message = "Balance must be greater than or equal to zero")
    private BigDecimal initialBalance;
}