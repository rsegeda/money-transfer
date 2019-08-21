/*
    Created by Roman Segeda on 20 August 2019
*/

package com.rsegeda.moneytransfer.service.model;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Data;

@Data
public class Account {

  final UUID uuid;

  UUID ownerUuid;

  AtomicReference<BigDecimal> balance;

  public Account(UUID uuid, UUID ownerUuid, BigDecimal deposit) {
    this.uuid = uuid;
    this.ownerUuid = ownerUuid;
    this.balance = new AtomicReference<>(deposit);
  }
}
