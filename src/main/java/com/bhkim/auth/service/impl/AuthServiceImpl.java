package com.bhkim.auth.service.impl;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.dto.RedisDTO;
import com.bhkim.auth.dto.request.AuthRequestDTO;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.handler.RedisHandler;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.security.UserDetail;
import com.bhkim.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static com.bhkim.auth.common.ConstDef.*;
import static com.bhkim.auth.exception.ExceptionEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    //    private final AuthRepository authRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisHandler redisHandler;

    @Override
    public AuthResponseDTO.Token signIn(UserRequestDTO.SignIn signIn) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성, 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = signIn.toAuthentication();
        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = null;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        } catch (BadCredentialsException e) {
            throw new ApiException(BAD_CREDENTIALS_EXCEPTION);
        }

        UserDetail user = (UserDetail) authentication.getPrincipal();
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
//        User user = userRepository.findById(signIn.getId()).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));

        String accessToken = jwtTokenProvider.generateToken(new JwtTokenProvider.PrivateClaims(user.getId(), user.getRole()), ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = jwtTokenProvider.generateToken(new JwtTokenProvider.PrivateClaims(user.getId(), user.getRole()), REFRESH_TOKEN_EXPIRE_TIME);

        // 레디스에 token 값 넣기
        redisHandler.setHashData(user.getId(), getTokenSaveInRedisToMap(user.getId(), refreshToken), REFRESH_TOKEN_EXPIRE_TIME);

        return AuthResponseDTO.Token.builder()
                .accessToken(accessToken) // 토큰 암호화
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public ApiResponseResult<Void> signUp(UserRequestDTO.Signup signup) {
        signup.setPasswordEncoding(passwordEncoder);
        User savedUser = userRepository.save(signup.toUserEntity());

        if(savedUser.getSeq() < 0) {
            throw new ApiException(DATABASE_INSERT_ERROR);
        }
        return ApiResponseResult.success();
    }

    @Override
    public ApiResponseResult<Boolean> checkDuplicateId(String id) {
        boolean existUser = userRepository.existsById(id);

        if(existUser) {
            throw new ApiException(DUPLICATION_VALUE_IN_DATABASE_ERROR);
        }
        return ApiResponseResult.success(true);
    }

    private static Map<String, Object> getTokenSaveInRedisToMap(String userId, String refreshToken) {
        return RedisDTO.Token.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiredDateTime(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRE_TIME).toString()).build().convertMap();
    }

}
