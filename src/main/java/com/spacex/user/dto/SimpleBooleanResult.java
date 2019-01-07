package com.spacex.user.dto;

import lombok.Data;

@Data
public class SimpleBooleanResult {

    private boolean result;

    public SimpleBooleanResult(boolean result) {
        this.result = result;
    }
}
