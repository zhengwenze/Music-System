package org.zwz.mod_setting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zwz.mod_setting.entity.Mess;
import org.zwz.mod_setting.entity.User;
import org.zwz.mod_setting.service.UserService;

@RestController
@RequestMapping("/setting")
public class SettingController {

    @Autowired
    UserService userService;

    @PostMapping("/setMessage")
    public Mess setMessage(@RequestBody User user, @RequestHeader("id") Integer id) {
        return userService.setMessage(user, id);
    }

    @PostMapping("/setPassword")
    public Mess setPassword(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword, @RequestHeader("id") Integer id) {
        return userService.setPassword(oldPassword, newPassword, id);
    }

    @PostMapping("/setIcon")
    public Mess setIcon(@RequestHeader("id") Integer id, @RequestParam("iconUrl") String iconUrl) {
        return userService.setIcon(iconUrl, id);
    }
}