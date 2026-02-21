package org.zwz.mod_music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.zwz.mod_music.entity.Music;

import java.util.List;

/**
 * 歌曲表 Mapper 接口
 *
 * @since 2025-12-20
 */
public interface MusicMapper extends BaseMapper<Music> {

    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            music ,
            user
            WHERE
            music.from_singer = user.id AND
            music.activation = 1\s
            ORDER BY
            RAND()\s
            LIMIT #{size}""")
    public List<Music> recomendRandom(@Param(value = "size") Integer size);

    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            music ,
            user
            WHERE
            music.from_singer = user.id AND
            music.activation = 1\s
            ORDER BY
            music.create_time DESC
            LIMIT #{size}""")
    public List<Music> recomendNew(@Param(value = "size") Integer size);

    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            music ,
            user
            WHERE
            music.from_singer = user.id AND
            music.activation = 1 
            ORDER BY
            music.listen_numb DESC
            LIMIT #{size}""")
    public List<Music> recomendHot(@Param(value = "size") Integer size);

    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            music ,
            user
            WHERE
            music.from_singer = user.id AND
            music.activation = 1 AND
            (music.music_name LIKE '%${keyword}%' OR
            user.username LIKE '%${keyword}%' )
            ORDER BY music.listen_numb DESC""")
    public List<Music> search(@Param(value = "keyword") String keyword);

    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            music ,
            user
            WHERE
            music.from_singer = user.id AND
            music.activation = 1 AND
            music.music_id = #{id} """)
    public Music getMusic(@Param(value = "id") Integer id);

    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            my_like ,
            music ,
            user 
            WHERE
            my_like.music = music.music_id AND
            music.from_singer = user.id AND
            my_like.user = #{id}""")
    List<Music> getLikeMusic(Integer id);

    /**
     * 更新歌曲播放量
     *
     * @param id  歌曲ID
     * @param num 增加的播放量
     * @return 更新结果
     */
    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            music ,
            user
            WHERE
            music.from_singer = user.id AND
            music.activation = 1 AND
            music.from_singer = #{id} """)
    public List<Music> getSingerMusic(@Param(value = "id") Integer id);

    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            music ,
            user,
            my_follow
            WHERE
            music.from_singer = user.id AND
            music.from_singer = my_follow.follow_id AND
            music.activation = 1 AND
            my_follow.user_id = #{userId}
            ORDER BY
            music.create_time DESC
            LIMIT #{size}""")
    public List<Music> recomendFollow(@Param(value = "userId") Integer userId, @Param(value = "size") Integer size);

    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            music ,
            user,
            my_like
            WHERE
            music.from_singer = user.id AND
            music.activation = 1 AND
            my_like.music = music.music_id AND
            my_like.user = #{userId}
            ORDER BY
            music.listen_numb DESC
            LIMIT #{size}""")
    public List<Music> recomendFavorite(@Param(value = "userId") Integer userId, @Param(value = "size") Integer size);

    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            music ,
            user
            WHERE
            music.from_singer = user.id AND
            music.activation = 1 
            ORDER BY
            music.create_time DESC
            LIMIT #{size}""")
    public List<Music> recomendNewest(@Param(value = "size") Integer size);

    @Select("""
            SELECT
            music.*,
            user.username
            FROM
            music ,
            user
            WHERE
            user.id = music.from_singer AND
            music.music_name LIKE '%${search}%' AND
            music.from_singer = #{id}
            """)
    public com.baomidou.mybatisplus.core.metadata.IPage<Music> searchMusic(com.baomidou.mybatisplus.extension.plugins.pagination.Page<Music> page, @Param(value = "search") String search, @Param(value = "id")Integer id);

    @Select("""
            SELECT SUM( listen_numb )
            FROM
            music
            WHERE
            music.from_singer = #{id}
            """)
    Integer getMusicNumb(@Param(value = "id")Integer id);

}