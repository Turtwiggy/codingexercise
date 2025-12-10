package com.example.codingexercise.gateway.dto;

import java.util.Map;

// Example
/* curl -s https://api.frankfurter.dev/v1/latest?base=USD&symbols=EUR */
// {
//   "base": "USD",
//   "date": "2025-12-09",
//   "rates": {
//     "AUD": 1.5066,
//     "BGN": 1.6807,
//     "BRL": 5.4643,
//     "CAD": 1.3847,
//     "...": "..."
//   }
// }
public record ExchangeRates(String base, String date, Map<String, Double> rates) {

}
