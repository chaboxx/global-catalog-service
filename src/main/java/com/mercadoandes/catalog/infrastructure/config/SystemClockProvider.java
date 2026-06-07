package com.mercadoandes.catalog.infrastructure.config;

import com.mercadoandes.catalog.application.port.ClockProvider;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@ApplicationScoped
public class SystemClockProvider implements ClockProvider {

    @Override
    public OffsetDateTime now() {
        return OffsetDateTime.now(ZoneOffset.UTC).withNano(0);
    }
}
