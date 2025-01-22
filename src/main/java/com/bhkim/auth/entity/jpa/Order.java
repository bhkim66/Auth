package com.bhkim.auth.entity.jpa;

import com.bhkim.auth.entity.jpa.base.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_SEQ")
    private Long seq;

    @Column(name = "ORDER_NUM", nullable = false)
    private String orderNum;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_SEQ", nullable = false)
    private User user;

    @Builder
    public Order(String orderNum, User user) {
        this.orderNum = orderNum;
        this.user = user;
    }
}
