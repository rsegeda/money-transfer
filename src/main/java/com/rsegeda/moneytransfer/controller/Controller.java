/*
    Created by Roman Segeda on 19 August 2019
*/

package com.rsegeda.moneytransfer.controller;

import com.rsegeda.moneytransfer.controller.dto.AccountDto;
import com.rsegeda.moneytransfer.controller.dto.TransferDto;
import com.rsegeda.moneytransfer.controller.response.BodyResponse;
import com.rsegeda.moneytransfer.controller.response.StatusResponse;
import com.rsegeda.moneytransfer.service.AccountService;
import com.rsegeda.moneytransfer.service.TransferService;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Response;
import spark.Request;

import static spark.Spark.*;

@Slf4j
public class Controller {

  @Setter(onMethod = @__( {@Inject}))
  private AccountService accountService;

  @Setter(onMethod = @__( {@Inject}))
  private TransferService transferService;

  @Setter(onMethod = @__( {@Inject}))
  private RequestJsonValidator requestJsonValidator;

  public void init() {
    get("/healthCheck", (req, res) -> "Service is alive");

    get("/accounts", this::getAccounts);
    post("/accounts", this::postAccount);
    get("/accounts/:uuid", this::getAccount);
    put("/accounts/:uuid", this::putAccount);
    delete("/accounts/:uuid", this::deleteAccount);

    post("/transfers", this::requestTransfer);
  }

  String getAccount(Request req, spark.Response res) {
    res.type("application/json");

    try {
      BodyResponse result =
          accountService.getAccount(req.params(":uuid")).thenApply(accountDto -> {
            res.status(Response.SC_OK);
            return new BodyResponse(StatusResponse.SUCCESSFUL, new Gson().toJsonTree(accountDto));
          }).exceptionally(throwable -> {
            res.status(Response.SC_NOT_FOUND);
            return new BodyResponse(StatusResponse.FAILED, throwable.getCause().getMessage());
          }).get();

      return new Gson().toJson(result);
    } catch (Exception e) {
      res.status(Response.SC_INTERNAL_SERVER_ERROR);
      return new Gson().toJson(new BodyResponse(StatusResponse.FAILED, e.getMessage()));
    }
  }

  String getAccounts(@SuppressWarnings("unused") Request req, spark.Response res) {
    res.type("application/json");

    try {
      BodyResponse result =
          accountService.getAccounts().thenApply(accounts -> {
            res.status(Response.SC_OK);
            return new BodyResponse(StatusResponse.SUCCESSFUL,
                new Gson().toJsonTree(accounts.toArray()));
          }).exceptionally(throwable -> {
            res.status(Response.SC_INTERNAL_SERVER_ERROR);
            return new BodyResponse(StatusResponse.FAILED, throwable.getCause().getMessage());
          }).get();

      return new Gson().toJson(result);
    } catch (Exception e) {
      res.status(Response.SC_INTERNAL_SERVER_ERROR);
      return new Gson().toJson(new BodyResponse(StatusResponse.FAILED, e.getMessage()));
    }
  }

  String deleteAccount(Request request, spark.Response response) {
    response.type("application/json");
    accountService.deleteAccount(request.params(":uuid"));
    response.status(Response.SC_NO_CONTENT);
    return "Account deleted";
  }

  String putAccount(Request req, spark.Response res) {
    res.type("application/json");

    try {
      requestJsonValidator.validatePutAccountJson(req.body());
      Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
      AccountDto newAccountDto = gson.fromJson(req.body(), AccountDto.class);

      BodyResponse result =
          accountService
              .updateAccount(req.params(":uuid"), newAccountDto)
              .thenApply(accountDto -> {
                res.status(Response.SC_OK);

                return new BodyResponse(StatusResponse.SUCCESSFUL,
                    new Gson().toJsonTree(accountDto));
              }).exceptionally(throwable -> {
            res.status(Response.SC_NOT_FOUND);

            return new BodyResponse(StatusResponse.FAILED, throwable.getCause().getMessage());
          }).get();

      return new Gson().toJson(result);
    } catch (Exception e) {
      res.status(Response.SC_INTERNAL_SERVER_ERROR);
      return new Gson().toJson(new BodyResponse(StatusResponse.FAILED, e.getMessage()));
    }
  }

  String postAccount(Request req, spark.Response res) {
    res.type("application/json");

    try {
      requestJsonValidator.validatePostAccountJson(req.body());
      Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
      AccountDto accountDto = gson.fromJson(req.body(), AccountDto.class);
      BodyResponse result = accountService.addAccount(accountDto)
          .thenApply(newAccount -> {
            res.status(Response.SC_CREATED);
            return new BodyResponse(StatusResponse.SUCCESSFUL, new Gson().toJsonTree(newAccount));
          })
          .exceptionally(throwable -> {
            res.status(Response.SC_BAD_REQUEST);
            return new BodyResponse(StatusResponse.FAILED, throwable.getCause().getMessage());
          })
          .get();

      return new Gson().toJson(result);
    } catch (Exception e) {
      res.status(Response.SC_INTERNAL_SERVER_ERROR);
      return new Gson().toJson(new BodyResponse(StatusResponse.FAILED, e.getMessage()));
    }
  }

  String requestTransfer(Request req, spark.Response res) {
    res.type("application/json");
    res.status(Response.SC_OK);

    try {
      requestJsonValidator.validatePostTransferJson(req.body());
      TransferDto transferDto = new Gson().fromJson(req.body(), TransferDto.class);
      BodyResponse orderResult = transferService
          .transfer(transferDto)
          .thenApply(balanceLeft -> new BodyResponse(StatusResponse.SUCCESSFUL,
              String.format("Funds left: %s", balanceLeft.toString())))
          .exceptionally(throwable -> {
            res.status(Response.SC_INTERNAL_SERVER_ERROR);
            return new BodyResponse(StatusResponse.FAILED,
                throwable.getCause().getMessage());
          })
          .get();

      return new Gson().toJson(orderResult);
    } catch (Exception e) {
      res.status(Response.SC_INTERNAL_SERVER_ERROR);
      return new Gson().toJson(new BodyResponse(StatusResponse.FAILED, e.getMessage()));
    }
  }
}
