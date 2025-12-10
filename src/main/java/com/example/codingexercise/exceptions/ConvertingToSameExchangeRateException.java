package com.example.codingexercise.exceptions;

public class ConvertingToSameExchangeRateException extends RuntimeException {
  public ConvertingToSameExchangeRateException(String currency) {
    super("An issue occured getting the exchange rate: " + currency);
  }
}