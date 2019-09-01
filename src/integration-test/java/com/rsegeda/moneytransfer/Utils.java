/*
    Created by Roman Segeda on 01 September 2019
*/

package com.rsegeda.moneytransfer;

import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.controller.response.BodyResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

class Utils {

  static String HOST_ADDRESS = "http://localhost:4567";

  static String createAccountAndUpdateCleanupList(List<String> listToCleanup) throws IOException {
    String body = "{\n" +
        "    \"ownerUuid\": \"" + UUID.randomUUID() + "\",\n" +
        "    \"balance\": \"100000\"\n" +
        "}";

    String uuidOfCreatedAccount = createAccount(body);
    listToCleanup.add(uuidOfCreatedAccount);
    return uuidOfCreatedAccount;
  }

  static String createAccountAndUpdateCleanupList(List<String> listToCleanup, BigDecimal balance) throws IOException {
    String body = "{\n" +
        "    \"ownerUuid\": \"" + UUID.randomUUID() + "\",\n" +
        "    \"balance\": \"" + balance + "\"\n" +
        "}";

    String uuidOfCreatedAccount = createAccount(body);
    listToCleanup.add(uuidOfCreatedAccount);
    return uuidOfCreatedAccount;
  }

  static String createAccountToDelete() throws IOException {
    String body = "{\n" +
        "    \"ownerUuid\": \"" + UUID.randomUUID() + "\",\n" +
        "    \"balance\": \"100000\"\n" +
        "}";

    return createAccount(body);
  }

  static String createAccountToPut(UUID ownerUUID, BigDecimal balance,
                                   List<String> accountsToCleanup) throws IOException {
    String body = "{\n" +
        "    \"ownerUuid\": \"" + ownerUUID + "\",\n" +
        "    \"balance\": \"" + balance + "\"\n" +
        "}";

    String uuid = createAccount(body);
    accountsToCleanup.add(uuid);
    return uuid;
  }

  private static String createAccount(String body) throws IOException {
    HttpPost request = new HttpPost(HOST_ADDRESS + "/accounts");
    request.setEntity(new StringEntity(body));
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json");

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    return parseBodyResponseToAccountDto(
        parseHttpResponseToBodyResponse(httpResponse))
        .getUuid().toString();

  }

  static BodyResponse parseHttpResponseToBodyResponse(HttpResponse response) throws IOException {
    String jsonFromResponse = EntityUtils.toString(response.getEntity());
    return new Gson().fromJson(jsonFromResponse, BodyResponse.class);
  }

  static AccountDto parseBodyResponseToAccountDto(BodyResponse bodyResponse) {
    return new Gson().fromJson(bodyResponse.getData(), AccountDto.class);
  }

  static Set<AccountDto> parseBodyResponseToAccountDtoList(BodyResponse bodyResponse) {
    Type accountSetType = new TypeToken<HashSet<AccountDto>>() {
    }.getType();
    return new Gson().fromJson(bodyResponse.getData(), accountSetType);
  }

  static HttpPost buildTransferRequest(String senderUuid, String receiverUuid,
                                       BigDecimal sum) throws UnsupportedEncodingException {
    String body = "{\n" +
        "    \"sender\": \"" + senderUuid + "\",\n" +
        "    \"receiver\": \"" + receiverUuid + "\",\n" +
        "    \"sum\": \"" + sum + "\"\n" +
        "}";

    HttpPost request = new HttpPost(HOST_ADDRESS + "/transfers");
    request.setEntity(new StringEntity(body));
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json");
    return request;
  }

  static AccountDto fetchAccount(String receiverUuid) throws IOException {
    HttpUriRequest getRequest = new HttpGet(HOST_ADDRESS + "/accounts/" + receiverUuid);
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(getRequest);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);
    return parseBodyResponseToAccountDto(bodyResponse);
  }
}
