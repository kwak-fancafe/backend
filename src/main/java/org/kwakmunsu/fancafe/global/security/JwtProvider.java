package org.kwakmunsu.fancafe.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.fancafe.global.security.dto.TokenResponse;
import org.kwakmunsu.fancafe.global.security.enums.TokenExpiration;
import org.kwakmunsu.fancafe.global.security.enums.TokenType;
import org.kwakmunsu.fancafe.member.domain.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider {

    private static final String CATEGORY_KEY = "category";

    private final SecretKey secretKey;

    public JwtProvider(@Value("${spring.jwt.secretKey}") String key) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponse createTokens(Long memberId, Role role) {
        String accessToken = createAccessToken(memberId, role);
        String refreshToken = createRefreshToken(role);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return TokenType.isAccessToken(claims.get(CATEGORY_KEY, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);

        Long memberId = Long.parseLong(claims.getSubject());
        String role = getAuthority(claims).name();
        GrantedAuthority authority = new SimpleGrantedAuthority(role);

        return new UsernamePasswordAuthenticationToken(
                memberId,
                null,
                Collections.singletonList(authority)
        );
    }

    public boolean isTokenValid(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (SignatureException e) {
            log.warn("[Invalid JWT signature], 유효하지 않는 JWT 서명 입니다. Token prefix: {}", maskToken(token));
        } catch (MalformedJwtException e) {
            log.warn("[Invalid JWT malformed], 잘못된 형식의 JWT 입니다. Token prefix: {}", maskToken(token));
        } catch (ExpiredJwtException e) {
            log.info("[Expired JWT], 만료된 JWT 입니다. Token prefix: {}", maskToken(token));
        } catch (UnsupportedJwtException e) {
            log.warn("[Unsupported JWT], 지원되지 않는 JWT 입니다. Token prefix: {}", maskToken(token));
        } catch (IllegalArgumentException e) {
            log.warn("[JWT claims is empty], 잘못된 JWT 입니다. Token prefix: {}", maskToken(token));
        }
        return false;
    }

    private String createAccessToken(Long memberId, Role role) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = getTokenExpirationTime(now, TokenExpiration.ACCESS_TOKEN);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim(CATEGORY_KEY, TokenType.ACCESS.getValue())
                .claim(TokenType.AUTHORIZATION_HEADER.getValue(), role)
                .issuedAt(toDate(now))
                .expiration(toDate(expiry))
                .signWith(secretKey)
                .compact();
    }

    private LocalDateTime getTokenExpirationTime(LocalDateTime now, TokenExpiration expiration) {
        return now.plusSeconds(expiration.getExpirationTime() / 1000);
    }

    private String createRefreshToken(Role role) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = getTokenExpirationTime(now, TokenExpiration.REFRESH_TOKEN);

        return Jwts.builder()
                .claim(CATEGORY_KEY, TokenType.REFRESH.getValue())
                .claim(TokenType.AUTHORIZATION_HEADER.getValue(), role)
                .issuedAt(toDate(now))
                .expiration(toDate(expiry))
                .signWith(secretKey)
                .compact();
    }

    private Role getAuthority(Claims claims) {
        return Role.valueOf(claims.get(TokenType.AUTHORIZATION_HEADER.getValue(), String.class));
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return "***";
        }
        return token.substring(0, 10) + "..." + token.substring(token.length() - 5);
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}