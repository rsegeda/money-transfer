/*
    Created by Roman Segeda on 02 September 2019
*/

package com.rsegeda.moneytransfer.controller;

import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestJsonValidatorTest {

  private RequestJsonValidator requestJsonValidator = new RequestJsonValidator();

  @Test
  void validatePostAccountJson() {
    String body = "{\n" +
        "    \"ownerUuid\": \"dd987832-e946-4a6e-8fea-61e9b7ea49b7\",\n" +
        "    \"balance\": 9000.50\n" +
        "}";

    assertDoesNotThrow(() -> requestJsonValidator.validatePostAccountJson(body));
  }

  @Test
  void validatePostAccountJsonMissingOwnerUUID() {
    String body = "{\n" +
        "    \"balance\": 9000.50\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePostAccountJson(body));
  }

  @Test
  void validatePostAccountJsonMissingBalance() {
    String body = "{\n" +
        "    \"ownerUuid\": \"dd987832-e946-4a6e-8fea-61e9b7ea49b7\"\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePostAccountJson(body));
  }

  @Test
  void validatePostAccountJsonBalanceIncorrectFormat() {
    String body = "{\n" +
        "    \"ownerUuid\": \"dd987832-e946-4a6e-8fea-61e9b7ea49b7\",\n" +
        "    \"balance\": \"9000.50\"\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePostAccountJson(body));
  }

  @Test
  void validatePostAccountJsonOwnerUUIDIncorrectFormat() {
    String body = "{\n" +
        "    \"ownerUuid\": \"someWrongUUID\",\n" +
        "    \"balance\": 9000.50\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePostAccountJson(body));
  }

  @Test
  void validatePostAccountJsonWrongFormat() {
    String body = "bodyContentNotJson";

    assertThrows(JSONException.class,
        () -> requestJsonValidator.validatePostAccountJson(body));
  }

  @Test
  void validatePutAccountJson() {
    String body = "{\n" +
        "    \"ownerUuid\": \"dd987832-e946-4a6e-8fea-61e9b7ea49b7\",\n" +
        "    \"balance\": 5000\n" +
        "}";

    assertDoesNotThrow(() -> requestJsonValidator.validatePutAccountJson(body));
  }

  @Test
  void validatePutAccountJsonOnlyOwnerUUID() {
    String body = "{\n" +
        "    \"ownerUuid\": \"dd987832-e946-4a6e-8fea-61e9b7ea49b7\"\n" +
        "}";

    assertDoesNotThrow(() -> requestJsonValidator.validatePutAccountJson(body));
  }

  @Test
  void validatePutAccountJsonOnlyBalance() {
    String body = "{\n" +
        "    \"balance\": 5000\n" +
        "}";

    assertDoesNotThrow(() -> requestJsonValidator.validatePutAccountJson(body));
  }

  @Test
  void validatePutAccountJsonBalanceIncorrectFormat() {
    String body = "{\n" +
        "    \"balance\": \"9000.50\"\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePutAccountJson(body));
  }

  @Test
  void validatePutAccountJsonOwnerUUIDIncorrectFormat() {
    String body = "{\n" +
        "    \"ownerUuid\": \"someWrongUUID\"\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePutAccountJson(body));
  }

  @Test
  void validatePostTransferJson() {
    String body = "{\n" +
        "    \"sender\": \"88b24bda-f928-4475-b753-04e4354a0214\",\n" +
        "    \"receiver\": \"243ee05b-7bbf-4846-a9e0-63f37385e7bf\",\n" +
        "    \"sum\": 1000.50\n" +
        "}";

    assertDoesNotThrow(() -> requestJsonValidator.validatePostTransferJson(body));
  }

  @Test
  void validatePostTransferJsonMissingSender() {
    String body = "{\n" +
        "    \"receiver\": \"243ee05b-7bbf-4846-a9e0-63f37385e7bf\",\n" +
        "    \"sum\": 1000.50\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePostTransferJson(body));
  }

  @Test
  void validatePostTransferJsonMissingReceiver() {
    String body = "{\n" +
        "    \"sender\": \"243ee05b-7bbf-4846-a9e0-63f37385e7bf\",\n" +
        "    \"sum\": 1000.50\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePostTransferJson(body));
  }

  @Test
  void validatePostTransferJsonMissingSum() {
    String body = "{\n" +
        "    \"sender\": \"88b24bda-f928-4475-b753-04e4354a0214\",\n" +
        "    \"receiver\": \"243ee05b-7bbf-4846-a9e0-63f37385e7bf\"\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePostTransferJson(body));
  }

  @Test
  void validatePostTransferJsonIncorrectSenderFormat() {
    String body = "{\n" +
        "    \"sender\": \"NOT_UUID\",\n" +
        "    \"receiver\": \"243ee05b-7bbf-4846-a9e0-63f37385e7bf\",\n" +
        "    \"sum\": 1000.50\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePostTransferJson(body));
  }

  @Test
  void validatePostTransferJsonIncorrectReceiverFormat() {
    String body = "{\n" +
        "    \"sender\": \"88b24bda-f928-4475-b753-04e4354a0214\",\n" +
        "    \"receiver\": \"NOT_UUID\",\n" +
        "    \"sum\": 1000.50\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePostTransferJson(body));
  }

  @Test
  void validatePostTransferJsonIncorrectSumFormat() {
    String body = "{\n" +
        "    \"sender\": \"88b24bda-f928-4475-b753-04e4354a0214\",\n" +
        "    \"receiver\": \"243ee05b-7bbf-4846-a9e0-63f37385e7bf\",\n" +
        "    \"sum\": \"1000.50\"\n" +
        "}";

    assertThrows(ValidationException.class,
        () -> requestJsonValidator.validatePostTransferJson(body));
  }
}
