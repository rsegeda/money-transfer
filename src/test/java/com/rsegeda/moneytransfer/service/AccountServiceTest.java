/*
    Created by Roman Segeda on 21 August 2019
*/

package com.rsegeda.moneytransfer.service;

import com.rsegeda.moneytransfer.TestUtils;
import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.exception.AccountServiceException;
import com.rsegeda.moneytransfer.exception.AccountUpdateException;
import com.rsegeda.moneytransfer.service.model.Account;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.rsegeda.moneytransfer.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  Account accountMock;
  @Mock
  AccountDto accountDtoMock;
  @Mock
  private AccountMapper accountMapperMock;

  private AccountService accountService;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.initMocks(this);
    accountService = new AccountService(TestUtils.generateTestAccounts());
    accountService.setAccountMapper(accountMapperMock);
  }

  @Test
  void updateAccountDetailsCopiesOwnerUUIDField() throws AccountUpdateException {
    Account to = TestUtils.getAccountWithBalance(BigDecimal.ZERO);
    Account from = TestUtils.getAccountWithBalance(BigDecimal.TEN);

    accountService.updateAccountDetails(to, from);

    Assertions.assertNotEquals(to.getUuid(), from.getUuid());
  }

  @Test
  void updateAccountDetailsSkipsUUIDField() throws AccountUpdateException {
    Account to = TestUtils.getAccountWithBalance(BigDecimal.ZERO);
    Account from = TestUtils.getAccountWithBalance(BigDecimal.TEN);

    accountService.updateAccountDetails(to, from);

    Assertions.assertNotEquals(to.getUuid(), from.getUuid());
  }

  @Test
  void getAccount() throws ExecutionException, InterruptedException {
    when(accountMapperMock.toDto(any())).thenReturn(accountDtoMock);

    CompletableFuture<AccountDto> result =
        accountService.getAccount(A_ACCOUNT.getUuid().toString());

    Assertions.assertNotNull(result.get());
  }

  @Test
  void getAccountHandlesException() {
    String uuid = UUID.randomUUID().toString();

    CompletableFuture<AccountDto> result = accountService.getAccount(uuid);

    try {
      result.get();
    } catch (InterruptedException | ExecutionException e) {
      Assertions.assertTrue(e.getCause() instanceof AccountServiceException);
    }
  }

  @Test
  void addAccount() throws ExecutionException, InterruptedException {
    AccountDto accountDto = TestUtils.getAccountDtoWithBalance(new BigDecimal(1000));
    UUID newUUID = UUID.randomUUID();

    when(accountMapperMock.toDomainCreateOrUpdate(accountDto)).thenReturn(accountMock);
    when(accountMock.getUuid()).thenReturn(newUUID);
    when(accountMapperMock.toDto(accountMock)).thenReturn(accountDtoMock);

    CompletableFuture<AccountDto> result = accountService.addAccount(accountDto);

    Assertions.assertEquals(accountDtoMock, result.get());
  }

  @Test
  void getAccounts() throws ExecutionException, InterruptedException {
    AccountDto aMockAccountDto = Mockito.mock(AccountDto.class);
    AccountDto bMockAccountDto = Mockito.mock(AccountDto.class);
    AccountDto cMockAccountDto = Mockito.mock(AccountDto.class);
    AccountDto dMockAccountDto = Mockito.mock(AccountDto.class);

    when(accountMapperMock.toDto(A_ACCOUNT)).thenReturn(aMockAccountDto);
    when(accountMapperMock.toDto(B_ACCOUNT)).thenReturn(bMockAccountDto);
    when(accountMapperMock.toDto(C_ACCOUNT)).thenReturn(cMockAccountDto);
    when(accountMapperMock.toDto(D_ACCOUNT)).thenReturn(dMockAccountDto);

    CompletableFuture<Set<AccountDto>> result = accountService.getAccounts();

    Assertions.assertTrue(
        result.get().containsAll(
            Arrays.asList(aMockAccountDto, bMockAccountDto, cMockAccountDto, dMockAccountDto)));
  }

  @Test
  void deleteAccount() throws ExecutionException, InterruptedException {
    AccountDto aMockAccountDto = Mockito.mock(AccountDto.class);
    AccountDto bMockAccountDto = Mockito.mock(AccountDto.class);
    AccountDto cMockAccountDto = Mockito.mock(AccountDto.class);

    String accountUUIDToRemove = D_ACCOUNT_UUID.toString();

    accountService.deleteAccount(accountUUIDToRemove);

    when(accountMapperMock.toDto(A_ACCOUNT)).thenReturn(aMockAccountDto);
    when(accountMapperMock.toDto(B_ACCOUNT)).thenReturn(bMockAccountDto);
    when(accountMapperMock.toDto(C_ACCOUNT)).thenReturn(cMockAccountDto);

    Set<AccountDto> result = accountService.getAccounts().get();

    Assertions.assertEquals(3, result.size());
  }

  @Test
  void updateAccountOwnerId() throws ExecutionException, InterruptedException,
      AccountUpdateException {

    AccountDto newAccountDto = new AccountDto(null, UUID.randomUUID(),
        D_ACCOUNT.getBalance().get());

    String accountUUIDToUpdate = D_ACCOUNT_UUID.toString();

    Account mappedAccount = new Account(UUID.fromString(accountUUIDToUpdate),
        newAccountDto.getOwnerUuid(),
        D_ACCOUNT.getBalance().get());

    when(accountMapperMock.toDomainCreateOrUpdate(newAccountDto)).thenReturn(mappedAccount);

    AccountDto updatedAccountDto = new AccountDto(D_ACCOUNT_UUID, newAccountDto.getOwnerUuid(),
        newAccountDto.getBalance());

    when(accountMapperMock.toDto(any(Account.class))).thenReturn(updatedAccountDto);

    CompletableFuture<AccountDto> result =
        accountService.updateAccount(accountUUIDToUpdate, newAccountDto);

    Assertions.assertEquals(newAccountDto.getOwnerUuid(), result.get().getOwnerUuid());
  }

  @Test
  void updateAccountDetails() {
    Account accountToUpdateWith = Mockito.mock(Account.class);
    Assertions.assertThrows(AccountUpdateException.class,
        () -> accountService.updateAccountDetails(A_ACCOUNT, accountToUpdateWith));
  }
}
