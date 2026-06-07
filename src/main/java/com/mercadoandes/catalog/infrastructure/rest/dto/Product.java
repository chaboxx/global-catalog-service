package com.mercadoandes.catalog.infrastructure.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Product(
        @NotBlank String id,
        @NotBlank String productId,
        @NotBlank String tenantCountry,
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String brand,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotBlank String currency,
        @NotNull @Min(0) Integer stock,
        @NotNull ProductStatus status,
        String lastUpdatedBy,
        OffsetDateTime updatedAt,
        String schemaVersion) {
}
