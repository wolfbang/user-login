package com.spacex.user.controller;

import com.google.common.base.Preconditions;
import com.spacex.user.dto.SimpleBooleanResult;
import com.spacex.user.dto.UserPasswordResetDTO;
import com.spacex.user.service.UserLoginService;
import com.spacex.user.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class UserAccountController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "reset", method = RequestMethod.POST)
    public SimpleBooleanResult resetPassword(@RequestBody UserPasswordResetDTO passwordResetDTO) {
        Preconditions.checkArgument(passwordResetDTO != null, "passwordResetDTO 不能为空");
        boolean result = userService.resetPassword(passwordResetDTO);
        return new SimpleBooleanResult(result);
    }
}
