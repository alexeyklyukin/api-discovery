package org.zalando.apidiscovery.storage.api.service.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationsDto {

    private List<ApplicationDto> applications = new ArrayList<>();
}
