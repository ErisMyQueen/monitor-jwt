package com.example.utils;

public class Const {
    public static final String JWT_BLACK_LIST="jwt:blacklist:";

    public static final String VERIFY_EMAIL_LIMIT="verify:email:limit:";
    public static final String VERIFY_EMAIL_DATA="verify:email:data";

    public static final int ORDER_CORS=-102; // 跨域优先级 越小越优先
    public static final int ORDER_LIMIT=-101; // 限流优先级

    public static final String FLOW_LIMIT_COUINTER="flow:couinter:";
    public static final String FLOW_LIMIT_BLOCK="flow:block:";

    //用户角色
    public final static String ROLE_DEFAULT = "user";
    //请求自定义属性
    public final static String ATTR_USER_ID = "userId";
    public final static String ATTR_CLIENT = "client";
}

