package org.zwz.mod_like.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_like.entity.Mess;
import org.zwz.mod_like.entity.Music;

import java.util.List;

/**
 * <p>
 * 歌曲表 服务类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
public interface MusicService extends IService<Music> {
    Mess search(String keyword);

    Mess getMusicById(Integer musicId, Integer userId);

    Mess getHotMusicList();

    Mess getNewMusicList();

    Mess addMusicToLikeList(Integer musicId, Integer userId);

    Mess removeMusicFromLikeList(Integer musicId, Integer userId);

    Mess getMyLikeMusic(Integer userId);

    //统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开
    //不要随意修改getLikeMusic！！！
    //该方法用于获取用户收藏的音乐列表
    //参数：id - 用户ID
    //返回：包含用户收藏的音乐列表的Mess对象
    List<Music> getLikeMusic(Integer id);
}