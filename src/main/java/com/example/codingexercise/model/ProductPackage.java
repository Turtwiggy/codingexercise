package com.example.codingexercise.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.example.codingexercise.service.PackageService;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class ProductPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    private String name;
    private String description;
    private List<String> productIds; // string because ids are from an external service

    // Calculated from the products
    @Transient
    private BigDecimal price;

    public ProductPackage() {
    }

    public ProductPackage(String name, String description, List<String> productIds) {
        this.name = name;
        this.description = description;
        this.productIds = productIds;
    }

    public UUID getId() {
        return uuid;
    }

    public void setId(UUID id) {
        this.uuid = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
