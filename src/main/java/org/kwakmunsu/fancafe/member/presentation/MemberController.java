package org.kwakmunsu.fancafe.member.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.global.security.annotation.LoginMember;
import org.kwakmunsu.fancafe.global.support.response.ApiResponse;
import org.kwakmunsu.fancafe.member.application.MemberFacade;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.kwakmunsu.fancafe.member.presentation.dto.MemberProfile;
import org.kwakmunsu.fancafe.member.presentation.dto.MemberProfileUpdateRequest;
import org.kwakmunsu.fancafe.member.presentation.dto.MemberRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @GetMapping("/api/v1/members/me")
    public ApiResponse<MemberProfile> getMemberProfile(@LoginMember Long memberId) {
        Member member = memberFacade.find(memberId);

        return ApiResponse.success(MemberProfile.from(member));
    }

    @PatchMapping(value = "/api/v1/members")
    public ApiResponse<Void> updateMemberProfile(
            @LoginMember Long memberId,
            @RequestBody @Valid MemberProfileUpdateRequest request
    ) {
        memberFacade.updateProfile(memberId,request.nickname());

        return ApiResponse.success();
    }

}