package org.zwz.mod_like.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 歌单歌曲关联表
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
@Getter
@Setter
@TableName("list_music")
public class ListMusic implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 歌曲id
     */
    private Integer music;

    /**
     * 歌单id
     */
    private Integer listid;

    /**
     * 添加时间
     */
    private LocalDateTime addTime;

    /**
     * 歌手用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String singerUsername;
}