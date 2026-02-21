package org.zwz.mod_msg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("msg")
public class Msg {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("title")
    private String title;

    @TableField("user_id")
    private Integer userId;

    @TableField("msg")
    private String msg;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("isread")
    private Integer isRead;

    // 修复方法名，与字段名保持一致
    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}