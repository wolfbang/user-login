package com.spacex.user.repository.mapper;

import tk.mybatis.mapper.common.Mapper;
import com.spacex.user.repository.po.UserPO;

@org.apache.ibatis.annotations.Mapper
public interface UserMapper extends Mapper<UserPO> {
}
