package com.example.codingexercise.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.gateway.dto.Product;

@RestController
public class ProductProxyController {

  ProductServiceGateway gateway;

  ProductProxyController(ProductServiceGateway gateway) {
    this.gateway = gateway;
  }

  @GetMapping("/products")
  public List<Product> list() {
    return gateway.getProducts();
  }

  @GetMapping("/products/{id}")
  public Product get(@PathVariable String id) {
    Optional<Product> p = gateway.getProduct(id);
    if (p.isEmpty())
      return null;
    return p.get();
  }

}
