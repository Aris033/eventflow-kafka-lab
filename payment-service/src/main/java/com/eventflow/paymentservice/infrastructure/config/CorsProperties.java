package com.eventflow.paymentservice.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "eventflow.cors")
public class CorsProperties {
    private List<String> allowedOrigins = List.of("http://localhost:5173", "http://localhost:8086");

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
