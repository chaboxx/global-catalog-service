package com.mercadoandes.catalog.reproducer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

@ApplicationScoped
public class PlainWorkerMapConsumer {

    @Inject
    Map<String, PlainWorker> workers;

    public int workerCount() {
        return workers.size();
    }
}
