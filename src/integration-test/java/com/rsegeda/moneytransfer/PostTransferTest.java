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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.rsegeda.moneytransfer.Utils.*;

class PostTransferTest {

  private static List<String> accountsToCleanup = new ArrayList<>();

  @AfterAll
  static void cleanup() throws IOException {
    for (String uuid : accountsToCleanup) {
      HttpDelete request = new HttpDelete(HOST_ADDRESS + "/accounts/" + uuid);
      HttpClientBuilder.create().build().execute(request);
    }
  }

  @Test
  void returnStatusCode200() throws IOException {
    String senderUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(6000));
    String receiverUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(500));
    BigDecimal sum = new BigDecimal(2000);

    HttpPost request = buildTransferRequest(senderUuid, receiverUuid, sum);
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    Assertions.assertEquals(HttpStatus.OK_200, httpResponse.getStatusLine().getStatusCode());
  }

  @Test
  void returnsMimeTypeJson() throws IOException {
    String senderUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(7000));
    String receiverUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(700));
    BigDecimal sum = new BigDecimal(1000);

    HttpPost request = buildTransferRequest(senderUuid, receiverUuid, sum);
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();

    Assertions.assertEquals(ContentType.APPLICATION_JSON.getMimeType(), mimeType);
  }

  @Test
  void returnsResponseBodyStatusSuccessful() throws IOException {
    String senderUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(9900));
    String receiverUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(700));
    BigDecimal sum = new BigDecimal(1000);

    HttpPost request = buildTransferRequest(senderUuid, receiverUuid, sum);
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);

    Assertions.assertEquals(StatusResponse.SUCCESSFUL, bodyResponse.getStatus());
  }

  @Test
  void returnsResponseBodyStatusFailed() throws IOException {
    String senderUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(900));
    String receiverUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(700));
    BigDecimal sum = new BigDecimal(1000);

    HttpPost request = buildTransferRequest(senderUuid, receiverUuid, sum);
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);

    Assertions.assertEquals(StatusResponse.FAILED, bodyResponse.getStatus());
  }

  @Test
  void returnsResponseBodyMessage() throws IOException {
    String senderUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(100));
    String receiverUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(700));
    BigDecimal sum = new BigDecimal(2000);

    HttpPost request = buildTransferRequest(senderUuid, receiverUuid, sum);
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    BodyResponse bodyResponse = parseHttpResponseToBodyResponse(httpResponse);

    Assertions.assertEquals(String.format("Account %s has not enough funds to transfer %s " +
        "credits", senderUuid, sum), bodyResponse.getMessage());
  }

  @Test
  void senderBalanceDecreases() throws IOException {
    String senderUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(9900));
    String receiverUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(700));
    BigDecimal sum = new BigDecimal(1000);

    HttpPost request = buildTransferRequest(senderUuid, receiverUuid, sum);
    HttpClientBuilder.create().build().execute(request);
    AccountDto accountDto = Utils.fetchAccount(senderUuid);

    Assertions.assertEquals(new BigDecimal(8900), accountDto.getBalance());
  }

  @Test
  void receiverBalanceIncreases() throws IOException {
    String senderUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(400));
    String receiverUuid = createAccountAndUpdateCleanupList(accountsToCleanup, new BigDecimal(700));
    BigDecimal sum = new BigDecimal(100);

    HttpPost request = buildTransferRequest(senderUuid, receiverUuid, sum);
    HttpClientBuilder.create().build().execute(request);
    AccountDto accountDto = Utils.fetchAccount(receiverUuid);

    Assertions.assertEquals(new BigDecimal(800), accountDto.getBalance());
  }
}
