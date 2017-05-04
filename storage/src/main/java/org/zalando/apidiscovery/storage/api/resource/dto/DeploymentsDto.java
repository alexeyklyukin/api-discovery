package org.zalando.apidiscovery.storage.api.resource.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentsDto {

    private List<DeploymentDto> deployments = new ArrayList<>();
}
