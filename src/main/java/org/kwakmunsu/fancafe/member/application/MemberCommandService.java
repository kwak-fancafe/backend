package org.kwakmunsu.fancafe.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.application.dto.NewMember;
import org.kwakmunsu.fancafe.member.domain.LoginId;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.kwakmunsu.fancafe.member.domain.Nickname;
import org.kwakmunsu.fancafe.member.domain.PasswordEncoder;
import org.kwakmunsu.fancafe.member.infrastructure.MemberJpaRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberCommandService {

    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public Member register(NewMember newMember) {
        validateNoDuplicates(newMember.loginId(), newMember.nickname());

        Member member = memberJpaRepository.save(Member.register(
                newMember.loginId(),
                newMember.password(),
                newMember.nickname(),
                passwordEncoder
        ));

        log.info("[MemberCommandService] 회원 등록 완료 - memberId: {}", member.getId());
        return member;
    }

    private void validateNoDuplicates(String loginId, String nickname) {
        if (memberJpaRepository.findByLoginId(new LoginId(loginId)).isPresent()) {
            throw new CoreException(ErrorType.MEMBER_DUPLICATE_LOGIN_ID);
        }
        if (memberJpaRepository.findByNickname(new Nickname(nickname)).isPresent()) {
            throw new CoreException(ErrorType.MEMBER_DUPLICATE_NICKNAME);
        }
    }

}