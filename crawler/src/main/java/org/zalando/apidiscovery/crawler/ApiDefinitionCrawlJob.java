package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.zalando.apidiscovery.crawler.gateway.ApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.LegacyApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.WellKnownSchemaGateway;
import org.zalando.stups.clients.kio.ApplicationBase;

import java.util.concurrent.Callable;

@Slf4j
class ApiDefinitionCrawlJob implements Callable<Void> {

    private final LegacyApiDiscoveryStorageGateway legacyStorageGateway;
    private final ApiDiscoveryStorageGateway storageGateway;
    private final WellKnownSchemaGateway schemaGateway;
    private final ApplicationBase app;

    ApiDefinitionCrawlJob(LegacyApiDiscoveryStorageGateway legacyStorageGateway,
                          ApiDiscoveryStorageGateway storageGateway,
                          WellKnownSchemaGateway schemaGateway,
                          ApplicationBase app) {
        this.legacyStorageGateway = legacyStorageGateway;
        this.storageGateway = storageGateway;
        this.schemaGateway = schemaGateway;
        this.app = app;
    }

    @Override
    public Void call() throws Exception {
        final JsonNode schemaDiscovery = schemaGateway.retrieveSchemaDiscovery(app);

        if (schemaDiscovery == null) {
            log.info("Api definition unavailable for {}", app.getId());
            pushCrawlingResultsWithoutSchemaDiscovery(app);
        } else {
            JsonNode apiDefinition = schemaGateway.retrieveApiDefinition(app, schemaDiscovery);
            log.info("Successfully crawled api definition of {}", app.getId());
            pushCrawlingResultsWithSchemaDiscovery(schemaDiscovery, apiDefinition, app);
        }
        return null;
    }

    private void pushCrawlingResultsWithoutSchemaDiscovery(ApplicationBase app){
        legacyStorageGateway.createOrUpdateApiDefinition(null, null, app);
        storageGateway.pushApiDefinition(null, null, app);
    }

    private void pushCrawlingResultsWithSchemaDiscovery(JsonNode schemaDiscovery, JsonNode apiDefinition, ApplicationBase app) {
        legacyStorageGateway.createOrUpdateApiDefinition(schemaDiscovery, apiDefinition, app);
        storageGateway.pushApiDefinition(schemaDiscovery, apiDefinition, app);
    }

}

