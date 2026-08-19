package org.example.secjwt.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties (
        // encoding-id -> encodingId
        String encodingId
) {
}