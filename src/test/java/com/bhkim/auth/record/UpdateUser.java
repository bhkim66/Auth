package com.bhkim.auth.record;

import com.bhkim.auth.common.TypeEnum;

public record UpdateUser(String name, int age, TypeEnum sex, String phoneNumber) {
    public UpdateUser {
    }
}
