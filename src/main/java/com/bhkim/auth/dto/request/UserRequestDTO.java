package com.bhkim.auth.dto.request;

import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.entity.jpa.User;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.bhkim.auth.common.RoleEnum.*;
import static com.bhkim.auth.common.TypeEnum.*;

@Data
public class UserRequestDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshToken {
        @NotBlank
        private String refreshToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserInfo {
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePassword {
        @NotBlank
        private String userId;

        @NotBlank(message = "비밀번호를 입력 해주세요")
//        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,12}$", message = "영문과 숫자를 혼합한 8~12자리를 입력해야 합니다")
        private String password;
    }

}
