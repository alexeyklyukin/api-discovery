package org.zalando.apidiscovery.storage.legacy;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.legacy.LegacyTestDataHelper.createBasicApiDefinition;
import static org.zalando.apidiscovery.storage.legacy.LegacyTestDataHelper.createDecommissionedApiDefinition;
import static org.zalando.apidiscovery.storage.legacy.LegacyTestDataHelper.createInactiveApiDefinition;
import static org.zalando.apidiscovery.storage.legacy.LegacyTestDataHelper.createUnsuccessfulApiDefinition;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DatabaseIntegrationTest {

    @Autowired
    private ApiDefinitionRepository repository;

    @Before
    public void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    public void persistGivenApiDefinitionAndRetrieveItFromDatabase() {
        ApiDefinition apiDefinition = createBasicApiDefinition();

        apiDefinition = repository.save(apiDefinition);

        assertThat(repository.findOne(apiDefinition.getApplicationId())).isEqualTo(apiDefinition);
    }

    @Test
    public void persistApiDefinitionWithMetadataFields() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setCreated(now(UTC));
        apiDefinition.setLastChanged(now(UTC));
        apiDefinition.setLastPersisted(now(UTC));

        apiDefinition = repository.save(apiDefinition);

        assertThat(repository.findOne(apiDefinition.getApplicationId())).isEqualTo(apiDefinition);
    }

    @Test
    public void persistApiDefinitionWithServiceUrl() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setServiceUrl("http://service.de");

        apiDefinition = repository.save(apiDefinition);

        assertThat(repository.findOne(apiDefinition.getApplicationId())).isEqualTo(apiDefinition);
    }

    @Test
    public void persistApiDefinitionWithLifecycleState() {
        ApiDefinition apiDefinition = repository.save(createBasicApiDefinition());

        apiDefinition.setLifecycleState(ApiLifecycleManager.INACTIVE);
        repository.save(apiDefinition);

        assertThat(repository.findOne(apiDefinition.getApplicationId()).getLifecycleState()).isEqualTo(ApiLifecycleManager.INACTIVE);
    }

    @Test
    public void shouldFindAllOldApiDefinitionsWhichAreUnsuccessful() {
        ApiDefinition apiDefinition = createUnsuccessfulApiDefinition();
        apiDefinition.setLastChanged(now(UTC).minusSeconds(10));
        apiDefinition = repository.save(apiDefinition);

        repository.save(createUnsuccessfulApiDefinition());
        repository.save(createUnsuccessfulApiDefinition());

        List<ApiDefinition> foundApi = repository.findOlderThanAndUnsuccessful(now(UTC).minusSeconds(5));
        assertThat(foundApi).containsOnly(apiDefinition);
    }

    @Test
    public void shouldFindAllOldApiDefinitionsWhichAreUnsuccessfulAndHaveNoLastChangedDateTimeSet() {
        ApiDefinition apiDefinition = createUnsuccessfulApiDefinition();
        apiDefinition.setLastChanged(null);
        apiDefinition = repository.save(apiDefinition);

        repository.save(createUnsuccessfulApiDefinition());
        repository.save(createUnsuccessfulApiDefinition());

        List<ApiDefinition> foundApi = repository.findOlderThanAndUnsuccessful(now(UTC).minusSeconds(10));
        assertThat(foundApi).containsOnly(apiDefinition);
    }

    @Test
    public void shouldFindAllNotUpdatedApiDefinitions() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setLastPersisted(now(UTC).minusSeconds(10));
        apiDefinition = repository.save(apiDefinition);

        ApiDefinition apiDefinitionWithNullLastPersisted = createUnsuccessfulApiDefinition();
        apiDefinitionWithNullLastPersisted.setLastPersisted(null);
        apiDefinitionWithNullLastPersisted = repository.save(apiDefinitionWithNullLastPersisted);

        repository.save(createBasicApiDefinition());
        repository.save(createUnsuccessfulApiDefinition());

        List<ApiDefinition> foundApi = repository.findNotUpdatedSince(now(UTC).minusSeconds(5));
        assertThat(foundApi).containsOnly(apiDefinition, apiDefinitionWithNullLastPersisted);
    }

    @Test
    public void shouldFindAllNotUpdatedAndInactiveApiDefinitions() {
        ApiDefinition apiDefinition = createInactiveApiDefinition();
        apiDefinition.setLastPersisted(now(UTC).minusSeconds(10));
        apiDefinition = repository.save(apiDefinition);

        ApiDefinition apiDefinitionWithNullLastPersisted = createInactiveApiDefinition();
        apiDefinitionWithNullLastPersisted.setLastPersisted(null);
        apiDefinitionWithNullLastPersisted = repository.save(apiDefinitionWithNullLastPersisted);

        repository.save(createBasicApiDefinition());
        repository.save(createUnsuccessfulApiDefinition());

        List<ApiDefinition> foundApi = repository.findNotUpdatedSince(now(UTC).minusSeconds(5));
        assertThat(foundApi).containsOnly(apiDefinition, apiDefinitionWithNullLastPersisted);
    }

    @Test
    public void shouldCountAlStatusStatesCorrectly() {
        repository.save(Arrays.asList(createBasicApiDefinition(),
            createBasicApiDefinition(),
            createBasicApiDefinition(),
            createUnsuccessfulApiDefinition(),
            createUnsuccessfulApiDefinition()));

        List<ApiDefinitionStatusStatistics> statusStatistics = repository.countStatus();
        assertThat(statusStatistics).containsOnlyOnce(
            new ApiDefinitionStatusStatistics("SUCCESS", 3L),
            new ApiDefinitionStatusStatistics("UNSUCCESSFUL", 2L));
    }

    @Test
    public void shouldCountAllLifecycleStatesCorrectly() {
        repository.save(Arrays.asList(createBasicApiDefinition(),
            createBasicApiDefinition(),
            createBasicApiDefinition(),
            createInactiveApiDefinition(),
            createInactiveApiDefinition(),
            createDecommissionedApiDefinition()));

        List<ApiDefinitionStatusStatistics> statusStatistics = repository.countLifecycleStates();
        assertThat(statusStatistics).containsOnlyOnce(
            new ApiDefinitionStatusStatistics(ApiLifecycleManager.ACTIVE, 3L),
            new ApiDefinitionStatusStatistics(ApiLifecycleManager.INACTIVE, 2L),
            new ApiDefinitionStatusStatistics(ApiLifecycleManager.DECOMMISSIONED, 1L));
    }
}
