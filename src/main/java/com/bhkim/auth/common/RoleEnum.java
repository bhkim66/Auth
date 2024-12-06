package com.bhkim.auth.common;

import java.util.Arrays;

public enum RoleEnum {
    SUPER_ADMIN("ROLE_SUPER_ADMIN"),
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");



    private final String name;

    RoleEnum(String name) {
        this.name = name;
    }

    public static TypeEnum findByVal(String v) {
        return Arrays.stream(TypeEnum.values())
                .filter(a -> a.hasCode(v))
                .findAny()
                .orElse(null);
    }
}
