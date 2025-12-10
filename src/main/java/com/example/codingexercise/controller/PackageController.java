package com.example.codingexercise.controller;

import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import com.example.codingexercise.service.PriceAdjusterService;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class PackageNotFoundException extends RuntimeException {
    public PackageNotFoundException(String id) {
        super("Could not find package " + id);
    }
}

@RestController
public class PackageController {

    private final PackageRepository packageRepository;
    private final PriceAdjusterService priceAdjustorService;

    public PackageController(PackageRepository packageRepository, PriceAdjusterService priceAdjustor) {
        this.packageRepository = packageRepository;
        this.priceAdjustorService = priceAdjustor;
    }

    @PostMapping("/packages")
    public ProductPackage create(@RequestBody ProductPackage newProductPackage) {
        return packageRepository.save(newProductPackage);
    }

    @GetMapping("/packages")
    public List<ProductPackage> list(
            @RequestParam(required = false, defaultValue = "USD") String currency) {
        return packageRepository
                .findAll()
                .stream()
                .peek(p -> p.setPrice(priceAdjustorService.getAdjustedPrice(p, currency)))
                .toList();
    }

    @GetMapping("/packages/{id}")
    public ProductPackage get(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "USD") String currency) {

        UUID uuid = UUID.fromString(id);

        ProductPackage p = packageRepository.findById(uuid)
                .orElseThrow(() -> new PackageNotFoundException(uuid.toString()));

        // calculate the price
        p.setPrice(priceAdjustorService.getAdjustedPrice(p, currency));

        return p;
    }

    @PutMapping("/packages/{id}")
    public ProductPackage update(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "USD") String currency,
            @RequestBody ProductPackage newProductPackage) {
        UUID uuid = UUID.fromString(id);
        return packageRepository.findById(uuid)
                .map(p -> {
                    p.setName(newProductPackage.getName());
                    p.setDescription(newProductPackage.getDescription());
                    p.setProductIds(newProductPackage.getProductIds());
                    p.setPrice(priceAdjustorService.getAdjustedPrice(p, currency));
                    return packageRepository.save(p);
                })
                .orElseGet(() -> {
                    return packageRepository.save(newProductPackage);
                });
    }

    @DeleteMapping("/packages/{id}")
    public void delete(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        packageRepository.deleteById(uuid);
    }

}
