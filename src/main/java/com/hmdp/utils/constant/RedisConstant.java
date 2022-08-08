package com.hmdp.utils.constant;

import org.springframework.context.annotation.Bean;

/**
 * @创建人 anan
 * @创建时间 2022/8/8
 * @描述
 */
public class RedisConstant {
    public static final String LOGIN_CODE_KEY="login:code:";
    public static final String LOGIN_USER_KEY="login:token:";
    public static final Integer LOGIN_CODE_TTL=2;
    public static final Integer LOGIN_USER_TTL=30;
}
