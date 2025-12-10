package com.example.codingexercise.gateway;

import com.example.codingexercise.gateway.dto.Product;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductServiceGateway {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceGateway.class);

    private RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public ProductServiceGateway(@Qualifier("product_auth") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // use the external api for products/{id}
    public Optional<Product> getProduct(String id) {
        Product p = restTemplate.getForObject(productServiceUrl + "/{id}", Product.class,
                id);

        if (p == null) {
            // log.info("Get product" + id + "returned null");
            return Optional.empty();
        }

        // log.info("Get product" + id + "returned price: " + p.usdPrice());
        return Optional.of(p);
    }

    // use the external api for /products
    public List<Product> getProducts() {
        ResponseEntity<Product[]> response = restTemplate.getForEntity(productServiceUrl, Product[].class);
        Product[] body = response.getBody();
        return List.of(body);
    }

}
