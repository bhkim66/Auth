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
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "USER_SEQ")
    private Long seq;

    @NotBlank
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "AGE", nullable = false)
    private int age;

    @Column(name = "SEX", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TypeEnum sex;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private TypeEnum status;

    @Column(name = "ACCESS_CODE")
    private String accessCode;

    @OneToMany(mappedBy = "user")
    private List<AuthHistory> authHistoryList;

//    @OneToMany(mappedBy = "user")
//    private List<GrantedAuthority> roles;
    @Column(name = "ROLE")
    private String role;

    @Builder
    public User(String role, String accessCode, TypeEnum status, String phoneNumber, TypeEnum sex, int age, String name, String password, String id, Long seq) {
        this.role = role;
        this.accessCode = accessCode;
        this.status = status;
        this.phoneNumber = phoneNumber;
        this.sex = sex;
        this.age = age;
        this.name = name;
        this.password = password;
        this.id = id;
        this.seq = seq;
    }
//    public void setMember(MemberDto.MemberInfo mi) {
//        this.name = mi.getName();
//        this.age = mi.getAge();
//        this.sex = mi.getSex();
//        this.phoneNumber = mi.getPhoneNumber();
//    }
}
