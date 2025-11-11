package com.zxylearn.chat_message_service.service.Impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxylearn.chat_message_service.mapper.MessageMapper;
import com.zxylearn.chat_message_service.pojo.entity.Message;
import com.zxylearn.chat_message_service.service.MessageService;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Override
    @Async
    public void saveMessageAsync(Message message) {
        if(message == null) {
            return;
        }
        baseMapper.insert(message);
    }

    @Override
    public Message getMessageByMessageId(Long messageId) {
        if(messageId == null) {
            return null;
        }
        return baseMapper.selectById(messageId);
    }
}
