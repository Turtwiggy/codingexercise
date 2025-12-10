package com.example.codingexercise.service;

import org.springframework.stereotype.Service;

import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.model.ProductPackage;

@Service
public class PackageService {

  private final ProductServiceGateway productServiceGateway;

  public PackageService(ProductServiceGateway productServiceGateway) {
    this.productServiceGateway = productServiceGateway;
  }

  // note: this calls the external api PER product id stored.
  public int calculatePrice(ProductPackage productPackage) {
    return productPackage.getProductIds()
        .stream()
        .map(productServiceGateway::getProduct)
        .map(opt -> opt.usdPrice())
        .reduce(0, Integer::sum);
  }

}
