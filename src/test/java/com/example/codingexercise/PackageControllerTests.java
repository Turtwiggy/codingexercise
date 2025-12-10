package com.example.codingexercise;

import com.example.codingexercise.exceptions.ConvertingToSameExchangeRateException;
import com.example.codingexercise.gateway.ConversionRateGateway;
import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.gateway.dto.ExchangeRates;
import com.example.codingexercise.gateway.dto.Product;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PackageControllerTests {

    private final TestRestTemplate restTemplate;
    private final PackageRepository packageRepository;

    @Autowired
    PackageControllerTests(TestRestTemplate restTemplate, PackageRepository packageRepository) {
        this.restTemplate = restTemplate;
        this.packageRepository = packageRepository;
    }

    @MockBean
    private ProductServiceGateway productServiceGateway;
    @MockBean
    private ConversionRateGateway conversionRateGateway;

    @Test
    void createPackage() {
        // arrange
        ProductPackage p0 = new ProductPackage("Test Name", "Test Desc", List.of("prod1"));

        // act
        ResponseEntity<ProductPackage> created = restTemplate.postForEntity("/packages", p0, ProductPackage.class);
        assertEquals(HttpStatus.OK, created.getStatusCode(), "Unexpected status code");

        // assert
        ProductPackage createdBody = created.getBody();
        assertNotNull(createdBody, "Unexpected body");
        assertEquals("Test Name", createdBody.getName(), "Unexpected name");
        assertEquals("Test Desc", createdBody.getDescription(), "Unexpected description");
        assertEquals(List.of("prod1"), createdBody.getProductIds(), "Unexpected products");

        ProductPackage productPackage = packageRepository.findById(createdBody.getId()).get();
        assertNotNull(productPackage, "Unexpected package");
        assertEquals(createdBody.getId(), productPackage.getId(), "Unexpected id");
        assertEquals(createdBody.getName(), productPackage.getName(), "Unexpected name");
        assertEquals(createdBody.getDescription(), productPackage.getDescription(), "Unexpected description");
        assertEquals(createdBody.getProductIds(), productPackage.getProductIds(), "Unexpected products");
    }

    @Test
    void getPackage() {

        // arrange
        ProductPackage toCreate = new ProductPackage("Test Package 2", "Test Desc 2", List.of("prod2"));
        ProductPackage productPackage = packageRepository.save(toCreate);
        UUID productUUID = productPackage.getId();
        String id = productUUID.toString();
        assertNotNull(id, "Unexpected id");

        // stub the gateway call
        Product mockProduct = new Product(id, "Test Product", 1);
        Mockito.when(productServiceGateway.getProduct(id)).thenReturn(Optional.of(mockProduct));

        // act
        ResponseEntity<ProductPackage> fetched = restTemplate.getForEntity("/packages/{id}", ProductPackage.class, id);
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");

        // assert
        ProductPackage fetchedBody = fetched.getBody();
        assertNotNull(fetchedBody, "Unexpected body");
        assertEquals(productPackage.getId(), fetchedBody.getId(), "Unexpected id");
        assertEquals(productPackage.getName(), fetchedBody.getName(), "Unexpected name");
        assertEquals(productPackage.getDescription(), fetchedBody.getDescription(), "Unexpected description");
        assertEquals(productPackage.getProductIds(), fetchedBody.getProductIds(), "Unexpected products");
    }

    @Test
    void listPackages() {

        // arrange
        packageRepository.deleteAll();
        ProductPackage package1 = new ProductPackage("Test Name 1", "Test Desc 1", List.of("prod1"));
        ProductPackage package2 = new ProductPackage("Test Name 2", "Test Desc 2", List.of("prod2"));
        ProductPackage productPackage1 = packageRepository.save(package1);
        ProductPackage productPackage2 = packageRepository.save(package2);

        // act
        ResponseEntity<List<ProductPackage>> fetched = restTemplate.exchange(
                "/packages",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProductPackage>>() {
                });

        // assert
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
        assertEquals(2, fetched.getBody().size(), "Unexpected number of packages");
    }

    @Test
    void getPackage__testExchangeUsdToUsd() {

        // arrange
        String productId = "prod1";
        ProductPackage toCreate = new ProductPackage("Test Package", "Test Desc", List.of(productId));
        ProductPackage productPackage = packageRepository.save(toCreate);
        String id = productPackage.getId().toString();

        // stub the product gateway call
        int mockPrice = 100;
        Product mockProduct = new Product(productId, "Test Product", mockPrice);
        Mockito.when(productServiceGateway.getProduct(productId)).thenReturn(Optional.of(mockProduct));

        // stub the conversion rate gateway call
        ConvertingToSameExchangeRateException ex = new ConvertingToSameExchangeRateException("USD");
        Mockito.when(conversionRateGateway.getConversionRates("USD", "USD")).thenThrow(ex);

        // act
        ResponseEntity<ProductPackage> fetched = restTemplate.getForEntity("/packages/{id}?currency=USD",
                ProductPackage.class, id);
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");

        // assert (usd => usd should return same price)
        ProductPackage body = fetched.getBody();
        BigDecimal price = body.getPrice();
        assertEquals(BigDecimal.valueOf(mockPrice), price, "unexpected price");
    }

    @Test
    void getPackage__testExchangeUsdToEur() {

        // arrange
        String productId = "prod1";
        ProductPackage toCreate = new ProductPackage("Test Package", "Test Desc", List.of(productId));
        ProductPackage productPackage = packageRepository.save(toCreate);
        String id = productPackage.getId().toString();

        // stub the product gateway call
        int mockPriceUSD = 100; // note: assume this api returns "USD"
        Product mockProduct = new Product(productId, "Test Product", mockPriceUSD);
        Mockito.when(productServiceGateway.getProduct(productId)).thenReturn(Optional.of(mockProduct));

        // stub the conversion rate gateway call (here, assume 1USD = 0.86EUR)
        ExchangeRates mockExchangeRates = new ExchangeRates("USD", "2025-12-09", Map.of("EUR", 0.86));
        Mockito.when(conversionRateGateway.getConversionRates("USD", "EUR")).thenReturn(mockExchangeRates);

        // act
        ResponseEntity<ProductPackage> fetched = restTemplate.getForEntity("/packages/{id}?currency=EUR",
                ProductPackage.class, id);
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");

        // assert (usd => usd should return same price)
        ProductPackage body = fetched.getBody();
        BigDecimal body_price = body.getPrice();
        BigDecimal calc_price = BigDecimal.valueOf(mockPriceUSD)
                .multiply(BigDecimal.valueOf(mockExchangeRates.rates().get("EUR")));
        assertTrue(body_price.compareTo(calc_price) == 0, "unexpected price");
    }

    @Test
    void updatePackage() {
        // arrange
        String productId = "prod1";
        ProductPackage toCreate = new ProductPackage("Test Package", "Test Desc", List.of(productId));
        ProductPackage productPackage = packageRepository.save(toCreate);
        String id = productPackage.getId().toString();

        // act
        ProductPackage updateInfo = new ProductPackage();
        updateInfo.setName("New Name");
        updateInfo.setDescription("New Desc");
        updateInfo.setProductIds(List.of("New Prod"));
        restTemplate.put("/packages/{id}", updateInfo, id);

        // assert
        ResponseEntity<ProductPackage> fetched = restTemplate.getForEntity("/packages/{id}", ProductPackage.class, id);
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
        ProductPackage fetchedBody = fetched.getBody();
        assertNotNull(fetchedBody, "Unexpected body");
        assertEquals(updateInfo.getName(), fetchedBody.getName(), "Unexpected name");
        assertEquals(updateInfo.getDescription(), fetchedBody.getDescription(), "Unexpected description");
        assertEquals(updateInfo.getProductIds(), fetchedBody.getProductIds(), "Unexpected products");
    }

    @Test
    void deletePackage() {

        // arrange
        ProductPackage p0 = new ProductPackage("Test Name", "Test Desc", List.of("prod1"));
        ProductPackage productPackage = packageRepository.save(p0);
        String id = productPackage.getId().toString();

        // check it exists
        ResponseEntity<ProductPackage> fetched = restTemplate.getForEntity("/packages/{id}", ProductPackage.class, id);
        assertEquals(id, fetched.getBody().getId().toString(), "Unexpected id");

        // act
        restTemplate.delete("/packages/{id}", id);

        // assert (check the package is deleted)
        // note: internally throws a "PackageNotFoundException"
        // but externally that just shows up as an error 500.
        ResponseEntity<ProductPackage> deleted = restTemplate.getForEntity("/packages/{id}", ProductPackage.class, id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, deleted.getStatusCode(), "Unexpected status code");
    }

}
