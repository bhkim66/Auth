package com.bhkim.auth.service;

import com.bhkim.auth.dto.AuthDto;
import com.bhkim.auth.dto.MemberDto;
import com.bhkim.auth.common.ApiResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService{

    @Override
    public ApiResponseResult<AuthDto.Token> SignIn(MemberDto.MemberInfo memberInfo) {
        return null;
    }

    @Override
    public ApiResponseResult<HttpStatus> SignOut() {
        return null;
    }

    @Override
    public ApiResponseResult<HttpStatus> SignUp(MemberDto.MemberInfo memberInfo) {
        return null;
    }

    @Override
    public ApiResponseResult<AuthDto.Token> reissueToken() {
        return null;
    }

    @Override
    public ApiResponseResult<MemberDto.MemberInfo> ValidationToken() {
        return null;
    }
}
