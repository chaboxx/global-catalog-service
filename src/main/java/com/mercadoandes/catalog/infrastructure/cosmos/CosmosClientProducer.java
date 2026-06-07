package com.mercadoandes.catalog.infrastructure.cosmos;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.core.credential.TokenCredential;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.util.Optional;


@ApplicationScoped
public class CosmosClientProducer {
    @ConfigProperty(name = "cosmos.endpoint") String endpoint;
    
    @ConfigProperty(name = "cosmos.managed-identity-client-id")
    Optional<String> managedIdentityClientId;

    @Produces
    @ApplicationScoped
    public CosmosAsyncClient createAsyncClient() {
        return new CosmosClientBuilder()
                .endpoint(endpoint)
                .credential(createCredential())
                .contentResponseOnWriteEnabled(true)
                .buildAsyncClient();
    }

    @Produces
    @ApplicationScoped
    public CosmosClient createClient() {
        return new CosmosClientBuilder()
                .endpoint(endpoint)
                .credential(createCredential())
                .contentResponseOnWriteEnabled(true)
                .buildClient();
    }

    private TokenCredential createCredential() {
        DefaultAzureCredentialBuilder builder = new DefaultAzureCredentialBuilder();
        if (managedIdentityClientId != null && managedIdentityClientId.isPresent() && !managedIdentityClientId.get().isBlank()) {
            builder.managedIdentityClientId(managedIdentityClientId.get().trim());
        }
        return builder.build();
    }
}
