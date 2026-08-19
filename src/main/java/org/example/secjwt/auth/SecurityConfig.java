package org.example.secjwt.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthProperties p; // 주입받을 수 있음
    private final JwtFilter jwtFilter;

    // SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // http...
        http
                // .cors...
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // 경로 보안
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html")
                                .permitAll()
                                .requestMatchers(
                                        HttpMethod.POST, "/user/signup")
                                .permitAll()
                                .requestMatchers(
                                        HttpMethod.POST, "/user/login")
                                .permitAll()
                );
        // 예외 처리
        // .exceptionHandling()
        return http.build();
    }

    // PasswordEncoder - 가입하고 DB 저장될 때 해싱해서 암호화된 비밀번호를 저장
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 그냥 bcrypt only
        // return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        Map<String, PasswordEncoder> encoderMap = Map.of(
                // bcprov -> 알아서 주입받음
                "argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8(),
                "scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8(),
                "bcrypt", new BCryptPasswordEncoder()
        );
        return new DelegatingPasswordEncoder(p.encodingId(), encoderMap);
    }
}
