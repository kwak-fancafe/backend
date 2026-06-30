package org.kwakmunsu.fancafe.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.UnitTestSupport;

class RefreshTokenTest extends UnitTestSupport {

    @Test
    void 리프레시_토큰_생성_성공() {
        var token = RefreshToken.create(1L, "rawToken");

        assertThat(token.getMemberId()).isEqualTo(1L);
        assertThat(token.getTokenHash()).isNotEqualTo("rawToken");
        assertThat(token.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void 동일한_토큰은_matches가_true를_반환한다() {
        var token = RefreshToken.create(1L, "rawToken");

        assertThat(token.matches("rawToken")).isTrue();
    }

    @Test
    void 다른_토큰은_matches가_false를_반환한다() {
        var token = RefreshToken.create(1L, "rawToken");

        assertThat(token.matches("otherToken")).isFalse();
    }

    @Test
    void 토큰_교체_성공() {
        var token = RefreshToken.create(1L, "oldToken");

        token.rotate("newToken");

        assertThat(token.matches("newToken")).isTrue();
        assertThat(token.matches("oldToken")).isFalse();
        assertThat(token.getExpiresAt()).isAfter(LocalDateTime.now());
    }

}
