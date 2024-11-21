package com.bhkim.auth.common;

import java.util.Arrays;

public enum Role {
    SUPER_ADMIN("role_superAdmin"),
    ADMIN("role_admin"),
    USER("role_user");



    private final String name;

    Role(String name) {
        this.name = name;
    }

    public static TypeEnum findByVal(String v) {
        return Arrays.stream(TypeEnum.values())
                .filter(a -> a.hasCode(v))
                .findAny()
                .orElse(null);
    }
}
