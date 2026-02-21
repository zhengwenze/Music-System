package org.zwz.mod_like.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_like.entity.ListMusic;
import org.zwz.mod_like.entity.Mess;
import org.zwz.mod_like.entity.Music;

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

    /**
     * 根据歌单ID获取歌曲列表
     *
     * @param listId 歌单ID
     * @return 歌曲列表
     */
    List<Music> getMusicListByListId(Integer listId);

    /**
     * 根据歌单ID删除所有关联的歌曲
     *
     * @param listId 歌单ID
     * @return 删除的数量
     */
    boolean deleteByListId(Integer listId);

    /**
     * 根据歌单ID和歌曲ID删除关联
     *
     * @param listId  歌单ID
     * @param musicId 歌曲ID
     * @return 删除的数量
     */
    boolean deleteByListIdAndMusicId(Integer listId, Integer musicId);

    /**
     * 批量添加歌曲到歌单
     *
     * @param listMusics 歌单歌曲关联列表
     * @return 添加的数量
     */
    boolean batchInsert(List<ListMusic> listMusics);

    /**
     * 统计歌单中的歌曲数量
     *
     * @param listId 歌单ID
     * @return 歌曲数量
     */
    int countByListId(Integer listId);

    /**
     * 添加单首歌曲到歌单
     *
     * @param listMusic 歌单歌曲关联
     * @return 是否添加成功
     */
    boolean addMusicToList(ListMusic listMusic);

    /**
     * 添加歌曲到歌单
     *
     * @param songId 歌曲ID
     * @param listId 歌单ID
     * @return 添加结果
     */
    Mess addSong(Integer songId, Integer listId);

    /**
     * 移除歌单中的歌曲
     *
     * @param songId 歌曲ID
     * @param listId 歌单ID
     * @return 移除结果
     */
    Mess removeSong(Integer songId, Integer listId);
}