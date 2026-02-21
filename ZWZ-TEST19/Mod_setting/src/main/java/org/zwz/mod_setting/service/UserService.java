package org.zwz.mod_setting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_setting.entity.Mess;
import org.zwz.mod_setting.entity.User;

public interface UserService extends IService<User> {
    // 统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开
    Mess setMessage(User user, Integer id);

    Mess setPassword(String oldPassword, String newPassword, Integer id);

    Mess setIcon(String iconUrl, Integer id);
}
