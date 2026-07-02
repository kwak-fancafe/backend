package org.kwakmunsu.fancafe.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(SecurityPaths.actuatorPermit()).permitAll();
                    auth.requestMatchers("/actuator/**").denyAll();
                    auth.requestMatchers(SecurityPaths.permitAll()).permitAll();
                    auth.requestMatchers(HttpMethod.POST, SecurityPaths.adminWithManager()).hasAnyRole("MANAGER", "CREATOR");
                    auth.requestMatchers(HttpMethod.PATCH, SecurityPaths.adminWithManager()).hasAnyRole("MANAGER", "CREATOR");
                    auth.requestMatchers(HttpMethod.DELETE, SecurityPaths.adminWithManager()).hasAnyRole("MANAGER", "CREATOR");
                    auth.requestMatchers(SecurityPaths.admin()).hasRole("CREATOR");
                    auth.anyRequest().hasAnyRole("FAN", "MANAGER", "CREATOR");
                });

        return http.build();
    }

}