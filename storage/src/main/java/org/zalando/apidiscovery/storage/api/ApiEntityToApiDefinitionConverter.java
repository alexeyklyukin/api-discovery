package org.zalando.apidiscovery.storage.api;

import java.util.List;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

public class ApiEntityToApiDefinitionConverter {


    public static ApiDefinitionDto toApiDefinitionDto(ApiEntity apiEntity) {
        List<DeploymentLinkDto> deploymentLinkDtos = null;
        if (apiEntity.getApiDeploymentEntities() != null) {
            deploymentLinkDtos = apiEntity.getApiDeploymentEntities().stream()
                .map(apiDeploymentEntity -> new DeploymentLinkDto.ApplicationLinkDto(apiDeploymentEntity))
                .collect(toList());
        }

        return ApiDefinitionDto.builder()
            .id(valueOf(apiEntity.getId()))
            .definition(apiEntity.getDefinition())
            .type(apiEntity.getDefinitionType())
            .applications(deploymentLinkDtos)
            .build();

    }

}
