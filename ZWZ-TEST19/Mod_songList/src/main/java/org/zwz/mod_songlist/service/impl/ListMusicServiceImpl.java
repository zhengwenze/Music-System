package org.zwz.mod_songlist.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_songlist.entity.ListMusic;
import org.zwz.mod_songlist.entity.Mess;
import org.zwz.mod_songlist.mapper.ListMusicMapper;
import org.zwz.mod_songlist.service.ListMusicService;

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
    public Mess addSong(Integer songId, Integer songListId) {
        try {
            // 验证参数是否为空
            if (songId == null || songListId == null) {
                return Mess.fail().message("歌曲ID或歌单ID不能为空");
            }
            
            // 检查歌曲是否已在歌单中
            QueryWrapper<ListMusic> wrapper = new QueryWrapper<>();
            wrapper
                    .eq("music", songId)
                    .eq("listid", songListId);
            
            if (count(wrapper) == 0) {
                // 创建新的歌单歌曲关联
                ListMusic listMusic = new ListMusic();
                listMusic.setListid(songListId);
                listMusic.setMusic(songId);
                listMusic.setAddTime(java.time.LocalDateTime.now()); // 设置添加时间
                
                boolean saveResult = save(listMusic);
                return saveResult ? Mess.success().message("添加成功") : Mess.fail().message("添加失败");
            } else {
                return Mess.fail().message("歌曲已在歌单中");
            }
        } catch (Exception e) {
            System.out.println("添加歌曲到歌单异常: " + e.getMessage());
            // 对外键约束错误进行特殊处理
            if (e.getMessage().contains("foreign key constraint")) {
                return Mess.fail().message("添加失败：歌曲或歌单不存在");
            }
            return Mess.fail().message("添加错误: " + e.getMessage());
        }
    }

    @Override
    public Mess removeSong(Integer songId, Integer songListId) {
        try {
            // 验证参数是否为空
            if (songId == null || songListId == null) {
                return Mess.fail().message("歌曲ID或歌单ID不能为空");
            }
            
            QueryWrapper<ListMusic> wrapper = new QueryWrapper<>();
            wrapper
                    .eq("music", songId)
                    .eq("listid", songListId);

            // 检查记录是否存在
            if (count(wrapper) == 0) {
                return Mess.fail().message("歌单中不存在该歌曲");
            }
            
            boolean removeResult = remove(wrapper);
            return removeResult ? Mess.success().message("移除成功") : Mess.fail().message("移除失败");
        } catch (Exception e) {
            System.out.println("从歌单移除歌曲异常: " + e.getMessage());
            // 对外键约束错误进行特殊处理
            if (e.getMessage().contains("foreign key constraint")) {
                return Mess.fail().message("移除失败：歌曲或歌单不存在");
            }
            return Mess.fail().message("移除错误: " + e.getMessage());
        }
    }
}