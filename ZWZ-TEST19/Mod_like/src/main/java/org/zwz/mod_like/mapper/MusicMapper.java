package org.zwz.mod_like.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.zwz.mod_like.entity.Music;

import java.util.List;

@Mapper
public interface MusicMapper extends BaseMapper<Music> {
    @Select("SELECT music.*, `user`.username as userName FROM mylike JOIN music ON mylike.music = music.music_id JOIN `user` ON music.from_singer = `user`.id WHERE mylike.user = #{id}")
    List<Music> getLikeMusic(Integer id);
}