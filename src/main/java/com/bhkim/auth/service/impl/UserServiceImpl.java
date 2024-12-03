package com.bhkim.auth.service.impl;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bhkim.auth.exception.ExceptionEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponseResult<UserResponseDTO.UserInfo> getMemberInfo(Long userSeq) {
        User user = userRepository.findBySeq(userSeq).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        UserResponseDTO.UserInfo userResponseDto = user.toDto(user);

        return ApiResponseResult.success(userResponseDto);
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> signUp(UserRequestDTO.Signup signup) {
        signup.setPassword(passwordEncoder.encode(signup.getPassword()));
        User savedUser = userRepository.save(signup.toUserEntity());

        if(savedUser.getSeq() < 0) {
            throw new ApiException(DATABASE_INSERT_ERROR);
        }
        return ApiResponseResult.success(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> checkDuplicateId(String id) {
        boolean existUser = userRepository.existsById(id);

        if(existUser) {
            throw new ApiException(DUPLICATION_VALUE_IN_DATABASE_ERROR);
        }
        return ApiResponseResult.success(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> authenticateMail(String accessCode, Long userSeq) {

        return null;
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> updateUser(UserRequestDTO.UpdateUserInfo updateUserInfo, Long userSeq) {
        //jwt로 멤버 조회가 필요함
        User findUser = userRepository.findBySeq(userSeq).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        User updateUser = findUser.toEntity(updateUserInfo);

        findUser.update(updateUser);
        return ApiResponseResult.success(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> changePassword(UserRequestDTO.UpdatePassword rawPassword, Long userSeq) {
        User findUser = userRepository.findBySeq(userSeq).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        // 이전 패스워드와 같은지 체크
        if(passwordEncoder.matches(rawPassword.getPassword(), findUser.getPassword())) {
            throw new ApiException(ILLEGAL_PASSWORD);
        }

        String encodePassword = passwordEncoder.encode(rawPassword.getPassword());
        findUser.updatePassWord(encodePassword);
        return ApiResponseResult.success(HttpStatus.OK);
    }

}
