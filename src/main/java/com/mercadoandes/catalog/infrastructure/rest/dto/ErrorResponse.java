package com.mercadoandes.catalog.infrastructure.rest.dto;

public record ErrorResponse(
        String code,
        String message,
        String traceId) {
}
