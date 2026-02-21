package org.zwz.mod_like.service;

import org.zwz.mod_like.entity.Mess;
import org.zwz.mod_like.entity.SongList;

/**
 * <p>
 * 歌单表 服务类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
public interface SongListService {
    //统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开
    Mess search(String keyword);

    Mess getSongList(Integer listId, Integer id);

    /**
     * 获取歌单详情，包含用户收藏状态
     * @param listId 歌单ID
     * @param userId 用户ID
     * @return 包含歌单详情和收藏状态的Mess对象
     */
    Mess getSongListDetail(Integer listId, Integer userId);

    Mess recommendList();

    Mess createList(SongList songlist, Integer id);

    Mess getMySongList(Integer id);

    Mess removeSongList(Integer listId, Integer id);
}