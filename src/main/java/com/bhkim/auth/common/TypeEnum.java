package com.bhkim.auth.common;

import java.util.Arrays;
import java.util.List;

import static com.bhkim.auth.common.ConstDef.*;


public enum TypeEnum {
    M(Arrays.asList(SEX_TYPE_MALE)),
    F(Arrays.asList(SEX_TYPE_FEMALE)),

    PENDING(Arrays.asList(PENDING_STATUS)),
    CERTIFIED(Arrays.asList(CERTIFIED_STATUS))
    ;

    private List<String> titleList;

    TypeEnum(List<String> titleList) {
        this.titleList = titleList;
    }

    public static TypeEnum findByVal(String v) {
        return Arrays.stream(TypeEnum.values())
                .filter(a -> a.hasCode(v))
                .findAny()
                .orElse(null);
    }

    public boolean hasCode(String val) {
        return titleList.stream().anyMatch(a -> a.equals(val));
    }
}
