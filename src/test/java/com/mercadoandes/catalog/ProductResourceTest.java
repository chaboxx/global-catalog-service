package com.mercadoandes.catalog;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static java.util.Map.entry;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class ProductResourceTest {

    @Test
    void createProduct() {
        String productId = uniqueProductId();

        given()
                .contentType("application/json")
                .body(productPayload(productId, "PE", new BigDecimal("3499.90"), 45, "electronics"))
                .when().post("/api/products")
                .then()
                .statusCode(201)
                .header("Location", endsWith("/api/products/PE/" + productId))
                .body("id", equalTo(productId))
                .body("productId", equalTo(productId))
                .body("tenantCountry", equalTo("PE"))
                .body("updatedAt", notNullValue());
    }

    @Test
    void createRejectsMismatchedIdentity() {
        String productId = uniqueProductId();

        given()
                .contentType("application/json")
                .body(Map.ofEntries(
                        entry("id", productId),
                        entry("productId", "OTHER-" + productId),
                        entry("tenantCountry", "CO"),
                        entry("name", "Laptop Enterprise 14"),
                        entry("category", "electronics"),
                        entry("brand", "Contoso"),
                        entry("price", new BigDecimal("3499.90")),
                        entry("currency", "PEN"),
                        entry("stock", 45),
                        entry("status", "ACTIVE")))
                .when().post("/api/products")
                .then()
                .statusCode(400);
    }

    private Map<String, Object> productPayload(String productId, String country, BigDecimal price, int stock, String category) {
        return Map.ofEntries(
                entry("id", productId),
                entry("productId", productId),
                entry("tenantCountry", country),
                entry("name", "Laptop Enterprise 14"),
                entry("category", category),
                entry("brand", "Contoso"),
                entry("price", price),
                entry("currency", "PEN"),
                entry("stock", stock),
                entry("status", "ACTIVE"),
                entry("lastUpdatedBy", "catalog-admin"),
                entry("schemaVersion", "1.0"));
    }

    private String uniqueProductId() {
        return "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
