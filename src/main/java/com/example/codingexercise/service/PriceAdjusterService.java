package com.example.codingexercise.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.codingexercise.config.ApplicationConfig;
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

  private static final Logger log = LoggerFactory.getLogger(PriceAdjusterService.class);
  private PackageService packageService;
  private ConversionRateGateway conversionRateGateway;

  PriceAdjusterService(PackageService packageService, ConversionRateGateway conversionRateGateway) {
    this.packageService = packageService;
    this.conversionRateGateway = conversionRateGateway;
  }

  public BigDecimal getAdjustedPrice(ProductPackage productPackage, String currency) {

    int price = packageService.calculatePrice(productPackage);

    String base = "USD"; // assume the productService returns USD
    if (base.equals(currency))
      return BigDecimal.valueOf(price);

    ExchangeRates r = conversionRateGateway.getConversionRates(base, currency);
    // log.info("Available rates: " + r.rates().size());
    // for (String key : r.rates().keySet())
    // log.info(key + " " + r.rates().get(key));

    if (r.rates().isEmpty())
      throw new InvalidExchangeRateException(currency + " (exchange is empty)");

    if (!r.rates().containsKey(currency))
      throw new InvalidExchangeRateException(currency + " (no key found)");

    Double rate = r.rates().get(currency);

    return BigDecimal.valueOf(price * rate);
  }

}
