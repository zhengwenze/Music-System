package org.zwz.mod_like.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.zwz.mod_like.entity.Likelist;

import java.util.List;

/**
 * <p>
 * 用户歌单收藏表 Mapper 接口
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
@Mapper  // 必须添加此注解
public interface LikelistMapper extends BaseMapper<Likelist> {

    /**
     * 根据用户ID查询收藏的歌单
     *
     * @param userId 用户ID
     * @return 收藏的歌单列表
     */
    @Select("SELECT * FROM likelist WHERE userId = #{userId}")
    List<Likelist> selectByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户ID和歌单ID查询收藏记录
     *
     * @param userId 用户ID
     * @param listId 歌单ID
     * @return 收藏记录
     */
    @Select("SELECT * FROM likelist WHERE userId = #{userId} AND listId = #{listId}")
    Likelist selectByUserIdAndListId(@Param("userId") Integer userId, @Param("listId") Integer listId);

    /**
     * 根据用户ID和歌单ID删除收藏
     *
     * @param userId 用户ID
     * @param listId 歌单ID
     * @return 删除的数量
     */
    @Delete("DELETE FROM likelist WHERE userId = #{userId} AND listId = #{listId}")
    int deleteByUserIdAndListId(@Param("userId") Integer userId, @Param("listId") Integer listId);

    /**
     * 统计用户收藏的歌单数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    @Select("SELECT COUNT(*) FROM likelist WHERE userId = #{userId}")
    int countByUserId(@Param("userId") Integer userId);

    /**
     * 根据歌单ID查询收藏该歌单的用户
     *
     * @param listId 歌单ID
     * @return 收藏该歌单的用户列表
     */
    @Select("SELECT * FROM likelist WHERE listId = #{listId}")
    List<Likelist> selectByListId(@Param("listId") Integer listId);

    /**
     * 统计歌单的收藏数量
     *
     * @param listId 歌单ID
     * @return 收藏数量
     */
    @Select("SELECT COUNT(*) FROM likelist WHERE listId = #{listId}")
    int countByListId(@Param("listId") Integer listId);
}