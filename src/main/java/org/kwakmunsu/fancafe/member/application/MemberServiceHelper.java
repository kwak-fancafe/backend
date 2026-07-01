package org.kwakmunsu.fancafe.member.application;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.global.support.EntityStatus;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.kwakmunsu.fancafe.member.infrastructure.MemberJpaRepository;
import org.springframework.stereotype.Component;

/**
 * 회원 관련 도메인 서비스에서 공통적으로 사용되는 회원 조회 로직을 제공하는 헬퍼 클래스입니다.
 * */
@RequiredArgsConstructor
@Component
public class MemberServiceHelper {

    private final MemberJpaRepository memberJpaRepository;

    public Member find(Long memberId) {
        return memberJpaRepository.findByIdAndStatus(memberId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CoreException(ErrorType.MEMBER_NOT_FOUND));
    }

}