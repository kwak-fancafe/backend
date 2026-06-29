package org.kwakmunsu.fancafe.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;

@Embeddable
public record Nickname(
        @Column(name = "nickname", nullable = false, unique = true)
        String value
) {

    public static final int MIN_LENGTH = 2;
    public static final int MAX_LENGTH = 20;

    // 한글 완성형·영문·숫자·밑줄만 허용 — HTML 특수문자 및 제어문자 차단
    private static final Pattern ALLOWED = Pattern.compile("^[가-힣a-zA-Z0-9_]+$");

    public Nickname {
        if (value == null) {
            throw new CoreException(ErrorType.MEMBER_INVALID_NICKNAME);
        }
        value = value.strip();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH || !ALLOWED.matcher(value).matches()) {
            throw new CoreException(ErrorType.MEMBER_INVALID_NICKNAME);
        }
    }

}