package com.bhkim.auth.common;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AuthorityEnum {
    WRITE("WRITE"),
    READ("READ");



    private final String value;

    AuthorityEnum(String value) {
        this.value = value;
    }

    public static TypeEnum findByVal(String v) {
        return Arrays.stream(TypeEnum.values())
                .filter(a -> a.hasCode(v))
                .findAny()
                .orElse(null);
    }
}
