package org.kwakmunsu.fancafe.member.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.global.support.response.ApiResponse;
import org.kwakmunsu.fancafe.member.application.MemberFacade;
import org.kwakmunsu.fancafe.member.presentation.dto.MemberRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController extends MemberControllerDocs {

    private final MemberFacade memberFacade;

    @PostMapping("/api/v1/members/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> register(@RequestBody @Valid MemberRegisterRequest request) {
        Long memberId = memberFacade.register(request.toNewMember());

        return ApiResponse.success(memberId);
    }

}