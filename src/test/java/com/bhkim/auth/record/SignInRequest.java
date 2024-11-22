package com.bhkim.auth.record;

public record SignInRequest(String id, String password) {
    public SignInRequest(final String id, final String password) {
        this.id = id;
        this.password = password;
//            Assert.hasText(id, "ID는 필수입니다");
//            Assert.hasText(password, "비밀번호는 필수입니다");
    }
}
