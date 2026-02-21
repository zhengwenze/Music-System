package org.zwz.mod_msg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_msg.entity.Mess;
import org.zwz.mod_msg.entity.Msg;

public interface MsgService extends IService<Msg> {
//一共四个方法，不可改动，不可改动！！！！
    /**
     * 发布消息
     * @param title 消息标题
     * @param message 消息内容
     * @param togroup 目标群组（可选）
     * @param towhose 目标用户（可选）
     * @return 消息发布结果
     */
    Mess addMsg(String title, String message,String togroup, String towhose);
    /**
     * 获取消息，接受一参数为用户id，返回该用户的所有消息列表
     */
    Mess getMsg(Integer id);
    /**
     * 获取未读消息数量，接受一参数为用户id，返回该用户未读消息数量
     */
    Mess getCount(Integer userId);
//    将未读消息设置为已读消息接口
//    当用户点击消息中心时即认为用户已读所有消息，在MsgController中添加设置为已读方法，接受一参数为用户id，返回设置结果
    Mess readMsg(Integer id);
}