package com.mercadoandes.catalog.infrastructure.persistence;

import com.mercadoandes.catalog.application.exception.ProductAlreadyExistsException;
import com.mercadoandes.catalog.application.exception.ProductNotFoundException;
import com.mercadoandes.catalog.application.port.ProductCatalogRepository;
import com.mercadoandes.catalog.domain.entity.Product;
import com.mercadoandes.catalog.domain.entity.ProductStatus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.enterprise.inject.Instance;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.models.SqlParameter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ProductPersistenceAdapter implements ProductCatalogRepository {

    @Inject
    Instance<CosmosClient> cosmosClient;

    @ConfigProperty(name = "cosmos.endpoint")
    String endpoint;

    @ConfigProperty(name = "cosmos.database")
    String databaseName;

    @ConfigProperty(name = "cosmos.container")
    String containerName;

    @ConfigProperty(name = "mercadoandes.catalog.use-mock-storage", defaultValue = "false")
    boolean useMockStorage;

    // Almacenamiento temporal en memoria para mantener el flujo funcional y pasar pruebas locales
    private final Map<String, Product> mockStorage = new ConcurrentHashMap<>();

    private String key(String country, String productId) {
        return country.trim().toUpperCase() + ":" + productId.trim();
    }

    private CosmosContainer getContainer() {
        return cosmosClient.get().getDatabase(databaseName).getContainer(containerName);
    }

    private ProductDocument toCosmosDocument(Product product) {
        ProductDocument doc = new ProductDocument();
        doc.setId(product.id());
        doc.setProductId(product.productId());
        doc.setTenantCountry(product.tenantCountry());
        doc.setName(product.name());
        doc.setCategory(product.category());
        doc.setBrand(product.brand());
        doc.setPrice(product.price());
        doc.setCurrency(product.currency());
        doc.setStock(product.stock());
        doc.setStatus(product.status() != null ? product.status().name() : null);
        doc.setLastUpdatedBy(product.lastUpdatedBy());
        doc.setUpdatedAt(product.updatedAt() != null ? product.updatedAt().toString() : null);
        doc.setSchemaVersion(product.schemaVersion());
        return doc;
    }

    private Product toDomain(ProductDocument doc) {
        if (doc == null) {
            return null;
        }
        return new Product(
                doc.getId(),
                doc.getProductId(),
                doc.getTenantCountry(),
                doc.getName(),
                doc.getCategory(),
                doc.getBrand(),
                doc.getPrice(),
                doc.getCurrency(),
                doc.getStock(),
                doc.getStatus() != null ? ProductStatus.valueOf(doc.getStatus()) : null,
                doc.getLastUpdatedBy(),
                doc.getUpdatedAt() != null ? OffsetDateTime.parse(doc.getUpdatedAt()) : null,
                doc.getSchemaVersion()
        );
    }

    @Override
    public Optional<Product> findByCountryAndProductId(String country, String productId) {
        return Optional.empty();
    }

    @Override
    public boolean existsByCountryAndProductId(String country, String productId) {
        return false;
    }

    @Override
    public void save(Product product) {
        if (useMockStorage) {
            String productKey = key(product.tenantCountry(), product.productId());
            if (mockStorage.containsKey(productKey)) {
                throw new ProductAlreadyExistsException("Product already exists");
            }
            mockStorage.put(productKey, product);
            return;
        }
        try {
            getContainer().createItem(toCosmosDocument(product), new PartitionKey(product.productId()), new CosmosItemRequestOptions());
        } catch (CosmosException e) {
            if (e.getStatusCode() == 409) {
                throw new ProductAlreadyExistsException("Product already exists");
            }
            throw e;
        }
    }

    @Override
    public void update(Product product) {
    }

    @Override
    public void delete(Product product) {
    }

    @Override
    public List<Product> search(String country, String category) {
        return List.of();
    }

    public long count() {
        return 0L;
    }

    public long countByCountry(String country) {
        return 0L;
    }

    public List<String> findRecentProductIds(int limit) {
        return List.of();
    }
}
