package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.UserRequestDTO;
import org.springframework.http.HttpStatus;

/**
 * 멤버 관련 서비스 로직
 */
public interface UserService {
    /**
     * 멤버 정보 조회
     * GET
     *
     * @return 멤버 INFO 값
     */
    ApiResponseResult<UserRequestDTO.MemberInfo> getMemberInfo();

    /**
     * 회원가입
     * POST
     *
     * @param signup 회원가입 필요한 정보
     * @return 성공 여부
     */
    ApiResponseResult<HttpStatus> signUp(UserRequestDTO.Signup signup);

    /**
     * 유저 정보 변경
     * PUT
     *
     * @param updateMemberInfo 유저 정보 변경 정보
     * @return 성공 여부
     */
    ApiResponseResult<HttpStatus> updateUser(UserRequestDTO.UpdateMemberInfo updateMemberInfo);

    /**
     * 비밀번호 변경
     * PUT
     *
     * @param updatePassword 회원가입 필요한 정보
     * @return 성공 여부
     */
    ApiResponseResult<HttpStatus> changePassword(UserRequestDTO.UpdatePassword updatePassword);


    /**
     * 회원가입 이메일 인증
     * PUT
     *
     * @Param accessCode 회원가입 이메일 코드
     * @return 성공 여부
     */
    ApiResponseResult<HttpStatus> authenticateMail(String accessCode);

    ApiResponseResult<HttpStatus> checkDuplicateId(String id);
}
