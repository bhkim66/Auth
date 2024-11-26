package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.AuthDTO;
import com.bhkim.auth.dto.request.UserRequestDTO;
import org.springframework.http.HttpStatus;

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
    ApiResponseResult<AuthDTO.Token> signIn(UserRequestDTO.Signup signup);

    /**
     * 로그아웃
     * GET
     * Redis에 값 소멸, 부여한 TOKEN 값의 유효성 사라짐
     *
     * @return 성공 여부
     */
    ApiResponseResult<HttpStatus> signOut();

    /**
     * 토큰 재발행
     * POST
     * @return TOKEN 값
     */
    ApiResponseResult<AuthDTO.Token> reissueToken();

    /**
     * 토큰 유효성 검사
     * GET
     * @return 해당 토큰에 해당하는 멤버의 정보
     */
    ApiResponseResult<UserRequestDTO.Signup> validationToken();
}
