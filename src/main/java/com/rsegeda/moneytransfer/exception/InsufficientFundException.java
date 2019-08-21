/*
    Created by Roman Segeda on 20 August 2019
*/

package com.rsegeda.moneytransfer.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundException extends RuntimeException {

  public InsufficientFundException(UUID uuid, BigDecimal sum) {
    super(String.format("Account %s has not enough funds to withdraw %s credits", uuid,
        sum.toString()));
  }
}
