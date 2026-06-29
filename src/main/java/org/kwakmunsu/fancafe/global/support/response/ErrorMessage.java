package org.kwakmunsu.fancafe.global.support.response;

import org.kwakmunsu.fancafe.global.support.error.ErrorType;

public record ErrorMessage(
        String code,
        String message,
        Object data
) {

    public ErrorMessage(ErrorType errorType) {
        this(errorType.name(), errorType.getMessage(), null);
    }

    public ErrorMessage(ErrorType errorType, Object data) {
        this(errorType.name(), errorType.getMessage(), data);
    }

}