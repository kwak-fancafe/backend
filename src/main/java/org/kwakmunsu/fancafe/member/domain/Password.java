package org.kwakmunsu.fancafe.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;

@Embeddable
public record Password(
        @Column(name = "password", nullable = false)
        String hashedValue
) {

    public static final int MIN_LENGTH = 8;
    private static final String REGEX =
            "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+]).{" + MIN_LENGTH + ",}$";

    public static Password create(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword == null || !rawPassword.matches(REGEX)) {
            throw new CoreException(ErrorType.MEMBER_INVALID_PASSWORD);
        }
        return new Password(encoder.encode(rawPassword));
    }

    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.hashedValue);
    }

}