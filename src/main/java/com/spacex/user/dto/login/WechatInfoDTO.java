package com.spacex.user.dto.login;

import lombok.Data;

@Data
public class WechatInfoDTO {
    private String openid;
    private String nickname;

    // some other info properties ....
}
