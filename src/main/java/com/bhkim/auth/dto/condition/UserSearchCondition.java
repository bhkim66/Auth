package com.bhkim.auth.dto.condition;

import com.bhkim.auth.common.TypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchCondition {
    private String id;
    private int minAge;
    private int maxAge;
    private TypeEnum sex;
}
