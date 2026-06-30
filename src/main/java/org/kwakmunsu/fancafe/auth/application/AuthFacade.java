package org.kwakmunsu.fancafe.auth.application;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.global.security.dto.TokenResponse;
import org.kwakmunsu.fancafe.member.application.MemberQueryService;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthFacade {

    private final MemberQueryService memberQueryService;
    private final AuthCommandService authCommandService;

    public TokenResponse login(String loginId, String password) {
        Member member = memberQueryService.authenticate(loginId, password);

        return authCommandService.issueTokens(member.getId(), member.getRole());
    }

    public TokenResponse reissue(String rawRefreshToken) {
        Long memberId = authCommandService.validateRefreshToken(rawRefreshToken);
        Member member = memberQueryService.getById(memberId);

        return authCommandService.issueTokens(member.getId(), member.getRole());
    }

    public void logout(Long memberId) {
        authCommandService.revoke(memberId);
    }

}