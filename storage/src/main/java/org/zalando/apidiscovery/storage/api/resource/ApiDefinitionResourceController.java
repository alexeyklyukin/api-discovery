package org.zalando.apidiscovery.storage.api.resource;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.apidiscovery.storage.api.domain.model.DiscoveredApiDefinitionDto;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;
import org.zalando.apidiscovery.storage.api.domain.service.ApiDefinitionProcessingService;
import org.zalando.apidiscovery.storage.api.domain.SwaggerParseException;

import static org.zalando.apidiscovery.storage.api.resource.LinkBuilderUtil.buildDefinitionDeploymentLink;

@CrossOrigin
@RestController
@RequestMapping("/api-definitions")
public class ApiDefinitionResourceController {

    private ApiDefinitionProcessingService apiDefinitionProcessingService;

    @Autowired
    public ApiDefinitionResourceController(ApiDefinitionProcessingService apiDefinitionProcessingService) {
        this.apiDefinitionProcessingService = apiDefinitionProcessingService;
    }

    @PostMapping
    public ResponseEntity<Void> postDiscoveredApiDefinition(@RequestBody DiscoveredApiDefinitionDto discoveredAPIDefinitionDto, UriComponentsBuilder builder)
            throws SwaggerParseException {
        final ApiEntity api = apiDefinitionProcessingService.processDiscoveredApiDefinition(discoveredAPIDefinitionDto);

        final URI location = buildDefinitionDeploymentLink(builder, api).encode().toUri();
        return ResponseEntity.created(location).build();
    }

}
