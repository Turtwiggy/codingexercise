package com.example.codingexercise.repository;

import com.example.codingexercise.model.ProductPackage;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<ProductPackage, UUID> {

}
