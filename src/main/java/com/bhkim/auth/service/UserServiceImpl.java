package com.bhkim.auth.service;

import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.UserDto;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bhkim.auth.exception.ExceptionEnum.DATABASE_INSERT_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponseResult<UserDto.UserInfo> getMemberInfo() {
        return null;
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> signUp(UserDto.UserInfo userInfo) {
        User savedUser = userRepository.save(userInfo.getUser());

        if(savedUser.getSeq() < 0) {
            throw new ApiException(DATABASE_INSERT_ERROR);
        }

        return ApiResponseResult.success(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> setMember(UserDto.UserInfo userInfo) {
        //jwt로 멤버 조회가 필요함
        return null;
    }

    @Override
    @Transactional
    public ApiResponseResult<HttpStatus> changePassword(UserDto.UserInfo userInfo) {
        return null;
    }
}
