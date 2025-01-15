package com.bhkim.auth.entity.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -106614763L;

    public static final QUser user = new QUser("user");

    public final ListPath<Address, QAddress> addresses = this.<Address, QAddress>createList("addresses", Address.class, QAddress.class, PathInits.DIRECT2);

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final StringPath id = createString("id");

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final EnumPath<com.bhkim.auth.common.RoleEnum> role = createEnum("role", com.bhkim.auth.common.RoleEnum.class);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final EnumPath<com.bhkim.auth.common.TypeEnum> sex = createEnum("sex", com.bhkim.auth.common.TypeEnum.class);

    public final EnumPath<com.bhkim.auth.common.TypeEnum> status = createEnum("status", com.bhkim.auth.common.TypeEnum.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

