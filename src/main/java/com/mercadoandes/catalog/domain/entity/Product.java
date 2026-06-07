package com.mercadoandes.catalog.domain.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Product(
        String id,
        String productId,
        String tenantCountry,
        String name,
        String category,
        String brand,
        BigDecimal price,
        String currency,
        Integer stock,
        ProductStatus status,
        String lastUpdatedBy,
        OffsetDateTime updatedAt,
        String schemaVersion) {
}
