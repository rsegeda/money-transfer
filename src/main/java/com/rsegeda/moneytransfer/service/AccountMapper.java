/*
    Created by Roman Segeda on 21 August 2019
*/

package com.rsegeda.moneytransfer.service;

import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.service.model.Account;

import java.util.UUID;

class AccountMapper {

  AccountDto toDto(Account account) {
    return new AccountDto(account.getUuid(), account.getOwnerUuid(), account.getBalance().get());
  }

  Account toDomainCreateOrUpdate(AccountDto accountDto) {
    return new Account(UUID.randomUUID(), accountDto.getOwnerUuid(), accountDto.getBalance());
  }
}
