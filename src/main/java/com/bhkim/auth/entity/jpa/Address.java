package com.bhkim.auth.entity.jpa;

import com.bhkim.auth.entity.jpa.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Entity
@Table(name = "ADDRESS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESS_SEQ")
    private Long seq;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_SEQ", nullable = false)
    private User user;

    @Column(name = "ZIPCODE", nullable = false)
    private int zipcode;

    @NotBlank
    @Column(name = "ADDRESS1", nullable = false)
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @Column(name = "NAME")
    private String name;


    @Builder
    public Address(int zipcode, String address1, User user) {
        this.zipcode = zipcode;
        this.address1 = address1;
        this.user = user;
    }
}
