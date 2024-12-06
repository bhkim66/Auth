package com.bhkim.auth.dto.request;

import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.entity.jpa.User;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static com.bhkim.auth.common.UserRole.*;
import static com.bhkim.auth.common.TypeEnum.*;

@Data
public class UserRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignIn {
        @NotBlank
        @Pattern(regexp = "^[a-z]+[a-z0-9]{5,19}$", message = "영문과 숫자를 혼합한 6~18자리를 입력해야 합니다")
        private String userId;

        @NotBlank(message = "비밀번호를 입력 해주세요")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(userId, password);
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Signup {
        @NotBlank
        @Pattern(regexp = "^[A-Za-z\\d]{6,18}$", message = "영문과 숫자를 혼합한 6~18자리를 입력해야 합니다")
        private String userId;

        @NotBlank(message = "비밀번호를 입력 해주세요")
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
                    .id(this.userId)
                    .name(this.name)
                    .password(this.password)
                    .sex(this.sex)
                    .age(this.age)
                    .phoneNumber(this.phoneNumber)
                    .role(USER)
                    .status(PENDING)
                    .accessCode(UUID.randomUUID().toString())
                    .build();
        }

        public void setPasswordEncoding(PasswordEncoder passwordEncoder) {
            this.password = passwordEncoder.encode(password);
        }
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
        @NotBlank(message = "비밀번호를 입력 해주세요")
//        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,12}$", message = "영문과 숫자를 혼합한 8~12자리를 입력해야 합니다")
        private String password;
    }

}
