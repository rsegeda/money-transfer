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
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.rsegeda.moneytransfer.Utils.*;

class GetAccountsTest {

  private static List<String> accountsToCleanup = new ArrayList<>();

  @AfterEach
  void cleanup() throws IOException {
    for (String uuid : accountsToCleanup) {
      HttpDelete request = new HttpDelete(HOST_ADDRESS + "/accounts/" + uuid);
      HttpClientBuilder.create().build().execute(request);
    }
  }

  @Test
  void returnStatusCode200() throws IOException {
    createAccountAndUpdateCleanupList(accountsToCleanup);
    createAccountAndUpdateCleanupList(accountsToCleanup);

    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts");

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    Assertions.assertEquals(HttpStatus.OK_200, httpResponse.getStatusLine().getStatusCode());
  }

  @Test
  void returnsMimeTypeJson() throws IOException {
    createAccountAndUpdateCleanupList(accountsToCleanup);
    createAccountAndUpdateCleanupList(accountsToCleanup);

    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts");
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();

    Assertions.assertEquals(ContentType.APPLICATION_JSON.getMimeType(), mimeType);
  }

  @Test
  void returnsResponseBodyStatusSuccessful() throws IOException {
    createAccountAndUpdateCleanupList(accountsToCleanup);
    createAccountAndUpdateCleanupList(accountsToCleanup);

    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts");

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);

    Assertions.assertEquals(StatusResponse.SUCCESSFUL, bodyResponse.getStatus());
  }

  @Test
  void returnsResponseBodyDataWithAllAccounts() throws IOException {
    String a = createAccountAndUpdateCleanupList(accountsToCleanup);
    String b = createAccountAndUpdateCleanupList(accountsToCleanup);
    String c = createAccountAndUpdateCleanupList(accountsToCleanup);
    String d = createAccountAndUpdateCleanupList(accountsToCleanup);

    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts");

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);
    Set<AccountDto> accountDtoSet = parseBodyResponseToAccountDtoList(bodyResponse);

    Assertions.assertEquals(4,
        accountDtoSet.stream()
            .filter(accountDto ->
                accountDto.getUuid().toString().matches(String.format("%s|%s|%s|%s", a, b, c, d)))
            .toArray().length);
  }

  @Test
  void returnsResponseBodyDataWithEmptyArray() throws IOException {
    HttpUriRequest request = new HttpGet(HOST_ADDRESS + "/accounts");

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);
    Set<AccountDto> accountDtoSet = parseBodyResponseToAccountDtoList(bodyResponse);

    Assertions.assertEquals(0, accountDtoSet.toArray().length);
  }
}
