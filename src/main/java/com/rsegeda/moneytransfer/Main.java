/*
    Created by Roman Segeda on 19 August 2019
*/

package com.rsegeda.moneytransfer;

import com.rsegeda.moneytransfer.controller.Controller;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static void main(String[] args) {
    Injector injector = Guice.createInjector();
    Controller controller = injector.getInstance(Controller.class);
    controller.init();
  }
}