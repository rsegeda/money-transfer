/*
    Created by Roman Segeda on 31 August 2019
*/

package com.rsegeda.moneytransfer.exception;

import java.math.BigDecimal;

public class IllegalTransferSumException extends RuntimeException {

  public IllegalTransferSumException(BigDecimal sum) {
    super(String.format("Sum is less or equal to zero. Value: %s", sum.toString()));
  }
}
