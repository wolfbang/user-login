package com.spacex.user.service;

import com.spacex.user.dto.UserLoginDTO;
import com.spacex.user.dto.UserRegisterDTO;

public interface UserLoginService {
    String register(UserRegisterDTO userRegisterDTO);

    String login(UserLoginDTO userLoginDTO);

    boolean logout(String account, String token);
}
