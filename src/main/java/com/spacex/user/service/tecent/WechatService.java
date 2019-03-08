package com.spacex.user.service.tecent;

import com.spacex.user.dto.login.WechatInfoDTO;

public interface WechatService {
    WechatInfoDTO getWechatUserInfo(String grantCode);
}
