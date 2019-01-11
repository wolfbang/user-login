package com.spacex.user.service.impl;

import com.spacex.user.dto.UserPasswordResetDTO;
import com.spacex.user.repository.mapper.UserMapper;
import com.spacex.user.repository.po.UserPO;
import com.spacex.user.service.UserService;
import com.spacex.user.util.JsonUtil;
import com.spacex.user.util.PasswordUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Override
    public UserPO getByAccount(String account) {
        Example example = new Example(UserPO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("account", account);

        List<UserPO> userPOs = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(userPOs)) {
            return null;
        }

        UserPO userPO = userPOs.get(0);
        return userPO;
    }

    @Override
    public boolean resetPassword(UserPasswordResetDTO passwordResetDTO) {
        logger.info(String.format("UserServiceImpl#resetPassword passwordResetDTO:%s", JsonUtil.toJson(passwordResetDTO)));
        doCheckOldPassword(passwordResetDTO);

        String newPassword = passwordResetDTO.getNewPassword();
        String newPasswordSalt = PasswordUtil.getSalt(32);
        String newPasswordSaltHash = PasswordUtil.sha256Hex(newPasswordSalt);

        String realPasswordHash = PasswordUtil.getPasswordHash(newPassword, newPasswordSaltHash);

        Example example = new Example(UserPO.class);

        String account = passwordResetDTO.getAccount();

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("account", account);

        UserPO userPO = new UserPO();
        userPO.setPassword(realPasswordHash);
        userPO.setPasswordSalt(newPasswordSaltHash);
        userPO.setUpdatedTime(new Date());

        userMapper.updateByExampleSelective(userPO, example);
        return true;
    }

    private void doCheckOldPassword(UserPasswordResetDTO passwordResetDTO) {
        String account = passwordResetDTO.getAccount();
        UserPO userPO = getByAccount(account);

        if (userPO == null) {
            throw new RuntimeException("修改密码失败");
        }

        String realPasswordHash = userPO.getPassword();
        String realPasswordSalt = userPO.getPasswordSalt();

        String oldPassword = passwordResetDTO.getOldPassword();

        String passwordHash = PasswordUtil.sha256Hex(oldPassword + realPasswordSalt);
        if (!StringUtils.equals(realPasswordHash, passwordHash)) {
            throw new RuntimeException("修改密码失败");
        }

    }
}
