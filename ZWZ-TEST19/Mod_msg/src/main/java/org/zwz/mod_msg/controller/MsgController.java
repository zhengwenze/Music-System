package org.zwz.mod_msg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zwz.mod_msg.entity.Mess;
import org.zwz.mod_msg.service.MsgService;

/**
 * 消息模块控制器类
 * <p>
 * 负责处理与消息相关的HTTP请求，包括消息的发布、获取、未读数量统计和已读状态更新
 * 所有接口以/msg为基础路径，通过不同的子路径区分不同的功能
 * </p>
 */
@RestController
@RequestMapping("/msg")
public class MsgController {
    // 一共四个方法，不可改动，不可改动！！！！
    // 一共四个方法，不可改动，不可改动！！！！
    // 一共四个方法，不可改动，不可改动！！！！

    /**
     * 消息服务接口注入
     * 通过依赖注入方式获取MsgService的实例，用于处理业务逻辑
     */
    @Autowired
    MsgService msgService;

    /**
     * 发布消息接口
     * <p>
     * 支持向指定群组或单个用户发送消息，根据传入参数决定消息发送范围
     * </p>
     * 
     * @param id      请求头参数，当前操作用户的ID
     * @param title   请求参数，消息标题
     * @param message 请求参数，消息内容
     * @param togroup 请求参数，可选，目标用户群组标识（如角色标识）
     * @param towhose 请求参数，可选，目标单个用户的ID
     * @return Mess对象，包含操作结果、状态码和响应消息
     * @throws Exception 当参数验证失败或业务处理出错时抛出异常
     */
    @PostMapping("/addMsg")
    public Mess addMsg(@RequestHeader("id") Integer id, @RequestParam String title, @RequestParam String message,
            @RequestParam(required = false) String togroup, @RequestParam(required = false) String towhose) {
        return msgService.addMsg(title, message, togroup, towhose);
    }

    /**
     * 获取用户消息列表接口
     * <p>
     * 根据用户ID获取该用户的所有消息记录，包括已读和未读消息
     * 消息列表按创建时间倒序排列，最新消息在前
     * </p>
     * 
     * @param id 请求头参数，当前操作用户的ID
     * @return Mess对象，包含消息列表数据、状态码和响应消息
     * @throws Exception 当用户不存在或数据查询出错时抛出异常
     */
    @GetMapping("/getMsg")
    public Mess getMsg(@RequestHeader("id") Integer id) {
        return msgService.getMsg(id);
    }

    /**
     * 获取用户未读消息数量接口
     * <p>
     * 统计指定用户当前未读消息的总数
     * </p>
     * 
     * @param id 请求头参数，当前操作用户的ID
     * @return Mess对象，包含未读消息数量、状态码和响应消息
     * @throws Exception 当用户不存在或数据统计出错时抛出异常
     */
    @GetMapping("/getCount")
    public Mess getCount(@RequestHeader("id") Integer id) {
        return msgService.getCount(id);
    }

    /**
     * 标记所有未读消息为已读接口
     * <p>
     * 将指定用户的所有未读消息状态更新为已读
     * </p>
     * 
     * @param id 请求头参数，当前操作用户的ID
     * @return Mess对象，包含操作结果、状态码和响应消息
     * @throws Exception 当用户不存在或更新操作出错时抛出异常
     */
    @PostMapping("/readMsg")
    public Mess readMsg(@RequestHeader("id") Integer id) {
        return msgService.readMsg(id);
    }
}