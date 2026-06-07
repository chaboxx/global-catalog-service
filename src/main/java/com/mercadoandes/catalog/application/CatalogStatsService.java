package com.mercadoandes.catalog.application;

import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CatalogStatsService {

    public CatalogSnapshot snapshot() {
        return new CatalogSnapshot(0, 0, Map.of(), List.of());
    }

    public record CatalogSnapshot(
            long totalProducts,
            long totalChanges,
            Map<String, Long> productsByCountry,
            List<String> recentProductIds) {
    }
}
