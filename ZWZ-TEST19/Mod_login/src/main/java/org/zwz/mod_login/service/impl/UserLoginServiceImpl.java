package org.zwz.mod_login.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_login.entity.LoginResponse;
import org.zwz.mod_login.entity.User;
import org.zwz.mod_login.mapper.UserMapper;
import org.zwz.mod_login.service.UserLoginService;
import org.zwz.mod_login.util.MD5Utils;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginResponse login(String username, String password) {
        LoginResponse response = new LoginResponse();
        User user = getUserByUsername(username);

        if (user == null) {
            response.setMessage("用户不存在");
            return response;
        }

        // 检查用户是否被冻结
        if (user.getActivation() == 0) {
            response.setMessage("该账户已被冻结！");
            return response;
        }

        if (!verifyPassword(password, user.getPassword())) {
            response.setMessage("密码错误");
            return response;
        }

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setMessage("登录成功");

        return response;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("username", username));
    }

    @Override
    public boolean verifyPassword(String inputPassword, String storedPassword) {
        return storedPassword.equals(MD5Utils.stringToMD5(inputPassword));
    }
}
