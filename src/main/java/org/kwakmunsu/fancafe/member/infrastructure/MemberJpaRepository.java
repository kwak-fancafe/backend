package org.kwakmunsu.fancafe.member.infrastructure;

import org.kwakmunsu.fancafe.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}
