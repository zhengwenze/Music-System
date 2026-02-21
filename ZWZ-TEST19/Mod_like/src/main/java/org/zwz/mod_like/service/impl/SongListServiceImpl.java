package org.zwz.mod_like.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_like.entity.Mess;
import org.zwz.mod_like.entity.Music;
import org.zwz.mod_like.entity.SongList;
import org.zwz.mod_like.mapper.ListMusicMapper;
import org.zwz.mod_like.mapper.SongListMapper;
import org.zwz.mod_like.service.SongListService;
import org.zwz.mod_like.utils.RedisUtils;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//歌单表 服务实现类
@Service
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements SongListService {

    // 注入依赖
    @Autowired
    SongListMapper songListMapper;
    @Autowired
    ListMusicMapper listMusicMapper;
    @Autowired
    RedisUtils redisUtils;

    @Override
    public Mess search(String keyword) {
        QueryWrapper<SongList> wrapper = new QueryWrapper<>();
        wrapper.like("listName", keyword);
        List<SongList> search = songListMapper.selectList(wrapper);
        return Mess.success().code(20).message("响应成功").data("songList", search);
    }

    @Override
    public Mess getSongList(Integer listId, Integer id) {
        // 查询歌单详情，包含收藏状态
        SongList songList = songListMapper.getSongListById(listId, id);
        if (songList == null) {
            return Mess.fail().message("歌单已被删除");
        }

        // 查询该歌单关联的歌曲列表
        List<Music> musicList = listMusicMapper.getMusicListByListId(listId);

        // 封装响应数据
        return Mess.success().data("listMessage", songList).data("musicList", musicList);
    }

    @Override
    public Mess getSongListDetail(Integer listId, Integer userId) {
        // 构建Redis缓存键，包含歌单ID和用户ID
        String key = "songList:detail:" + listId + ":" + userId;

        // 尝试从Redis获取缓存数据
        Map<String, Object> cachedData = (Map<String, Object>) redisUtils.get(key);

        if (cachedData != null) {
            // 缓存存在，直接返回
            return Mess.success()
                    .data("listMessage", cachedData.get("listMessage"))
                    .data("musicList", cachedData.get("musicList"));
        }

        // 缓存不存在，从数据库查询
        SongList songList = songListMapper.getSongListById(listId, userId);
        if (songList == null) {
            return Mess.fail().message("歌单不存在");
        }

        List<Music> musicList = listMusicMapper.getMusicListByListId(listId);

        // 构建缓存数据
        Map<String, Object> data = new HashMap<>();
        data.put("listMessage", songList);
        data.put("musicList", musicList);

        // 将数据存入Redis，设置过期时间为30分钟
        redisUtils.set(key, data, 1800L, TimeUnit.SECONDS);

        return Mess.success().data("listMessage", songList).data("musicList", musicList);
    }

    @Override
    public Mess recommendList() {
        // 简单实现，返回前10个歌单作为推荐
        QueryWrapper<SongList> wrapper = new QueryWrapper<>();
        wrapper.last("LIMIT 10");
        List<SongList> lists = songListMapper.selectList(wrapper);
        return Mess.success().data("list", lists);
    }

    @Override
    public Mess createList(SongList songlist, Integer id) {
        try {
            // 移除不存在的方法调用
            return save(songlist) ? Mess.success().message("创建成功") : Mess.fail().message("创建失败");
        } catch (Exception e) {
            return Mess.fail().message(e.getMessage());
        }
    }

    @Override
    public Mess getMySongList(Integer id) {
        QueryWrapper<SongList> wrapper = new QueryWrapper<>();
        wrapper.eq("user", id);
        List<SongList> lists = list(wrapper);
        return Mess.success().data("list", lists);
    }

    @Override
    public Mess removeSongList(Integer listId, Integer id) {
        try {
            QueryWrapper<SongList> wrapper = new QueryWrapper<>();
            wrapper.eq("user", id).eq("id", listId);
            if (count(wrapper) > 0) {
                // 先删除歌单与歌曲的所有关联记录
                QueryWrapper<org.zwz.mod_like.entity.ListMusic> listMusicWrapper = new QueryWrapper<>();
                listMusicWrapper.eq("listid", listId);
                listMusicMapper.delete(listMusicWrapper);
                // 再删除歌单本身
                if (remove(wrapper)) {
                    return Mess.success().message("删除成功");
                }
            }
            return Mess.fail().message("删除失败");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Mess.fail().message("删除错误");
        }
    }
}

