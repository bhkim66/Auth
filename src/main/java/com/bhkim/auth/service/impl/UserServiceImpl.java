package com.bhkim.auth.service.impl;

import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.dto.RedisDTO;
import com.bhkim.auth.dto.request.AuthRequestDTO;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.facade.AuthFacade;
import com.bhkim.auth.handler.RedisHandler;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisHandler redisHandler;
    private final AuthFacade authFacade;

    @Override
    public UserResponseDTO.UserInfo getMemberInfo(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        return user.toDto(user);
    }

    @Override
    public ApiResponseResult<Void> signOut() {
        //ATK에 문제가 없을 때 redis에 값 삭제
        String userId = getCurrentUserId();
        redisHandler.deleteData(userId);
        SecurityContextHolder.clearContext();
        return ApiResponseResult.success(null);
    }

    @Override
    public AuthResponseDTO.Token reissueToken(AuthRequestDTO.RefreshToken refreshToken) {
        String userId = getCurrentUserId();
        // refreshToken in Redis
        String redisRefreshToken = redisHandler.getHashData(userId, REDIS_KEY_REFRESH_TOKEN);
        JwtTokenProvider.PrivateClaims privateClaims = jwtTokenProvider.parseRefreshToken(refreshToken.getRefreshToken(), redisRefreshToken);
        String newAccessToken = jwtTokenProvider.generateToken(privateClaims, ACCESS_TOKEN_EXPIRE_TIME);
        String newRefreshToken = jwtTokenProvider.generateToken(privateClaims, REFRESH_TOKEN_EXPIRE_TIME);

        // 레디스에 token 값 넣기
        Map<String, Object> hashMap = RedisDTO.Token.builder()
                .userId(userId)
                .refreshToken(newRefreshToken)
                .expiredDateTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build().convertMap();
        redisHandler.setHashData(userId, hashMap, REFRESH_TOKEN_EXPIRE_TIME);
        return AuthResponseDTO.Token.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }


    @Override
    @Transactional
    public ApiResponseResult<Boolean> updateUser(UserRequestDTO.UpdateUserInfo updateUserInfo, String userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        User updateUser = findUser.toEntity(updateUserInfo);
        findUser.update(updateUser);

        return ApiResponseResult.success(true);
    }

    @Override
    @Transactional
    public ApiResponseResult<Boolean> changePassword(UserRequestDTO.UpdatePassword rawPassword, String userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        // 이전 패스워드와 같은지 체크
        if (passwordEncoder.matches(rawPassword.getPassword(), findUser.getPassword())) {
            throw new ApiException(ILLEGAL_PASSWORD);
        }

        String encodePassword = passwordEncoder.encode(rawPassword.getPassword());
        findUser.updatePassWord(encodePassword);
        return ApiResponseResult.success(true);
    }


    private String getCurrentUserId() {
        return authFacade.getCurrentUserId();
    }
}
