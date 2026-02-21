package org.zwz.mod_login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_login.entity.LoginRequest;
import org.zwz.mod_login.entity.Mess;
import org.zwz.mod_login.entity.User;
import org.zwz.mod_login.mapper.UserMapper;
import org.zwz.mod_login.service.UserService;
import org.zwz.mod_login.util.MD5Utils;

import java.time.LocalDate;

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
    private UserMapper userMapper;

    @Override
    public Mess register(User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            return Mess.fail().code(400).message("用户名已存在");
        }

        // 检查邮箱是否已存在
        wrapper.clear();
        wrapper.eq("email", user.getEmail());
        if (userMapper.selectCount(wrapper) > 0) {
            return Mess.fail().code(400).message("邮箱已被注册");
        }

        // 检查手机号是否已存在
        wrapper.clear();
        wrapper.eq("phone", user.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            return Mess.fail().code(400).message("手机号已被注册");
        }

        // 设置用户初始信息
        user.setCreateTime(LocalDate.now());
        user.setActivation(1); // 直接设置为已激活状态
        // 使用前端传递的角色值，如果未传递则默认设置为普通用户角色
        if (user.getRole() == null || (user.getRole() != 0 && user.getRole() != 1 && user.getRole() != 2)) {
            user.setRole(2);
        }

        // 密码MD5加密
        user.setPassword(MD5Utils.stringToMD5(user.getPassword()));

        // 保存用户信息
        int insert = userMapper.insert(user);
        if (insert > 0) {
            return Mess.success().code(20).message("注册成功");
        }

        return Mess.fail().code(400).message("注册失败");
    }

    @Override
    public String activation(String token) {
        return "无需激活，注册后即可直接登录！";
    }

    @Override
    public Mess login(LoginRequest loginRequest) {
        try {
            // 查找用户：支持用户名或手机号登录
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("username", loginRequest.getUsername()).or().eq("phone", loginRequest.getUsername());
            User loginUser = userMapper.selectOne(wrapper);

            if (loginUser == null) {
                return Mess.fail().code(400).message("用户名或密码不正确");
            }

            // 检查用户是否被冻结
            if (loginUser.getActivation() == 0) {
                return Mess.fail().code(403).message("该账户已被冻结！");
            }

            // 验证密码
            if (!loginUser.getPassword().equals(MD5Utils.stringToMD5(loginRequest.getPassword()))) {
                return Mess.fail().code(400).message("用户名或密码不正确");
            }

            // 将密码设为null，避免返回敏感信息
            loginUser.setPassword(null);

            // 设置正确的响应格式
            return Mess.success().code(20).message("响应成功")
                    .data("user", loginUser);
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return Mess.fail().code(500).message("登录失败，内部错误：" + e.getMessage());
        }
    }

    @Override
    public Mess getInfo(Integer userId) {
        // 根据ID获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Mess.fail().code(404).message("用户不存在");
        }
        // 直接返回用户信息，不修改任何字段，不准进行中间处理
        return Mess.success().code(20).message("响应成功").data("user", user);
    }
}