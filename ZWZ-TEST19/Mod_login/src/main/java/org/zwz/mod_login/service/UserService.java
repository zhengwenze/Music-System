package org.zwz.mod_login.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_login.entity.LoginRequest;
import org.zwz.mod_login.entity.Mess;
import org.zwz.mod_login.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
public interface UserService extends IService<User> {

    Mess register(User user);

    String activation(String token);

    Mess login(LoginRequest loginRequest);

    Mess getInfo(Integer userId);

}