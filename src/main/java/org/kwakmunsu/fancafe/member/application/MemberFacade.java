package org.kwakmunsu.fancafe.member.application;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.member.application.dto.NewMember;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberFacade {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    public Long register(NewMember newMember) {
        Member member = memberCommandService.register(newMember);

        return member.getId();
    }

    public Member find(Long memberId) {
        return memberQueryService.getById(memberId);
    }

}