package com.bhkim.auth.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class RequestMail {
    private String title;
    private String sendTo;
    private String templates; //사용할 templates 파일 이름
    private Map<String, String> context;// 내용
}
