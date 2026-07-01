package org.kwakmunsu.fancafe.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.ControllerTestSupport;
import org.kwakmunsu.fancafe.auth.presentation.dto.LoginRequest;
import org.kwakmunsu.fancafe.auth.presentation.dto.ReissueRequest;
import org.kwakmunsu.fancafe.global.security.dto.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class AuthControllerTest extends ControllerTestSupport {

    @Test
    void 로그인_성공() {
        given(authFacade.login(any(String.class), any(String.class))).willReturn(
                TokenResponse.builder().accessToken("access").refreshToken("refresh").build()
        );

        mvcTester.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(new LoginRequest("user01", "Pass1!aa")))
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"))
                .hasPathSatisfying("$.data.accessToken", v -> v.assertThat().isEqualTo("access"))
                .hasPathSatisfying("$.data.refreshToken", v -> v.assertThat().isEqualTo("refresh"));
    }

    @Test
    void 토큰_재발급_성공() {
        given(authFacade.reissue(any(String.class))).willReturn(
                TokenResponse.builder().accessToken("newAccess").refreshToken("newRefresh").build()
        );

        mvcTester.post()
                .uri("/api/v1/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(new ReissueRequest("someRefreshToken")))
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"));
    }

    @Test
    void 로그아웃_성공() {
        mvcTester.post()
                .uri("/api/v1/auth/logout")
                .with(ControllerTestSupport.fanAuth())
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"));
    }

}