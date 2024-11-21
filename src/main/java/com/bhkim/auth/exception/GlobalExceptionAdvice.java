package com.bhkim.auth.exception;

import com.bhkim.auth.common.ApiResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseResult<?>> handleException(Exception e) {
        log.error("[Exception] cause ={}, message ={}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        ExceptionEnum error = ExceptionEnum.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(error.getStatus()).body(ApiResponseResult.failure(error));
    }

    // Custom 오류에 대한 처리
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponseResult<?>> handleApiException(ApiException e) {
        log.error("[ApiException] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        return ResponseEntity.status(e.getException().getStatus()).body(ApiResponseResult.failure(e.getException()));
    }

    //메소드가 잘못되었거나 부적합한 인수를 전달했을 경우 -> 필수 파라미터 없을 때
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseResult<?>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("[IllegalArgumentException] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        ExceptionEnum error = ExceptionEnum.ILLEGAL_ARGUMENT_ERROR;
        return ResponseEntity.status(error.getStatus()).body(ApiResponseResult.failure(error));
    }

    //requestbody에 잘못된 값이 들어왔을 때
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponseResult<?>> handleIOException(IOException e) {
        log.error("[IOException] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        ExceptionEnum error = ExceptionEnum.IO_ARGUMENT_ERROR;
        return ResponseEntity.status(error.getStatus()).body(ApiResponseResult.failure(error));
    }

    //@Valid 유효성 검사에서 예외가 발생했을 때 -> requestbody에 잘못 들어왔을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseResult<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("[MethodArgumentNotValidException] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        ExceptionEnum error = ExceptionEnum.METHOD_ARGUMENT_NOT_VALID_ERROR;
        String message = Optional.ofNullable(e.getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("");
        return ResponseEntity.status(error.getStatus()).body(ApiResponseResult.failure(error.getErrorCode(), message));
    }
}
