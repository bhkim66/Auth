package com.bhkim.auth.dto;

import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.entity.jpa.Member;
import lombok.Builder;
import lombok.Getter;

public class MemberDto {
    @Getter
    public static class MemberInfo {
        private String id;
        private String name;
        private int age;
        private TypeEnum sex;
        private String phoneNumber;

        @Builder
        public MemberInfo(String id, String name, int age, TypeEnum sex, String phoneNumber) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.sex = sex;
            this.phoneNumber = phoneNumber;
        }

        public Member dtoConvertMember() {
            return Member.builder()
                    .id(this.id)
                    .name(this.name)
                    .sex(this.sex)
                    .age(this.age)
                    .phoneNumber(this.phoneNumber)
                    .build();
        }
    }
}
