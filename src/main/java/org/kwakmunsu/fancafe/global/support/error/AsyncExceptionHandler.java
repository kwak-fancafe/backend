package org.kwakmunsu.fancafe.global.support.error;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.logging.LogLevel;

/**
 * 비동기 작업 중 발생한 예외를 처리하는 핸들러
 *
 * <p>@Async 메서드에서 발생한 예외를 잡아서 로깅합니다.
 * AsyncConfig 에서 이 핸들러를 등록하여 사용합니다.
 */
@RequiredArgsConstructor
@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(@NonNull Throwable throwable, @NonNull Method method, Object @NonNull ... params) {
        if (throwable instanceof CoreException e) {
            String logMessage = String.format("비동기 작업 중 CoreException 발생 - Method: %s, ErrorType: %s, Message: %s",
                    method.getName(),
                    e.getErrorType().name(),
                    e.getMessage()
            );

            switch (e.getErrorType().getLogLevel()) {
                case LogLevel.ERROR -> log.error(logMessage, e);
                case LogLevel.WARN ->  log.warn(logMessage, e);
                default ->             log.info(logMessage, e);
            }
        } else {
            String logMessage = String.format("비동기 작업 중 Exception 발생 - Method: %s, Error: %s",
                    method.getName(),
                    throwable.getMessage()
            );
            log.error("{} (argCount={})", logMessage, params.length, throwable);
        }
    }

}