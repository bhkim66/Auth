package com.bhkim.auth.entity.jpa;

import com.bhkim.auth.common.TypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "Member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_SEQ")
    private Long seq;

    @NotBlank
    @Pattern(regexp = "^[a-z]+[a-z0-9]{5,19}$")
    @Column(name = "MEMBER_ID")
    private String id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Zㄱ-힣][a-zA-Zㄱ-힣 ]*$")
    @Column(name = "MEMBER_NAME")
    private String name;

    @NotBlank
    @Size(min = 1, max = 150)
    @Column(name = "MEMBER_AGE")
    private int age;

    @NotBlank
    @Column(name = "MEMBER_SEX")
    private TypeEnum sex;

    @Column(name = "MEMBER_PHONE_NUMBER")
    private String phoneNumber;
}
