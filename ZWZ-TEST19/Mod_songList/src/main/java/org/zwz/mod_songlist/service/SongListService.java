package org.zwz.mod_songlist.service;

import org.zwz.mod_songlist.entity.Mess;
import org.zwz.mod_songlist.entity.SongList;

/**
 * <p>
 * 歌单表 服务类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
public interface SongListService {
    // 只要实现以下6个方法即可，不要添加其他方法
    // 1，添加查询歌单接口方法/search
    Mess search(String keyword);
    // 2，添加查询歌单详情接口方法/getSongList
    Mess getSongList(Integer listId, Integer id);
    // 3，添加推荐歌单接口方法/recommendList
    Mess recommendList();
    // 4，添加创建歌单接口方法/createList
    Mess createList(SongList songlist, Integer id);
    // 5，添加查询我的歌单接口方法/getMySongList
    Mess getMySongList(Integer id);
    // 6，添加删除歌单接口方法/removeSongList
    Mess removeSongList(Integer listId, Integer id);
    
    // 添加编辑歌单接口方法/updateList
    Mess updateList(SongList songList, Integer id);
}
