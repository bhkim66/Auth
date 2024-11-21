package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.AuthDto;
import com.bhkim.auth.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
//    private final AuthRepository authRepository;

    @Override
    public ApiResponseResult<AuthDto.Token> signIn(UserDto.UserInfo userInfo) {



        return null;
    }

    @Override
    public ApiResponseResult<HttpStatus> signOut() {
            return null;
    }

    @Override
    public ApiResponseResult<AuthDto.Token> reissueToken() {
        return null;
    }

    @Override
    public ApiResponseResult<UserDto.UserInfo> validationToken() {
        return null;
    }
}
