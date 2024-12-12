package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.AuthRequestDTO;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import com.bhkim.auth.dto.response.UserResponseDTO;

/**
 * 인증/인가 체크가 필요한 서비스
 */
public interface UserService {
    /**
     * 멤버 정보 조회
     * GET
     *
     * @return 멤버 INFO 값
     */
    UserResponseDTO.UserInfo getMemberInfo(String userId);

    /**
     * 로그아웃
     * GET
     * Redis에 값 소멸, 부여한 TOKEN 값의 유효성 사라짐
     *
     * @return 성공 여부
     */
    ApiResponseResult<Void> signOut();

    /**
     * 토큰 재발행
     * POST
     * @return TOKEN 값
     */
    AuthResponseDTO.Token reissueToken();

    /**
     * 유저 정보 변경
     * PUT
     *
     * @param updateUserInfo 유저 정보 변경 정보
     * @return 성공 여부
     */
    ApiResponseResult<Boolean> updateUser(UserRequestDTO.UpdateUserInfo updateUserInfo, String userId);

    /**
     * 비밀번호 변경
     * PUT
     *
     * @param rawPassword 회원가입 필요한 정보
     * @return 성공 여부
     */
    ApiResponseResult<Boolean> changePassword(UserRequestDTO.UpdatePassword rawPassword, String userId);
}
