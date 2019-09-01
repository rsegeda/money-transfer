/*
    Created by Roman Segeda on 21 August 2019
*/

package com.rsegeda.moneytransfer.controller;

import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.controller.dto.TransferDto;
import com.rsegeda.moneytransfer.exception.AccountServiceException;
import com.rsegeda.moneytransfer.service.AccountService;
import com.rsegeda.moneytransfer.service.TransferService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import spark.Request;
import spark.Response;

import static com.rsegeda.moneytransfer.TestUtils.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

  @Mock
  AccountService accountServiceMock;

  @Mock
  TransferService transferServiceMock;

  @Mock
  Request requestMock;

  @Mock
  Response responseMock;

  @Captor
  ArgumentCaptor<AccountDto> accountDtoArgumentCaptor;

  @Captor
  ArgumentCaptor<TransferDto> transferDtoArgumentCaptor;

  private Controller controller;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);
    controller = new Controller();
    controller.setAccountService(accountServiceMock);
    controller.setTransferService(transferServiceMock);
  }

  @Test
  void deleteAccount() {
    when(requestMock.params(":uuid")).thenReturn("exampleUuid");
    doNothing().when(accountServiceMock).deleteAccount(anyString());
    controller.deleteAccount(requestMock, responseMock);

    verify(accountServiceMock).deleteAccount("exampleUuid");
  }

  @Test
  void deleteAccountResponseStatus() {
    when(requestMock.params(":uuid")).thenReturn("exampleUuid");
    doNothing().when(accountServiceMock).deleteAccount(anyString());
    controller.deleteAccount(requestMock, responseMock);

    verify(responseMock).status(204);
  }

  @Test
  void getAccount() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    String exampleUuid = "exampleUuid";
    doNothing().when(responseMock).type("application/json");
    when(requestMock.params(":uuid")).thenReturn(exampleUuid);
    when(accountServiceMock.getAccount(exampleUuid)).thenReturn(CompletableFuture.completedFuture(accountDto));
    String result = controller.getAccount(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithAccountDto(accountDto), result);
  }

  @Test
  void getAccountResponseStatus() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    String exampleUuid = "exampleUuid";
    doNothing().when(responseMock).type("application/json");
    when(requestMock.params(":uuid")).thenReturn(exampleUuid);
    when(accountServiceMock.getAccount(exampleUuid)).thenReturn(CompletableFuture.completedFuture(accountDto));

    controller.getAccount(requestMock, responseMock);

    verify(responseMock).status(200);
  }

  @Test
  void getAccountHandlesCompletionException() {
    String exampleUuid = "exampleUuid";

    doNothing().when(responseMock).type("application/json");
    when(requestMock.params(":uuid")).thenReturn(exampleUuid);
    AccountServiceException expectedException = new AccountServiceException("Not found");
    CompletableFuture<AccountDto> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(expectedException);
    when(accountServiceMock.getAccount(exampleUuid)).thenReturn(failedFuture);

    String result = controller.getAccount(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithThrowable(expectedException), result);
  }

  @Test
  void getAccountHandlesCompletionExceptionResponseStatus() {
    String exampleUuid = "exampleUuid";

    doNothing().when(responseMock).type("application/json");
    when(requestMock.params(":uuid")).thenReturn(exampleUuid);
    AccountServiceException expectedException = new AccountServiceException("Not found");
    CompletableFuture<AccountDto> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(expectedException);
    when(accountServiceMock.getAccount(exampleUuid)).thenReturn(failedFuture);

    controller.getAccount(requestMock, responseMock);

    verify(responseMock).status(404);
  }

  @Test
  void getAccountHandlesExecutionException() {
    doNothing().when(responseMock).type("application/json");
    String exampleUuid = "exampleUuid";
    when(requestMock.params(":uuid")).thenReturn(exampleUuid);
    AccountServiceException expectedException = new AccountServiceException("Not found");
    when(accountServiceMock.getAccount(exampleUuid)).thenThrow(expectedException);

    String result = controller.getAccount(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithThrowable(expectedException), result);
  }

  @Test
  void getAccountHandlesExecutionExceptionResponseStatus() {
    doNothing().when(responseMock).type("application/json");
    String exampleUuid = "exampleUuid";
    when(requestMock.params(":uuid")).thenReturn(exampleUuid);
    AccountServiceException expectedException = new AccountServiceException("Not found");
    when(accountServiceMock.getAccount(exampleUuid)).thenThrow(expectedException);

    controller.getAccount(requestMock, responseMock);

    verify(responseMock).status(500);
  }

  @Test
  void putAccountHandlesExecutionException() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(accountDto));
    doNothing().when(responseMock).type("application/json");
    String exampleUuid = "exampleUuid";
    when(requestMock.params(":uuid")).thenReturn(exampleUuid);
    AccountServiceException expectedException = new AccountServiceException("Not found");
    when(accountServiceMock.updateAccount(eq(exampleUuid), accountDtoArgumentCaptor.capture()))
        .thenThrow(expectedException);

    String result = controller.putAccount(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithThrowable(expectedException), result);
  }

  @Test
  void putAccountHandlesExecutionExceptionResponseStatus() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(accountDto));
    doNothing().when(responseMock).type("application/json");
    String exampleUuid = "exampleUuid";
    when(requestMock.params(":uuid")).thenReturn(exampleUuid);
    AccountServiceException expectedException = new AccountServiceException("Not found");
    when(accountServiceMock.updateAccount(eq(exampleUuid), accountDtoArgumentCaptor.capture()))
        .thenThrow(expectedException);

    controller.putAccount(requestMock, responseMock);

    verify(responseMock).status(500);
  }

  @Test
  void putAccountHandlesCompletionException() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    doNothing().when(responseMock).type("application/json");
    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(accountDto));
    String exampleUuid = "exampleUuid";
    when(requestMock.params(":uuid")).thenReturn(exampleUuid);
    AccountServiceException expectedException = new AccountServiceException("Not found");
    CompletableFuture<AccountDto> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(expectedException);
    when(accountServiceMock.updateAccount(eq(exampleUuid), accountDtoArgumentCaptor.capture()))
        .thenReturn(failedFuture);

    String result = controller.putAccount(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithThrowable(expectedException), result);
  }

  @Test
  void putAccount() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(accountDto));
    String exampleUuid = "exampleUuid";
    doNothing().when(responseMock).type("application/json");
    when(requestMock.params(":uuid")).thenReturn(exampleUuid);
    when(accountServiceMock.updateAccount(eq(exampleUuid), accountDtoArgumentCaptor.capture()))
        .thenReturn(CompletableFuture.completedFuture(accountDto));

    String result = controller.putAccount(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithAccountDto(accountDto), result);
  }

  @Test
  void getAccounts() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    AccountDto accountDtoSecond =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("30000"));

    Set<AccountDto> accountDtoList = new HashSet<AccountDto>() {{
      add(accountDto);
      add(accountDtoSecond);
    }};

    doNothing().when(responseMock).type("application/json");
    when(accountServiceMock.getAccounts()).thenReturn(CompletableFuture.completedFuture(accountDtoList));
    String result = controller.getAccounts(requestMock, responseMock);

    Assertions.assertEquals(
        getDeserializedResponseDtoWithArrayOfAccountDto(accountDtoList), result);
  }

  @Test
  void getAccountsResponseStatus() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    AccountDto accountDtoSecond =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("30000"));

    Set<AccountDto> accountDtoList = new HashSet<AccountDto>() {{
      add(accountDto);
      add(accountDtoSecond);
    }};

    doNothing().when(responseMock).type("application/json");
    when(accountServiceMock.getAccounts()).thenReturn(CompletableFuture.completedFuture(accountDtoList));

    controller.getAccounts(requestMock, responseMock);

    verify(responseMock).status(200);
  }

  @Test
  void getAccountsHandlesCompletionException() {
    doNothing().when(responseMock).type("application/json");
    AccountServiceException expectedException = new AccountServiceException("Not found");
    CompletableFuture<Set<AccountDto>> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(expectedException);
    when(accountServiceMock.getAccounts()).thenReturn(failedFuture);

    String result = controller.getAccounts(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithThrowable(expectedException), result);
  }

  @Test
  void getAccountsHandlesCompletionExceptionResponseStatus() {
    doNothing().when(responseMock).type("application/json");
    AccountServiceException expectedException = new AccountServiceException("Not found");
    CompletableFuture<Set<AccountDto>> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(expectedException);
    when(accountServiceMock.getAccounts()).thenReturn(failedFuture);

    controller.getAccounts(requestMock, responseMock);

    verify(responseMock).status(500);
  }

  @Test
  void getAccountsHandlesExecutionException() {
    doNothing().when(responseMock).type("application/json");
    AccountServiceException expectedException = new AccountServiceException("Unknown issue");
    when(accountServiceMock.getAccounts()).thenThrow(expectedException);
    String result = controller.getAccounts(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithThrowable(expectedException), result);
  }

  @Test
  void getAccountsHandlesExecutionExceptionResponseStatus() {
    doNothing().when(responseMock).type("application/json");
    AccountServiceException expectedException = new AccountServiceException("Unknown issue");
    when(accountServiceMock.getAccounts()).thenThrow(expectedException);

    controller.getAccounts(requestMock, responseMock);

    verify(responseMock).status(500);
  }

  @Test
  void postAccount() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(accountDto));
    doNothing().when(responseMock).type("application/json");
    when(accountServiceMock.addAccount(accountDtoArgumentCaptor.capture())).thenReturn(CompletableFuture.completedFuture(accountDto));

    String result = controller.postAccount(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithAccountDto(accountDto), result);
  }

  @Test
  void postAccountHandlesCompletionException() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    doNothing().when(responseMock).type("application/json");
    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(accountDto));
    AccountServiceException expectedException = new AccountServiceException("Not found");
    CompletableFuture<AccountDto> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(expectedException);
    when(accountServiceMock.addAccount(accountDtoArgumentCaptor.capture()))
        .thenReturn(failedFuture);

    String result = controller.postAccount(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithThrowable(expectedException), result);
  }

  @Test
  void postAccountHandlesExecutionException() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(accountDto));
    doNothing().when(responseMock).type("application/json");
    AccountServiceException expectedException = new AccountServiceException("Not found");
    when(accountServiceMock.addAccount(accountDtoArgumentCaptor.capture())).thenThrow(expectedException);

    String result = controller.postAccount(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithThrowable(expectedException), result);
  }

  @Test
  void postAccountHandlesExecutionExceptionResponseStatus() {
    AccountDto accountDto =
        new AccountDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(accountDto));
    doNothing().when(responseMock).type("application/json");
    AccountServiceException expectedException = new AccountServiceException("Not found");
    when(accountServiceMock.addAccount(accountDtoArgumentCaptor.capture())).thenThrow(expectedException);

    controller.postAccount(requestMock, responseMock);

    verify(responseMock).status(500);
  }

  @Test
  void requestTransfer() {
    TransferDto transferDto =
        new TransferDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(transferDto));
    doNothing().when(responseMock).type("application/json");
    when(transferServiceMock.transfer(transferDtoArgumentCaptor.capture())).thenReturn(CompletableFuture.completedFuture(BigDecimal.ZERO));

    String result = controller.requestTransfer(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDto(), result);
  }

  @Test
  void requestTransferHandlesCompletionException() {
    TransferDto transferDto =
        new TransferDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(transferDto));
    doNothing().when(responseMock).type("application/json");
    AccountServiceException expectedException = new AccountServiceException("Unknown issue");
    CompletableFuture<BigDecimal> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(expectedException);
    when(transferServiceMock.transfer(transferDtoArgumentCaptor.capture())).thenReturn(failedFuture);

    String result = controller.requestTransfer(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithThrowable(expectedException), result);
  }

  @Test
  void requestTransferHandlesCompletionExceptionResponseStatus() {
    TransferDto transferDto =
        new TransferDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(transferDto));
    doNothing().when(responseMock).type("application/json");
    AccountServiceException expectedException = new AccountServiceException("Unknown issue");
    CompletableFuture<BigDecimal> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(expectedException);
    when(transferServiceMock.transfer(transferDtoArgumentCaptor.capture())).thenReturn(failedFuture);

    controller.requestTransfer(requestMock, responseMock);

    verify(responseMock).status(500);
  }

  @Test
  void requestTransferHandlesExecutionException() {
    TransferDto transferDto =
        new TransferDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(transferDto));
    doNothing().when(responseMock).type("application/json");
    AccountServiceException expectedException = new AccountServiceException("Unknown issue");
    when(transferServiceMock.transfer(transferDtoArgumentCaptor.capture())).thenThrow(expectedException);

    String result = controller.requestTransfer(requestMock, responseMock);

    Assertions.assertEquals(getDeserializedResponseDtoWithThrowable(expectedException), result);
  }

  @Test
  void requestTransferHandlesExecutionExceptionResponseStatus() {
    TransferDto transferDto =
        new TransferDto(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90000"));

    Mockito.when(requestMock.body()).thenReturn(new Gson().toJson(transferDto));
    doNothing().when(responseMock).type("application/json");
    AccountServiceException expectedException = new AccountServiceException("Unknown issue");
    when(transferServiceMock.transfer(transferDtoArgumentCaptor.capture())).thenThrow(expectedException);

    controller.requestTransfer(requestMock, responseMock);

    verify(responseMock).status(500);
  }
}
