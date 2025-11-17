
package com.zxylearn.chat_payment_service.service.Impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxylearn.chat_payment_service.mapper.MessageMapper;
import com.zxylearn.chat_payment_service.pojo.entity.Message;
import com.zxylearn.chat_payment_service.service.MessageService;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Override
    public boolean save(Message message) {
        if(message == null) {
            return false;
        }
        return baseMapper.insert(message) > 0;
    }

}
