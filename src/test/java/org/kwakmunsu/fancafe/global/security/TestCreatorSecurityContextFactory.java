package org.kwakmunsu.fancafe.global.security;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.fancafe.global.security.annotation.TestCreator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Slf4j
public class TestCreatorSecurityContextFactory implements WithSecurityContextFactory<TestCreator> {

    @Override
    public SecurityContext createSecurityContext(TestCreator annotation) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        GrantedAuthority authority = new SimpleGrantedAuthority(annotation.role());
        Long adminId = annotation.id();

        log.debug("Creator ID: {}, Role: {}", adminId, authority);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                adminId,
                null,
                Collections.singletonList(authority)
        );
        securityContext.setAuthentication(authentication);

        return securityContext;
    }

}