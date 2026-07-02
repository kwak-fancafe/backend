package org.kwakmunsu.fancafe.global.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityPaths {

    private static final String[] PERMIT_ALL = {
            "/api/v1/auth/login",
            "/api/v1/auth/reissue",
            "/api/v1/members/sign-up",
            "/api/v1/members/password",
            "/swagger/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error"
    };

    private static final String[] ACTUATOR_PERMIT = {
            "/actuator/health",
            "/actuator/info",
            "/actuator/prometheus"
    };

    private static final String[] ADMIN = {
            "/api/v1/admin/**",
    };

    private static final String[] ADMIN_WITH_MANAGER = {
            "/api/v1/posts/**",
    };

    public static String[] permitAll() {
        return PERMIT_ALL.clone();
    }

    public static String[] actuatorPermit() {
        return ACTUATOR_PERMIT.clone();
    }

    public static String[] admin() {
        return ADMIN.clone();
    }

    public static String[] adminWithManager() {
        return ADMIN_WITH_MANAGER.clone();
    }
}