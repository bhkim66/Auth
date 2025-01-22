package com.bhkim.auth.dto.condition;

import lombok.Getter;

@Getter
public class PagingCondition {
    private final int offset = 0;
    private final int limit = 3;
}
