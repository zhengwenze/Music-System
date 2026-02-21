package org.zwz.mod_music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_music.entity.Mess;
import org.zwz.mod_music.entity.User;
import org.zwz.mod_music.mapper.MusicMapper;
import org.zwz.mod_music.mapper.UserMapper;
import org.zwz.mod_music.service.UserService;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    MusicMapper musicMapper;

    @Override
    public Mess RecommendSinger() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper
            .eq("role", 1)
            .eq("activation", 1)
            .last("LIMIT 10")
            .orderByAsc("RAND()");
        List<User> list = list(wrapper);
        return Mess.success().data("list", list);
    }

    @Override
    public Mess search(String keyword) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper
            .eq("role", 1)
            .eq("activation", 0)
            .and(w -> w.like("username", keyword) 
                .or()
                .like("about", keyword));
        List<User> list = list(wrapper);
        return Mess.success().data("list", list);
    }

    @Override
    public Mess singerDetails(Integer id) {
        try {
            // 参数校验
            if (id == null) {
                return Mess.success().data("singer", null).setCode(200).setMessage("参数缺失，返回默认数据");
            }
            
            // 查询歌手信息
            User user = baseMapper.selectById(id);
            if (user == null || !"1".equals(user.getRole())) {
                return Mess.success().data("singer", null).setCode(200).setMessage("歌手不存在，返回默认数据");
            }
            
            return Mess.success().data("singer", user).setCode(200).setMessage("获取歌手详情成功");
        } catch (Exception e) {
            // 异常情况下返回默认数据
            System.err.println("获取歌手详情出错: " + e.getMessage());
            return Mess.success().data("singer", null).setCode(200).setMessage("获取歌手详情出错，返回默认数据");
        }
    }
}


