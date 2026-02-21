package org.zwz.mod_admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_admin.entity.Mess;
import org.zwz.mod_admin.entity.User;

public interface UserService extends IService<User> {
    // 统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开
    Mess searchUser(Integer pn, Integer size, String keyword);

    Mess freezeUser(Integer id);

    Mess unFreezeUser(Integer id);

}
