package com.finance.accountservice.mapper;

import com.finance.accountservice.dto.request.CreateAccountRequest;
import com.finance.accountservice.dto.response.AccountResponse;
import com.finance.accountservice.entity.Account;
import com.finance.accountservice.entity.AccountStatus;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(CreateAccountRequest request) {

        return Account.builder()
                .ownerName(request.getOwnerName())
                .email(request.getEmail())
                .balance(request.getBalance())
                .status(AccountStatus.ACTIVE)
                .build();
    }

    public AccountResponse toResponse(Account account) {

        return AccountResponse.builder()
                .id(account.getId())
                .ownerName(account.getOwnerName())
                .email(account.getEmail())
                .balance(account.getBalance())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .build();
    }

}