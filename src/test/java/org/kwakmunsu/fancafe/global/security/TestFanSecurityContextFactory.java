package org.kwakmunsu.fancafe.global.security;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.fancafe.global.security.annotation.TestFan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Slf4j
public class TestFanSecurityContextFactory implements WithSecurityContextFactory<TestFan> {

    @Override
    public SecurityContext createSecurityContext(TestFan annotation) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        GrantedAuthority authority = new SimpleGrantedAuthority(annotation.role());
        Long memberId = annotation.id();

        log.debug("Fan ID: {}, Role: {}", memberId, authority);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                memberId,
                null,
                Collections.singletonList(authority)
        );
        securityContext.setAuthentication(authentication);

        return securityContext;
    }

}