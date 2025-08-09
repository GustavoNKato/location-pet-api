package com.itau.challenge_localization_api.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the PositionStack API.
 * Maps properties from application.yml to this class.
 */
@Configuration
@ConfigurationProperties(prefix = "position-stack")
@Data
public class PositionStackConfig {
    private String baseUrl;
    private String key;
}
