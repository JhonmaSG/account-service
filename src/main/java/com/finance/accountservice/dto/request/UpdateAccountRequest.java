package com.finance.accountservice.dto.request;

import com.finance.accountservice.entity.AccountStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAccountRequest {

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @NotNull
    private AccountStatus status;
}