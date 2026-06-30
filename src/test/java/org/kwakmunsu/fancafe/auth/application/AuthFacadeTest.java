package org.kwakmunsu.fancafe.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.IntegrationTestSupport;
import org.kwakmunsu.fancafe.auth.infrastructure.RefreshTokenJpaRepository;
import org.kwakmunsu.fancafe.global.security.dto.TokenResponse;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.application.MemberCommandService;
import org.kwakmunsu.fancafe.member.application.dto.NewMember;
import org.kwakmunsu.fancafe.member.infrastructure.MemberJpaRepository;

@RequiredArgsConstructor
class AuthFacadeTest extends IntegrationTestSupport {

    final AuthFacade authFacade;
    final MemberCommandService memberCommandService;
    final MemberJpaRepository memberJpaRepository;
    final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @AfterEach
    void tearDown() {
        refreshTokenJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
    }

    @Test
    void 로그인_성공() {
        memberCommandService.register(newMember());

        var tokens = authFacade.login("testuser1", "Pass1!aa");

        assertThat(tokens).extracting(
                        TokenResponse::accessToken,
                        TokenResponse::refreshToken
                ).doesNotContainNull();
        assertThat(refreshTokenJpaRepository.count()).isEqualTo(1);
    }

    @Test
    void 로그인_실패_존재하지_않는_계정() {
        assertThatThrownBy(() -> authFacade.login("notexist", "Pass1!aa"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_NOT_FOUND_ACCOUNT.getMessage());
    }

    @Test
    void 로그인_실패_잘못된_비밀번호() {
        memberCommandService.register(newMember());

        assertThatThrownBy(() -> authFacade.login("testuser1", "WrongPass1!"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_NOT_FOUND_ACCOUNT.getMessage());
    }

    @Test
    void 재로그인_시_리프레시_토큰_교체() {
        memberCommandService.register(newMember());

        authFacade.login("testuser1", "Pass1!aa");
        authFacade.login("testuser1", "Pass1!aa");

        // 두 번 로그인해도 저장된 토큰은 1개 (upsert)
        assertThat(refreshTokenJpaRepository.count()).isEqualTo(1);
    }

    @Test
    void 토큰_재발급_성공() {
        memberCommandService.register(newMember());
        var tokens = authFacade.login("testuser1", "Pass1!aa");

        var newTokens = authFacade.reissue(tokens.refreshToken());

        assertThat(newTokens.accessToken()).isNotNull();
        assertThat(newTokens.refreshToken()).isNotNull();
    }

    @Test
    void 재발급_후_이전_토큰_사용_불가() throws InterruptedException {
        memberCommandService.register(newMember());
        var tokens = authFacade.login("testuser1", "Pass1!aa");

        // JWT는 초 단위 타임스탬프 → 다른 초에 발급해야 다른 토큰 생성
        Thread.sleep(1100);
        authFacade.reissue(tokens.refreshToken());

        assertThatThrownBy(() -> authFacade.reissue(tokens.refreshToken()))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.AUTH_REFRESH_TOKEN_MISMATCH.getMessage());
    }

    @Test
    void 로그아웃_성공() {
        memberCommandService.register(newMember());
        authFacade.login("testuser1", "Pass1!aa");
        Long memberId = memberJpaRepository.findAll().getFirst().getId();

        authFacade.logout(memberId);

        assertThat(refreshTokenJpaRepository.count()).isZero();
    }

    @Test
    void 로그아웃_후_재발급_실패() {
        memberCommandService.register(newMember());
        var tokens = authFacade.login("testuser1", "Pass1!aa");
        Long memberId = memberJpaRepository.findAll().getFirst().getId();
        authFacade.logout(memberId);

        assertThatThrownBy(() -> authFacade.reissue(tokens.refreshToken()))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.AUTH_REFRESH_TOKEN_NOT_FOUND.getMessage());
    }

    private NewMember newMember() {
        return NewMember.builder()
                .loginId("testuser1")
                .password("Pass1!aa")
                .nickname("테스터")
                .build();
    }

}
