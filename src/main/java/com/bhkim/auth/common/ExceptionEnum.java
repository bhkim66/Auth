package com.bhkim.auth.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@ToString
public enum ExceptionEnum {
    ILLEGAL_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "E400", "잘못된 인수값이 전달 됐습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "예기치 못한 오류가 발생 했습니다"),
    IO_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "E510", "잘못된 값 입니다"),

    METHOD_ARGUMENT_NOT_VALID_ERROR(HttpStatus.BAD_REQUEST, "E510", "잘못된 값 입니다"),




    DATABASE_INSERT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E505", "데이터 INSERT ERROR 발생")

    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;
}
