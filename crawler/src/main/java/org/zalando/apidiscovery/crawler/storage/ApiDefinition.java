package org.zalando.apidiscovery.crawler.storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiDefinition {

    public static ApiDefinition UNSUCCESSFUL = ApiDefinition.builder().status("UNSUCCESSFUL").build();

    private String status;
    private String type;
    private String apiName;
    private String appName;
    private String version;
    private String serviceUrl;
    private String url;
    private String ui;
    private String definition;
}
