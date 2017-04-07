package org.zalando.apidiscovery.storage;

import org.apache.commons.lang3.RandomStringUtils;
import org.zalando.apidiscovery.storage.api.CrawledApiDefinitionDto;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

public final class TestDataHelper {

    private TestDataHelper() {
    }

    public static ApiDefinition createUnsuccessfulApiDefinition() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setStatus("UNSUCCESSFUL");
        return apiDefinition;
    }

    public static ApiDefinition createInactiveApiDefinition() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setLifecycleState(ApiLifecycleManager.INACTIVE);
        return apiDefinition;
    }

    public static ApiDefinition createDecommissionedApiDefinition() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setLifecycleState(ApiLifecycleManager.DECOMMISSIONED);
        return apiDefinition;
    }

    public static ApiDefinition createBasicApiDefinition() {
        ApiDefinition apiDefinition = new ApiDefinition();
        apiDefinition.setApplicationId(RandomStringUtils.randomAlphabetic(20));
        apiDefinition.setStatus("SUCCESS");
        apiDefinition.setLifecycleState(ApiLifecycleManager.ACTIVE);
        apiDefinition.setLastPersisted(now(UTC));
        apiDefinition.setLastChanged(apiDefinition.getLastPersisted());
        return apiDefinition;
    }


    public static CrawledApiDefinitionDto crawledMetaApi(String version, String definitionDiff) {
        return CrawledApiDefinitionDto.builder()
                .applicationName("Meta Application")
                .apiName("meta-api")
                .version(version)
                .definition("{\"info\":{\"title\":\"Meta API\",\"version\":\"" + version + "\"}, \"diff\":\"" + definitionDiff + "\"}")
                .build();
    }

    public static String crawlerUberApi() throws IOException, URISyntaxException {
        return readFile("uber.json");
    }

    public static String invalidCrawledApi() throws IOException, URISyntaxException {
        return readFile("invalid-crawler-data.json");
    }

    public static String minimalCrawledApi() throws IOException, URISyntaxException {
        return readFile("minimal-crawler-data.json");
    }

    public static String instagramApiDefinition() throws IOException, URISyntaxException {
        return readFile("instagram-api-definition.json");
    }

    private static String readFile(String fileName) throws URISyntaxException, IOException {
        URI fileLocation = Thread.currentThread().getContextClassLoader().getResource(fileName).toURI();
        Path path = Paths.get(fileLocation);
        return Files.lines(path).collect(Collectors.joining());
    }
}
