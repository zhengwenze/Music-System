package org.zwz.mod_like.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.zwz.mod_like.entity.SongList;

import java.util.List;

@Mapper
public interface SongListMapper extends BaseMapper<SongList> {
    @Select("SELECT song_list.*, user.username, 1 as isLike " +
            "FROM song_list " +
            "INNER JOIN likelist ON song_list.id = likelist.listId " +
            "INNER JOIN user ON song_list.user = user.id " +
            "WHERE likelist.userId = #{id}")
    public List<SongList> getLikeSongList(@Param(value = "id") Integer id);

    /**
     * 根据歌单ID和用户ID查询歌单详情，包含收藏状态
     * @param listId 歌单ID
     * @param userId 用户ID
     * @return 歌单详情
     */
    @Select("SELECT song_list.*, user.username, " +
            "CASE WHEN likelist.id IS NOT NULL THEN 1 ELSE 0 END as isLike " +
            "FROM song_list " +
            "INNER JOIN user ON song_list.user = user.id " +
            "LEFT JOIN likelist ON song_list.id = likelist.listId AND likelist.userId = #{userId} " +
            "WHERE song_list.id = #{listId}")
    public SongList getSongListById(@Param(value = "listId") Integer listId, @Param(value = "userId") Integer userId);
}