package com.bhkim.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionEnum {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "예기치 못한 오류가 발생했습니다")

    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;
}
