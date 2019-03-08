package com.spacex.user.controller;

import com.spacex.user.dto.TokenResult;
import com.spacex.user.dto.login.QQLoginDTO;
import com.spacex.user.dto.login.WechatLoginDTO;
import com.spacex.user.service.OAuthLoginService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("login")
public class OAuthLoginController {

    @Resource
    private OAuthLoginService oAuthLoginService;

    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    public TokenResult wechat(@RequestBody WechatLoginDTO wechatLoginDTO) {
        String token = oAuthLoginService.loginByWechat(wechatLoginDTO);
        return new TokenResult("");
    }

    @RequestMapping(value = "/qq", method = RequestMethod.POST)
    public TokenResult qq(@RequestBody QQLoginDTO qqLoginDTO) {
        return new TokenResult("");
    }
}
