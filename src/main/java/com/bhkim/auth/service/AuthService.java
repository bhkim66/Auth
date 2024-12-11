package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.AuthRequestDTO;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import org.springframework.http.ResponseEntity;

/**
 * 권한 체크 서비스
 * 유저의 정보는 request에서 받아온 토큰 정보를 복호화 한뒤 redis에서 조회해온다
 */
public interface AuthService {

    /**
     * 로그인
     * POST
     *
     * @param signup id, pw 로그인 정보
     * @return TOKEN 값
     */
    AuthResponseDTO.Token signIn(AuthRequestDTO.SignIn signup);

    /**
     * 회원가입
     * POST
     *
     * @param signup 회원가입 필요한 정보
     */
    ApiResponseResult<Void> signUp(AuthRequestDTO.Signup signup);

    /**
     * ID 중복 체크
     *
     * @param id the id
     * @return 중복 여부
     */
    ApiResponseResult<Boolean> checkDuplicateId(String id);

}
