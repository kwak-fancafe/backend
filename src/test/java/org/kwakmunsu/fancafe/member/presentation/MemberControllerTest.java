package org.kwakmunsu.fancafe.member.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.ControllerTestSupport;
import org.kwakmunsu.fancafe.fixture.MemberFixture;
import org.kwakmunsu.fancafe.member.presentation.dto.MemberRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class MemberControllerTest extends ControllerTestSupport {

    @Test
    void 회원가입_성공() {
        given(memberFacade.register(any())).willReturn(1L);

        var request = new MemberRegisterRequest("user01", "Pass1!aa", "홍길동");

        mvcTester.post()
                .uri("/api/v1/members/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .assertThat()
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"))
                .hasPathSatisfying("$.data", v -> v.assertThat().isEqualTo(1));

    }

    @Test
    void 회원_프로필_조회_성공() {
        // given
        var member = MemberFixture.member();
        given(memberFacade.find(any(Long.class))).willReturn(member);

        // when
        mvcTester.get()
                .uri("/api/v1/members/me")
                .with(ControllerTestSupport.fanAuth())
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"))
                .hasPathSatisfying("$.data.nickname", v -> v.assertThat().isEqualTo(member.getNickname().value()))
                .hasPathSatisfying("$.data.loginId", v -> v.assertThat().isEqualTo(member.getLoginId().value()))
                .hasPathSatisfying("$.data.role", v -> v.assertThat().isEqualTo(member.getRole().name()));
    }


}
