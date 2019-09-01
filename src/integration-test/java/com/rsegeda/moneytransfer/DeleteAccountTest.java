/*
    Created by Roman Segeda on 31 August 2019
*/

package com.rsegeda.moneytransfer;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.rsegeda.moneytransfer.Utils.HOST_ADDRESS;
import static com.rsegeda.moneytransfer.Utils.createAccountToDelete;

class DeleteAccountTest {

  @Test
  void returnsStatusCode204() throws IOException {
    String accountToDeleteUuid = createAccountToDelete();
    HttpDelete request = new HttpDelete(HOST_ADDRESS + "/accounts/" + accountToDeleteUuid);

    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    Assertions.assertEquals(HttpStatus.NO_CONTENT_204,
        httpResponse.getStatusLine().getStatusCode());
  }
}