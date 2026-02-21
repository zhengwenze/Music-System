package org.zwz.mod_singer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 歌手实体类
 * 对应数据库user表中role=1的用户
 */
@TableName(value = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Singer implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "username")
    private String username;

    @TableField(value = "password")
    private String password;

    @TableField(value = "email")
    private String email;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "role")
    private Integer role;

    @TableField(value = "activation")
    private Integer activation;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "image_url")
    private String imageUrl;

    @TableField(value = "about")
    private String about;
}