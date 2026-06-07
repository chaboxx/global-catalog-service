package com.mercadoandes.catalog.domain.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProductChange(
        String id,
        String productId,
        String tenantCountry,
        String changeType,
        BigDecimal previousPrice,
        BigDecimal currentPrice,
        OffsetDateTime changedAt,
        String correlationId) {
}
