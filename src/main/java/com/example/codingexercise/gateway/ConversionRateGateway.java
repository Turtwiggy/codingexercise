package com.example.codingexercise.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.codingexercise.gateway.dto.ExchangeRates;

class ConvertingToSameExchangeRateException extends RuntimeException {
  public ConvertingToSameExchangeRateException(String currency) {
    super("An issue occured getting the exchange rate: " + currency);
  }
}

@Component
public class ConversionRateGateway {

  private static final Logger log = LoggerFactory.getLogger(ConversionRateGateway.class);
  private final RestTemplate restTemplate;

  @Value("${product.service.exchangerate_url}")
  private String exchangeRateUrl;

  public ConversionRateGateway(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public ExchangeRates getConversionRates(String base, String to) {

    if (base.equals(to))
      throw new ConvertingToSameExchangeRateException(to);

    // e.g. https://api.frankfurter.dev/v1/latest?base=USD&symbols=EUR
    String url = exchangeRateUrl + "?base=" + base + "&symbols=" + to;

    // see the raw response
    // String raw = restTemplate.getForObject(url, String.class);
    // log.info("raw: " + raw);

    ExchangeRates rates = restTemplate.getForObject(url, ExchangeRates.class);
    log.info("Rates: " + rates);

    return rates;
  }

}
