package org.kwakmunsu.fancafe.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.fancafe.auth.domain.RefreshToken;
import org.kwakmunsu.fancafe.auth.infrastructure.RefreshTokenRepository;
import org.kwakmunsu.fancafe.global.security.JwtProvider;
import org.kwakmunsu.fancafe.global.security.dto.TokenResponse;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.domain.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthCommandService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenResponse issueTokens(Long memberId, Role role) {
        TokenResponse tokens = jwtProvider.createTokens(memberId, role);

        String rawRefreshToken = tokens.refreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId)
                .map(existing -> {
                    existing.rotate(rawRefreshToken);
                    return existing;
                })
                .orElse(RefreshToken.create(memberId, rawRefreshToken));
        refreshTokenRepository.save(refreshToken);

        log.info("[AuthCommandService] 토큰 발급 - memberId={}", memberId);
        return tokens;
    }

    @Transactional(readOnly = true)
    public Long validateRefreshToken(String rawRefreshToken) {
        if (!jwtProvider.isTokenValid(rawRefreshToken) || !jwtProvider.isRefreshToken(rawRefreshToken)) {
            throw new CoreException(ErrorType.AUTH_EXPIRED_REFRESH_TOKEN);
        }

        Long memberId = jwtProvider.getMemberIdFromToken(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CoreException(ErrorType.AUTH_REFRESH_TOKEN_NOT_FOUND));

        if (!storedToken.matches(rawRefreshToken)) {
            throw new CoreException(ErrorType.AUTH_REFRESH_TOKEN_MISMATCH);
        }

        return memberId;
    }

    @Transactional
    public void revoke(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
        log.info("[AuthCommandService] 토큰 폐기 - memberId={}", memberId);
    }

}
