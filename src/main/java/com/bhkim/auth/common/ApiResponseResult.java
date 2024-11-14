package com.bhkim.auth.common;

import com.bhkim.auth.exception.ExceptionEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponseResult<T> {
    private final boolean success;
    private final T data;
    private final ApiError error;

    @Getter
    public static class ApiError {
        private String errorMessage;
        private String errorCode;

        public ApiError(String errorMessage, String errorCode) {
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
        }
    }

    public static <T> ApiResponseResult<T> success(T data) {
        return new ApiResponseResult<T>(true, data, null);
    }

    public static <T> ApiResponseResult<T> failure(ExceptionEnum e) {
        return new ApiResponseResult<T>(false, null, new ApiError(e.getErrorCode(), e.getErrorMessage()));
    }

    public static <T> ApiResponseResult<T> failure(String errorCode, String message ) {
        return new ApiResponseResult<T>(false, null, new ApiError(errorCode, message));
    }

}

