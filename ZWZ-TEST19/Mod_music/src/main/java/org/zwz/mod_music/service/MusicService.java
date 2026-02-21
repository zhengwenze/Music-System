package org.zwz.mod_music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_music.entity.Mess;
import org.zwz.mod_music.entity.Music;

import java.util.List;

public interface MusicService extends IService<Music> {

    // 推荐歌曲接口
    Mess recommend();

    // 音乐排行榜接口
    Mess getRecommendList(String param);

    // 搜索歌曲
    Mess search(String keyword);

    // 获取歌曲详情，用音乐id（musicId）来查询音乐详情。
    Mess getMusic(Integer musicId, Integer id);

    // 获取歌手推荐
    Mess getRecommendSinger(Integer id);

    // 获取歌手详情，包括歌手信息和音乐列表，用歌手id来查询。
    Mess singerDetail(Integer id);

    // 获取最新歌曲推荐，用size来指定推荐歌曲数量。
    Mess getRecommendNewest(Integer size);

    Mess addMusic(Music music, Integer id);

    Mess getMusic(String keyword, Integer id, Integer pn, Integer size);

    Mess getMessage(Integer id);
    
    // 获取歌手的所有音乐
    List<Music> getSingerMusic(Integer id);
    
    // 更新音乐信息
    Mess updateMusic(Music music, Integer id);
    
    // 删除音乐
    Mess deleteMusic(Integer musicId, Integer id);
}