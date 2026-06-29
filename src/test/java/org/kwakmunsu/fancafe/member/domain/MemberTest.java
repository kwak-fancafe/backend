package org.kwakmunsu.fancafe.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.UnitTestSupport;
import org.kwakmunsu.fancafe.fixture.MemberFixture;
import org.kwakmunsu.fancafe.global.support.EntityStatus;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;

class MemberTest extends UnitTestSupport {

    @Test
    void 회원_등록_성공() {
        var member = MemberFixture.member();

        assertThat(member).extracting(
                Member::getRole,
                Member::getMemberStatus,
                Member::getEntityStatus
        ).containsExactly(
                Role.FAN,
                MemberStatus.ACTIVE,
                EntityStatus.ACTIVE
        );
    }

    @Test
    void 회원_정지_성공() {
        var member = MemberFixture.member();

        member.ban();

        assertThat(member).extracting(
                Member::getMemberStatus,
                Member::getEntityStatus
        ).containsExactly(
                MemberStatus.BANNED,
                EntityStatus.ACTIVE
        );
    }

    @Test
    void 회원_정지_해제_성공() {
        var member = MemberFixture.bannedMember();

        member.unban();

        assertThat(member.getMemberStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    void 회원_탈퇴_성공() {
        var member = MemberFixture.member();

        member.withdraw();

        assertThat(member).extracting(
                Member::getMemberStatus,
                Member::getEntityStatus
        ).containsExactly(
                MemberStatus.WITHDRAWN,
                EntityStatus.DELETED
        );
        assertThat(member.getDeletedAt()).isNotNull();
    }

    @Test
    void 회원_정지_실패_이미_정지된_회원() {
        var member = MemberFixture.bannedMember();

        assertThatThrownBy(member::ban)
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_CANNOT_BAN.getMessage());
    }

    @Test
    void 회원_정지_실패_탈퇴한_회원() {
        var member = MemberFixture.withdrawnMember();

        assertThatThrownBy(member::ban)
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_CANNOT_BAN.getMessage());
    }

    @Test
    void 회원_정지_해제_실패_정지_상태가_아닌_회원() {
        var member = MemberFixture.member();

        assertThatThrownBy(member::unban)
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_CANNOT_UNBAN.getMessage());
    }

    @Test
    void 회원_탈퇴_실패_이미_탈퇴한_회원() {
        var member = MemberFixture.withdrawnMember();

        assertThatThrownBy(member::withdraw)
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_CANNOT_WITHDRAW.getMessage());
    }

    @Test
    void 역할_변경_성공() {
        Member member = MemberFixture.member();

        member.changeRole(Role.MANAGER);

        assertThat(member.getRole()).isEqualTo(Role.MANAGER);
    }

    @Test
    void 닉네임_변경_성공() {
        var member = MemberFixture.member();

        member.changeNickname("새닉네임");

        assertThat(member.getNickname().value()).isEqualTo("새닉네임");
    }

    @Test
    void 닉네임_변경_실패_형식_위반() {
        var member = MemberFixture.member();

        assertThatThrownBy(() -> member.changeNickname("가"))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_INVALID_NICKNAME.getMessage());
    }

    @Test
    void 프로필_이미지_변경_성공() {
        var member = MemberFixture.member();

        member.updateProfileImage("https://s3.example.com/profile.jpg");

        assertThat(member.getProfileImageUrl()).isEqualTo("https://s3.example.com/profile.jpg");
    }

    @Test
    void 비밀번호_검증_성공() {
        var member = MemberFixture.member();

        assertThat(member.verifyPassword(MemberFixture.RAW_PASSWORD, MemberFixture.ENCODER)).isTrue();
    }

    @Test
    void 비밀번호_검증_실패() {
        var member = MemberFixture.member();

        assertThat(member.verifyPassword("Wrong1!aa", MemberFixture.ENCODER)).isFalse();
    }

}
