package org.zwz.mod_songlist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 歌单表
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
@TableName(value = "song_list")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongList implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "user")
    private Integer user;

    @TableField(value = "image")
    private String image;

    @TableField(value = "message")
    private String message;

    @TableField(value = "create_date")
    private LocalDate createDate;

    @TableField(value = "tags")
    private String tags;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private Integer isLike;
}
