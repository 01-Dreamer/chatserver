package com.zxylearn.chat_auth_service.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxylearn.chat_auth_service.pojo.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
