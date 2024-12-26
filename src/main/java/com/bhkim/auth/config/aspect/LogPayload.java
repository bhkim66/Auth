package com.bhkim.auth.config.aspect;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LogPayload {
    private String userId;
    private String ip;
    private String userAgent;
    private String request;
    private String response;

    @Builder
    public LogPayload(String userId, String ip, String userAgent, String request, String response) {
        this.userId = userId;
        this.ip = ip;
        this.userAgent = userAgent;
        this.request = request;
        this.response = response;
    }
}
