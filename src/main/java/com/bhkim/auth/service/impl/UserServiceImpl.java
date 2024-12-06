package com.bhkim.auth.service.impl;

import com.bhkim.auth.config.security.JwtTokenProvider;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bhkim.auth.exception.ExceptionEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponseDTO.UserInfo getMemberInfo(Long userSeq) {
        User user = userRepository.findBySeq(userSeq).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        return user.toDto(user);
    }

    @Override
    @Transactional
    public ApiResponseResult<Void> signUp(UserRequestDTO.Signup signup) {
        signup.setPassword(passwordEncoder.encode(signup.getPassword()));
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

    @Override
    @Transactional
    public ApiResponseResult<Boolean> updateUser(UserRequestDTO.UpdateUserInfo updateUserInfo) {
        //jwt로 멤버 조회가 필요함
        String token = "";
        String userId = jwtTokenProvider.getUserId(token);

        User findUser = userRepository.findById(userId).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        User updateUser = findUser.toEntity(updateUserInfo);

        findUser.update(updateUser);
        return ApiResponseResult.success(true);
    }

    @Override
    @Transactional
    public ApiResponseResult<Boolean> changePassword(UserRequestDTO.UpdatePassword rawPassword) {
        String token = "";
        String userId = jwtTokenProvider.getUserId(token);

        User findUser = userRepository.findById(userId).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        // 이전 패스워드와 같은지 체크
        if(passwordEncoder.matches(rawPassword.getPassword(), findUser.getPassword())) {
            throw new ApiException(ILLEGAL_PASSWORD);
        }

        String encodePassword = passwordEncoder.encode(rawPassword.getPassword());
        findUser.updatePassWord(encodePassword);
        return ApiResponseResult.success(true);
    }

}
