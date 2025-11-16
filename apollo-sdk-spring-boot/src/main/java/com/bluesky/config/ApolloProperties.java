package com.bluesky.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
@Data
public class ApolloProperties {
    private String portalUrl;
    private String token;
    private String appId;
    private String env;
    private String cluster;
    private String namespace;
    private String operator;
}
