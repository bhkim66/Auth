package com.bhkim.auth.service;

import com.bhkim.auth.dto.MemberDto;
import com.bhkim.auth.common.ApiResponseResult;

public interface MemberService {
    ApiResponseResult<MemberDto.MemberInfo> getMemberInfo();


}
