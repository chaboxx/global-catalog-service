package com.mercadoandes.catalog.infrastructure.rest;

import com.mercadoandes.catalog.infrastructure.rest.dto.Product;
import com.mercadoandes.catalog.infrastructure.rest.dto.ProductChange;
import com.mercadoandes.catalog.infrastructure.rest.dto.ProductStatus;

final class ProductRestMapper {

    private ProductRestMapper() {
    }

    static com.mercadoandes.catalog.domain.entity.Product toDomain(Product product) {
        if (product == null) {
            return null;
        }

        return new com.mercadoandes.catalog.domain.entity.Product(
                product.id(),
                product.productId(),
                product.tenantCountry(),
                product.name(),
                product.category(),
                product.brand(),
                product.price(),
                product.currency(),
                product.stock(),
                com.mercadoandes.catalog.domain.entity.ProductStatus.valueOf(product.status().name()),
                product.lastUpdatedBy(),
                product.updatedAt(),
                product.schemaVersion());
    }

    static Product toResponse(com.mercadoandes.catalog.domain.entity.Product product) {
        return new Product(
                product.id(),
                product.productId(),
                product.tenantCountry(),
                product.name(),
                product.category(),
                product.brand(),
                product.price(),
                product.currency(),
                product.stock(),
                ProductStatus.valueOf(product.status().name()),
                product.lastUpdatedBy(),
                product.updatedAt(),
                product.schemaVersion());
    }

    static ProductChange toResponse(com.mercadoandes.catalog.domain.entity.ProductChange change) {
        return new ProductChange(
                change.id(),
                change.productId(),
                change.tenantCountry(),
                change.changeType(),
                change.previousPrice(),
                change.currentPrice(),
                change.changedAt(),
                change.correlationId());
    }
}
