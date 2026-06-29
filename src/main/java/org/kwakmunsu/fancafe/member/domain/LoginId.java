package org.kwakmunsu.fancafe.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;

@Embeddable
public record LoginId(
        @Column(name = "login_id", nullable = false, unique = true)
        String value
) {
    public static final int MIN_LENGTH = 4;
    public static final int MAX_LENGTH = 20;
    private static final String REGEX = "^[a-zA-Z0-9]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$";

    public LoginId {
        if (value == null || !value.matches(REGEX)) {
            throw new CoreException(ErrorType.MEMBER_INVALID_LOGIN_ID);
        }
    }

}