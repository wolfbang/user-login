package com.spacex.user.util;

import org.apache.commons.lang3.RandomStringUtils;

public class TokenUtil {
    public static String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        return RandomStringUtils.random(length, true, false);
    }
}
