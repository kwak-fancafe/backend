package org.kwakmunsu.fancafe.community.post.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.community.post.application.PostFacade;
import org.kwakmunsu.fancafe.community.post.presentation.dto.PostRegisterRequest;
import org.kwakmunsu.fancafe.global.security.annotation.LoginMember;
import org.kwakmunsu.fancafe.global.support.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController extends PostControllerDocs {

    private final PostFacade postFacade;

    @PostMapping("/api/v1/posts")
    public ApiResponse<Void> register(
            @LoginMember Long memberId,
            @RequestBody @Valid PostRegisterRequest request
    ) {
        postFacade.register(request.newPost(), memberId);

        return ApiResponse.success();
    }

}
