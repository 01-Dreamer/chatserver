package com.zxylearn.chat_session_service.mapper;


import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxylearn.chat_session_service.pojo.entity.SessionMember;

@Mapper
public interface SessionMemberMapper extends BaseMapper<SessionMember> {

}
