package org.kwakmunsu.fancafe.member.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.ControllerTestSupport;
import org.kwakmunsu.fancafe.member.application.MemberFacade;
import org.kwakmunsu.fancafe.member.presentation.dto.MemberRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class MemberControllerTest extends ControllerTestSupport {

    @MockitoBean
    private MemberFacade memberFacade;

    @Test
    void 회원가입_성공() {
        given(memberFacade.register(any())).willReturn(1L);

        var request = new MemberRegisterRequest("user01", "Pass1!aa", "홍길동");

        mvcTester.post()
                .uri("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .assertThat()
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"))
                .hasPathSatisfying("$.data", v -> v.assertThat().isEqualTo(1));

    }

}
