package org.kwakmunsu.fancafe.global.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.UnitTestSupport;
import org.kwakmunsu.fancafe.member.domain.Role;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.json.JsonMapper;

class JwtFilterTest extends UnitTestSupport {

    @Mock
    JwtProvider jwtProvider;

    @Mock
    FilterChain filterChain;

    JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(jwtProvider, JsonMapper.builder().build());
        SecurityContextHolder.clearContext();
    }

    // ---- shouldNotFilter ----

    @Test
    void 로그인_경로는_필터를_건너뜀() {
        assertThat(jwtFilter.shouldNotFilter(requestWithPath("/v1/auth/login"))).isTrue();
    }

    @Test
    void 회원가입_경로는_필터를_건너뜀() {
        assertThat(jwtFilter.shouldNotFilter(requestWithPath("/v1/members/sign-up"))).isTrue();
    }

    @Test
    void actuator_health_경로는_필터를_건너뜀() {
        assertThat(jwtFilter.shouldNotFilter(requestWithPath("/actuator/health"))).isTrue();
    }

    @Test
    void 일반_API_경로는_필터를_실행한다() {
        assertThat(jwtFilter.shouldNotFilter(requestWithPath("/v1/posts"))).isFalse();
    }

    // ---- 토큰 없음 ----

    @Test
    void Authorization_헤더_없으면_401_AUTH_EMPTY_TOKEN_응답() throws Exception {
        var req = requestWithPath("/v1/posts");
        var res = new MockHttpServletResponse();

        jwtFilter.doFilterInternal(req, res, filterChain);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(res.getContentAsString()).contains("AUTH_EMPTY_TOKEN");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void Bearer_접두사_없는_헤더면_401_AUTH_EMPTY_TOKEN_응답() throws Exception {
        var req = requestWithPath("/v1/posts");
        req.addHeader("Authorization", "Basic sometoken");
        var res = new MockHttpServletResponse();

        jwtFilter.doFilterInternal(req, res, filterChain);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(res.getContentAsString()).contains("AUTH_EMPTY_TOKEN");
        verify(filterChain, never()).doFilter(any(), any());
    }

    // ---- 유효하지 않은 토큰 ----

    @Test
    void 유효하지_않은_토큰이면_401_AUTH_INVALID_TOKEN_응답() throws Exception {
        var req = requestWithPath("/v1/posts");
        req.addHeader("Authorization", "Bearer invalidtoken");
        var res = new MockHttpServletResponse();

        given(jwtProvider.isTokenValid("invalidtoken")).willReturn(false);

        jwtFilter.doFilterInternal(req, res, filterChain);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(res.getContentAsString()).contains("AUTH_INVALID_TOKEN");
        verify(filterChain, never()).doFilter(any(), any());
    }

    // ---- Refresh 토큰 오용 ----

    @Test
    void Refresh_토큰을_Access_자리에_사용하면_401_AUTH_INVALID_TOKEN_응답() throws Exception {
        var req = requestWithPath("/v1/posts");
        req.addHeader("Authorization", "Bearer refreshtoken");
        var res = new MockHttpServletResponse();

        given(jwtProvider.isTokenValid("refreshtoken")).willReturn(true);
        given(jwtProvider.isAccessToken("refreshtoken")).willReturn(false);

        jwtFilter.doFilterInternal(req, res, filterChain);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(res.getContentAsString()).contains("AUTH_INVALID_TOKEN");
        verify(filterChain, never()).doFilter(any(), any());
    }

    // ---- 정상 ----

    @Test
    void 유효한_Access_토큰이면_SecurityContext에_인증_정보가_저장된다() throws Exception {
        var req = requestWithPath("/v1/posts");
        req.addHeader("Authorization", "Bearer validtoken");
        var res = new MockHttpServletResponse();

        given(jwtProvider.isTokenValid("validtoken")).willReturn(true);
        given(jwtProvider.isAccessToken("validtoken")).willReturn(true);
        given(jwtProvider.getAuthentication("validtoken")).willReturn(
                new UsernamePasswordAuthenticationToken(1L, null,
                        List.of(new SimpleGrantedAuthority(Role.ROLE_FAN.name())))
        );

        jwtFilter.doFilterInternal(req, res, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(1L);
        verify(filterChain).doFilter(req, res);
    }

    @Test
    void 유효한_Access_토큰이면_필터_체인이_계속_실행된다() throws Exception {
        var req = requestWithPath("/v1/posts");
        req.addHeader("Authorization", "Bearer validtoken");
        var res = new MockHttpServletResponse();

        given(jwtProvider.isTokenValid("validtoken")).willReturn(true);
        given(jwtProvider.isAccessToken("validtoken")).willReturn(true);
        given(jwtProvider.getAuthentication("validtoken")).willReturn(
                new UsernamePasswordAuthenticationToken(1L, null,
                        List.of(new SimpleGrantedAuthority(Role.ROLE_FAN.name())))
        );

        jwtFilter.doFilterInternal(req, res, filterChain);

        verify(filterChain).doFilter(req, res);
    }

    private MockHttpServletRequest requestWithPath(String path) {
        var req = new MockHttpServletRequest();
        req.setServletPath(path);
        return req;
    }

}
