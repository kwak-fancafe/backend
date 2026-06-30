package org.kwakmunsu.fancafe.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.UnitTestSupport;
import org.kwakmunsu.fancafe.auth.domain.RefreshToken;
import org.kwakmunsu.fancafe.auth.infrastructure.RefreshTokenRepository;
import org.kwakmunsu.fancafe.global.security.JwtProvider;
import org.kwakmunsu.fancafe.global.security.dto.TokenResponse;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.domain.Role;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class AuthCommandServiceTest extends UnitTestSupport {

    @Mock
    JwtProvider jwtProvider;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    AuthCommandService authCommandService;

    @Test
    void 신규_토큰_발급_성공() {
        given(jwtProvider.createTokens(1L, Role.ROLE_FAN)).willReturn(
                TokenResponse.builder()
                        .accessToken("access")
                        .refreshToken("refresh")
                        .build()
        );
        given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.empty());

        var result = authCommandService.issueTokens(1L, Role.ROLE_FAN);

        assertThat(result).extracting(
                        TokenResponse::accessToken,
                        TokenResponse::refreshToken
                ).containsExactly("access", "refresh");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void 재로그인_시_기존_토큰_rotation() {
        var existing = RefreshToken.create(1L, "oldToken");
        given(jwtProvider.createTokens(1L, Role.ROLE_FAN))
                .willReturn(TokenResponse.builder().accessToken("access").refreshToken("newToken").build());
        given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.of(existing));

        authCommandService.issueTokens(1L, Role.ROLE_FAN);

        assertThat(existing.matches("newToken")).isTrue();
        verify(refreshTokenRepository).save(existing);
    }

    @Test
    void validateRefreshToken_성공() {
        var stored = RefreshToken.create(1L, "validToken");
        given(jwtProvider.isTokenValid("validToken")).willReturn(true);
        given(jwtProvider.isRefreshToken("validToken")).willReturn(true);
        given(jwtProvider.getMemberIdFromToken("validToken")).willReturn(1L);
        given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.of(stored));

        Long memberId = authCommandService.validateRefreshToken("validToken");

        assertThat(memberId).isEqualTo(1L);
    }

    @Test
    void validateRefreshToken_실패_유효하지_않은_JWT() {
        given(jwtProvider.isTokenValid(any())).willReturn(false);

        assertThatThrownBy(() -> authCommandService.validateRefreshToken("expiredToken"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.AUTH_EXPIRED_REFRESH_TOKEN.getMessage());
    }

    @Test
    void validateRefreshToken_실패_리프레시_토큰_아님() {
        given(jwtProvider.isTokenValid(any())).willReturn(true);
        given(jwtProvider.isRefreshToken(any())).willReturn(false);

        assertThatThrownBy(() -> authCommandService.validateRefreshToken("accessToken"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.AUTH_EXPIRED_REFRESH_TOKEN.getMessage());
    }

    @Test
    void validateRefreshToken_실패_저장된_토큰_없음() {
        given(jwtProvider.isTokenValid(any())).willReturn(true);
        given(jwtProvider.isRefreshToken(any())).willReturn(true);
        given(jwtProvider.getMemberIdFromToken(any())).willReturn(1L);
        given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authCommandService.validateRefreshToken("token"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.AUTH_REFRESH_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    void validateRefreshToken_실패_해시_불일치() {
        var stored = RefreshToken.create(1L, "correctToken");
        given(jwtProvider.isTokenValid(any())).willReturn(true);
        given(jwtProvider.isRefreshToken(any())).willReturn(true);
        given(jwtProvider.getMemberIdFromToken(any())).willReturn(1L);
        given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.of(stored));

        assertThatThrownBy(() -> authCommandService.validateRefreshToken("wrongToken"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.AUTH_REFRESH_TOKEN_MISMATCH.getMessage());
    }

    @Test
    void revoke_성공() {
        authCommandService.revoke(1L);

        verify(refreshTokenRepository).deleteByMemberId(1L);
    }

}
