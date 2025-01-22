package com.bhkim.auth.dto.condition;

import com.bhkim.auth.common.TypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserSearchCondition extends PagingCondition{
    private Long seq;
    private String id;
    private int minAge;
    private int maxAge;
    private TypeEnum sex;
}
