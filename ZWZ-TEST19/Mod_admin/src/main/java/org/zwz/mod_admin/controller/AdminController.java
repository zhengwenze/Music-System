package org.zwz.mod_admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zwz.mod_admin.entity.Mess;
import org.zwz.mod_admin.entity.Music;
import org.zwz.mod_admin.service.MusicService;
import org.zwz.mod_admin.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserService userService;
    @Autowired
    MusicService musicService;

    // 分页查询用户接口，示例接口http://localhost:8081/admin/pageUser/1/10?keyword=,请求头为用户id
    @GetMapping(value = {"/pageUser/{current}/{size}"})
    public Mess pageUser(@PathVariable("current") Integer current, @PathVariable("size") Integer size, @RequestHeader("id") Integer id, String keyword) {
        return userService.searchUser(current, size, keyword);
    }

    // 冻结用户接口，示例接口http://localhost:8081/admin/freezeUser?id=1,请求头为用户id
    @PostMapping("/freezeUser")
    public Mess freezeUser(@RequestHeader("id") Integer adminId, Integer id) {
        return userService.freezeUser(id);
    }

    // 解冻用户接口，示例接口http://localhost:8081/admin/unfreezeUser?id=1,请求头为用户id
    @PostMapping("/unfreezeUser")
    public Mess unFreezeUser(@RequestHeader("id") Integer adminId, Integer id) {
        return userService.unFreezeUser(id);
    }

    // 分页查询音乐接口，示例接口http://localhost:8081/admin/pageMusic/1/10?keyword=
    @GetMapping("/pageMusic/{current}/{size}")
    public Mess pageSearchMusic(@PathVariable("current") Integer current, @PathVariable("size") Integer size, @RequestParam(required = false) String keyword) {
        return musicService.searchMusic(current, size, keyword);
    }

    // 冻结音乐接口，示例接口http://localhost:8081/admin/freezeMusic?id=1,请求头为用户id
    @PostMapping("/freezeMusic")
    public Mess freezeMusic(@RequestHeader("id") Integer adminId, Integer id) {
        return musicService.freezeMusic(id);
    }

    // 解冻音乐接口，示例接口http://localhost:8081/admin/unfreezeMusic?id=1,请求头为用户id
    @PostMapping("/unfreezeMusic")
    public Mess unFreezeMusic(@RequestHeader("id") Integer adminId, Integer id) {
        return musicService.unFreezeMusic(id);
    }
    
    // 编辑歌曲接口，示例接口http://localhost:8081/admin/editMusic，请求头为用户id，请求体为歌曲信息
    @PostMapping("/editMusic")
    public Mess editMusic(@RequestHeader("id") Integer adminId, @RequestBody Music music) {
        return musicService.editMusic(music);
    }
}