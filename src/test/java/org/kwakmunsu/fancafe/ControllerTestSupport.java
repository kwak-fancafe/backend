package org.kwakmunsu.fancafe;

import java.util.List;
import org.kwakmunsu.fancafe.auth.application.AuthFacade;
import org.kwakmunsu.fancafe.auth.presentation.AuthController;
import org.kwakmunsu.fancafe.community.post.application.PostFacade;
import org.kwakmunsu.fancafe.community.post.presentation.PostController;
import org.kwakmunsu.fancafe.global.security.TestSecurityConfig;
import org.kwakmunsu.fancafe.member.application.MemberFacade;
import org.kwakmunsu.fancafe.member.domain.Role;
import org.kwakmunsu.fancafe.member.presentation.MemberController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = {
        MemberController.class,
        AuthController.class,
        PostController.class,
})
public abstract class ControllerTestSupport {

    protected static RequestPostProcessor fanAuth() {
        var auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority(Role.ROLE_FAN.name()))
        );
        return SecurityMockMvcRequestPostProcessors.authentication(auth);
    }

    protected static RequestPostProcessor creatorAuth() {
        var auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority(Role.ROLE_CREATOR.name()))
        );
        return SecurityMockMvcRequestPostProcessors.authentication(auth);
    }

    protected static RequestPostProcessor managerAuth() {
        var auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority(Role.ROLE_MANAGER.name()))
        );
        return SecurityMockMvcRequestPostProcessors.authentication(auth);
    }

    @Autowired
    protected MockMvcTester mvcTester;

    @Autowired
    protected JsonMapper jsonMapper;

    @MockitoBean
    protected MemberFacade memberFacade;

    @MockitoBean
    protected AuthFacade authFacade;

    @MockitoBean
    protected PostFacade postFacade;

}