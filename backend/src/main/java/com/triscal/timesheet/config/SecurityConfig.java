package com.triscal.timesheet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Resource Server JWT (OIDC). A identidade vem DIRETAMENTE do Microsoft Entra ID
 * (sem Keycloak). O token só traz identidade (email/UPN/oid); papéis e hierarquia
 * são 100% internos (perfil, funcionario_perfil), aplicados nas camadas de serviço.
 *
 * Valida: assinatura (JWKS do Entra), emissor (issuer) e AUDIENCE (o app/API).
 * Para o emissor v2 (`.../v2.0`) bater, a App Registration da API deve ter
 * `accessTokenAcceptedVersion = 2` (manifest) — ver docs/ENTRA_SSO.md.
 */
@Configuration
public class SecurityConfig {

    private final String issuer;
    private final String audience;

    public SecurityConfig(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer,
            @Value("${OIDC_AUDIENCE:}") String audience) {
        this.issuer = issuer;
        this.audience = audience;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> {}));
        return http.build();
    }

    /** Decoder com validação de emissor + audience (quando OIDC_AUDIENCE estiver definido). */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuer);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> validator = (audience == null || audience.isBlank())
            ? withIssuer
            : new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator(audience));
        decoder.setJwtValidator(validator);
        return decoder;
    }

    /** Aceita o token se a claim `aud` contiver a audience esperada (client-id da API). */
    private static OAuth2TokenValidator<Jwt> audienceValidator(String expected) {
        return jwt -> jwt.getAudience() != null && jwt.getAudience().contains(expected)
            ? org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success()
            : org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
                new org.springframework.security.oauth2.core.OAuth2Error(
                    "invalid_audience", "Audience esperada: " + expected, null));
    }
}
