/*
    Created by Roman Segeda on 01 September 2019
*/

package com.rsegeda.moneytransfer;

import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.controller.response.BodyResponse;
import com.rsegeda.moneytransfer.controller.response.StatusResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.rsegeda.moneytransfer.Utils.*;

class PutAccountTest {

  private static List<String> accountsToCleanup = new ArrayList<>();

  @AfterAll
  static void cleanup() throws IOException {
    for (String uuid : accountsToCleanup) {
      HttpDelete request = new HttpDelete(HOST_ADDRESS + "/accounts/" + uuid);
      HttpClientBuilder.create().build().execute(request);
    }
  }

  @Test
  void returnsStatus200() throws IOException {
    String accountUUID =
        createAccountToPut(UUID.randomUUID(), new BigDecimal(10000), accountsToCleanup);

    HttpPut request = new HttpPut(HOST_ADDRESS + "/accounts/" + accountUUID);
    String body = "{\n" +
        "    \"balance\": \"9000\"\n" +
        "}";

    request.setEntity(new StringEntity(body));
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json");

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    Assertions.assertEquals(HttpStatus.OK_200, httpResponse.getStatusLine().getStatusCode());
  }

  @Test
  void returnsMimeTypeJson() throws IOException {
    String accountUUID =
        createAccountToPut(UUID.randomUUID(), new BigDecimal(10000), accountsToCleanup);

    HttpPut request = new HttpPut(HOST_ADDRESS + "/accounts/" + accountUUID);
    String body = "{\n" +
        "    \"balance\": \"2000\"\n" +
        "}";

    request.setEntity(new StringEntity(body));

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();

    Assertions.assertEquals(ContentType.APPLICATION_JSON.getMimeType(), mimeType);
  }

  @Test
  void returnsResponseBodyStatusSuccessful() throws IOException {
    String accountUUID =
        createAccountToPut(UUID.randomUUID(), new BigDecimal(10000), accountsToCleanup);

    HttpPut request = new HttpPut(HOST_ADDRESS + "/accounts/" + accountUUID);
    String body = "{\n" +
        "    \"balance\": \"9000\"\n" +
        "}";

    request.setEntity(new StringEntity(body));

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);

    Assertions.assertEquals(StatusResponse.SUCCESSFUL, bodyResponse.getStatus());
  }

  @Test
  void returnsResponseBodyWithUpdatedAccountOwnerIdField() throws IOException {
    String accountUUID =
        createAccountToPut(UUID.randomUUID(), new BigDecimal(10000), accountsToCleanup);

    HttpPut request = new HttpPut(HOST_ADDRESS + "/accounts/" + accountUUID);

    String newOwnerId = UUID.randomUUID().toString();

    String body = "{\n" +
        "    \"ownerUuid\": \"" + newOwnerId + "\"\n" +
        "}";

    request.setEntity(new StringEntity(body));

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);
    AccountDto accountDto = parseBodyResponseToAccountDto(bodyResponse);

    Assertions.assertEquals(newOwnerId, accountDto.getOwnerUuid().toString());
  }

  @Test
  void updatesAccount() throws IOException {
    String accountUUID =
        createAccountToPut(UUID.randomUUID(), new BigDecimal(60000), accountsToCleanup);

    HttpPut request = new HttpPut(HOST_ADDRESS + "/accounts/" + accountUUID);
    UUID newOwnerId = UUID.randomUUID();

    String body = "{\n" +
        "    \"ownerUuid\": \"" + newOwnerId + "\"\n" +
        "}";

    request.setEntity(new StringEntity(body));
    HttpClientBuilder.create().build().execute(request);
    AccountDto accountDto = fetchAccount(accountUUID);

    Assertions.assertEquals(newOwnerId, accountDto.getOwnerUuid());
  }
}
