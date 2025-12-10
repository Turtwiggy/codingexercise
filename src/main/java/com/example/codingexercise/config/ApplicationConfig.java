package com.example.codingexercise.config;

import com.example.codingexercise.gateway.dto.Product;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import com.example.codingexercise.service.ProductService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

  @Bean
  CommandLineRunner initDatabase(ProductService productService, PackageRepository packageRepository) {

    return args -> {

      // list products to check we can connect to the external api
      List<Product> products = productService.listProducts();
      for (Product p : products)
        log.info("Product Available: " + p.id() + ", name: " + p.name() + ", usdPrice: " + p.usdPrice());

      // seed some packages
      ProductPackage p0 = new ProductPackage("Product0", "The zeroth and best product", List.of());
      ProductPackage p1 = new ProductPackage("Product1", "The iterated and newer product", List.of());
      log.info("Preloading " + packageRepository.save(p0).getName());
      log.info("Preloading " + packageRepository.save(p1).getName());
    };
  }

}
