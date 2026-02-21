package org.zwz.mod_singer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.zwz.mod_singer.entity.Singer;

import java.util.List;

/**
 * 歌手Mapper接口
 */
@Mapper
public interface SingerMapper extends BaseMapper<Singer> {

    /**
     * 根据角色查询用户（歌手）
     *
     * @param role 角色（1表示歌手）
     * @return 歌手列表
     */
    @Select("SELECT * FROM user WHERE role = #{role}")
    List<Singer> selectByRole(@Param("role") Integer role);

    /**
     * 根据用户名查询歌手
     *
     * @param username 用户名
     * @return 歌手信息
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND role = 1")
    Singer selectSingerByUsername(@Param("username") String username);

    /**
     * 查询所有激活的歌手
     *
     * @return 激活的歌手列表
     */
    @Select("SELECT * FROM user WHERE role = 1 AND activation = 0")
    List<Singer> selectActiveSingers();
}