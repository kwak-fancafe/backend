package org.kwakmunsu.fancafe.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.UnitTestSupport;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;

class NicknameTest extends UnitTestSupport {

    @Test
    void 닉네임_생성_성공_한글() {
        var nickname = new Nickname("홍길동");

        assertThat(nickname.value()).isEqualTo("홍길동");
    }

    @Test
    void 닉네임_생성_성공_영문() {
        var nickname = new Nickname("userNick");

        assertThat(nickname.value()).isEqualTo("userNick");
    }

    @Test
    void 닉네임_생성_실패_null값() {
        assertThatThrownBy(() -> new Nickname(null))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_NICKNAME.getMessage());
    }

    @Test
    void 닉네임_생성_실패_최소길이_미만() {
        assertThatThrownBy(() -> new Nickname("가"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_NICKNAME.getMessage());
    }

    @Test
    void 닉네임_생성_실패_최대길이_초과() {
        var tooLong = "가".repeat(Nickname.MAX_LENGTH + 1);

        assertThatThrownBy(() -> new Nickname(tooLong))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_NICKNAME.getMessage());
    }

}
