package com.bhkim.auth.record;

import com.bhkim.auth.common.TypeEnum;

public record SignUpRequest(String id, String password, String name, int age, TypeEnum sex, String phoneNumber) {
    public SignUpRequest {
    }
}
