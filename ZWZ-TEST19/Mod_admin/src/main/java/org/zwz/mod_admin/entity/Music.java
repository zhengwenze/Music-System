package org.zwz.mod_admin.entity;

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
 * 歌曲表实体类
 * 对应数据库中的music表，用于存储歌曲相关信息
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-19
 */
// MyBatis-Plus注解：指定实体类对应的数据库表名
@TableName(value = "music")
// Lombok注解：自动生成getter、setter、toString、equals、hashCode等方法
@Data
// Lombok注解：自动生成无参构造函数
@NoArgsConstructor
// Lombok注解：自动生成全参构造函数
@AllArgsConstructor
// 实现Serializable接口，支持序列化和反序列化
public class Music implements Serializable {
    // 序列化版本UID，用于版本控制
    private static final long serialVersionUID = 1L;

    // 主键ID，使用自增策略
    @TableId(value = "music_id", type = IdType.AUTO)
    private Integer musicId;

    // 歌曲名称，对应数据库music_name字段
    @TableField(value = "music_name")
    private String musicName;

    // 歌曲文件URL地址，对应数据库music_url字段
    @TableField(value = "music_url")
    private String musicUrl;

    // 歌曲文件大小（单位待定），此字段不在数据库表中
    @TableField(exist = false)
    private String musicSize;

    // 歌曲封面图片URL，对应数据库image_url字段
    @TableField(value = "image_url")
    private String imageUrl;

    // 歌手姓名，此字段不在数据库表中，可能是关联查询结果
    @TableField(exist = false)
    private String musicSinger;

    // 所属专辑名称，此字段不在数据库表中，可能是关联查询结果
    @TableField(exist = false)
    private String musicAlbum;

    // 歌曲时长（单位：秒），对应数据库timelength字段
    @TableField(value = "timelength")
    private Integer timeLength;

    // 歌词内容（旧字段，可能用于前端显示），此字段不在数据库表中
    @TableField(exist = false)
    private String musicLyric;

    // 歌词内容（新字段），对应数据库lyric字段
    @TableField(value = "lyric")
    private String lyric;

    // 收听次数，对应数据库listen_numb字段
    @TableField(value = "listen_numb")
    private Integer listenNumb;

    // 收藏数量，此字段不在数据库表中，可能是统计字段
    @TableField(exist = false)
    private Integer musicCollectCount;

    // 下载数量，此字段不在数据库表中，可能是统计字段
    @TableField(exist = false)
    private Integer musicDownloadCount;

    // 分享数量，此字段不在数据库表中，可能是统计字段
    @TableField(exist = false)
    private Integer musicShareCount;

    // 评论数量，此字段不在数据库表中，可能是统计字段
    @TableField(exist = false)
    private Integer musicCommentCount;

    // 歌手ID（外键），对应数据库from_singer字段
    @TableField(value = "from_singer")
    private Integer fromSinger;

    // 歌手用户名，对应数据库singer_username字段
    @TableField(value = "singer_username")
    private String singerUsername;

    // 歌曲创建日期，对应数据库create_time字段
    @TableField(value = "create_time")
    private LocalDate createTime;

    // 歌曲更新时间，此字段不在数据库表中
    @TableField(exist = false)
    private LocalDateTime musicUpdateTime;

    // 激活状态（0-未激活，1-已激活），对应数据库activation字段
    @TableField(value = "activation")
    private Integer activation;

    // 歌曲标签（多个标签用逗号分隔），对应数据库tags字段
    @TableField(value = "tags")
    private String tags;

    // 用户名（可能是上传者或相关用户），此字段不在数据库表中
    @TableField(exist = false)
    private String userName;

    // 标签列表（将tags字段按逗号分割后的列表），此字段不在数据库表中
    @TableField(exist = false)
    private List<String> tagList;

    // 当前用户是否喜欢此歌曲（0-不喜欢，1-喜欢），此字段不在数据库表中
    @TableField(exist = false)
    private Integer isLike;
}