package org.zalando.apidiscovery.storage.api.resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.apidiscovery.storage.api.service.dto.DiscoveredApiDefinitionDto;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;
import org.zalando.apidiscovery.storage.api.service.ApiDefinitionProcessingService;

import java.net.URI;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ApiDefinitionResourceControllerTest {

    private ApiDefinitionResourceController apiDefinitionController;

    @Mock
    private ApiDefinitionProcessingService apiDefinitionService;

    @Before
    public void setUp() throws Exception {
        apiDefinitionController = new ApiDefinitionResourceController(apiDefinitionService);
    }

    @Test
    public void shouldReturnHttpCodeCreatedAndLocationHeader() throws Exception {
        final ApiEntity api = ApiEntity.builder().apiName("meta-api").apiVersion("1.0").id(1l).build();
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUri(new URI("http://localhost/"));
        final String definitionUriPattern = "http://localhost/apis/meta-api/versions/1.0/definitions/\\d+";
        doReturn(api).when(apiDefinitionService).processDiscoveredApiDefinition(any(DiscoveredApiDefinitionDto.class));

        final ResponseEntity<Void> response = apiDefinitionController.postDiscoveredApiDefinition(null, builder);
        final URI location = response.getHeaders().getLocation();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(location).isNotNull();
        assertThat(Pattern.matches(definitionUriPattern, location.toString())).isTrue();
    }
}
