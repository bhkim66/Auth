package com.bhkim.auth.mock;

import com.bhkim.auth.common.RoleEnum;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.bhkim.auth.common.RoleEnum.*;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {
    String id() default "bhkim62";

    RoleEnum role() default USER;
}
