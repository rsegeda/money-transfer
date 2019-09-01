/*
    Created by Roman Segeda on 19 August 2019
*/

package com.rsegeda.moneytransfer.service;

import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.exception.AccountServiceException;
import com.rsegeda.moneytransfer.exception.AccountUpdateException;
import com.rsegeda.moneytransfer.exception.AccountWasNotFoundException;
import com.rsegeda.moneytransfer.service.model.Account;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;

@Slf4j
@Singleton
public class AccountService {

  private final ConcurrentHashMap<UUID, Account> accounts;

  private AccountMapper accountMapper;

  public AccountService() {
    this(new ConcurrentHashMap<>());
  }

  AccountService(ConcurrentHashMap<UUID, Account> accounts) {
    this.accounts = accounts;
  }

  @Inject
  public void setAccountMapper(AccountMapper accountMapper) {
    this.accountMapper = accountMapper;
  }

  public CompletableFuture<Set<AccountDto>> getAccounts() {
    return CompletableFuture.supplyAsync(() ->
        this.accounts.values()
            .stream()
            .map(accountMapper::toDto)
            .collect(Collectors.toSet()));
  }

  public CompletableFuture<AccountDto> addAccount(AccountDto account) {
    Account newAcc = accountMapper.toDomainCreateOrUpdate(account);
    this.accounts.put(newAcc.getUuid(), newAcc);
    return CompletableFuture.completedFuture(accountMapper.toDto(newAcc));
  }

  public CompletableFuture<AccountDto> getAccount(String uuid) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        UUID parsedUUID = UUID.fromString(uuid);
        return accountMapper.toDto(findAccount(parsedUUID));
      } catch (Exception e) {
        throw new AccountServiceException(e.getMessage());
      }
    });
  }

  public void deleteAccount(String uuid) {
    UUID accUuid = UUID.fromString(uuid);
    this.accounts.remove(accUuid);
  }

  public CompletableFuture<AccountDto> updateAccount(String uuid, AccountDto newAccountDto) {
    return CompletableFuture.supplyAsync(() -> {
      UUID accUuid = UUID.fromString(uuid);
      Account accountToUpdate = findAccount(accUuid);
      Account updatedAccount = updateAccountDetails(accountToUpdate,
          accountMapper.toDomainCreateOrUpdate(newAccountDto));

      return accountMapper.toDto(updatedAccount);
    });
  }

  Account findAccount(UUID uuid) {
    return Optional.ofNullable(this.accounts.get(uuid))
        .orElseThrow(() -> new AccountWasNotFoundException(uuid.toString()));
  }

  Account updateAccountDetails(Account updatedAccount, Account updaterAccount) throws AccountUpdateException {
    try {
      for (Map.Entry<String, Object> e : PropertyUtils.describe(updaterAccount).entrySet()) {
        if (e.getValue() != null && !e.getKey().equals("class") && !e.getKey().equals("uuid")) {
          PropertyUtils.setProperty(updatedAccount, e.getKey(), e.getValue());
        }
      }
    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
      throw new AccountUpdateException(e.getMessage());
    }

    return updatedAccount;
  }
}
