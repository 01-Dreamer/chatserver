package com.zxylearn.chat_message_service.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxylearn.chat_message_service.pojo.entity.Message;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}
