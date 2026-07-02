package org.kwakmunsu.fancafe.community.post.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.fancafe.ControllerTestSupport;
import org.kwakmunsu.fancafe.community.post.domain.PostStatus;
import org.kwakmunsu.fancafe.community.post.presentation.dto.PostRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class PostControllerTest extends ControllerTestSupport {

    @Test
    void 게시물_등록_성공() {
        // when & then
        mvcTester.post()
                .uri("/api/v1/posts")
                .with(ControllerTestSupport.managerAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(new PostRegisterRequest("제목", "내용", PostStatus.PUBLIC, 1L)))
                .assertThat()
                .apply(print())
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"));


    }

}