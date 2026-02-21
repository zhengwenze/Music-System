package org.zwz.mod_like.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.zwz.mod_like.entity.Music;
import org.zwz.mod_like.entity.MyLike;

import java.util.List;

/**
 * <p>
 * 用户喜欢表 Mapper 接口
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
@Mapper
public interface MyLikeMapper extends BaseMapper<MyLike> {
    @Select("SELECT * FROM mylike WHERE user = #{userId}")
    List<MyLike> selectByUserId(@Param("userId") Integer userId);
    
    @Select("SELECT * FROM mylike WHERE user = #{userId} AND music = #{musicId}")
    MyLike selectByUserIdAndMusicId(@Param("userId") Integer userId, @Param("musicId") Integer musicId);
    
    @Select("SELECT ml.* FROM mylike ml JOIN songlist sl ON ml.music = sl.id WHERE ml.user = #{userId} AND sl.id = #{songListId}")
    MyLike selectByUserIdAndSongListId(@Param("userId") Integer userId, @Param("songListId") Integer songListId);
    
    @Delete("DELETE FROM mylike WHERE id = #{likeId} AND user = #{userId}")
    int deleteByLikeIdAndUserId(@Param("likeId") Integer likeId, @Param("userId") Integer userId);
    
    @Select("SELECT COUNT(*) FROM mylike WHERE user = #{userId}")
    int countByUserId(@Param("userId") Integer userId);
    
    @Select("SELECT m.*, u.username as userName FROM mylike ml JOIN music m ON ml.music = m.music_id JOIN user u ON m.from_singer = u.id WHERE ml.user = #{userId}")
    List<Music> selectMyLikeMusicList(@Param("userId") Integer userId);
}