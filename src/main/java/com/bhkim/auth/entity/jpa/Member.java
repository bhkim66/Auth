package com.bhkim.auth.entity.jpa;

import com.bhkim.auth.common.Role;
import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.dto.MemberDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Pattern(regexp = "^[a-z]+[a-z0-9]{5,19}$", message = "영문과 숫자를 혼합한 6~18자리를 입력해야 합니다")
    @Column(name = "MEMBER_ID")
    private String id;

//    @NotBlank(message = "비밀번호를 입력 해주세요")
    @Column(name = "password")
    private String password;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Zㄱ-힣][a-zA-Zㄱ-힣 ]*$", message = "이름은 영문 한글로 이뤄져야 합니다")
    @Column(name = "MEMBER_NAME")
    private String name;

    @Min(value = 1, message = "나이는 1살 이상 150세 이하이여야 합니다")
    @Max(value= 150, message = "나이는 1살 이상 150세 이하이여야 합니다")
    @Column(name = "MEMBER_AGE")
    private int age;

    @Column(name = "MEMBER_AGE")
    private List<GrantedAuthority> roles;

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

//    public void setMember(MemberDto.MemberInfo mi) {
//        this.name = mi.getName();
//        this.age = mi.getAge();
//        this.sex = mi.getSex();
//        this.phoneNumber = mi.getPhoneNumber();
//    }
}
