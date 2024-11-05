package com.bhkim.auth.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponseResult<T> {
    private boolean success;
    private T data;
    private ExceptionEnum e;

    @Builder
    public ApiResponseResult(boolean success, T data, ExceptionEnum e) {
        this.success = success;
        this.data = data;
        this.e = e;
    }

    public static <T> ApiResponseResult<T> success(T data) {
        return ApiResponseResult.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponseResult<T> failure(ExceptionEnum e) {
        return ApiResponseResult.<T>builder()
                .success(false)
                .e(e)
                .build();
    }
}

