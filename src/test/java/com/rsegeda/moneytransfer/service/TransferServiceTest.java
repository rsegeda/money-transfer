/*
    Created by Roman Segeda on 30 August 2019
*/

package com.rsegeda.moneytransfer.service;

import com.rsegeda.moneytransfer.controller.dto.TransferDto;
import com.rsegeda.moneytransfer.exception.IllegalTransferSumException;
import com.rsegeda.moneytransfer.exception.InsufficientFundException;
import com.rsegeda.moneytransfer.service.model.Account;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.rsegeda.moneytransfer.TestUtils.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

  @Mock
  AccountService accountServiceMock;

  private TransferService transferService;

  @BeforeEach
  void setup() {
    transferService = new TransferService();
    transferService.setAccountService(accountServiceMock);
  }

  @Test
  void requestTransferSenderBalanceDecreases() throws ExecutionException, InterruptedException {
    BigDecimal sum = new BigDecimal(500);
    TransferDto transferDto = new TransferDto(B_ACCOUNT_UUID, C_ACCOUNT_UUID, sum);

    Account senderAccount = new Account(UUID.randomUUID(), UUID.randomUUID(),
        new BigDecimal(1000));

    when(accountServiceMock.findAccount(B_ACCOUNT_UUID)).thenReturn(senderAccount);
    when(accountServiceMock.findAccount(C_ACCOUNT_UUID)).thenReturn(C_ACCOUNT);

    CompletableFuture<BigDecimal> result = transferService.transfer(transferDto);

    Assertions.assertEquals(new BigDecimal(500), result.get());
  }

  @Test
  void requestTransferReceiverBalanceIncreases() throws ExecutionException, InterruptedException {
    BigDecimal sum = new BigDecimal(500);
    TransferDto transferDto = new TransferDto(B_ACCOUNT_UUID, C_ACCOUNT_UUID, sum);

    Account receiverAccount = new Account(UUID.randomUUID(), UUID.randomUUID(),
        new BigDecimal(1000));

    when(accountServiceMock.findAccount(B_ACCOUNT_UUID)).thenReturn(B_ACCOUNT);
    when(accountServiceMock.findAccount(C_ACCOUNT_UUID)).thenReturn(receiverAccount);

    transferService.transfer(transferDto).get();

    Assertions.assertEquals(new BigDecimal(1500), receiverAccount.getBalance().get());
  }

  @Test
  void requestTransferNegativeSum() throws ExecutionException, InterruptedException {
    BigDecimal sum = new BigDecimal(-500);
    TransferDto transferDto = new TransferDto(B_ACCOUNT_UUID, C_ACCOUNT_UUID, sum);

    try {
      transferService.transfer(transferDto).get();
    } catch (IllegalTransferSumException e) {
      Assertions.assertTrue(e.getMessage().startsWith("Sum is less or equal to zero. Value:"));
    }
  }

  @Test
  void requestTransferInsufficientFunds() {
    BigDecimal moreThanHaveSum = new BigDecimal(1000).add(B_ACCOUNT.getBalance().get());
    TransferDto transferDto = new TransferDto(B_ACCOUNT_UUID, C_ACCOUNT_UUID, moreThanHaveSum);

    when(accountServiceMock.findAccount(B_ACCOUNT_UUID)).thenReturn(B_ACCOUNT);
    when(accountServiceMock.findAccount(C_ACCOUNT_UUID)).thenReturn(C_ACCOUNT);

    try {
      transferService.transfer(transferDto).get();
    } catch (InterruptedException | ExecutionException e) {
      Assertions.assertTrue(e.getCause() instanceof InsufficientFundException);
    }
  }
}
