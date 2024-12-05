package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.AuthRequestDTO;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.AuthResponseDTO;
import org.springframework.http.HttpStatus;
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
    AuthResponseDTO.Token signIn(UserRequestDTO.SignIn signup);

    /**
     * 로그아웃
     * GET
     * Redis에 값 소멸, 부여한 TOKEN 값의 유효성 사라짐
     *
     * @return 성공 여부
     */
    ResponseEntity<Boolean> signOut();

    /**
     * 토큰 재발행
     * POST
     * @return TOKEN 값
     */
    AuthResponseDTO.Token reissueToken(AuthRequestDTO.RefreshToken refreshToken);

    /**
     * 토큰 유효성 검사
     * GET
     * @return 해당 토큰에 해당하는 멤버의 정보
     */
    UserRequestDTO.Signup validationToken();
}
