package com.example.codingexercise.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.codingexercise.gateway.ConversionRateGateway;
import com.example.codingexercise.gateway.dto.ExchangeRates;
import com.example.codingexercise.model.ProductPackage;

class InvalidExchangeRateException extends RuntimeException {
  public InvalidExchangeRateException(String currency) {
    super("An issue occured getting the exchange rate: " + currency);
  }
}

@Service
public class PriceAdjusterService {

  private PackageService packageService;
  private ConversionRateGateway conversionRateGateway;

  PriceAdjusterService(PackageService packageService, ConversionRateGateway conversionRateGateway) {
    this.packageService = packageService;
    this.conversionRateGateway = conversionRateGateway;
  }

  public BigDecimal getAdjustedPrice(ProductPackage productPackage, String currency) {

    int price = packageService.calculatePrice(productPackage);

    ExchangeRates r = conversionRateGateway.getConversionRates("USD", currency);

    if (r.rates().isEmpty())
      throw new InvalidExchangeRateException(currency + " (exchange is empty)");

    if (r.rates().containsKey(currency))
      throw new InvalidExchangeRateException(currency + " (no key found)");

    Double rate = r.rates().get(currency);

    return BigDecimal.valueOf(price * rate);
  }

}
