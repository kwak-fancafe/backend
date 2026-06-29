package org.kwakmunsu.fancafe.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.UnitTestSupport;
import org.kwakmunsu.fancafe.fixture.MemberFixture;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;

class PasswordTest extends UnitTestSupport {

    @Test
    void 비밀번호_생성_성공() {
        var password = Password.create("Pass1!aa", MemberFixture.ENCODER);

        assertThat(password.hashedValue()).isNotNull();
    }

    @Test
    void 비밀번호_생성_실패_null값() {
        assertThatThrownBy(() -> Password.create(null, MemberFixture.ENCODER))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_PASSWORD.getMessage());
    }

    @Test
    void 비밀번호_생성_실패_최소길이_미만() {
        assertThatThrownBy(() -> Password.create("P1!aaaa", MemberFixture.ENCODER))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_PASSWORD.getMessage());
    }

    @Test
    void 비밀번호_생성_실패_숫자_없음() {
        assertThatThrownBy(() -> Password.create("Password!", MemberFixture.ENCODER))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_PASSWORD.getMessage());
    }

    @Test
    void 비밀번호_생성_실패_특수문자_없음() {
        assertThatThrownBy(() -> Password.create("Password1", MemberFixture.ENCODER))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_PASSWORD.getMessage());
    }

    @Test
    void 비밀번호_생성_실패_영문_없음() {
        assertThatThrownBy(() -> Password.create("12345678!", MemberFixture.ENCODER))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_PASSWORD.getMessage());
    }

    @Test
    void 비밀번호_일치_검증_성공() {
        var rawPassword = "Pass1!aa";
        var password = Password.create(rawPassword, MemberFixture.ENCODER);

        assertThat(password.matches(rawPassword, MemberFixture.ENCODER)).isTrue();
    }

    @Test
    void 비밀번호_일치_검증_실패() {
        var password = Password.create("Pass1!aa", MemberFixture.ENCODER);

        assertThat(password.matches("Wrong1!aa", MemberFixture.ENCODER)).isFalse();
    }

}
