package com.zxylearn.chat_session_service.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxylearn.chat_session_service.mapper.SessionApplicationMapper;
import com.zxylearn.chat_session_service.pojo.entity.SessionApplication;
import com.zxylearn.chat_session_service.service.SessionApplicationService;

@Service
public class SessionApplicationServiceImpl extends ServiceImpl<SessionApplicationMapper, SessionApplication> implements SessionApplicationService {

}
