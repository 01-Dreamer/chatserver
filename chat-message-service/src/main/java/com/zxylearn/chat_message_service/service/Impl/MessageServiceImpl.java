package com.zxylearn.chat_message_service.service.Impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxylearn.chat_message_service.mapper.MessageMapper;
import com.zxylearn.chat_message_service.pojo.entity.Message;
import com.zxylearn.chat_message_service.service.MessageService;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Override
    public boolean saveMessage(Message message) {
        if(message == null) {
            return false;
        }
        return baseMapper.insert(message) > 0;
    }
}
