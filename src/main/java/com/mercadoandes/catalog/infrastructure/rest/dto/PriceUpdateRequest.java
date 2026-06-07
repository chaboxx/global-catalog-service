package com.mercadoandes.catalog.infrastructure.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PriceUpdateRequest(
        @NotNull @DecimalMin("0.0") BigDecimal price,
        String updatedBy) {
}
