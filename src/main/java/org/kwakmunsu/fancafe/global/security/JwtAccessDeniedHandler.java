package org.kwakmunsu.fancafe.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
        @NonNull HttpServletRequest request,
        HttpServletResponse response,
        @NonNull AccessDeniedException accessDeniedException
    ) throws IOException {
        log.warn("[Auth] 접근 권한 없음: 요청한 리소스에 대한 권한이 없습니다");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

}