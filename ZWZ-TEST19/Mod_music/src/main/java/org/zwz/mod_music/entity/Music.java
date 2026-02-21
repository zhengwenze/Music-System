package org.zwz.mod_music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 歌曲表
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
@TableName(value = "music")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Music implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "music_id", type = IdType.AUTO)
    private Integer musicId;

    @TableField(value = "music_name")
    private String musicName;

    @TableField(value = "music_url")
    private String musicUrl;

    @TableField(exist = false)
    private String musicSize;

    @TableField(value = "image_url")
    private String imageUrl;

    @TableField(exist = false)
    private String musicSinger;

    @TableField(exist = false)
    private String musicAlbum;

    @TableField(value = "timelength")
    private Integer timeLength;

    @TableField(exist = false)
    private String musicLyric;

    @TableField(value = "lyric")
    private String lyric;

    @TableField(value = "listen_numb")
    private Integer listenNumb;

    @TableField(exist = false)
    private Integer musicCollectCount;

    @TableField(exist = false)
    private Integer musicDownloadCount;

    @TableField(exist = false)
    private Integer musicShareCount;

    @TableField(exist = false)
    private Integer musicCommentCount;
    @TableField(value = "from_singer")
    private Integer fromSinger;
// 音乐实体类Music.必须要有歌手用户名字段，不准删除，数据库里也有该字段singer_username
// 音乐实体类Music.必须要有歌手用户名字段，不准删除，数据库里也有该字段singer_username
// 音乐实体类Music.必须要有歌手用户名字段，不准删除，数据库里也有该字段singer_username
// 音乐实体类Music.必须要有歌手用户名字段，不准删除，数据库里也有该字段singer_username
// 音乐实体类Music.必须要有歌手用户名字段，不准删除，数据库里也有该字段singer_username
    @TableField(value = "singer_username")
    private String singerUsername;
    @TableField(value = "create_time")
    private LocalDate createTime;

    @TableField(exist = false)
    private LocalDateTime musicUpdateTime;

    @TableField(value = "activation")
    private Integer activation;

    @TableField(value = "tags")
    private String tags;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private List<String> tagList;

    @TableField(exist = false)
    private Integer isLike;
}