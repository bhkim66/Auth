package com.bhkim.auth.service;

import com.bhkim.auth.dto.UserDto;
import com.bhkim.auth.common.ApiResponseResult;
import org.springframework.http.HttpStatus;

/**
 * 멤버 관련 서비스 로직
 */
public interface UserService {
    /**
     * 멤버 정보 조회
     * GET
     * @return 멤버 INFO 값
     */
    ApiResponseResult<UserDto.UserInfo> getMemberInfo();

    /**
     * 회원가입
     * POST
     * @param userInfo 회원가입 필요한 정보
     * @return 성공 여부
     */
    ApiResponseResult<HttpStatus> signUp(UserDto.UserInfo userInfo);

    /**
     * 유저 정보 변경
     * PUT
     * @param userInfo 회원가입 필요한 정보
     * @return 성공 여부
     */
    ApiResponseResult<HttpStatus> setMember(UserDto.UserInfo userInfo);

    /**
     * 비밀번호 변경
     * PUT
     * @param userInfo 회원가입 필요한 정보
     * @return 성공 여부
     */
    ApiResponseResult<HttpStatus> changePassword(UserDto.UserInfo userInfo);
}
