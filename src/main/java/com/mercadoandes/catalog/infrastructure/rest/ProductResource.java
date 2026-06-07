package com.mercadoandes.catalog.infrastructure.rest;

import com.mercadoandes.catalog.api.ProductsApi;
import com.mercadoandes.catalog.application.ProductCatalogService;
import com.mercadoandes.catalog.infrastructure.rest.dto.PriceUpdateRequest;
import com.mercadoandes.catalog.infrastructure.rest.dto.Product;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/")
public class ProductResource implements ProductsApi {

    @Inject
    ProductCatalogService productCatalogService;

    @Override
    public Response createProduct(Product product) {
        com.mercadoandes.catalog.domain.entity.Product createdProduct = productCatalogService.create(ProductRestMapper.toDomain(product));
        URI location = URI.create("/api/products/" + createdProduct.tenantCountry() + "/" + createdProduct.productId());
        return Response.created(location).entity(ProductRestMapper.toResponse(createdProduct)).build();
    }

    @Override
    public Response getProduct(String country, String productId) {
        return Response.ok(ProductRestMapper.toResponse(productCatalogService.get(country, productId))).build();
    }

    @Override
    public Response updateProduct(String country, String productId, Product product) {
        return Response.ok(ProductRestMapper.toResponse(productCatalogService.update(country, productId, ProductRestMapper.toDomain(product)))).build();
    }

    @Override
    public Response updatePrice(String country, String productId, PriceUpdateRequest request) {
        return Response.ok(ProductRestMapper.toResponse(productCatalogService.updatePrice(country, productId, request.price(), request.updatedBy()))).build();
    }

    @Override
    public Response deleteProduct(String country, String productId) {
        productCatalogService.delete(country, productId);
        return Response.noContent().build();
    }

    @Override
    public Response searchProducts(String country, String category) {
        return Response.ok(productCatalogService.search(country, category).stream()
                .map(ProductRestMapper::toResponse)
                .toList()).build();
    }

    @Override
    public Response getProductChanges(String country, String productId) {
        return Response.ok(productCatalogService.getChanges(country, productId).stream()
                .map(ProductRestMapper::toResponse)
                .toList()).build();
    }
}
