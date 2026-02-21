package org.zwz.mod_like.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.zwz.mod_like.entity.ListMusic;
import org.zwz.mod_like.entity.Music;

import java.util.List;

/**
 * <p>
 * 歌单歌曲关联表 Mapper 接口
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
@Mapper
public interface ListMusicMapper extends BaseMapper<ListMusic> {

    @Select("SELECT user.username, music.* FROM music, user, list_music WHERE list_music.music = music.music_id AND music.from_singer = user.id AND list_music.listid = #{id}")
    List<Music> getMusicListByListId(@Param(value = "id") Integer id);

    /**
     * 根据歌单ID删除所有关联的歌曲
     *
     * @param listId 歌单ID
     * @return 删除的数量
     */
    @Delete("DELETE FROM list_music WHERE listid = #{listId}")
    int deleteByListId(@Param("listId") Integer listId);

    /**
     * 根据歌单ID和歌曲ID删除关联
     *
     * @param listId  歌单ID
     * @param musicId 歌曲ID
     * @return 删除的数量
     */
    @Delete("DELETE FROM list_music WHERE listid = #{listId} AND music = #{musicId}")
    int deleteByListIdAndMusicId(@Param("listId") Integer listId, @Param("musicId") Integer musicId);

    /**
     * 批量添加歌曲到歌单
     *
     * @param listMusics 歌单歌曲关联列表
     * @return 添加的数量
     */
    @Insert("<script>INSERT INTO list_music (listid, music) VALUES <foreach collection='listMusics' item='item' separator=','>(#{item.listid}, #{item.music})</foreach></script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int batchInsert(@Param("listMusics") List<ListMusic> listMusics);

    /**
     * 统计歌单中的歌曲数量
     *
     * @param listId 歌单ID
     * @return 歌曲数量
     */
    @Select("SELECT COUNT(*) FROM list_music WHERE listid = #{listId}")
    int countByListId(@Param("listId") Integer listId);
}