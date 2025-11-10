package com.zxylearn.chat_message_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxylearn.chat_message_service.pojo.entity.Message;

public interface MessageService extends IService<Message> {
    boolean saveMessage(Message message);
}
