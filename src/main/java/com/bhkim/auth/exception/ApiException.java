package com.bhkim.auth.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private ExceptionEnum e;

    public ApiException(ExceptionEnum e) {
        super(e.getErrorMessage());
        this.e = e;
    }

    public ExceptionEnum getException() {
        return e;
    }
}
