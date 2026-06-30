package org.kwakmunsu.fancafe.auth.infrastructure;

import java.util.Optional;
import org.kwakmunsu.fancafe.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

}
