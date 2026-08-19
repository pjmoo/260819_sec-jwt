package org.example.secjwt.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class JwtFilter extends OncePerRequestFilter {
    private final AuthProperties p;
    private final JwtProvider jwtProvider; // claims 해석이 가능

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            // AccessToken -> 2가지 방법으로 저장
            // 1. header
            // 2. cookie
            // -> Header에 들어가 있다 -> request
            String token = extractToken(request);
            Claims claims = jwtProvider.parseClaims(token);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), // username
                    null,
                    AuthorityUtils.createAuthorityList("ROLE_USER")
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            SecurityContextHolder.clearContext(); // 에러로 인해서 인증 정보 꼬이는 걸 배제
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String extractToken(HttpServletRequest request) {
        // 1. Header
        String authHeader = request.getHeader("Authorization");
        if (
                StringUtils.hasText(authHeader)
                        && authHeader.startsWith("Bearer ")
        ) {
            return authHeader.substring(7);
            // Bearer {...}
        }
        // 2. Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies) // 쿠키 배열을 stream
                    .filter(cookie -> cookie.getName().equals("accessToken"))
                    // filter -> accessToken 쿠키 이름으로 필터링
                    .findFirst()
                    // 첫번째 발견됨
                    .map(Cookie::getValue) // 있다면 그 값을 가져오고
                    .orElseThrow(); // 없으면 throw
        }
        throw new IllegalArgumentException("토큰을 추출할 수 없음");
    }
}
