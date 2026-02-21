package org.zwz.mod_admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_admin.entity.Mess;
import org.zwz.mod_admin.entity.Music;
import org.zwz.mod_admin.mapper.MusicMapper;
import org.zwz.mod_admin.service.MusicService;

/**
 * <p>
 * 歌曲表 服务实现类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-21
 */
@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {
    @Autowired
    MusicMapper musicMapper;

    @Override
    public Mess searchMusic(Integer pn, Integer size, String keyword) {
        try {
            Page<Music> page = new Page<>(pn, size);
            QueryWrapper<Music> wrapper = new QueryWrapper<>();
            // 查询所有列，包括新增的singer_username
            wrapper.select("music_id", "music_name", "music_url", "image_url", "timelength", "lyric", "listen_numb", "from_singer", "create_time", "activation", "tags", "singer_username");
            if (keyword != null && !keyword.isEmpty()) {
                wrapper.like("music_name", keyword);
            }
            IPage<Music> musicIPage = musicMapper.selectPage(page, wrapper);
            return Mess.success().data("page", musicIPage);
        } catch (Exception e) {
            // 添加异常处理，记录错误信息
            e.printStackTrace();
            return Mess.fail().message("音乐分页查询失败：" + e.getMessage());
        }
    }
    
    @Override
    public Mess freezeMusic(Integer musicId) {
        UpdateWrapper<Music> wrapper = new UpdateWrapper<>();
        wrapper.eq("music_id", musicId).set("activation", 0);
        update(wrapper);
        return Mess.success().message("冻结成功");
    }

    @Override
    public Mess unFreezeMusic(Integer musicId) {
        UpdateWrapper<Music> wrapper = new UpdateWrapper<>();
        wrapper.eq("music_id", musicId).set("activation", 1);
        update(wrapper);
        return Mess.success().message("解冻成功");
    }

    @Override
    public Mess editMusic(Music music) {
        try {
            // 验证歌曲ID是否存在
            if (music.getMusicId() == null) {
                return Mess.fail().message("歌曲ID不能为空");
            }
            
            // 检查歌曲是否存在
            Music existingMusic = musicMapper.selectById(music.getMusicId());
            if (existingMusic == null) {
                return Mess.fail().message("该歌曲不存在");
            }
            
            // 使用UpdateWrapper进行选择性更新
            UpdateWrapper<Music> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("music_id", music.getMusicId());
            
            // 选择性更新非空字段
            if (music.getMusicName() != null) {
                updateWrapper.set("music_name", music.getMusicName());
            }
            if (music.getMusicUrl() != null) {
                updateWrapper.set("music_url", music.getMusicUrl());
            }
            if (music.getImageUrl() != null) {
                updateWrapper.set("image_url", music.getImageUrl());
            }
            if (music.getTimeLength() != null) {
                updateWrapper.set("timelength", music.getTimeLength());
            }
            if (music.getLyric() != null) {
                updateWrapper.set("lyric", music.getLyric());
            }
            if (music.getFromSinger() != null) {
                updateWrapper.set("from_singer", music.getFromSinger());
            }
            if (music.getSingerUsername() != null) {
                updateWrapper.set("singer_username", music.getSingerUsername());
            }
            if (music.getTags() != null) {
                updateWrapper.set("tags", music.getTags());
            }
            if (music.getActivation() != null) {
                updateWrapper.set("activation", music.getActivation());
            }
            
            // 执行更新操作
            int result = musicMapper.update(null, updateWrapper);
            
            if (result > 0) {
                return Mess.success().message("歌曲信息更新成功");
            } else {
                return Mess.fail().message("歌曲信息更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Mess.fail().message("歌曲信息更新异常：" + e.getMessage());
        }
    }
}




