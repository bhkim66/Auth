package com.bhkim.auth.entity.jpa;

import com.bhkim.auth.common.TypeEnum;
import jakarta.persistence.*;
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

    @Column(name = "MEMBER_ID")
    private String id;

    @Column(name = "MEMBER_NAME")

    private String name;

    @Column(name = "MEMBER_AGE")
    private int age;

    @Column(name = "MEMBER_SEX")
    private TypeEnum sex;

    @Column(name = "MEMBER_PHONE_NUMBER")
    private String phoneNumber;


}
