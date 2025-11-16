package com.zxylearn.chat_session_service.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("session_application")
public class SessionApplication {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("creater_id")
    private Long createrId;

    @TableField("is_group")
    boolean isGroup;

    @TableField("name")
    String name;

    @TableField("avatar")
    String avatar;
}
