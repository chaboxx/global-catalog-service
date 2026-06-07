package com.mercadoandes.catalog.reproducer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import java.util.Map;

@Singleton
public class PlainWorkerMapProducer {

    @Produces
    @ApplicationScoped
    Map<String, PlainWorker> workers() {
        return Map.of("worker", new PlainWorker());
    }
}

