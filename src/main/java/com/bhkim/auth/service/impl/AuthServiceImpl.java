package com.bhkim.auth.service.impl;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.AuthService;
import com.bhkim.auth.handler.RedisHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.bhkim.auth.exception.ExceptionEnum.BAD_CREDENTIALS_EXCEPTION;
import static com.bhkim.auth.exception.ExceptionEnum.ILLEGAL_ARGUMENT_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
//    private final AuthRepository authRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisHandler redisHandler;

    @Override
    public ApiResponseResult<UserResponseDTO.Token> signIn(UserRequestDTO.SignIn signIn) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성, 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = signIn.toAuthentication();
        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new ApiException(BAD_CREDENTIALS_EXCEPTION);
        }

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        User user = userRepository.findById(signIn.getId()).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));

        String accessToken = jwtTokenProvider.generateToken(new JwtTokenProvider.PrivateClaims(user.getId(), user.getRole()));
        String refreshToken = jwtTokenProvider.generateToken(new JwtTokenProvider.PrivateClaims(user.getId(), user.getRole()));
//        redisUtil.insertMemberRedis(tokenInfo);
//          레디스에 값 넣기
        //토큰 암호화
        accessToken = jwtTokenProvider.encToken(accessToken);
        refreshToken = jwtTokenProvider.encToken(refreshToken);

        return ApiResponseResult.success(UserResponseDTO.Token
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .publishTime(LocalDateTime.now())
                .build());
    }

    @Override
    public ApiResponseResult<HttpStatus> signOut() {
            return null;
    }

    @Override
    public ApiResponseResult<UserResponseDTO.Token> reissueToken() {
        return null;
    }

    @Override
    public ApiResponseResult<UserRequestDTO.Signup> validationToken() {
        return null;
    }
}
