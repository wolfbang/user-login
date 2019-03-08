package com.spacex.user.service.impl;

import com.spacex.user.dto.login.QQLoginDTO;
import com.spacex.user.dto.login.WechatInfoDTO;
import com.spacex.user.dto.login.WechatLoginDTO;
import com.spacex.user.enums.LoginType;
import com.spacex.user.repository.mapper.UserAuthMapper;
import com.spacex.user.repository.mapper.UserMapper;
import com.spacex.user.repository.po.UserAuthPO;
import com.spacex.user.repository.po.UserPO;
import com.spacex.user.service.OAuthLoginService;
import com.spacex.user.service.tecent.WechatService;
import com.spacex.user.util.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OAuthLoginServiceImpl implements OAuthLoginService {

    private Logger logger = LoggerFactory.getLogger(OAuthLoginServiceImpl.class);

    @Resource
    private UserAuthMapper userAuthMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private WechatService wechatService;

    @Transactional
    @Override
    public String loginByWechat(WechatLoginDTO wechatLoginDTO) {
        logger.info(String.format("OAuthLoginServiceImpl#loginByWechat wechatLoginDTO:%s", JsonUtil.toJson(wechatLoginDTO)));
        String grantCode = wechatLoginDTO.getGrantCode();
        WechatInfoDTO wechatInfoDTO = wechatService.getWechatUserInfo(grantCode);

        Example example = new Example(UserAuthPO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", LoginType.WECHAT.getCode());
        criteria.andEqualTo("account", wechatInfoDTO.getOpenid());

        List<UserAuthPO> userAuthPOs = userAuthMapper.selectByExample(example);

        boolean isNewUser = CollectionUtils.isEmpty(userAuthPOs) ? true : false;

        if (!isNewUser) {

        } else {

            // create user
            UserPO userPO = new UserPO();
            userPO.setName(wechatInfoDTO.getNickname());
            userPO.setStatus(1);
            userMapper.insertSelective(userPO);

            Long userId = userPO.getId();

            // create userAuth
            UserAuthPO userAuthPO = new UserAuthPO();
            userAuthPO.setType(LoginType.WECHAT.getCode());
            userAuthPO.setAccount(wechatInfoDTO.getOpenid());
            userAuthPO.setUserId(userId);
            userAuthMapper.insertSelective(userAuthPO);
        }


        return null;
    }

    @Override
    public String loginByQQ(QQLoginDTO qqLoginDTO) {
        return null;
    }
}
