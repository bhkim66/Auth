package com.bhkim.auth.config.security;

import com.bhkim.auth.exception.ApiException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

import static com.bhkim.auth.exception.ExceptionEnum.MEMBER_REQUIRED;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtTokenProvider jwtTokenProvider;

//    @Value("${kpnp.header.atk}") private String ATK;
//    @Value("${kpnp.header.rtk}") private String RTK;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Request Header 에서 JWT 토큰 추출
//        String atk = jwtTokenProvider.resolveToken(request, ATK);
//        String rtk = jwtTokenProvider.resolveToken(request, RTK);
        String userAgent = request.getHeader("User-Agent");

        // 헤더 전체정보 보기
        Enumeration<String> em = request.getHeaderNames();

        while(em.hasMoreElements()){
            String name = em.nextElement() ;
            String val = request.getHeader(name) ;

            log.info("header : {}", name);
            log.info("val : {}", val);
        }

        // 접속 기기 확인
//        boolean mobile1 = userAgent.matches(".*(iPhone|iPod|Android|Windows CE|BlackBerry|Symbian|Windows Phone" +
//                "|webOS|Opera Mini|Opera Mobi|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson).*");
//        boolean mobile2 = userAgent.matches(".*(LG|SAMSUNG|Samsung).*");
//
//        if (mobile1 || mobile2) {
//            ConstDef.connectChannel = "M";
//        } else {
//            ConstDef.connectChannel = "P";
//        }

        // Redis 에 해당 accessToken logout 여부 확인
        // 2. validateToken 으로 토큰 유효성 검사
        String requestURI = request.getRequestURI();
        log.info("url : {} " , requestURI);
//        log.info("atk : {}" , atk);
//        log.info("rtk : {}" , rtk);

        try {
//            if (atk != null) {
//                jwtTokenProvider.validateToken(atk);
//                Authentication authentication = jwtTokenProvider.getAuthentication(atk);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                ConstDef.accessToken = atk;
//            }
//           else if(atk == null || !jwtTokenProvider.validateToken(atk)) {
//                SecurityContextHolder.clearContext();
//            }
//            if (rtk != null) {
//                jwtTokenProvider.validateToken(rtk);
//                ConstDef.refreshToken = rtk;
//            }
        } catch (JwtException e) {
            log.error("e message : {} ", e.getMessage());
            throw new ApiException(MEMBER_REQUIRED);
        }
        filterChain.doFilter(request, response);
    }
}
