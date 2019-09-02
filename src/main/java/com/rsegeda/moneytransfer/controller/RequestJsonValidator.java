/*
    Created by Roman Segeda on 02 September 2019
*/

package com.rsegeda.moneytransfer.controller;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

class RequestJsonValidator {

  void validatePostAccountJson(String body) {
    validateJson(body, "/accountPostSchema.json");
  }

  void validatePutAccountJson(String body) {
    validateJson(body, "/accountPutSchema.json");
  }

  void validatePostTransferJson(String body) {
    validateJson(body, "/transferPostSchema.json");
  }

  private void validateJson(String json, String schemaFile) {
    JSONObject jsonSchema = new JSONObject(
        new JSONTokener(Controller.class.getResourceAsStream(schemaFile)));
    Schema schema = SchemaLoader.load(jsonSchema);
    JSONObject jsonSubject = new JSONObject(json);

    schema.validate(jsonSubject);
  }
}
