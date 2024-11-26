package com.bhkim.auth.dto;

import com.bhkim.auth.common.RoleEnum;
import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.entity.jpa.User;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

import static com.bhkim.auth.common.RoleEnum.*;
import static com.bhkim.auth.common.TypeEnum.*;

@Data
public class UserRequestDTO {

    @Builder
    public static class MemberInfo {
        @NotBlank
        @Pattern(regexp = "^[a-z]+[a-z0-9]{5,19}$", message = "영문과 숫자를 혼합한 6~18자리를 입력해야 합니다")
        private String id;
    }

    @Builder
    public static class Signup {
        @NotBlank
        @Pattern(regexp = "^[A-Za-z\\d]{6,18}$", message = "영문과 숫자를 혼합한 6~18자리를 입력해야 합니다")
        private String id;

        @NotBlank(message = "비밀번호를 입력 해주세요")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,12}$", message = "영문과 숫자를 혼합한 8~12자리를 입력해야 합니다")
        private String password;

        @NotBlank
        @Pattern(regexp = "^[a-zA-Zㄱ-힣][a-zA-Zㄱ-힣 ]*$", message = "이름은 영문 한글로 이뤄져야 합니다")
        private String name;

        @Min(value = 1, message = "나이는 1살 이상 150세 이하이여야 합니다")
        @Max(value= 150, message = "나이는 1살 이상 150세 이하이여야 합니다")
        private int age;

        @NotNull
        private TypeEnum sex;

        private String phoneNumber;

        public User toUserEntity() {
            return User.builder()
                    .id(this.id)
                    .name(this.name)
                    .password(this.password)
                    .sex(this.sex)
                    .age(this.age)
                    .phoneNumber(this.phoneNumber)
                    .role(USER.toString())
                    .status(PENDING)
                    .accessCode(UUID.randomUUID().toString())
                    .build();
        }
    }

    @Builder
    public static class UpdateMemberInfo {
        @NotBlank
        @Pattern(regexp = "^[a-zA-Zㄱ-힣][a-zA-Zㄱ-힣 ]*$", message = "이름은 영문 한글로 이뤄져야 합니다")
        private String name;

        @Min(value = 1, message = "나이는 1살 이상 150세 이하이여야 합니다")
        @Max(value= 150, message = "나이는 1살 이상 150세 이하이여야 합니다")
        private int age;

        @NotNull
        private TypeEnum sex;

        private String phoneNumber;
    }

    @Builder
    public static class UpdatePassword {
        @NotBlank(message = "비밀번호를 입력 해주세요")
        private String password;
    }

}
