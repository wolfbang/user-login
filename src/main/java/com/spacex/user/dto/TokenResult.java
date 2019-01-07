package com.spacex.user.dto;

import lombok.Data;

@Data
public class TokenResult {
    private String token;

    public TokenResult(String token) {
        this.token = token;
    }
}
