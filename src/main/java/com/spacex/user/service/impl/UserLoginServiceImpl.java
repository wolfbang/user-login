package com.spacex.user.service.impl;

import com.google.common.collect.Maps;
import com.spacex.user.dto.UserLoginDTO;
import com.spacex.user.dto.UserRegisterDTO;
import com.spacex.user.repository.mapper.UserMapper;
import com.spacex.user.repository.po.UserPO;
import com.spacex.user.service.UserLoginService;
import com.spacex.user.service.UserService;
import com.spacex.user.util.BeanCopyUtil;
import com.spacex.user.util.JsonUtil;
import com.spacex.user.util.PasswordUtil;
import com.spacex.user.util.TokenUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    private Logger logger = LoggerFactory.getLogger(UserLoginServiceImpl.class);

    private static final int TOKEN_LENGTH = 32;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Override
    public String register(UserRegisterDTO userRegisterDTO) {
        logger.info(String.format("UserLoginServiceImpl#register userRegisterDTO:%s", JsonUtil.toJson(userRegisterDTO)));
        doCheck(userRegisterDTO);
        UserPO userPO = BeanCopyUtil.map(userRegisterDTO, UserPO.class);

        String passwordSalt = RandomStringUtils.random(TOKEN_LENGTH, true, false);
        String passwordSaltHash = PasswordUtil.sha256Hex(passwordSalt);

        String passwordHash = PasswordUtil.sha256Hex(userRegisterDTO.getPassword() + passwordSaltHash);

        userPO.setAccount(userRegisterDTO.getAccount());
        userPO.setPassword(passwordHash);
        userPO.setPasswordSalt(passwordSaltHash);
        userPO.setStatus(1);

        Date current = new Date();
        userPO.setLastLoginTime(current);
        userPO.setCreatedTime(current);
        userPO.setUpdatedTime(current);

        userMapper.insertSelective(userPO);
        return TokenUtil.generate(TOKEN_LENGTH);
    }

    private void doCheck(UserRegisterDTO userRegisterDTO) {
        String account = userRegisterDTO.getAccount();
        UserPO userPO = userService.getByAccount(account);
        if (userPO != null) {
            throw new RuntimeException(String.format("账号:%s已经被占用！", account));
        }
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) {
        logger.info(String.format("UserLoginServiceImpl#login userLoginDTO:%s", JsonUtil.toJson(userLoginDTO)));

        String account = userLoginDTO.getAccount();
        String password = userLoginDTO.getPassword();

        doCheckFailCounter(account, 5);
        doCheckPassword(account, password);

        String token = TokenUtil.generate(TOKEN_LENGTH);
        doSetUserTokenMap(account, token);

        updateLastLoginTime(account);
        return token;
    }

    private void doCheckFailCounter(String account, int maxLimit) {
        String failKey = "magellan:user:login:fail:" + account;
        String counter = stringRedisTemplate.opsForValue().get(failKey);

        if (StringUtils.isNotBlank(counter) && StringUtils.isNumeric(counter) && Integer.parseInt(counter) > maxLimit) {
            Long expiredTime = stringRedisTemplate.getExpire(failKey);
            Long time = (expiredTime / 3600L);
            String message = String.format("登录错误次数过多，账号被锁定，请过%s个小时再尝试", time);

            if (time <= 0) {
                time = expiredTime / 60;
                message = String.format("登录错误次数过多，账号被锁定，请过%s个分钟再尝试", time);
            }
            throw new RuntimeException(message);

        }
    }

    private void doCheckPassword(String account, String password) {

        UserPO userPO = userService.getByAccount(account);

        if (userPO == null) {
            String failKey = "magellan:user:login:fail:" + account;
            stringRedisTemplate.opsForValue().increment(failKey, 1L);
            stringRedisTemplate.expire(failKey, 6, TimeUnit.HOURS);
            throw new RuntimeException("用户名或者秘密不正确");
        }

        String realPasswordHash = userPO.getPassword();
        String realPasswordSaltHash = userPO.getPasswordSalt();

        String passwordHash = PasswordUtil.getPasswordHash(password, realPasswordSaltHash);

        if (!StringUtils.equalsIgnoreCase(realPasswordHash, passwordHash)) {
            String failKey = "magellan:user:login:fail:" + account;
            stringRedisTemplate.opsForValue().increment(failKey, 1L);
            stringRedisTemplate.expire(failKey, 6, TimeUnit.HOURS);
            throw new RuntimeException("用户名或者秘密不正确");
        }

    }

    private void doSetUserTokenMap(String account, String token) {
        String key = "magellan:user:token:info:" + token;
        Map<String, String> userLoginInfoMap = Maps.newHashMap();
        userLoginInfoMap.put("account", account);
        userLoginInfoMap.put("token", token);
        stringRedisTemplate.opsForHash().putAll(key, userLoginInfoMap);
        stringRedisTemplate.expire(key, 2, TimeUnit.HOURS);
    }

    private void updateLastLoginTime(String account) {
        Example example = new Example(UserPO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("account", account);

        UserPO userPO = new UserPO();
        userPO.setLastLoginTime(new Date());
        userMapper.updateByExampleSelective(userPO, example);
    }

    @Override
    public boolean logout(String account, String token) {
        String key = "magellan:user:token:info:" + account;
        Map<Object, Object> userTokenInfoMap = stringRedisTemplate.opsForHash().entries(key);

        if (MapUtils.isNotEmpty(userTokenInfoMap)) {
            String accountInRedis = (String) userTokenInfoMap.get("account");
            if (StringUtils.equals(account, accountInRedis)) {
                stringRedisTemplate.delete(key);
                return true;
            }
        }
        return false;
    }
}
