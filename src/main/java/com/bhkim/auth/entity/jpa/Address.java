package com.bhkim.auth.entity.jpa;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "Address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESS_SEQ")
    private Long addressSeq;

    @NotBlank
    @Column(name = "ZIPCODE")
    private int zipcode;

    @NotBlank
    @Column(name = "ADDRESS1")
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @NotEmpty
    @Column(name = "NAME")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEMBER_SEQ", nullable = false)
    private Member member;
}
