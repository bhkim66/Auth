package com.bhkim.auth.service.impl;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.AuthDTO;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.service.AuthService;
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
    public ApiResponseResult<AuthDTO.Token> signIn(UserRequestDTO.Signup signup) {



        return null;
    }

    @Override
    public ApiResponseResult<HttpStatus> signOut() {
            return null;
    }

    @Override
    public ApiResponseResult<AuthDTO.Token> reissueToken() {
        return null;
    }

    @Override
    public ApiResponseResult<UserRequestDTO.Signup> validationToken() {
        return null;
    }
}
