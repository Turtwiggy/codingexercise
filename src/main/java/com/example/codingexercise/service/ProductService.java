package com.example.codingexercise.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.gateway.dto.Product;

@Service
public class ProductService {

  private final ProductServiceGateway gateway;

  public ProductService(ProductServiceGateway gateway) {
    this.gateway = gateway;
  }

  public List<Product> listProducts() {
    return gateway.getProducts();
  }

  public Product getProduct(String id) {
    return gateway.getProduct(id);
  }

}
