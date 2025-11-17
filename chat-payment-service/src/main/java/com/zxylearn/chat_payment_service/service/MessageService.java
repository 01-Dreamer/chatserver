package com.zxylearn.chat_payment_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxylearn.chat_payment_service.pojo.entity.Message;

public interface MessageService extends IService<Message> {
    public boolean save(Message message);
}