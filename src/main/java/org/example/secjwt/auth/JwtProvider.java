package org.example.secjwt.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@EnableConfigurationProperties(AuthProperties.class)
@RequiredArgsConstructor
public class JwtProvider {
    private final AuthProperties p;

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(p.jwt().secret().getBytes());
    }

    public String issueAccessToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + p.jwt().accessExpiry().toMillis());
        return Jwts.builder()
                .subject(subject) // username
                .issuedAt(now) // 발행시간
                .expiration(expiry) // 만료시간
                .signWith(getSecretKey())
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                // jwt 관련된 exception이 난다
                .getPayload();
    }
}
