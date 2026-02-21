package org.zwz.mod_like.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_like.entity.ListMusic;
import org.zwz.mod_like.entity.Mess;
import org.zwz.mod_like.entity.Music;
import org.zwz.mod_like.mapper.ListMusicMapper;
import org.zwz.mod_like.service.ListMusicService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 歌单歌曲关联表 服务实现类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
@Service
public class ListMusicServiceImpl extends ServiceImpl<ListMusicMapper, ListMusic> implements ListMusicService {

    @Autowired
    private ListMusicMapper listMusicMapper;

    @Override
    public List<Music> getMusicListByListId(Integer listId) {
        return listMusicMapper.getMusicListByListId(listId);
    }

    @Override
    public boolean deleteByListId(Integer listId) {
        int count = listMusicMapper.deleteByListId(listId);
        return count > 0;
    }

    @Override
    public boolean deleteByListIdAndMusicId(Integer listId, Integer musicId) {
        int count = listMusicMapper.deleteByListIdAndMusicId(listId, musicId);
        return count > 0;
    }

    @Override
    public boolean batchInsert(List<ListMusic> listMusics) {
        if (listMusics == null || listMusics.isEmpty()) {
            return false;
        }
        int count = listMusicMapper.batchInsert(listMusics);
        return count == listMusics.size();
    }

    @Override
    public int countByListId(Integer listId) {
        return listMusicMapper.countByListId(listId);
    }

    @Override
    public boolean addMusicToList(ListMusic listMusic) {
        return save(listMusic);
    }

    @Override
    public Mess addSong(Integer songId, Integer listId) {
        try {
            // 检查是否已经添加过
            QueryWrapper<ListMusic> wrapper = new QueryWrapper<>();
            wrapper
                    .eq("listid", listId)
                    .eq("music", songId);
            if (count(wrapper) == 0) {
                ListMusic listMusic = new ListMusic();
                // 使用反射设置字段值
                try {
                    // 设置listid字段
                    java.lang.reflect.Field listIdField = ListMusic.class.getDeclaredField("listid");
                    listIdField.setAccessible(true);
                    listIdField.set(listMusic, listId);
                    
                    // 设置music字段
                    java.lang.reflect.Field musicField = ListMusic.class.getDeclaredField("music");
                    musicField.setAccessible(true);
                    musicField.set(listMusic, songId);
                    
                    // 设置addTime字段
                    java.lang.reflect.Field addTimeField = ListMusic.class.getDeclaredField("addTime");
                    addTimeField.setAccessible(true);
                    addTimeField.set(listMusic, new Date());
                } catch (Exception e) {
                    e.printStackTrace();
                    return Mess.fail().message("设置字段失败");
                }
                save(listMusic);
                return Mess.success().message("添加歌曲成功");
            } else {
                return Mess.fail().message("添加失败，歌曲已存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Mess.fail().message("添加歌曲失败");
        }
    }

    @Override
    public Mess removeSong(Integer songId, Integer listId) {
        try {
            QueryWrapper<ListMusic> wrapper = new QueryWrapper<>();
            wrapper
                    .eq("music", songId)
                    .eq("listid", listId);
            return remove(wrapper) ? Mess.success().message("移除成功") : Mess.fail().message("移除失败");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Mess.fail().message("移除错误");
        }
    }
}