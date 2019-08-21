/*
    Created by Roman Segeda on 20 August 2019
*/

package com.rsegeda.moneytransfer.exception;

public class AccountWasNotFoundException extends RuntimeException {

  public AccountWasNotFoundException(String uuid) {
    super(String.format("Account %s was not found", uuid));
  }
}
