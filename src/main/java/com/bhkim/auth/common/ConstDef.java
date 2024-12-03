package com.bhkim.auth.common;

import io.jsonwebtoken.SignatureAlgorithm;

public abstract class ConstDef {
    public static final String SEX_TYPE_MALE = "남자";
    public static final String SEX_TYPE_FEMALE = "여자";

    public static final String PENDING_STATUS = "이메일 인증 전";
    public static final String CERTIFIED_STATUS = "이메일 인증 완료";

    public static final String CHANNEL_TYPE_PC = "PC";
    public static final String CHANNEL_TYPE_MOBILE = "Mobile";

    public static final String REDIS_KEY_PREFIX = "USER:";

    public static final String ROLE_TYPES = "ROLE_TYPES";
    public static final String USER_ID = "USER_ID";
    public static final SignatureAlgorithm KEY_USE_ALGORITHM = SignatureAlgorithm.HS256;


}
