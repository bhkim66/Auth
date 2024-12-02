package com.bhkim.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * The enum Exception enum.
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum ExceptionEnum {
    ILLEGAL_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "E400", "잘못된 인수값이 전달 됐습니다"),

    /** 인증되지 않은 멤버 접근 */
    MEMBER_REQUIRED(HttpStatus.UNAUTHORIZED, "E401", "로그인이 필요한 서비스입니다"),

    // ID, 비밀번호 잘못 된 값 입력
    BAD_CREDENTIALS_EXCEPTION(HttpStatus.OK, "E411", "아이디 또는 비밀번호를 잘못 입력했습니다.\n" + "입력하신 내용을 다시 확인해주세요."),
    ILLEGAL_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "E412", "이전 비밀번호와 다른 비밀번호를 입력해주세요"),



    /** 잘못된 메일 인증 코드 */
    INVALID_MAIL_CODE_ERROR(HttpStatus.BAD_REQUEST, "E405", "잘못된 인증 코드 입니다"),

    /** 토큰이 유효하지 않을 때 or 로그아웃 된 토큰으로 인증 요청할 때 */
    INVALID_TOKEN_VALUE_ERROR(HttpStatus.UNAUTHORIZED, "E411", "유효하지 않은 토큰 입니다"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "예기치 못한 오류가 발생 했습니다"),
    DATABASE_INSERT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E505", "데이터 INSERT ERROR 발생"),
    DUPLICATION_VALUE_IN_DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E506", "중복된 값 입니다"),
    IO_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "E510", "잘못된 값 입니다"),


    /** Validation에서 오류 발생 시 */
    METHOD_ARGUMENT_NOT_VALID_ERROR(HttpStatus.BAD_REQUEST, "E511", "잘못된 입력 값 입니다"),

    ILLEGAL_TOKEN_VALUE(HttpStatus.BAD_REQUEST, "E512", "토큰의 값이 유효하지 않음"),




    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;
}
