package com.pradeep.slms.security;

import com.pradeep.slms.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final DatabaseJwtAuthenticationConverter jwtAuthenticationConverter;
    private final ObjectMapper objectMapper;

    @Value("${clerk.jwt.issuer-uri:}")
    private String clerkIssuerUri;

    @Value("${clerk.jwt.jwk-set-uri:}")
    private String clerkJwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/webhooks/clerk").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            objectMapper.writeValue(response.getOutputStream(),
                                    ApiResponse.fail("Authentication required", HttpStatus.UNAUTHORIZED.name()));
                        })
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            objectMapper.writeValue(response.getOutputStream(),
                                    ApiResponse.fail("Access denied", HttpStatus.FORBIDDEN.name()));
                        })
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        if (clerkIssuerUri != null && !clerkIssuerUri.isBlank()) {
            return JwtDecoders.fromIssuerLocation(clerkIssuerUri);
        }

        if (clerkJwkSetUri != null && !clerkJwkSetUri.isBlank()) {
            return NimbusJwtDecoder.withJwkSetUri(clerkJwkSetUri).build();
        }

        return token -> {
            throw new JwtException("Configure clerk.jwt.issuer-uri or clerk.jwt.jwk-set-uri to validate Clerk JWTs");
        };
    }
}
