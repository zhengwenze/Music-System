package org.zwz.mod_singer.service;

import org.zwz.mod_singer.entity.Singer;

import java.util.List;

/**
 * 歌手服务接口
 */
public interface SingerService {

    /**
     * 获取所有歌手
     *
     * @return 歌手列表
     */
    List<Singer> getAllSingers();

    /**
     * 根据ID获取歌手
     *
     * @param id 歌手ID
     * @return 歌手信息
     */
    Singer getSingerById(Integer id);

    /**
     * 根据用户名获取歌手
     *
     * @param username 用户名
     * @return 歌手信息
     */
    Singer getSingerByUsername(String username);

    /**
     * 获取所有激活的歌手
     *
     * @return 激活的歌手列表
     */
    List<Singer> getActiveSingers();

    /**
     * 更新歌手信息
     *
     * @param singer 歌手信息
     * @return 是否成功
     */
    boolean updateSinger(Singer singer);

    /**
     * 激活歌手
     *
     * @param id 歌手ID
     * @return 是否成功
     */
    boolean activateSinger(Integer id);

    /**
     * 禁用歌手
     *
     * @param id 歌手ID
     * @return 是否成功
     */
    boolean deactivateSinger(Integer id);
}