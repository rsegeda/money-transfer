/*
    Created by Roman Segeda on 20 August 2019
*/

package com.rsegeda.moneytransfer.controller.response;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BodyResponse {

  private final StatusResponse status;
  private String message;
  private JsonElement data;

  public BodyResponse(StatusResponse status, JsonElement data) {
    this.status = status;
    this.data = data;
  }

  public BodyResponse(StatusResponse status, String message) {
    this.status = status;
    this.message = message;
  }
}