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

    @OneToMany(mappedBy = "user")
    private List<AuthHistory> authHistoryList;

//    @OneToMany(mappedBy = "user")
//    private List<GrantedAuthority> roles;
    @Column(name = "ROLE")
    private String role;

    @Builder
    public User(String id, String password, String name, int age, TypeEnum sex, String phoneNumber, String role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
//    public void setMember(MemberDto.MemberInfo mi) {
//        this.name = mi.getName();
//        this.age = mi.getAge();
//        this.sex = mi.getSex();
//        this.phoneNumber = mi.getPhoneNumber();
//    }
}
