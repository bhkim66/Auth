package com.bhkim.auth.dto;

import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.entity.jpa.Member;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MemberDto {

    @Getter
    public static class MemberInfo {
        //        @NotBlank
//        @Pattern(regexp = "^[a-z]+[a-z0-9]{5,19}$", message = "영문과 숫자를 혼합한 6~18자리를 입력해야 합니다")
//        private String id;
//
//        @NotBlank(message = "비밀번호를 입력 해주세요")
//        private String password;
//
//        @NotBlank
//        @Pattern(regexp = "^[a-zA-Zㄱ-힣][a-zA-Zㄱ-힣 ]*$", message = "이름은 영문 한글로 이뤄져야 합니다")
//        private String name;
//
//        @Min(value = 1, message = "나이는 1살 이상 150세 이하이여야 합니다")
//        @Max(value= 150, message = "나이는 1살 이상 150세 이하이여야 합니다")
//        private int age;
//
//        @NotNull
//        @Column(name = "MEMBER_SEX")
//        private TypeEnum sex;
//
//        private String phoneNumber;
        private Member member;

        @Builder
        public MemberInfo(Member member) {
            this.member = member;
        }
    }

}
