package org.kwakmunsu.fancafe.global.security;

import static org.kwakmunsu.fancafe.global.security.enums.TokenType.AUTHORIZATION_HEADER;
import static org.kwakmunsu.fancafe.global.security.enums.TokenType.BEARER_PREFIX;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.global.support.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JsonMapper jsonMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return Arrays.stream(SecurityPaths.permitAll()) // PermitAll
                .anyMatch(pattern -> pathMatcher.match(pattern, path))
                || Arrays.stream(SecurityPaths.actuatorPermit()) // PermitAll
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> tokenOpt = getTokenFromHeader(request);

        if (tokenOpt.isEmpty()) {
            sendErrorResponse(response, ErrorType.AUTH_EMPTY_TOKEN);
            return;
        }

        String token = tokenOpt.get();
        if (!jwtProvider.isTokenValid(token)) {
            sendErrorResponse(response, ErrorType.AUTH_INVALID_TOKEN);
            return;
        }

        if (!jwtProvider.isAccessToken(token)) {
            log.warn("[JWTFilter] Access Token이 아닌 토큰이 전달되었습니다. token: {}", token);
            sendErrorResponse(response, ErrorType.AUTH_INVALID_TOKEN);
            return;
        }

        Authentication authentication = jwtProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER.getValue());

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX.getValue())) {
            String token = bearerToken.substring(BEARER_PREFIX.getValue().length());

            return Optional.of(token);
        }

        return Optional.empty();
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorType errorType) throws IOException {
        log.warn("[Auth] Jwt 인증 처리 중 예외 발생: {}", errorType.getMessage());

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorType.getStatus().value());

        String json = jsonMapper.writeValueAsString(ApiResponse.error(errorType));
        response.getWriter().write(json);
    }

}