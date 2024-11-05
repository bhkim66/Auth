package com.bhkim.auth.service;

import com.bhkim.auth.dto.MemberDto;
import com.bhkim.auth.entity.ApiResponseResult;

public interface MemberService {
    ApiResponseResult<MemberDto.MemberInfo> getMemberInfo();
}
