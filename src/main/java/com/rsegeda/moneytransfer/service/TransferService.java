/*
    Created by Roman Segeda on 30 August 2019
*/

package com.rsegeda.moneytransfer.service;

import com.rsegeda.moneytransfer.controller.dto.TransferDto;
import com.rsegeda.moneytransfer.exception.IllegalTransferSumException;
import com.rsegeda.moneytransfer.exception.InsufficientFundException;
import com.rsegeda.moneytransfer.service.model.Account;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferService {

  private AccountService accountService;

  @Inject
  public void setAccountService(AccountService accountService) {
    this.accountService = accountService;
  }

  public CompletableFuture<BigDecimal> transfer(TransferDto transferDto) {
    BigDecimal sum = transferDto.getSum();

    if (sum.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalTransferSumException(sum);
    }

    Account senderAccount = accountService.findAccount(transferDto.getSender());
    Account receiverAccount = accountService.findAccount(transferDto.getReceiver());

    return CompletableFuture.supplyAsync(() -> {
      BigDecimal balanceLeft = senderAccount.getBalance().updateAndGet(balance -> {
        if (balance.compareTo(sum) < 0) {
          throw new InsufficientFundException(senderAccount.getUuid(), sum);
        }

        return balance.subtract(sum);
      });

      receiverAccount.getBalance().updateAndGet(balance -> balance.add(sum));

      log.info(String.format("Account top up: %s with %s credits",
          receiverAccount.getUuid().toString(), sum.toString()));

      return balanceLeft;
    });
  }
}
