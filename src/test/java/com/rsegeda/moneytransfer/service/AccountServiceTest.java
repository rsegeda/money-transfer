/*
    Created by Roman Segeda on 21 August 2019
*/

package com.rsegeda.moneytransfer.service;

import com.rsegeda.moneytransfer.TestUtils;
import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.controller.dto.TransferDto;
import com.rsegeda.moneytransfer.exception.AccountServiceException;
import com.rsegeda.moneytransfer.exception.AccountUpdateException;
import com.rsegeda.moneytransfer.exception.InsufficientFundException;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  Account accountMock;
  @Mock
  AccountDto accountDtoMock;
  @Mock
  private AccountMapper accountMapperMock;
  private AccountService accountServiceSpy;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.initMocks(this);
    accountServiceSpy = Mockito.spy(new AccountService(TestUtils.generateTestAccounts()));
    accountServiceSpy.setAccountMapper(accountMapperMock);
  }

  @Test
  void topUpAccount() throws ExecutionException, InterruptedException {
    Account account = TestUtils.getAccountWithBalance(new BigDecimal(1000));
    BigDecimal before = account.getBalance().get();
    BigDecimal sum = new BigDecimal(666);
    BigDecimal balanceExpected = before.add(sum);

    CompletableFuture<BigDecimal> result = accountServiceSpy.topUpAccount(account, sum);

    Assertions.assertEquals(balanceExpected, result.get());
  }

  @Test
  void withdraw() throws ExecutionException, InterruptedException {
    Account account = TestUtils.getAccountWithBalance(new BigDecimal(1000));
    BigDecimal before = account.getBalance().get();
    BigDecimal sum = new BigDecimal(666);
    BigDecimal balanceExpected = before.subtract(sum);

    CompletableFuture<BigDecimal> result = accountServiceSpy.withdrawFromAccount(account, sum);
    Assertions.assertEquals(balanceExpected, result.get());
  }

  @Test
  void withdrawInsufficientFunds() {
    Account account = TestUtils.getAccountWithBalance(new BigDecimal(1000));
    BigDecimal sum = new BigDecimal(1666);

    CompletableFuture<BigDecimal> result = accountServiceSpy.withdrawFromAccount(account, sum);

    result.exceptionallyAsync(e -> {
      Assertions.assertTrue(e.getCause() instanceof InsufficientFundException);
      return BigDecimal.ZERO;
    });
  }

  @Test
  void updateAccountDetailsCopiesOwnerUUIDField() throws AccountUpdateException {
    Account to = TestUtils.getAccountWithBalance(BigDecimal.ZERO);
    Account from = TestUtils.getAccountWithBalance(BigDecimal.TEN);

    accountServiceSpy.updateAccountDetails(to, from);

    Assertions.assertNotEquals(to.getUuid(), from.getUuid());
  }

  @Test
  void updateAccountDetailsSkipsUUIDField() throws AccountUpdateException {
    Account to = TestUtils.getAccountWithBalance(BigDecimal.ZERO);
    Account from = TestUtils.getAccountWithBalance(BigDecimal.TEN);

    accountServiceSpy.updateAccountDetails(to, from);

    Assertions.assertNotEquals(to.getUuid(), from.getUuid());
  }

  @Test
  void getAccount() throws ExecutionException, InterruptedException {
    String uuid = UUID.randomUUID().toString();

    doReturn(accountMock).when(accountServiceSpy).findAccount(UUID.fromString(uuid));
    when(accountMapperMock.toDto(accountMock)).thenReturn(accountDtoMock);

    CompletableFuture<AccountDto> result = accountServiceSpy.getAccount(uuid);

    Assertions.assertNotNull(result.get());
  }

  @Test
  void getAccountHandlesException() {
    String uuid = UUID.randomUUID().toString();

    CompletableFuture<AccountDto> result = accountServiceSpy.getAccount(uuid);

    result.exceptionallyAsync(e -> {
      Assertions.assertTrue(e.getCause() instanceof AccountServiceException);
      return null;
    });
  }

  @Test
  void addAccount() throws ExecutionException, InterruptedException {
    AccountDto accountDto = TestUtils.getAccountDtoWithBalance(new BigDecimal(1000));
    UUID newUUID = UUID.randomUUID();

    when(accountMapperMock.toDomainCreateOrUpdate(accountDto)).thenReturn(accountMock);
    when(accountMock.getUuid()).thenReturn(newUUID);
    when(accountMapperMock.toDto(accountMock)).thenReturn(accountDtoMock);

    CompletableFuture<AccountDto> result = accountServiceSpy.addAccount(accountDto);

    Assertions.assertEquals(accountDtoMock, result.get());
  }

  @Test
  void getAccounts() throws ExecutionException, InterruptedException {
    // Uses the generated accounts

    AccountDto aMockAccountDto = Mockito.mock(AccountDto.class);
    AccountDto bMockAccountDto = Mockito.mock(AccountDto.class);
    AccountDto cMockAccountDto = Mockito.mock(AccountDto.class);
    AccountDto dMockAccountDto = Mockito.mock(AccountDto.class);

    when(accountMapperMock.toDto(A_ACCOUNT)).thenReturn(aMockAccountDto);
    when(accountMapperMock.toDto(B_ACCOUNT)).thenReturn(bMockAccountDto);
    when(accountMapperMock.toDto(C_ACCOUNT)).thenReturn(cMockAccountDto);
    when(accountMapperMock.toDto(D_ACCOUNT)).thenReturn(dMockAccountDto);

    CompletableFuture<Set<AccountDto>> result = accountServiceSpy.getAccounts();

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

    accountServiceSpy.deleteAccount(accountUUIDToRemove);

    when(accountMapperMock.toDto(A_ACCOUNT)).thenReturn(aMockAccountDto);
    when(accountMapperMock.toDto(B_ACCOUNT)).thenReturn(bMockAccountDto);
    when(accountMapperMock.toDto(C_ACCOUNT)).thenReturn(cMockAccountDto);

    Set<AccountDto> result = accountServiceSpy.getAccounts().get();

    Assertions.assertEquals(3, result.size());
  }

  @Test
  void updateAccountOwnerId() throws ExecutionException, InterruptedException,
      AccountUpdateException {
    AccountDto newAccountDto = new AccountDto(null, UUID.randomUUID(), new BigDecimal(10000));
    String accountUUIDToUpdate = D_ACCOUNT_UUID.toString();

    doReturn(D_ACCOUNT).when(accountServiceSpy).findAccount(D_ACCOUNT_UUID);

    Account updatedAccountDetails = new Account(D_ACCOUNT_UUID, newAccountDto.getOwnerUuid(),
        newAccountDto.getBalance());

    Account updaterAccountMock = Mockito.mock(Account.class);

    when(accountMapperMock.toDomainCreateOrUpdate(newAccountDto)).thenReturn(updaterAccountMock);

    doReturn(updatedAccountDetails).when(accountServiceSpy).updateAccountDetails(D_ACCOUNT,
        updaterAccountMock);

    AccountDto updatedAccountDto = new AccountDto(D_ACCOUNT_UUID, newAccountDto.getOwnerUuid(),
        newAccountDto.getBalance());

    doReturn(updatedAccountDto).when(accountMapperMock).toDto(updatedAccountDetails);

    CompletableFuture<AccountDto> result =
        accountServiceSpy.updateAccount(accountUUIDToUpdate, newAccountDto);

    Assertions.assertEquals(newAccountDto.getOwnerUuid(), result.get().getOwnerUuid());
  }

  @Test
  void requestTransfer() throws ExecutionException, InterruptedException {
    BigDecimal sum = new BigDecimal(500);
    TransferDto transferDto = new TransferDto(B_ACCOUNT_UUID, C_ACCOUNT_UUID, sum);

    doReturn(B_ACCOUNT).when(accountServiceSpy).findAccount(B_ACCOUNT_UUID);
    doReturn(C_ACCOUNT).when(accountServiceSpy).findAccount(C_ACCOUNT_UUID);

    BigDecimal senderNewBalance = B_ACCOUNT.getBalance().get().subtract(sum);
    doReturn(CompletableFuture.completedFuture(senderNewBalance))
        .when(accountServiceSpy)
        .withdrawFromAccount(B_ACCOUNT, sum);

    BigDecimal receiverNewBalance = C_ACCOUNT.getBalance().get().add(sum);
    doReturn(CompletableFuture.completedFuture(receiverNewBalance))
        .when(accountServiceSpy)
        .topUpAccount(C_ACCOUNT, sum);

    CompletableFuture<BigDecimal> result = accountServiceSpy.requestTransfer(transferDto);

    Assertions.assertEquals(senderNewBalance, result.get());
  }

  @Test
  void updateAccountDetails() {
    Account accountToUpdateWith = Mockito.mock(Account.class);
    Assertions.assertThrows(AccountUpdateException.class,
        () -> accountServiceSpy.updateAccountDetails(A_ACCOUNT, accountToUpdateWith));
  }
}
