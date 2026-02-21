package org.zwz.mod_songlist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.zwz.mod_songlist.entity.SongList;

import java.util.List;

/**
 * <p>
 * 歌单表 Mapper 接口
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
public interface SongListMapper extends BaseMapper<SongList> {

    @Select("SELECT\n" +
            "song_list.*,\n" +
            "user.username\n" +
            "FROM\n" +
            "song_list ,\n" +
            "user\n" +
            "WHERE\n" +
            "song_list.user = user.id AND\n" +
            "song_list.name LIKE CONCAT('%', #{keyword}, '%')")
    List<SongList> search(@Param(value = "keyword") String keyword);

    @Select("SELECT\n" +
            "song_list.*,\n" +
            "user.username\n" +
            "FROM\n" +
            "song_list ,\n" +
            "user\n" +
            "WHERE\n" +
            "song_list.user = user.id AND\n" +
            "song_list.id = #{listId}")
    SongList getListById(@Param(value = "listId") Integer listId);

    @Select("SELECT\n" +
            "song_list.*,\n" +
            "user.username\n" +
            "FROM\n" +
            "song_list ,\n" +
            "user ,\n" +
            "likelist\n" +
            "WHERE\n" +
            "song_list.user = user.id AND\n" +
            "song_list.id = likelist.listID AND\n" +
            "likelist.userid = #{id}")
    List<SongList> getLikeSongList(@Param(value = "id") Integer id);

    @Select("SELECT\n" +
            "song_list.*,\n" +
            "user.username\n" +
            "FROM\n" +
            "song_list\n" +
            "JOIN user ON song_list.user = user.id\n" +
            "ORDER BY RAND()\n" +
            "LIMIT 10")
    List<SongList> recommendList();
}
