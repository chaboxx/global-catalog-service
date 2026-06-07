package com.mercadoandes.catalog.infrastructure.config;

import com.mercadoandes.catalog.application.port.IdGenerator;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class UuidGenerator implements IdGenerator {

    @Override
    public String newId() {
        return UUID.randomUUID().toString();
    }
}
