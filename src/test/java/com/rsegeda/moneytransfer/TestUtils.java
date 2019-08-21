/*
    Created by Roman Segeda on 25 August 2019
*/

package com.rsegeda.moneytransfer;

import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.service.model.Account;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TestUtils {

  public static final UUID B_ACCOUNT_UUID = UUID.fromString("c87ce5c0-c2d4-11e9-9cb5-2a2ae2dbcce4");
  public static final UUID C_ACCOUNT_UUID = UUID.fromString("0dbb30d0-c378-11e9-9cb5-2a2ae2dbcce4");
  public static final UUID D_ACCOUNT_UUID = UUID.fromString("13cae66e-c378-11e9-9cb5-2a2ae2dbcce4");
  private static final UUID A_ACCOUNT_UUID =
      UUID.fromString("c87ce318-c2d4-11e9-9cb5-2a2ae2dbcce4");

  private static final UUID A_ACCOUNT_OWNER_UUID =
      UUID.fromString("1bcd480f-c24a-4ea7-b9fe-d4a8f6a22b37");

  private static final UUID B_ACCOUNT_OWNER_UUID =
      UUID.fromString("148df32e-0b30-4996-9b7f-a00338b19d66");

  private static final UUID C_ACCOUNT_OWNER_UUID =
      UUID.fromString("afd03e92-6333-4a35-8ee5-afefe50fe146");

  private static final UUID D_ACCOUNT_OWNER_UUID =
      UUID.fromString("23a291f2-666e-4bed-b7a0-f943014cca85");

  public static Account A_ACCOUNT =
      new Account(A_ACCOUNT_UUID, A_ACCOUNT_OWNER_UUID, new BigDecimal(1000));

  public static Account B_ACCOUNT =
      new Account(B_ACCOUNT_UUID, B_ACCOUNT_OWNER_UUID, new BigDecimal(2000));

  public static Account C_ACCOUNT =
      new Account(C_ACCOUNT_UUID, C_ACCOUNT_OWNER_UUID, new BigDecimal(3000));

  public static Account D_ACCOUNT =
      new Account(D_ACCOUNT_UUID, D_ACCOUNT_OWNER_UUID, new BigDecimal(6000));

  public static ConcurrentHashMap<UUID, Account> generateTestAccounts() {
    return new ConcurrentHashMap<UUID, Account>() {
      {
        put(A_ACCOUNT_UUID, A_ACCOUNT);
        put(B_ACCOUNT_UUID, B_ACCOUNT);
        put(C_ACCOUNT_UUID, C_ACCOUNT);
        put(D_ACCOUNT_UUID, D_ACCOUNT);
      }
    };
  }

  public static Account getAccountWithBalance(BigDecimal deposit) {
    return new Account(UUID.randomUUID(), UUID.randomUUID(), deposit);
  }

  public static AccountDto getAccountDtoWithBalance(BigDecimal deposit) {
    return new AccountDto(UUID.randomUUID(), UUID.randomUUID(), deposit);
  }

  public static String getDeserializedResponseDtoWithArrayOfAccountDto(Set<AccountDto> accountDtoList) {
    StringBuilder sb = new StringBuilder("{\"status\":\"SUCCESSFUL\",\"data\":[");

    for (Iterator<AccountDto> iterator = accountDtoList.iterator(); iterator.hasNext(); ) {
      AccountDto accountDto = iterator.next();
      sb.append(String.format("{\"uuid\":\"%s\",\"ownerUuid\":\"%s\",\"balance\":%s}",
          accountDto.getUuid().toString(), accountDto.getOwnerUuid().toString(),
          accountDto.getBalance().toString()));

      if (iterator.hasNext()) {
        sb.append(",");
      }
    }

    sb.append("]}");

    return sb.toString();
  }

  public static String getDeserializedResponseDtoWithAccountDto(AccountDto accountDto) {
    return "{\"status\":\"SUCCESSFUL\"," +
        "\"data\":{\"uuid\":\"" + accountDto.getUuid() + "\"," +
        "\"ownerUuid\":\"" + accountDto.getOwnerUuid() + "\",\"balance\":" + accountDto.getBalance() + "}}";
  }

  public static String getDeserializedResponseDto() {
    return "{\"status\":\"SUCCESSFUL\",\"message\":\"Funds left: 0\"}";
  }

  public static String getDeserializedResponseDtoWithThrowable(Throwable t) {
    return "{\"status\":\"FAILED\",\"message\":\"" + t.getMessage() + "\"}";
  }
}
