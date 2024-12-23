package com.bhkim.auth.repository;

import com.bhkim.auth.dto.condition.UserSearchCondition;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.bhkim.auth.entity.jpa.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserResponseDTO.UserInfo> searchUserWithCondition(UserSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        if(hasText(condition.getId())) {
            builder.and(user.id.eq(condition.getId()));
        }
        if(hasText(condition.getSex().name())) {
            builder.and(user.sex.eq(condition.getSex()));
        }
        if(condition.getMinAge() > 0) {
            builder.and(user.age.goe(condition.getMinAge()));
        }
        if(condition.getMaxAge() > 0) {
            builder.and(user.age.loe(condition.getMaxAge()));
        }

        return queryFactory.select(Projections.constructor(UserResponseDTO.UserInfo.class,
                        user.id,
                        user.name,
                        user.age,
                        user.sex,
                        user.phoneNumber
                ))
                .from(user)
                .where(builder)
                .fetch();
    }
}
