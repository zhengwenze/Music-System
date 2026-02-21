package org.zwz.mod_singer.service;

import org.zwz.mod_music.entity.Mess;
import org.zwz.mod_singer.entity.Music;

/**
 * 音乐服务接口
 */
public interface MusicService {
    
    /**
     * 分页查询音乐
     */
    Mess getMusic(String keyword, Integer id, Integer pn, Integer size);
    
    /**
     * 添加音乐
     */
    Mess addMusic(Music music, Integer id);
    
    /**
     * 更新音乐
     */
    Mess updateMusic(Music music, Integer id);
    
    /**
     * 删除音乐
     */
    Mess deleteMusic(Integer musicId, Integer id);
    
    /**
     * 获取消息
     */
    Mess getMessage(Integer id);
    
    /**
     * 获取当前歌手发布的所有歌曲
     */
    Mess getAllSongsBySingerId(Integer id);
}