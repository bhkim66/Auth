package com.bhkim.auth.entity.jpa;

import com.bhkim.auth.common.TypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Table(name = "Member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

    @Min(1)
    @Max(150)
    @Column(name = "MEMBER_AGE")
    private int age;

    @NotNull
    @Column(name = "MEMBER_SEX")
    @Enumerated(value = EnumType.STRING)
    private TypeEnum sex;

    @Column(name = "MEMBER_PHONE_NUMBER")
    private String phoneNumber;

    @OneToMany(mappedBy = "member")
    private List<AuthHistory> authHistoryList;

    @Builder
    public Member(String id, String name, int age, TypeEnum sex, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
    }


}
