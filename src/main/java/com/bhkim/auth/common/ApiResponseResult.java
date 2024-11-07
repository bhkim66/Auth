package com.bhkim.auth.common;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponseResult<T> {
    private final boolean success;
    private final T data;
    private final ExceptionEnum e;

    public static <T> ApiResponseResult<T> success(T data) {
        return new ApiResponseResult<T>(true, data, null);
    }

    public static <T> ApiResponseResult<T> failure(ExceptionEnum e) {
        return new ApiResponseResult<T>(true, null, e);
    }
}

