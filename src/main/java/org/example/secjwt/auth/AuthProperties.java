package org.example.secjwt.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(
        // encoding-id -> encodingId
        String encodingId,
        Jwt jwt
) {
    public record Jwt(String secret, Duration accessExpiry) {
    }
}