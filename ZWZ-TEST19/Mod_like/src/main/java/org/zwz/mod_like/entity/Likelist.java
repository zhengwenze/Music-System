package org.zwz.mod_like.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户歌单收藏表，负责存储用户收藏的歌单
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
@Getter
@Setter
@TableName("likelist")
public class Likelist implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 收藏id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @TableField("userId")
    private Integer userId;

    /**
     * 歌单id
     */
    @TableField("listId")
    private Integer listId;

    /**
     * 收藏时间
     */
    private LocalDateTime createTime;
}