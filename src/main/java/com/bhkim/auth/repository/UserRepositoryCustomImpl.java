package com.bhkim.auth.repository;

import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.dto.condition.UserSearchCondition;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Set;

import static com.bhkim.auth.entity.jpa.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserResponseDTO.UserInfo> searchUserWithCondition(UserSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        if (hasText(condition.getId())) {
            builder.and(user.id.eq(condition.getId()));
        }
        if (hasText(condition.getSex().name())) {
            builder.and(user.sex.eq(condition.getSex()));
        }
        if (condition.getMinAge() > 0) {
            builder.and(user.age.goe(condition.getMinAge()));
        }
        if (condition.getMaxAge() > 0) {
            builder.and(user.age.loe(condition.getMaxAge()));
        }

//        return queryFactory.select(Projections.constructor(UserResponseDTO.UserInfo.class,
//                        user.id,
//                        user.name,
//                        user.age,
//                        user.sex,
//                        user.phoneNumber
//                ))
//                .from(user)
//                .where(builder)
//                .fetch();

        return queryFactory.select(Projections.constructor(UserResponseDTO.UserInfo.class,
                        user.id,
                        user.name,
                        user.age,
                        user.sex,
                        user.phoneNumber
                ))
                .from(user)
                .where(
                        idContain(condition.getId()),
                        ageGoe(condition.getMinAge()),
                        ageLoe(condition.getMaxAge()),
                        sexEq(condition.getSex())
                )
                .fetch();

    }


    @Override
    public Page<User> getUserOrders(UserSearchCondition condition, Pageable page) {
        List<User> users = queryFactory.selectFrom(user)
//                .join(user.orders, order)
//                .where(user.id.eq(condition.getId()))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(user.count())
                .from(user);

        return PageableExecutionUtils.getPage(users, page, countQuery::fetchOne);
    }

    private BooleanExpression sexEq(TypeEnum sex) {
        return sex != null ? user.sex.eq(sex) : null;
    }

    private BooleanExpression ageGoe(int minAge) {
        return minAge > 0 ? user.age.goe(minAge) : null;
    }

    private BooleanExpression ageLoe(int maxAge) {
        return maxAge > 0 ? user.age.loe(maxAge) : null;
    }

    private BooleanExpression idContain(String id) {
        return id != null ? user.id.contains(id) : null;
    }
}
