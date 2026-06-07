package com.mercadoandes.catalog.application.port;

import com.mercadoandes.catalog.domain.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductCatalogRepository {

    Optional<Product> findByCountryAndProductId(String country, String productId);

    boolean existsByCountryAndProductId(String country, String productId);

    void save(Product product);

    void update(Product product);

    void delete(Product product);

    List<Product> search(String country, String category);
}
