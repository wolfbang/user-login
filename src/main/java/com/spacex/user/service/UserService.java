package com.spacex.user.service;

import com.spacex.user.dto.UserPasswordResetDTO;
import com.spacex.user.repository.po.UserPO;

public interface UserService {
    UserPO getByAccount(String account);

    boolean resetPassword(UserPasswordResetDTO passwordResetDTO);
}
