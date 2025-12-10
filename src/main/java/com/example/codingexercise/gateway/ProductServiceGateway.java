package com.example.codingexercise.gateway;

import com.example.codingexercise.gateway.dto.Product;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductServiceGateway {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public ProductServiceGateway(@Qualifier("product_auth") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // use the external api for products/{id}
    public Product getProduct(String id) {
        return restTemplate.getForObject(productServiceUrl + "/{id}", Product.class,
                id);
    }

    // use the external api for /products
    public List<Product> getProducts() {
        ResponseEntity<Product[]> response = restTemplate.getForEntity(productServiceUrl, Product[].class);
        Product[] body = response.getBody();
        return List.of(body);
    }

}
