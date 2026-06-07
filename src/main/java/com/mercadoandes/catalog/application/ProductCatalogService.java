package com.mercadoandes.catalog.application;

import java.math.BigDecimal;
import java.util.List;

import com.mercadoandes.catalog.application.exception.InvalidProductIdentityException;
import com.mercadoandes.catalog.application.exception.ProductNotFoundException;
import com.mercadoandes.catalog.application.port.ClockProvider;
import com.mercadoandes.catalog.application.port.ProductCatalogRepository;
import com.mercadoandes.catalog.domain.entity.Product;
import com.mercadoandes.catalog.domain.entity.ProductChange;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductCatalogService {

    private static final Logger LOG = Logger.getLogger(ProductCatalogService.class);
    private static final String DEFAULT_SCHEMA_VERSION = "1.0";
    private static final String DEFAULT_UPDATED_BY = "catalog-api";

    @Inject
    ProductCatalogRepository productRepository;

    @Inject
    ClockProvider clockProvider;

    @Transactional
    public Product create(Product product) {
        validateIdentity(product, product.tenantCountry(), product.productId());
        Product normalizedProduct = normalizeProduct(product);

        LOG.infof(
                "Create product request accepted country=%s productId=%s thread=%s",
                normalizedProduct.tenantCountry(),
                normalizedProduct.productId(),
                Thread.currentThread().getName());

        saveProduct(normalizedProduct);

        LOG.infof(
                "Create product request completed country=%s productId=%s",
                normalizedProduct.tenantCountry(),
                normalizedProduct.productId());

        return normalizedProduct;
    }

    public Product get(String country, String productId) {
        throw new ProductNotFoundException("Product not found");
    }

    @Transactional
    public Product update(String country, String productId, Product product) {
        validateIdentity(product, country, productId);
        return product;
    }

    @Transactional
    public Product updatePrice(String country, String productId, BigDecimal price, String updatedBy) {
        return null;
    }

    @Transactional
    public void delete(String country, String productId) {
    }

    public List<Product> search(String country, String category) {
        return List.of();
    }

    public List<ProductChange> getChanges(String country, String productId) {
        return List.of();
    }

    private void validateIdentity(Product product, String country, String productId) {
        if (product == null) {
            throw new InvalidProductIdentityException("Product payload is required");
        }

        if (!productId.equals(product.id()) || !productId.equals(product.productId())) {
            throw new InvalidProductIdentityException("id and productId must match the path productId");
        }

        if (!country.equalsIgnoreCase(product.tenantCountry())) {
            throw new InvalidProductIdentityException("tenantCountry must match the path country");
        }
    }

    private Product normalizeProduct(Product product) {
        String country = product.tenantCountry().trim().toUpperCase();
        String updatedBy = product.lastUpdatedBy() == null || product.lastUpdatedBy().isBlank()
                ? DEFAULT_UPDATED_BY
                : product.lastUpdatedBy().trim();
        String schemaVersion = product.schemaVersion() == null || product.schemaVersion().isBlank()
                ? DEFAULT_SCHEMA_VERSION
                : product.schemaVersion().trim();

        return new Product(
                product.productId().trim(),
                product.productId().trim(),
                country,
                product.name().trim(),
                product.category().trim().toLowerCase(),
                product.brand().trim(),
                product.price(),
                product.currency().trim().toUpperCase(),
                product.stock(),
                product.status(),
                updatedBy,
                clockProvider.now(),
                schemaVersion);
    }

    private void saveProduct(Product product) {
        productRepository.save(product);
    }
}
