package com.zxylearn.chat_payment_service.pojo.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("message")
public class Message {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("session_id")
    private Long sessionId;

    @TableField("sender_id")
    private Long senderId;

    @TableField("type")
    private String type;

    @TableField("create_time")
    private Date createTime;

    @TableField("content")
    private String content;
}
