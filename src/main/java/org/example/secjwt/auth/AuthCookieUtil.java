package org.example.secjwt.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(AuthProperties.class)
@RequiredArgsConstructor
public class AuthCookieUtil {
    private final AuthProperties p;

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(
                        "accessToken", token
                ).httpOnly(true)
                .secure(true) // https - http://localhost
                .maxAge(p.jwt().accessExpiry())
                .build();
    }
}
