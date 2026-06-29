package org.kwakmunsu.fancafe.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.UnitTestSupport;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;

class LoginIdTest extends UnitTestSupport {

    @Test
    void 로그인_아이디_생성_성공() {
        var loginId = new LoginId("user01");

        assertThat(loginId.value()).isEqualTo("user01");
    }

    @Test
    void 로그인_아이디_생성_실패_null값() {
        assertThatThrownBy(() -> new LoginId(null))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_LOGIN_ID.getMessage());
    }

    @Test
    void 로그인_아이디_생성_실패_최소길이_미만() {
        assertThatThrownBy(() -> new LoginId("abc"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_LOGIN_ID.getMessage());
    }

    @Test
    void 로그인_아이디_생성_실패_최대길이_초과() {
        var tooLong = "a".repeat(LoginId.MAX_LENGTH + 1);

        assertThatThrownBy(() -> new LoginId(tooLong))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_LOGIN_ID.getMessage());
    }

    @Test
    void 로그인_아이디_생성_실패_한글포함() {
        assertThatThrownBy(() -> new LoginId("홍길동01"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_LOGIN_ID.getMessage());
    }

    @Test
    void 로그인_아이디_생성_실패_특수문자포함() {
        assertThatThrownBy(() -> new LoginId("user!@"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_LOGIN_ID.getMessage());
    }

}
