package com.mercadoandes.catalog.application.port;

import java.time.OffsetDateTime;

public interface ClockProvider {

    OffsetDateTime now();
}
