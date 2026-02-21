package org.zwz.mod_admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_admin.entity.Mess;
import org.zwz.mod_admin.entity.User;
import org.zwz.mod_admin.mapper.UserMapper;
import org.zwz.mod_admin.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Mess searchUser(Integer pn, Integer size, String keyword) {
        Page<User> userPage = new Page<>(pn, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (keyword != null) {
            wrapper.like("username", keyword);
        }
        return Mess.success().data("page", userMapper.selectPage(userPage, wrapper));
    }

    @Override
    public Mess freezeUser(Integer id) {
        UpdateWrapper<User> userWrapper = new UpdateWrapper<>();
        userWrapper.set("activation", 0).eq("id", id);
        boolean result = userMapper.update(null, userWrapper) > 0;
        if (result) {
            return Mess.success().message("冻结成功");
        } else {
            return Mess.success().message("冻结失败");
        }
    }

    @Override
    public Mess unFreezeUser(Integer id) {
        UpdateWrapper<User> userWrapper = new UpdateWrapper<>();
        userWrapper.set("activation", 1).eq("id", id);
        boolean result = userMapper.update(null, userWrapper) > 0;
        if (result) {
            return Mess.success().message("解冻成功");
        } else {
            return Mess.success().message("解冻失败");
        }
    }
}