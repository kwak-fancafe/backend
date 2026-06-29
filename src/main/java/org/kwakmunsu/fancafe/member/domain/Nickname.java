package org.kwakmunsu.fancafe.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;

@Embeddable
public record Nickname(
        @Column(name = "nickname", nullable = false, unique = true)
        String value
) {

    public static final int MIN_LENGTH = 2;
    public static final int MAX_LENGTH = 20;

    public Nickname {
        if (value == null || value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new CoreException(ErrorType.MEMBER_INVALID_NICKNAME);
        }
    }

}