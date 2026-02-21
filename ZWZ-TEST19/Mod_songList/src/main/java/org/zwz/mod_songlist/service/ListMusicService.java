package org.zwz.mod_songlist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_songlist.entity.ListMusic;
import org.zwz.mod_songlist.entity.Mess;
import org.zwz.mod_songlist.entity.Music;

import java.util.List;

/**
 * <p>
 * 歌单歌曲关联表 服务类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
public interface ListMusicService extends IService<ListMusic> {
// 只要实现以下2方法即可（addSong和removeSong），不要添加其他方法
// 只要实现以下2方法即可（addSong和removeSong），不要添加其他方法
// 只要实现以下2方法即可（addSong和removeSong），不要添加其他方法
    /**
     * 添加歌曲到歌单
     * @param songId 歌曲ID
     * @param listId 歌单ID
     * @return 添加结果
     */
    Mess addSong(Integer songId, Integer listId);

    /**
     * 移除歌单中的歌曲
     * @param songId 歌曲ID
     * @param listId 歌单ID
     * @return 移除结果
     */
    Mess removeSong(Integer songId, Integer listId);
}