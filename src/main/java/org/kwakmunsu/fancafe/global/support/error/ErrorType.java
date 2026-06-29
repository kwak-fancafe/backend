package org.kwakmunsu.fancafe.global.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    // DEFAULT
    DEFAULT_BAD_REQUEST                 (HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다.", LogLevel.INFO),
    DEFAULT_METHOD_NOT_ALLOWED          (HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다.", LogLevel.WARN),
    DEFAULT_UNAUTHORIZED                (HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.", LogLevel.WARN),
    DEFAULT_FORBIDDEN                   (HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", LogLevel.WARN),
    DEFAULT_NOT_FOUND                   (HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다.", LogLevel.INFO),
    DEFAULT_DUPLICATE                   (HttpStatus.CONFLICT, "이미 존재하는 리소스입니다.", LogLevel.INFO),
    DEFAULT_SERVICE_NOT_YET_OPEN        (HttpStatus.FORBIDDEN, "서비스가 아직 오픈되지 않았습니다.", LogLevel.INFO),
    DEFAULT_TOO_MANY_REQUESTS           (HttpStatus.TOO_MANY_REQUESTS, "잠시 후 다시 시도해주세요.", LogLevel.INFO),
    DEFAULT_CONCURRENT_UPDATE_CONFLICT  (HttpStatus.CONFLICT, "요청하신 데이터가 이미 변경되었습니다. 새로고침 후 다시 시도해 주세요.", LogLevel.WARN),
    DEFAULT_LOCK_ACQUISITION_TIMEOUT    (HttpStatus.CONFLICT, "요청이 충돌했습니다. 잠시 후 다시 시도해 주세요.", LogLevel.WARN),
    DEFAULT_ERROR                       (HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", LogLevel.ERROR),


    // MEMBER
    MEMBER_INVALID_LOGIN_ID   (HttpStatus.BAD_REQUEST, "로그인 아이디 형식이 올바르지 않습니다.", LogLevel.INFO),
    MEMBER_INVALID_NICKNAME   (HttpStatus.BAD_REQUEST, "닉네임 형식이 올바르지 않습니다.", LogLevel.INFO),
    MEMBER_INVALID_PASSWORD   (HttpStatus.BAD_REQUEST, "비밀번호 형식이 올바르지 않습니다.", LogLevel.INFO),
    MEMBER_CANNOT_BAN         (HttpStatus.BAD_REQUEST, "정지할 수 없는 상태의 회원입니다.", LogLevel.WARN),
    MEMBER_CANNOT_UNBAN       (HttpStatus.BAD_REQUEST, "정지 해제할 수 없는 상태의 회원입니다.", LogLevel.WARN),
    MEMBER_CANNOT_WITHDRAW    (HttpStatus.BAD_REQUEST, "탈퇴할 수 없는 상태의 회원입니다.", LogLevel.WARN),
    MEMBER_DUPLICATE_LOGIN_ID (HttpStatus.CONFLICT, "이미 존재하는 로그인 아이디입니다.", LogLevel.INFO),
    MEMBER_DUPLICATE_NICKNAME (HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다.", LogLevel.INFO),

    // AUTH
    AUTH_EMPTY_TOKEN                    (HttpStatus.UNAUTHORIZED, "JWT 토큰이 존재하지 않습니다.", LogLevel.WARN),
    AUTH_INVALID_TOKEN                  (HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.", LogLevel.WARN),
    AUTH_EMPTY_SECURITY_CONTEXT         (HttpStatus.UNAUTHORIZED, "Security Context 에 인증 정보가 없습니다.", LogLevel.WARN),

    ;

    private final HttpStatus status;
    private final String message;
    private final LogLevel logLevel;

}