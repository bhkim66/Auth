package com.bhkim.auth.entity.jpa;

import com.bhkim.auth.common.TypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Entity
@Table(name = "User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTime implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "USER_SEQ")
    private Long seq;

    @NotBlank
    @Pattern(regexp = "^[a-z]+[a-z0-9]{5,19}$", message = "영문과 숫자를 혼합한 6~18자리를 입력해야 합니다")
    @Column(name = "ID")
    private String id;

//    @NotBlank(message = "비밀번호를 입력 해주세요")
    @Column(name = "password")
    private String password;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Zㄱ-힣][a-zA-Zㄱ-힣 ]*$", message = "이름은 영문 한글로 이뤄져야 합니다")
    @Column(name = "NAME")
    private String name;

    @Min(value = 1, message = "나이는 1살 이상 150세 이하이여야 합니다")
    @Max(value= 150, message = "나이는 1살 이상 150세 이하이여야 합니다")
    @Column(name = "AGE")
    private int age;

    @Column(name = "AGE")
    private List<GrantedAuthority> roles;

    @NotNull
    @Column(name = "SEX")
    @Enumerated(value = EnumType.STRING)
    private TypeEnum sex;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @OneToMany(mappedBy = "user")
    private List<AuthHistory> authHistoryList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Builder
    public User(String id, String name, int age, TypeEnum sex, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
    }

    public User(Long seq, String id, String password) {
        this.seq = seq;
        this.id = id;
        this.password = password;
    }



//    public void setMember(MemberDto.MemberInfo mi) {
//        this.name = mi.getName();
//        this.age = mi.getAge();
//        this.sex = mi.getSex();
//        this.phoneNumber = mi.getPhoneNumber();
//    }
}
