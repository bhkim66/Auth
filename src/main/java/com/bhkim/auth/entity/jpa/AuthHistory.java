package com.bhkim.auth.entity.jpa;

import com.bhkim.auth.entity.jpa.base.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "AUTH_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTH_HISTORY_SEQ")
    private Long authHistorySeq;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_SEQ", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "ACTIVE_DETAIL", nullable = false)
    private String activeDetail;
}
