/*
    Created by Roman Segeda on 21 August 2019
*/

package com.rsegeda.moneytransfer.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class TransferDto {

  final UUID sender;
  final UUID receiver;
  final BigDecimal sum;

}
