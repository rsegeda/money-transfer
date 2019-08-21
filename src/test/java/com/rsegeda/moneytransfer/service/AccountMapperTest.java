/*
    Created by Roman Segeda on 25 August 2019
*/

package com.rsegeda.moneytransfer.service;

import com.rsegeda.moneytransfer.TestUtils;
import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.service.model.Account;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.rsegeda.moneytransfer.TestUtils.A_ACCOUNT;

class AccountMapperTest {

  private AccountMapper accountMapper = new AccountMapper();

  @Test
  void toDtoSameUuid() {
    AccountDto result = accountMapper.toDto(A_ACCOUNT);
    Assertions.assertEquals(A_ACCOUNT.getUuid(), result.getUuid());
  }

  @Test
  void toDtoSameOwnerUuid() {
    AccountDto result = accountMapper.toDto(A_ACCOUNT);
    Assertions.assertEquals(A_ACCOUNT.getOwnerUuid(), result.getOwnerUuid());
  }

  @Test
  void toDtoSameBalance() {
    AccountDto result = accountMapper.toDto(A_ACCOUNT);
    Assertions.assertEquals(A_ACCOUNT.getBalance().get(), result.getBalance());
  }

  @Test
  void toDomainCreateOrUpdateUuidDiffers() {
    AccountDto accountDto = TestUtils.getAccountDtoWithBalance(new BigDecimal(2000));
    Account result = accountMapper.toDomainCreateOrUpdate(accountDto);
    Assertions.assertNotEquals(accountDto.getUuid(), result.getUuid());
  }

  @Test
  void toDomainCreateOrUpdateSameOwnerUuid() {
    AccountDto accountDto = TestUtils.getAccountDtoWithBalance(new BigDecimal(2000));
    Account result = accountMapper.toDomainCreateOrUpdate(accountDto);
    Assertions.assertEquals(accountDto.getOwnerUuid(), result.getOwnerUuid());
  }

  @Test
  void toDomainCreateOrUpdateSameBalance() {
    AccountDto accountDto = TestUtils.getAccountDtoWithBalance(new BigDecimal(2000));
    Account result = accountMapper.toDomainCreateOrUpdate(accountDto);
    Assertions.assertEquals(accountDto.getBalance(), result.getBalance().get());
  }
}
