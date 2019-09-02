/*
    Created by Roman Segeda on 01 September 2019
*/

package com.rsegeda.moneytransfer;

import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.controller.response.BodyResponse;
import com.rsegeda.moneytransfer.controller.response.StatusResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.rsegeda.moneytransfer.Utils.*;

class PostAccountTest {

  private static List<String> accountsToCleanup = new ArrayList<>();

  @AfterAll
  static void cleanup() throws IOException {
    for (String uuid : accountsToCleanup) {
      HttpDelete request = new HttpDelete(HOST_ADDRESS + "/accounts/" + uuid);
      HttpClientBuilder.create().build().execute(request);
    }
  }

  @Test
  void returnsStatusCode201() throws IOException {
    HttpPost request = new HttpPost(HOST_ADDRESS + "/accounts");
    String body = "{\n" +
        "    \"ownerUuid\": \"" + UUID.randomUUID() + "\",\n" +
        "    \"balance\": 9000\n" +
        "}";

    request.setEntity(new StringEntity(body));
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json");

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    Assertions.assertEquals(HttpStatus.CREATED_201, httpResponse.getStatusLine().getStatusCode());

    accountsToCleanup.add(
        parseBodyResponseToAccountDto(
            parseHttpResponseToBodyResponse(httpResponse))
            .getUuid().toString());
  }

  @Test
  void returnsResponseMimeTypeJson() throws IOException {
    HttpPost request = new HttpPost(HOST_ADDRESS + "/accounts");
    String body = "{\n" +
        "    \"ownerUuid\": \"" + UUID.randomUUID() + "\",\n" +
        "    \"balance\": 6000\n" +
        "}";

    request.setEntity(new StringEntity(body));
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json");

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();

    Assertions.assertEquals(ContentType.APPLICATION_JSON.getMimeType(), mimeType);

    accountsToCleanup.add(
        parseBodyResponseToAccountDto(
            parseHttpResponseToBodyResponse(httpResponse))
            .getUuid().toString());
  }

  @Test
  void returnsResponseBodyStatusSuccessful() throws IOException {
    HttpPost request = new HttpPost(HOST_ADDRESS + "/accounts");
    String body = "{\n" +
        "    \"ownerUuid\": \"" + UUID.randomUUID() + "\",\n" +
        "    \"balance\": 2000\n" +
        "}";

    request.setEntity(new StringEntity(body));
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json");

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);

    Assertions.assertEquals(StatusResponse.SUCCESSFUL, bodyResponse.getStatus());

    accountsToCleanup.add(parseBodyResponseToAccountDto(bodyResponse).getUuid().toString());
  }

  @Test
  void returnsResponseBodyDataWithUUID() throws IOException {
    HttpPost request = new HttpPost(HOST_ADDRESS + "/accounts");
    String body = "{\n" +
        "    \"ownerUuid\": \"" + UUID.randomUUID() + "\",\n" +
        "    \"balance\": 22000\n" +
        "}";

    request.setEntity(new StringEntity(body));
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json");

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);
    AccountDto accountDto = parseBodyResponseToAccountDto(bodyResponse);

    Assertions.assertNotNull(accountDto.getUuid());

    accountsToCleanup.add(accountDto.getUuid().toString());
  }

  @Test
  void returnsResponseBodyDataWithBalance() throws IOException {
    HttpPost request = new HttpPost(HOST_ADDRESS + "/accounts");
    String body = "{\n" +
        "    \"ownerUuid\": \"" + UUID.randomUUID() + "\",\n" +
        "    \"balance\": 222200\n" +
        "}";

    request.setEntity(new StringEntity(body));

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);
    AccountDto accountDto = parseBodyResponseToAccountDto(bodyResponse);

    Assertions.assertNotNull(accountDto.getBalance());

    accountsToCleanup.add(accountDto.getUuid().toString());
  }

}
