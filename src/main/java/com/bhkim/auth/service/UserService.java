package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.UserResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    UserResponseDTO.UserInfo getMemberInfo(Long userSeq);

    /**
     * 회원가입
     * POST
     *
     * @param signup 회원가입 필요한 정보
     */
    ResponseEntity<Void> signUp(UserRequestDTO.Signup signup);

    /**
     * 유저 정보 변경
     * PUT
     *
     * @param updateUserInfo 유저 정보 변경 정보
     * @return 성공 여부
     */
    ResponseEntity<Boolean> updateUser(UserRequestDTO.UpdateUserInfo updateUserInfo, Long userSeq);

    /**
     * 비밀번호 변경
     * PUT
     *
     * @param rawPassword 회원가입 필요한 정보
     * @return 성공 여부
     */
    ResponseEntity<Boolean> changePassword(UserRequestDTO.UpdatePassword rawPassword, Long userSeq);


    /**
     * ID 중복 체크
     *
     * @param id the id
     * @return 중복 여부
     */
    ResponseEntity<Boolean> checkDuplicateId(String id);
}
