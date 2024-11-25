package com.bhkim.auth.entity.jpa;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ADDRESS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESS_SEQ")
    private Long addressSeq;

    @NotBlank
    @Column(name = "ZIPCODE", nullable = false)
    private int zipcode;

    @NotBlank
    @Column(name = "ADDRESS1", nullable = false)
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @NotEmpty
    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_SEQ", nullable = false)
    private User user;
}
