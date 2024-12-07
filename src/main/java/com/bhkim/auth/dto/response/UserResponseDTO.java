package com.bhkim.auth.dto.response;


import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.entity.jpa.User;
import lombok.Builder;

public class UserResponseDTO {

    public static class UserInfo {
        private String id;
        private String name;
        private Integer age;
        private TypeEnum sex;
        private String phoneNumber;

        @Builder
        public UserInfo(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.age = user.getAge();
            this.sex = user.getSex();
            this.phoneNumber = user.getPhoneNumber();
        }
    }
}
