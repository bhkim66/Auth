package com.bhkim.auth.common;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private ExceptionEnum e;

    public ApiException(ExceptionEnum e) {
        super(e.getErrorMessage());
        this.e = e;
    }

    public ExceptionEnum getExceptionEnum() {
        return e;
    }
}
