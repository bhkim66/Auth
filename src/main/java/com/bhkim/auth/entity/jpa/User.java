package com.bhkim.auth.entity.jpa;

import com.bhkim.auth.common.RoleEnum;
import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Entity
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_SEQ")
    private Long seq;

    @NotBlank
    @Column(name = "ID", nullable = false, unique = true)
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

    @Column(name = "ROLE")
    private RoleEnum role;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Address> addresses = new ArrayList<>();

    @Builder
    public User(RoleEnum role, TypeEnum status, String phoneNumber, TypeEnum sex, int age, String name, String password, String id) {
        this.role = role;
        this.status = status;
        this.phoneNumber = phoneNumber;
        this.sex = sex;
        this.age = age;
        this.name = name;
        this.password = password;
        this.id = id;
    }
    public void update(User user) {
        this.name = user.getName();
        this.age = user.getAge();
        this.sex = user.getSex();
        this.phoneNumber = user.getPhoneNumber();
    }

    public void updatePassWord(String password) {
        this.password = password;
    }

    public User toEntity(UserRequestDTO.UpdateUserInfo umi) {
        return User.builder()
                .name(umi.getName())
                .sex(umi.getSex())
                .age(umi.getAge())
                .phoneNumber(umi.getPhoneNumber())
                .build();
    }

    public UserResponseDTO.UserInfo toDto(User user) {
        return UserResponseDTO.UserInfo.builder()
                .user(user)
                .build();
    }
}
