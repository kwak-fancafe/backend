package org.kwakmunsu.fancafe.member.application;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.domain.LoginId;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.kwakmunsu.fancafe.member.domain.PasswordEncoder;
import org.kwakmunsu.fancafe.member.infrastructure.MemberJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberQueryService {

    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public Member authenticate(String loginId, String password) {
        Member member = memberJpaRepository.findByLoginId(new LoginId(loginId))
                .orElseThrow(() -> new CoreException(ErrorType.MEMBER_NOT_FOUND_ACCOUNT));

        if (member.verifyPassword(password, passwordEncoder)) return member;

        throw new CoreException(ErrorType.MEMBER_NOT_FOUND_ACCOUNT);
    }

    public Member getById(Long memberId) {
        return memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new CoreException(ErrorType.MEMBER_NOT_FOUND_ACCOUNT));
    }

}
