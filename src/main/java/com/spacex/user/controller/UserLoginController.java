package com.spacex.user.controller;

import com.google.common.base.Preconditions;
import com.spacex.user.dto.SimpleBooleanResult;
import com.spacex.user.dto.TokenResult;
import com.spacex.user.dto.UserLoginDTO;
import com.spacex.user.dto.UserRegisterDTO;
import com.spacex.user.service.UserLoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserLoginController {

    @Autowired
    private UserLoginService userLoginService;

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public TokenResult register(@RequestBody UserRegisterDTO userRegisterDTO) {
        Preconditions.checkArgument(userRegisterDTO != null, "userRegisterDTO不能为空");
        String token = userLoginService.register(userRegisterDTO);
        return new TokenResult(token);
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public TokenResult login(@RequestBody UserLoginDTO userLoginDTO) {
        Preconditions.checkArgument(userLoginDTO != null, "userLoginDTO不能为空");
        String token = userLoginService.login(userLoginDTO);
        return new TokenResult(token);
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public SimpleBooleanResult logout(@RequestParam String account, @RequestParam String token) {
        Preconditions.checkArgument(StringUtils.isNotBlank(account), "account不能为空");
        boolean result = userLoginService.logout(account,token);
        return new SimpleBooleanResult(result);
    }
}
