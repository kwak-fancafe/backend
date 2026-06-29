package org.kwakmunsu.fancafe.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.IntegrationTestSupport;
import org.kwakmunsu.fancafe.fixture.MemberFixture;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.application.dto.NewMember;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.kwakmunsu.fancafe.member.domain.MemberStatus;
import org.kwakmunsu.fancafe.member.domain.Role;
import org.kwakmunsu.fancafe.member.infrastructure.MemberJpaRepository;

@RequiredArgsConstructor
class MemberCommandServiceTest extends IntegrationTestSupport {

    final MemberCommandService memberCommandService;
    final MemberJpaRepository memberJpaRepository;

    @AfterEach
    void tearDown() {
        memberJpaRepository.deleteAll();
    }

    @Test
    void 회원가입_성공() {
        var newMember = NewMember.builder()
                .loginId("newuser1")
                .password("Pass1!aa")
                .nickname("새유저")
                .build();

        var member = memberCommandService.register(newMember);

        assertThat(member.getId()).isNotNull();
        assertThat(member).extracting(
                Member::getRole,
                Member::getMemberStatus
        ).containsExactly(Role.FAN, MemberStatus.ACTIVE);
    }

    @Test
    void 회원가입_실패_loginId_중복() {
        memberJpaRepository.save(MemberFixture.member());

        var newMember = NewMember.builder()
                .loginId(MemberFixture.LOGIN_ID)
                .password("Pass1!aa")
                .nickname("다른닉네임")
                .build();

        assertThatThrownBy(() -> memberCommandService.register(newMember))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_DUPLICATE_LOGIN_ID.getMessage());
    }

    @Test
    void 회원가입_실패_nickname_중복() {
        memberJpaRepository.save(MemberFixture.member());

        var newMember = NewMember.builder()
                .loginId("otheruser1")
                .password("Pass1!aa")
                .nickname(MemberFixture.NICKNAME)
                .build();

        assertThatThrownBy(() -> memberCommandService.register(newMember))
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.MEMBER_DUPLICATE_NICKNAME.getMessage());
    }

}