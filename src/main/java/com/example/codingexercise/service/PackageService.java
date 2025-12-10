package com.example.codingexercise.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.model.ProductPackage;

@Service
public class PackageService {

  private final ProductService productService;

  public PackageService(ProductService productService) {
    this.productService = productService;
  }

  // note: this calls the external api per product id stored.
  public int calculatePrice(ProductPackage productPackage) {

    return productPackage.getProductIds().stream()
        .map(productService::getProduct)
        .filter(Optional::isPresent)
        .map(opt -> opt.get().usdPrice())
        .reduce(0, Integer::sum);
  }

}
