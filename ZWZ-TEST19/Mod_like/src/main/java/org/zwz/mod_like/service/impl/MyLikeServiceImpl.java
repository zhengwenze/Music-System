package org.zwz.mod_like.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_like.entity.Likelist;
import org.zwz.mod_like.entity.Mess;
import org.zwz.mod_like.entity.Music;
import org.zwz.mod_like.entity.MyLike;
import org.zwz.mod_like.mapper.LikelistMapper;
import org.zwz.mod_like.mapper.MyLikeMapper;
import org.zwz.mod_like.service.MyLikeService;
import org.zwz.mod_like.utils.RedisUtils;

import java.util.List;

@Service
public class MyLikeServiceImpl implements MyLikeService {
    @Autowired
    RedisUtils redisUtils;
    
    @Autowired
    MyLikeMapper myLikeMapper;
    
    @Autowired
    LikelistMapper likeListMapper;
    @Override
    public Mess addLike(MyLike myLike) {
        QueryWrapper<MyLike> wrapper = new QueryWrapper<>();
        wrapper.eq("user", myLike.getUserId()).eq("music", myLike.getMusicId());
        if (myLikeMapper.selectCount(wrapper) == 0) {
            myLikeMapper.insert(myLike);
            redisUtils.remove("myLikeID_" + String.valueOf(myLike.getUserId()));
            return Mess.success().message("添加成功");
        } else {
            return Mess.fail().message("添加失败 您已喜欢此音乐");
        }
    }

    @Override
    public Mess addLike(Integer musicId, Integer id) {
        try {
            QueryWrapper<MyLike> wrapper = new QueryWrapper<>();
            wrapper.eq("user", id).eq("music", musicId);
            if (myLikeMapper.selectCount(wrapper) == 0) {
                MyLike myLike = new MyLike();
                myLike.setMusicId(musicId);
                myLike.setUserId(id);
                myLikeMapper.insert(myLike);
                redisUtils.remove("myLikeID_" + String.valueOf(id));
                return Mess.success().message("添加成功");
            } else {
                return Mess.fail().message("添加失败 您已喜欢此音乐");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Mess.fail().message("添加收藏失败：" + e.getMessage());
        }
    }

    @Override
    public Mess removeLike(Integer musicId, Integer id) {
        try {
            QueryWrapper<MyLike> wrapper = new QueryWrapper<>();
            wrapper.eq("music", musicId).eq("user", id);
            if (myLikeMapper.delete(wrapper) > 0) {
                redisUtils.remove("myLikeID_" + String.valueOf(id));
                return Mess.success().message("移除成功");
            } else {
                return Mess.fail().message("移除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Mess.fail().message("移除收藏失败：" + e.getMessage());
        }
    }

    @Override
    public Mess getMyLikes(Integer userId) {
        try {
            List<Music> musicList = myLikeMapper.selectMyLikeMusicList(userId);
            return Mess.success().data("likelist", musicList);
        } catch (Exception e) {
            e.printStackTrace();
            return Mess.fail().message("获取收藏歌曲失败");
        }
    }

    @Override
    public Mess checkMusicIsLiked(Integer musicId, Integer userId) {
        QueryWrapper<MyLike> wrapper = new QueryWrapper<>();
        wrapper.eq("user", userId).eq("music", musicId);
        if (myLikeMapper.selectCount(wrapper) > 0) {
            return Mess.success().data("isLiked", 1);
        } else {
            return Mess.success().data("isLiked", 0);
        }
    }

    @Override
    public Mess checkSongListIsLiked(Integer songListId, Integer userId) {
        QueryWrapper<Likelist> wrapper = new QueryWrapper<>();
        wrapper.eq("userId", userId).eq("listId", songListId);
        if (likeListMapper.selectCount(wrapper) > 0) {
            return Mess.success().data("isLiked", 1);
        } else {
            return Mess.success().data("isLiked", 0);
        }
    }

    @Override
    public Mess getLikeCount(Integer userId) {
        Long count = myLikeMapper.selectCount(new QueryWrapper<MyLike>().eq("user", userId));
        return Mess.success().data("count", count);
    }
}