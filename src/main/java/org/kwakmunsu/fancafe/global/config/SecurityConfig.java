package org.kwakmunsu.fancafe.global.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.global.security.JwtAccessDeniedHandler;
import org.kwakmunsu.fancafe.global.security.JwtAuthenticationEntryPoint;
import org.kwakmunsu.fancafe.global.security.JwtFilter;
import org.kwakmunsu.fancafe.global.security.JwtProvider;
import org.kwakmunsu.fancafe.global.security.SecurityPaths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private static final List<String> ALLOWED_METHODS = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtProvider jwtProvider;
    private final JsonMapper jsonMapper;

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(SecurityPaths.actuatorPermit()).permitAll();
                    auth.requestMatchers("/actuator/**").denyAll();
                    auth.requestMatchers(SecurityPaths.permitAll()).permitAll();
                    auth.requestMatchers(SecurityPaths.admin()).hasRole("CREATOR");
                    auth.anyRequest().hasAnyRole("FAN", "MANAGER", "CREATOR");
                });

        http
                .addFilterBefore(new JwtFilter(jwtProvider, jsonMapper), UsernamePasswordAuthenticationFilter.class);

        http
                .exceptionHandling(handle -> handle
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler));

        http
                .cors(
                        corsCustomizer -> corsCustomizer.configurationSource(_ -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(allowedOrigins);
                            config.setAllowedMethods(ALLOWED_METHODS);
                            config.setAllowedHeaders(List.of("*"));
                            config.setAllowCredentials(true);
                            config.setMaxAge(3600L); // 1 hour

                            return config;
                        })
                );

        return http.build();
    }

}