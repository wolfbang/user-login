package com.spacex.user.enums;

public enum LoginType {
    WECHAT(1), QQ(2), WEIBO(3), FACEBOOK(4);

    private Integer code;

    LoginType(Integer type) {
        this.code = type;
    }

    public Integer getCode() {
        return code;
    }
}
