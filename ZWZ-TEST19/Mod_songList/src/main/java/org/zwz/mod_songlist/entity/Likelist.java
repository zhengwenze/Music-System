package org.zwz.mod_songlist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户歌单收藏表
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
@Getter
@Setter
@TableName("likelist")
public class Likelist implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 歌单id
     */
    private Integer listId;

    /**
     * 收藏时间
     */
    private LocalDateTime createTime;
}