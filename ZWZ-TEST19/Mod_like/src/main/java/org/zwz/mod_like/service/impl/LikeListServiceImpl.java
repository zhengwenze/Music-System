package org.zwz.mod_like.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_like.entity.Likelist;
import org.zwz.mod_like.entity.Mess;
import org.zwz.mod_like.entity.SongList;
import org.zwz.mod_like.mapper.LikelistMapper;
import org.zwz.mod_like.mapper.SongListMapper;
import org.zwz.mod_like.service.LikelistService;
import org.zwz.mod_like.utils.RedisUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class LikeListServiceImpl extends ServiceImpl<LikelistMapper, Likelist> implements LikelistService {

    @Autowired
    SongListMapper songListMapper;

    @Autowired
    RedisUtils redisUtils;

    @Override
    public Mess getList(Integer id) {
        // 构建Redis缓存键
        String key = "user:like:list:" + id;
        
        // 尝试从Redis获取缓存数据
        List<SongList> likeSongList = (List<SongList>) redisUtils.get(key);
        
        // 如果缓存不存在，从数据库查询
        if (likeSongList == null) {
            try {
                likeSongList = songListMapper.getLikeSongList(id);
                // 将查询结果存入Redis，设置过期时间为1小时
                redisUtils.set(key, likeSongList, 3600L, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                return Mess.fail().message("查询失败");
            }
        }
        
        return Mess.success().message("查询成功").data("likeSongList", likeSongList);
    }

    @Override
    public Mess addList(Integer listId, Integer id) {
        // 构建Redis缓存键
        String key = "user:like:list:" + id;
        
        try {
            // 1. 检查是否已经收藏
            QueryWrapper<Likelist> wrapper = new QueryWrapper<>();
            wrapper.eq("userId", id);
            wrapper.eq("listId", listId);
            Likelist list = getBaseMapper().selectOne(wrapper);

            if (list != null) {
                return Mess.fail().code(10).message("已收藏该歌单");
            }

            // 2. 创建收藏记录
            Likelist likeList = new Likelist();
            likeList.setListId(listId);
            likeList.setUserId(id);
            
            // 3. 执行添加收藏
            boolean save = save(likeList);
            if (save) {
                // 4. 清除缓存
                redisUtils.remove(key);
                return Mess.success().code(20).message("收藏成功");
            } else {
                return Mess.fail().code(10).message("收藏失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Mess.fail().message("服务器错误");
        }
    }

    @Override
    public Mess removeList(Integer listId, Integer id) {
        // 构建Redis缓存键
        String key = "user:like:list:" + id;
        
        try {
            // 1. 构建查询条件
            QueryWrapper<Likelist> wrapper = new QueryWrapper<>();
            wrapper.eq("userId", id);
            wrapper.eq("listId", listId);
            
            // 2. 执行移除收藏
            boolean remove = remove(wrapper);

            if (remove) {
                // 3. 清除缓存
                redisUtils.remove(key);
                return Mess.success().code(20).message("移除收藏成功");
            } else {
                return Mess.fail().code(10).message("移除收藏失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Mess.fail().message("服务器错误");
        }
    }
}