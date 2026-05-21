package com.finance.accountservice.transaction.mapper;

import com.finance.accountservice.transaction.dto.response.TransactionResponse;
import com.finance.accountservice.transaction.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountNumber", source = "account.accountNumber")
    TransactionResponse toResponse(Transaction transaction);
}
