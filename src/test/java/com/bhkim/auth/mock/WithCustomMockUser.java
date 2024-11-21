package com.bhkim.auth.mock;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WitchCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {
    String id() default "bhkim";

    String role() default "USER";


}
