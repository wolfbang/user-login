package com.spacex.user.service;

import com.spacex.user.dto.login.QQLoginDTO;
import com.spacex.user.dto.login.WechatLoginDTO;

public interface OAuthLoginService {
    String loginByWechat(WechatLoginDTO wechatLoginDTO);

    String loginByQQ(QQLoginDTO qqLoginDTO);
}
