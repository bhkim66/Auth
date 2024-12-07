package com.bhkim.auth.common;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleEnum {
    SUPER_ADMIN("ROLE_SUPER_ADMIN"),
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");



    private final String value;

    RoleEnum(String value) {
        this.value = value;
    }

    public static TypeEnum findByVal(String v) {
        return Arrays.stream(TypeEnum.values())
                .filter(a -> a.hasCode(v))
                .findAny()
                .orElse(null);
    }
}
