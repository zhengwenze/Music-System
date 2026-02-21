package org.zwz.mod_like.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_like.entity.Mess;
import org.zwz.mod_like.entity.Music;
import org.zwz.mod_like.mapper.MusicMapper;
import org.zwz.mod_like.service.MusicService;

import java.util.List;

/**
 * <p>
 * 歌曲表 服务实现类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {
    @Autowired
    MusicMapper musicMapper;

    @Override
    public Mess search(String keyword) {
        return null;
    }

    @Override
    public Mess getMusicById(Integer musicId, Integer userId) {
        return null;
    }

    @Override
    public Mess getHotMusicList() {
        return null;
    }

    @Override
    public Mess getNewMusicList() {
        return null;
    }

    @Override
    public Mess addMusicToLikeList(Integer musicId, Integer userId) {
        return null;
    }

    @Override
    public Mess removeMusicFromLikeList(Integer musicId, Integer userId) {
        return null;
    }

    @Override
    public Mess getMyLikeMusic(Integer userId) {
        return null;
    }

    @Override
    public List<Music> getLikeMusic(Integer id) {
        List<Music> myLikeList = null;
        try {
            // 直接从数据库获取
            myLikeList = musicMapper.getLikeMusic(id);
        } catch (Exception e) {
            e.printStackTrace();
            myLikeList = musicMapper.getLikeMusic(id);
        }
        return myLikeList;
    }
}