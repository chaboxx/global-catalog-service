package com.mercadoandes.catalog.application;

import com.mercadoandes.catalog.application.exception.InvalidProductIdentityException;
import com.mercadoandes.catalog.application.exception.ProductAlreadyExistsException;
import com.mercadoandes.catalog.application.port.ClockProvider;
import com.mercadoandes.catalog.application.port.ProductCatalogRepository;
import com.mercadoandes.catalog.domain.entity.Product;
import com.mercadoandes.catalog.domain.entity.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductCatalogServiceTest {

    private static final OffsetDateTime FIXED_NOW = OffsetDateTime.parse("2026-05-09T18:30:00Z");

    private ProductCatalogService service;
    private InMemoryProductCatalogRepository productRepository;

    @BeforeEach
    void setUp() {
        service = new ProductCatalogService();
        productRepository = new InMemoryProductCatalogRepository();
        service.productRepository = productRepository;
        service.clockProvider = () -> FIXED_NOW;
    }

    @Test
    void createNormalizesProductAndSaves() {
        Product created = service.create(product(" PROD-1 ", "pe", " Electronics ", new BigDecimal("10.00"), 5));

        assertEquals("PROD-1", created.id());
        assertEquals("PROD-1", created.productId());
        assertEquals("PE", created.tenantCountry());
        assertEquals("electronics", created.category());
        assertEquals("PEN", created.currency());
        assertEquals(FIXED_NOW, created.updatedAt());
        assertEquals("1.0", created.schemaVersion());
    }

    @Test
    void createRejectsDuplicatedProduct() {
        service.create(product("PROD-1", "PE", "electronics", new BigDecimal("10.00"), 5));

        assertThrows(ProductAlreadyExistsException.class,
                () -> service.create(product("PROD-1", "PE", "electronics", new BigDecimal("12.00"), 6)));
    }

    @Test
    void createThrowsImmediatelyOnTransientPersistenceFailure() {
        productRepository.failuresBeforeSave = 1;

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.create(product("PROD-1", "PE", "electronics", new BigDecimal("10.00"), 5)));

        assertEquals("Transient persistence failure", exception.getMessage());
        assertEquals(1, productRepository.saveCalls);
    }

    @Test
    void createRejectsInvalidPayload() {
        Product invalidProduct = new Product(
                "PROD-1",
                "OTHER-PROD-1",
                "PE",
                "Laptop Enterprise 14",
                "electronics",
                "Contoso",
                new BigDecimal("10.00"),
                "PEN",
                5,
                ProductStatus.ACTIVE,
                "catalog-admin",
                null,
                null);

        assertThrows(InvalidProductIdentityException.class, () -> service.create(invalidProduct));
    }

    private Product product(String productId, String country, String category, BigDecimal price, int stock) {
        return new Product(
                productId,
                productId,
                country,
                " Laptop Enterprise 14 ",
                category,
                " Contoso ",
                price,
                " pen ",
                stock,
                ProductStatus.ACTIVE,
                " catalog-admin ",
                null,
                null);
    }

    private static String key(String country, String productId) {
        return country + ":" + productId;
    }

    private static class InMemoryProductCatalogRepository implements ProductCatalogRepository {
        private final Map<String, Product> products = new HashMap<>();
        private int saveCalls;
        private int failuresBeforeSave;
        private boolean duplicateOnSave;

        @Override
        public Optional<Product> findByCountryAndProductId(String country, String productId) {
            return Optional.ofNullable(products.get(key(country, productId)));
        }

        @Override
        public boolean existsByCountryAndProductId(String country, String productId) {
            return products.containsKey(key(country, productId));
        }

        @Override
        public void save(Product product) {
            saveCalls++;
            if (duplicateOnSave) {
                throw new ProductAlreadyExistsException("Product already exists");
            }
            if (failuresBeforeSave > 0) {
                failuresBeforeSave--;
                throw new RuntimeException("Transient persistence failure");
            }
            if (existsByCountryAndProductId(product.tenantCountry(), product.productId())) {
                throw new ProductAlreadyExistsException("Product already exists");
            }
            products.put(key(product.tenantCountry(), product.productId()), product);
        }

        @Override
        public void update(Product product) {
            products.put(key(product.tenantCountry(), product.productId()), product);
        }

        @Override
        public void delete(Product product) {
            products.remove(key(product.tenantCountry(), product.productId()));
        }

        @Override
        public List<Product> search(String country, String category) {
            return products.values().stream()
                    .filter(product -> country.equals(product.tenantCountry()))
                    .filter(product -> category == null || category.equals(product.category()))
                    .toList();
        }
    }
}
