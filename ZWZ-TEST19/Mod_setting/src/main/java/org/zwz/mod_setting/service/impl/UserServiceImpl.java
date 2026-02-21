package org.zwz.mod_setting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.zwz.mod_setting.entity.Mess;
import org.zwz.mod_setting.entity.User;
import org.zwz.mod_setting.mapper.UserMapper;
import org.zwz.mod_setting.service.UserService;
import org.zwz.mod_setting.utils.MD5Utils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Mess setMessage(User user, Integer id) {
        try {
            // 调试日志：打印接收到的用户对象和ID
            System.out.println("Received user object: " + user);
            System.out.println("Received id from header: " + id);
            
            // 优先使用请求体中的id字段，如果不存在则使用请求头中的id
            Integer userId = (user.getId() != null && user.getId() > 0) ? user.getId() : id;
            System.out.println("Final userId: " + userId);
            
            // 获取当前用户的原始信息
            User originalUser = getById(userId);
            System.out.println("Original user: " + originalUser);
            
            if (originalUser == null) {
                return Mess.fail().message("用户不存在");
            }

            // 创建更新条件包装器
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id);

            // 只验证和更新真正被修改的字段
            if (user.getUsername() != null) {
                // 只有当用户名有变化时才进行验证和更新
                if (!user.getUsername().equals(originalUser.getUsername())) {
                    System.out.println("Username has changed, checking uniqueness");
                    QueryWrapper<User> usernameWrapper = new QueryWrapper<>();
                    usernameWrapper.eq("username", user.getUsername())
                                   .ne("id", userId); // 排除当前用户
                    if (exists(usernameWrapper)) {
                        return Mess.fail().message("用户名已存在");
                    }
                    updateWrapper.set("username", user.getUsername());
                }
            }

            if (user.getEmail() != null) {
                // 只有当邮箱有变化时才进行验证和更新
                if (!user.getEmail().equals(originalUser.getEmail())) {
                    System.out.println("Email has changed, checking uniqueness");
                    QueryWrapper<User> emailWrapper = new QueryWrapper<>();
                    emailWrapper.eq("email", user.getEmail())
                               .ne("id", userId); // 排除当前用户
                    if (exists(emailWrapper)) {
                        return Mess.fail().message("邮箱已存在");
                    }
                    updateWrapper.set("email", user.getEmail());
                }
            }

            if (user.getPhone() != null) {
                // 只有当手机号有变化时才进行验证和更新
                if (!user.getPhone().equals(originalUser.getPhone())) {
                    System.out.println("Phone has changed, checking uniqueness");
                    QueryWrapper<User> phoneWrapper = new QueryWrapper<>();
                    phoneWrapper.eq("phone", user.getPhone())
                               .ne("id", userId); // 排除当前用户
                    if (exists(phoneWrapper)) {
                        return Mess.fail().message("手机号码已存在");
                    }
                    updateWrapper.set("phone", user.getPhone());
                }
            }

            if (user.getAbout() != null) {
                updateWrapper.set("about", user.getAbout());
            }

            // 调试日志：打印更新条件
            System.out.println("Update wrapper: " + updateWrapper);
            
            // 检查是否有需要更新的字段
            // 如果updateWrapper没有SET子句，直接返回成功
            if (updateWrapper.getSqlSet() != null && !updateWrapper.getSqlSet().isEmpty()) {
                // 执行更新
                boolean updated = update(updateWrapper);
                System.out.println("Update result: " + updated);
                
                if (updated) {
                    return Mess.success().message("更新成功");
                } else {
                    return Mess.fail().message("更新失败");
                }
            } else {
                // 没有字段需要更新，直接返回成功
                System.out.println("No fields to update, returning success");
                return Mess.success().message("更新成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Mess.fail().message("更新失败：" + e.getMessage());
        }
    }

    @Override
    public Mess setPassword(String oldPassword, String newPassword, Integer id) {
        User user = getById(id);
        String odpassword = MD5Utils.stringToMD5(oldPassword);
        if (user.getPassword().equals(odpassword)) {
            user.setPassword(MD5Utils.stringToMD5(newPassword));
            updateById(user);
            return Mess.success().message("修改成功");
        }
        return Mess.fail().message("密码错误");
    }

    @Override
    public Mess setIcon(String iconUrl, Integer id) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id).set("image_url", iconUrl);
        return update(wrapper) ? Mess.success().message("更新成功") : Mess.fail().message("更新失败");
    }
}