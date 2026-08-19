package org.example.secjwt.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class JwtFilter {
    private final AuthProperties p;
    private final JwtProvider jwtProvider; // claims 해석이 가능
}
