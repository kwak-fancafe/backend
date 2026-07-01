package org.kwakmunsu.fancafe.member.infrastructure;

import java.util.Optional;
import org.kwakmunsu.fancafe.global.support.EntityStatus;
import org.kwakmunsu.fancafe.member.domain.LoginId;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.kwakmunsu.fancafe.member.domain.Nickname;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(LoginId loginId);
    Optional<Member> findByNickname(Nickname nickname);

    Optional<Member> findByIdAndStatus(Long id, EntityStatus status);
}
