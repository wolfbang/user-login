package com.spacex.user.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class PasswordUtil {

    public static String getSalt(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        return RandomStringUtils.random(length, true, false);
    }

    public static String sha256Hex(String text) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("text must not be blank");
        }
        return DigestUtils.sha256Hex(text);
    }

    public static String getPasswordHash(String password, String passwordSalt) {
        return sha256Hex(password + passwordSalt);
    }
}
