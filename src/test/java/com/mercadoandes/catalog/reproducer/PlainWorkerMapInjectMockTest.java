package com.mercadoandes.catalog.reproducer;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(PlainWorkerMapInjectMockTest.NoDatabaseProfile.class)
class PlainWorkerMapInjectMockTest {

    @Inject
    PlainWorkerMapConsumer consumer;

    @InjectMock
    Map<String, PlainWorker> workers;

    @Test
    void shouldUseMockedMap() {
        assertEquals(0, consumer.workerCount());
    }

    public static class NoDatabaseProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                "quarkus.flyway.migrate-at-start", "false",
                "quarkus.hibernate-orm.schema-management.strategy", "none"
            );
        }
    }
}
