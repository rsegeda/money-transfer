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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.rsegeda.moneytransfer.Utils.*;

class GetAccountTest {

  private static List<String> accountsToCleanup = new ArrayList<>();
  private static String accountToGetUuid;

  @BeforeAll
  static void setup() throws IOException {
    accountToGetUuid = createAccountAndUpdateCleanupList(accountsToCleanup);
  }

  @AfterAll
  static void cleanup() throws IOException {
    for (String uuid : accountsToCleanup) {
      HttpDelete request = new HttpDelete(HOST_ADDRESS + "/accounts/" + uuid);
      HttpClientBuilder.create().build().execute(request);
    }
  }

  @Test
  void returnsStatusCode404() throws IOException {
    String uuid = UUID.randomUUID().toString();
    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts/" + uuid);

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    Assertions.assertEquals(HttpStatus.NOT_FOUND_404, httpResponse.getStatusLine().getStatusCode());
  }

  @Test
  void returnStatusCode200() throws IOException {
    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts/" + accountToGetUuid);

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    Assertions.assertEquals(HttpStatus.OK_200, httpResponse.getStatusLine().getStatusCode());
  }

  @Test
  void returnsMimeTypeJson() throws IOException {
    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts/" + accountToGetUuid);

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();

    Assertions.assertEquals(ContentType.APPLICATION_JSON.getMimeType(), mimeType);
  }

  @Test
  void returnsResponseBodyStatusSuccessful() throws IOException {
    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts/" + accountToGetUuid);

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);

    Assertions.assertEquals(StatusResponse.SUCCESSFUL, bodyResponse.getStatus());
  }

  @Test
  void returnsResponseBodyDataWithUUID() throws IOException {
    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts/" + accountToGetUuid);

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);
    AccountDto accountDto = parseBodyResponseToAccountDto(bodyResponse);

    Assertions.assertNotNull(accountDto.getUuid());
  }

  @Test
  void returnsResponseBodyDataWithBalance() throws IOException {
    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts/" + accountToGetUuid);

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);
    AccountDto accountDto = parseBodyResponseToAccountDto(bodyResponse);

    Assertions.assertNotNull(accountDto.getBalance());
  }

}
