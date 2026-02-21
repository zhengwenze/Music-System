package org.zwz.mod_msg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwz.mod_msg.entity.Mess;
import org.zwz.mod_msg.entity.Msg;
import org.zwz.mod_msg.entity.User;
import org.zwz.mod_msg.mapper.MsgMapper;
import org.zwz.mod_msg.mapper.UserMapper;
import org.zwz.mod_msg.service.MsgService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//方法包括：
//addMsg：添加消息（支持按群组或按用户ID发送）。
//gotMsg：根据用户ID获取消息列表。
//gotCount：获取用户未读消息数量。
//readMsg：将用户的所有消息标记为已读。
@Service
public class MsgServiceImpl extends ServiceImpl<MsgMapper, Msg> implements MsgService {
    @Autowired
    UserMapper userMapper;

    @Override
    public Mess addMsg(String title, String message, String togroup, String tomhose) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (togroup != null && !togroup.equals("")) {
            switch (togroup) {
                case "role1":
                    wrapper.eq("role", 1);
                    break;
                case "role2":
                    wrapper.eq("role", 2);
                    break;
                default:
                    wrapper.eq("role", 1).or().eq("role", 2);
            }
            ArrayList<Msg> msgArrayList = new ArrayList<>();
            List<User> userList = userMapper.selectList(wrapper);
            userList.forEach(e -> {
                Msg msg = new Msg();
                msg.setCreateTime(LocalDateTime.now());
                msg.setTitle(title);
                msg.setIsRead(0);
                msg.setMsg(message);
                msg.setUserId(e.getId());
                msgArrayList.add(msg);
            });
            saveBatch(msgArrayList);
            return Mess.success().mess("发布成功");
        } else if (tomhose == null || !tomhose.matches("-?\\d+(\\.\\d+)?")) {
            return Mess.fail().mess("请发送正确的ID");
        } else {
            User user = userMapper.selectById(tomhose);
            if (user == null) {
                return Mess.fail().mess("该用户不存在");
            } else {
                Msg msg = new Msg();
                msg.setUserId(Integer.parseInt(tomhose));
                msg.setMsg(message);
                msg.setTitle(title);
                msg.setIsRead(0);
                msg.setCreateTime(LocalDateTime.now());
                save(msg);
                return Mess.success().mess("发布成功");
            }
        }
    }

    @Override
    public Mess getMsg(Integer id) {
        QueryWrapper<Msg> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", id);
        return Mess.success().mess("消息获取成功").data("msg", list(wrapper));
    }

    @Override
    public Mess getCount(Integer id) {
        QueryWrapper<Msg> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", id);
        wrapper.eq("isread", 0);
        return Mess.success().data("unReadMsg", count(wrapper));
    }

    @Override
    public Mess readMsg(Integer id) {
        UpdateWrapper<Msg> wrapper = new UpdateWrapper<>();
        wrapper.set("isread", 1).eq("user_id", id);
        update(wrapper);
        return Mess.success();
    }
}