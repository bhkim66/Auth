package com.bhkim.auth.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class TokenInfo {
    private Long userSeq;
    private String userId;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private List<String> authorityList = new ArrayList<>();
    private Long rtkExpirationTime;
    private String rtkExpirationDate;

    @Builder
    public TokenInfo(Long userSeq, String userId, String grantType, String accessToken, String refreshToken, List<String> authorityList, Long rtkExpirationTime, String rtkExpirationDate) {
        this.userSeq = userSeq;
        this.userId = userId;
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.authorityList = authorityList;
        this.rtkExpirationTime = rtkExpirationTime;
        this.rtkExpirationDate = rtkExpirationDate;
    }

    public TokenInfo encToken(String encAccessToken, String encRefreshToken) {
        encAccessToken(encAccessToken);
        encRefreshToken(encRefreshToken);
        return this;
    }

    private void encAccessToken(String encAccessToken) {
        this.accessToken = encAccessToken;
    }

    private void encRefreshToken(String encRefreshToken) {
        this.refreshToken = encRefreshToken;
    }
}
