package com.example.codingexercise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.codingexercise.gateway.ConversionRateGateway;
import com.example.codingexercise.gateway.dto.ExchangeRates;

@RestController
public class ConversionRateProxyController {

  ConversionRateGateway gateway;

  public ConversionRateProxyController(ConversionRateGateway gateway) {
    this.gateway = gateway;
  }

  @GetMapping("/rates/{currency}")
  public ExchangeRates get(@PathVariable String currency) {
    ExchangeRates rates = gateway.getConversionRates("USD", currency);
    return rates;
  }

}
