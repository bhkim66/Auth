package com.bhkim.auth.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class TokenInfo {
    private Long memSeq;
    private String memId;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime publishTime;
    private List<String> memberType = new ArrayList<>();
    private Long rtkExpirationTime;
    private String rtkExpirationDate;

    @Builder
    public TokenInfo(Long memSeq, String memId, String grantType, String accessToken, String refreshToken, LocalDateTime publishTime, List<String> memberType, Long rtkExpirationTime, String rtkExpirationDate) {
        this.memSeq = memSeq;
        this.memId = memId;
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.publishTime = publishTime;
        this.memberType = memberType;
        this.rtkExpirationTime = rtkExpirationTime;
        this.rtkExpirationDate = rtkExpirationDate;
    }
}
