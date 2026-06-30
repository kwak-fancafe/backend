package org.kwakmunsu.fancafe.auth.infrastructure;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.auth.domain.RefreshToken;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public void save(RefreshToken refreshToken) {
        refreshTokenJpaRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByMemberId(Long memberId) {
        return refreshTokenJpaRepository.findByMemberId(memberId);
    }

    public void deleteByMemberId(Long memberId) {
        refreshTokenJpaRepository.deleteByMemberId(memberId);
    }

}