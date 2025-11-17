package com.zxylearn.chat_session_service.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxylearn.chat_session_service.Utils.JwtUtil;
import com.zxylearn.chat_session_service.pojo.entity.Message;
import com.zxylearn.chat_session_service.pojo.entity.Session;
import com.zxylearn.chat_session_service.pojo.entity.SessionMember;
import com.zxylearn.chat_session_service.pojo.entity.User;
import com.zxylearn.chat_session_service.redis.RedisService;
import com.zxylearn.chat_session_service.service.MessageService;
import com.zxylearn.chat_session_service.service.SessionMemberService;
import com.zxylearn.chat_session_service.service.SessionService;
import com.zxylearn.chat_session_service.service.UserService;

import lombok.extern.slf4j.Slf4j;

import com.zxylearn.chat_session_service.hutool.IdGeneratorService;

@Slf4j
@RestController
public class SessionController {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IdGeneratorService idGeneratorService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionMemberService sessionMemberService;

    @Autowired
    private MessageService messageService;

    ////////////////////////////////////////
    // 申请添加好友
    ////////////////////////////////////////
    @PostMapping("applyAddFriend")
    public boolean applyAddFriend(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("friendId") Long friendId) throws Exception {

        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return false;
        }

        if(userId.equals(friendId)) {
            return false;
        }

        // 判断用户是否存在
        if (!userService.isUserExist(friendId)) {
            return false;
        }

        // 判断用户是否已经是好友关系
        if (sessionService.areFriends(userId, friendId)) {
            return false;
        }

        // 添加好友申请缓存
        redisService.addFriendApplication(userId.toString(), friendId.toString());

        // 向被申请者发送消息
        Map<String, String> content = new HashMap();
        content.put("applicantId", userId.toString());
        String processedContent = objectMapper.writeValueAsString(content);

        Message message = new Message();
        message.setId(idGeneratorService.getId());
        message.setSessionId(-1L);
        message.setSenderId(-1L);
        message.setType("friend_application");
        message.setCreateTime(new Date());
        message.setContent(processedContent);
        messageService.save(message);
        String processedMessage = objectMapper.writeValueAsString(message);
        if (redisService.isUserOnline(friendId.toString())) {
            redisService.publish(friendId.toString(), processedMessage);
        } else {
            redisService.addUnreadMessage(friendId.toString(), message.getId().toString());
        }

        return true;
    }

    ////////////////////////////////////////
    // 接受添加好友
    ////////////////////////////////////////
    @PostMapping("acceptAddFriend")
    public boolean acceptAddFriend(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("applicantId") Long applicantId) throws Exception {

        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return false;
        }

        // 判断接受是否合法
        if (!redisService.isFriendApplication(applicantId.toString(), userId.toString())) {
            return false;
        }

        // 创建会话关系
        Session session = new Session();
        session.setCreaterId(applicantId);
        session.setPeerId(userId);
        if (!sessionService.addSession(session)) {
            return false;
        }

        // 将好友关系插入会话
        User applicant = userService.getUser(applicantId);
        User requester = userService.getUser(userId);
        SessionMember sessionMember = new SessionMember();
        sessionMember.setSessionId(session.getId());

        sessionMember.setId(null);
        sessionMember.setUserId(userId);
        sessionMember.setRemark(applicant.getNickname());
        sessionMemberService.addSessionMember(sessionMember);

        sessionMember.setId(null);
        sessionMember.setUserId(applicantId);
        sessionMember.setRemark(requester.getNickname());
        sessionMemberService.addSessionMember(sessionMember);

        // 删除好友申请缓存
        redisService.delFriendApplication(applicantId.toString(), userId.toString());

        // 更新会话缓存
        redisService.updateSessionMembers(session.getId().toString(), applicantId.toString(), true);
        redisService.updateSessionMembers(session.getId().toString(), userId.toString(), true);

        // 向申请者和被申请者发送好友通过消息
        Map<String, String> content = new HashMap();
        content.put("sessionId", session.getId().toString());
        content.put("applicantId", applicant.getId().toString());
        content.put("applicantNickName", applicant.getNickname());
        content.put("applicantAvatar", applicant.getAvatar());
        content.put("requesterId", requester.getId().toString());
        content.put("requesterNickName", requester.getNickname());
        content.put("requesterAvatar", requester.getAvatar());
        String processedContent = objectMapper.writeValueAsString(content);

        Message message = new Message();
        message.setId(idGeneratorService.getId());
        message.setSessionId(-1L);
        message.setSenderId(-1L);
        message.setType("friend_acceptance");
        message.setCreateTime(new Date());
        message.setContent(processedContent);
        messageService.save(message);
        String processedMessage = objectMapper.writeValueAsString(message);
        if (redisService.isUserOnline(applicantId.toString())) {
            redisService.publish(applicantId.toString(), processedMessage);
        } else {
            redisService.addUnreadMessage(applicantId.toString(), message.getId().toString());
        }
        if (redisService.isUserOnline(userId.toString())) {
            redisService.publish(userId.toString(), processedMessage);
        } else {
            redisService.addUnreadMessage(userId.toString(), message.getId().toString());
        }

        return true;
    }

    ////////////////////////////////////////
    // 创建群聊
    ////////////////////////////////////////
    @PostMapping("createGroupSession")
    public Long createGroupSession(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("name") String name) throws Exception {

        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return null;
        }

        // 创建群聊
        Session session = new Session();
        session.setCreaterId(userId);
        session.setPeerId(-1L);
        if (!sessionService.addSession(session)) {
            return null;
        }

        // 将用户加入群聊
        SessionMember sessionMember = new SessionMember();
        sessionMember.setSessionId(session.getId());
        sessionMember.setUserId(session.getCreaterId());
        sessionMember.setRemark(null);
        sessionMemberService.addSessionMember(sessionMember);

        return session.getId();
    }

    ////////////////////////////////////////
    // 申请加入群聊
    ////////////////////////////////////////
    @PostMapping("applyJoinGroup")
    public boolean applyJoinGroup(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("sessionId") Long sessionId) throws Exception {

        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return false;
        }

        // 检查群聊是否存在
        Session session = sessionService.getSession(sessionId);
        if (session == null || !session.getPeerId().equals(-1L)) {
            return false;
        }
        Long createrId = session.getCreaterId();

        // 检查用户是否已经是群成员
        if(sessionMemberService.isUserInSession(userId, sessionId)) {
            return false;
        }

        // 添加加入群聊申请缓存
        redisService.addGroupApplication(userId.toString(), sessionId.toString(), createrId.toString());

        // 向群主发送消息
        Map<String, String> content = new HashMap();
        content.put("applicantId", userId.toString());
        content.put("sessionId", sessionId.toString());
        String processedContent = objectMapper.writeValueAsString(content);

        Message message = new Message();
        message.setId(idGeneratorService.getId());
        message.setSessionId(-1L);
        message.setSenderId(-1L);
        message.setType("group_application");
        message.setCreateTime(new Date());
        message.setContent(processedContent);
        messageService.save(message);
        String processedMessage = objectMapper.writeValueAsString(message);
        if(redisService.isUserOnline(createrId.toString())) {
            redisService.publish(createrId.toString(), processedMessage);
        } else {
            redisService.addUnreadMessage(createrId.toString(), message.getId().toString());
        }

        return true;
    }

    ////////////////////////////////////////
    // 同意加入群聊
    ////////////////////////////////////////
    @PostMapping("acceptJoinGroup")
    public boolean acceptJoinGroup(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("sessionId") Long sessionId,
            @RequestParam("applicantId") Long applicantId) throws Exception {
        
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return false;
        }

        // 判断接受是否合法
        if (!redisService.isGroupApplication(applicantId.toString(), sessionId.toString(), userId.toString())) {
            return false;
        }

        // 将用户加入群聊
        SessionMember sessionMember = new SessionMember();
        sessionMember.setSessionId(sessionId);
        sessionMember.setUserId(applicantId);
        sessionMember.setRemark(null);
        sessionMemberService.addSessionMember(sessionMember);

        // 删除加入群聊申请缓存
        redisService.delGroupApplication(applicantId.toString(), sessionId.toString(), userId.toString());

        // 更新会话缓存
        redisService.updateSessionMembers(sessionId.toString(), applicantId.toString(), true);

        // 向申请者发送群聊通过消息
        Session session = sessionService.getSession(sessionId);
        Map<String, String> content = new HashMap();
        content.put("sessionId", sessionId.toString());
        content.put("sessionName", session.getName());
        content.put("sessionAvatar", session.getAvatar());
        String processedContent = objectMapper.writeValueAsString(content);

        Message message = new Message();
        message.setId(idGeneratorService.getId());
        message.setSessionId(-1L);
        message.setSenderId(-1L);
        message.setType("group_acceptance");
        message.setCreateTime(new Date());
        message.setContent(processedContent);
        messageService.save(message);
        String processedMessage = objectMapper.writeValueAsString(message);
        if (redisService.isUserOnline(applicantId.toString())) {
            redisService.publish(applicantId.toString(), processedMessage);
        } else {
            redisService.addUnreadMessage(applicantId.toString(), message.getId().toString());
        }

        return true;
    }
}
