package org.kwakmunsu.fancafe.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.fancafe.global.security.TokenHasher;
import org.kwakmunsu.fancafe.global.security.enums.TokenExpiration;
import org.kwakmunsu.fancafe.global.support.BaseEntity;

@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long memberId;

    @Column(nullable = false)
    private String tokenHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public static RefreshToken create(Long memberId, String rawToken) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.memberId = memberId;
        refreshToken.tokenHash = TokenHasher.hash(rawToken);
        refreshToken.expiresAt = LocalDateTime.now().plusSeconds(TokenExpiration.REFRESH_TOKEN.getExpirationTime() / 1000);
        return refreshToken;
    }

    public boolean matches(String rawToken) {
        return this.tokenHash.equals(TokenHasher.hash(rawToken));
    }

    public void rotate(String newRawToken) {
        this.tokenHash = TokenHasher.hash(newRawToken);
        this.expiresAt = LocalDateTime.now().plusSeconds(TokenExpiration.REFRESH_TOKEN.getExpirationTime() / 1000);
    }

}