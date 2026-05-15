package com.finance.accountservice.mapper;

import com.finance.accountservice.dto.request.CreateAccountRequest;
import com.finance.accountservice.dto.response.AccountResponse;
import com.finance.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "balance", source = "initialBalance")
    Account toEntity(CreateAccountRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    AccountResponse toResponse(Account account);
}