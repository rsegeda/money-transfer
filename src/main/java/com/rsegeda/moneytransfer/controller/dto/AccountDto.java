/*
    Created by Roman Segeda on 21 August 2019
*/

package com.rsegeda.moneytransfer.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.google.gson.annotations.Expose;
import lombok.Data;

@Data
public class AccountDto {

  @Expose(deserialize = false)
  final UUID uuid;

  @Expose
  final UUID ownerUuid;

  @Expose
  final BigDecimal balance;

  public AccountDto(UUID uuid, UUID ownerUuid, BigDecimal deposit) {
    this.uuid = uuid;
    this.ownerUuid = ownerUuid;
    this.balance = deposit;
  }
}
