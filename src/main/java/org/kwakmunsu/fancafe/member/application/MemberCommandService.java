package org.kwakmunsu.fancafe.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.fancafe.global.support.EntityStatus;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.application.dto.NewMember;
import org.kwakmunsu.fancafe.member.domain.LoginId;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.kwakmunsu.fancafe.member.domain.Nickname;
import org.kwakmunsu.fancafe.member.domain.PasswordEncoder;
import org.kwakmunsu.fancafe.member.infrastructure.MemberJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberCommandService {

    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public Member register(NewMember newMember) {
        validateNoDuplicates(newMember.loginId(), newMember.nickname());

        Member member;
        try {
            member = memberJpaRepository.save(Member.register(
                    newMember.loginId(),
                    newMember.password(),
                    newMember.nickname(),
                    passwordEncoder
            ));
        } catch (DataIntegrityViolationException e) {
            // validateNoDuplicates() 통과 후 동시 요청으로 인한 유니크 제약 충돌 — 409로 변환
            throw new CoreException(ErrorType.DEFAULT_DUPLICATE);
        }

        log.info("[MemberCommandService] 회원 등록 완료 - memberId: {}", member.getId());
        return member;
    }

    @Transactional
    public void updateProfile(Long memberId, String nickname) {
        if (memberJpaRepository.findByNickname(new Nickname(nickname)).isPresent()) {
            throw new CoreException(ErrorType.MEMBER_DUPLICATE_NICKNAME);
        }

        Member member = memberJpaRepository.findByIdAndStatus(memberId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CoreException(ErrorType.MEMBER_NOT_FOUND));

        member.changeNickname(nickname);

        log.info("[MemberCommandService] 회원 프로필 업데이트 완료 - memberId: {}", member.getId());
    }

    // NOTE: loginId나 nickname 중복은 삭제된 회원이 존재하는 경우에도 발생할 수 있으므로, 삭제된 회원도 포함하여 중복 체크
    private void validateNoDuplicates(String loginId, String nickname) {
        if (memberJpaRepository.findByLoginId(new LoginId(loginId)).isPresent()) {
            throw new CoreException(ErrorType.MEMBER_DUPLICATE_LOGIN_ID);
        }
        if (memberJpaRepository.findByNickname(new Nickname(nickname)).isPresent()) {
            throw new CoreException(ErrorType.MEMBER_DUPLICATE_NICKNAME);
        }
    }
}