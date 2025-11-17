package com.zxylearn.chat_payment_service.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxylearn.chat_payment_service.pojo.entity.Message;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}
