package org.zwz.mod_singer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zwz.mod_music.entity.Mess;
import org.zwz.mod_singer.entity.Music;
import org.zwz.mod_singer.entity.Singer;
import org.zwz.mod_singer.service.MusicService;
import org.zwz.mod_singer.service.SingerService;

import java.util.List;

/**
 * 歌手控制器
 */
@RestController
@RequestMapping("/singer")
@RequiredArgsConstructor
public class SingerController {
    // 以下接口尽量不要修改，否则会导致前端页面的异常
    @Autowired
    SingerService singerService;
    @Autowired
    MusicService musicService;

    /**
     * 获取所有歌手
     */
    @GetMapping("/all")
    public ResponseEntity<List<Singer>> getAllSingers() {
        List<Singer> singers = singerService.getAllSingers();
        return ResponseEntity.ok(singers);
    }

    /**
     * 根据ID获取歌手
     */
    @GetMapping("/{id}")
    public ResponseEntity<Singer> getSingerById(@PathVariable Integer id) {
        Singer singer = singerService.getSingerById(id);
        if (singer != null) {
            return ResponseEntity.ok(singer);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据用户名获取歌手
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Singer> getSingerByUsername(@PathVariable String username) {
        Singer singer = singerService.getSingerByUsername(username);
        if (singer != null) {
            return ResponseEntity.ok(singer);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 获取所有激活的歌手
     */
    @GetMapping("/active")
    public ResponseEntity<List<Singer>> getActiveSingers() {
        List<Singer> singers = singerService.getActiveSingers();
        return ResponseEntity.ok(singers);
    }

    /**
     * 更新歌手信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateSinger(@PathVariable Integer id, @RequestBody Singer singer) {
        singer.setId(id);
        singer.setRole(1); // 确保设置为歌手角色
        boolean success = singerService.updateSinger(singer);
        if (success) {
            return ResponseEntity.ok("歌手信息更新成功");
        }
        return ResponseEntity.badRequest().body("歌手信息更新失败");
    }

    /**
     * 激活特定id歌手
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateSinger(@PathVariable Integer id) {
        boolean success = singerService.activateSinger(id);
        if (success) {
            return ResponseEntity.ok("歌手激活成功");
        }
        return ResponseEntity.badRequest().body("歌手激活失败");
    }

    /**
     * 禁用歌手
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateSinger(@PathVariable Integer id) {
        boolean success = singerService.deactivateSinger(id);
        if (success) {
            return ResponseEntity.ok("歌手禁用成功");
        }
        return ResponseEntity.badRequest().body("歌手禁用失败");
    }

    @GetMapping("/pageMusic/{pn}/{size}")
    public Mess getPage(@PathVariable(value = "pn") Integer pn, @PathVariable(value = "size") Integer size,
            String keyword, @RequestHeader("id") Integer id) {
        return musicService.getMusic(keyword, id, pn, size);
    }

    @PostMapping("/addMusic")
    public Mess addMusic(@RequestBody Music music, @RequestHeader("id") Integer id) {
        return musicService.addMusic(music, id);
    }

    @PostMapping("/update")
    public Mess update(@RequestBody Music music, @RequestHeader("id") Integer id) {
        return musicService.updateMusic(music, id);
    }

    @PostMapping("/delete")
    public Mess delete(@RequestParam Integer musicId, @RequestHeader("id") Integer id) {
        return musicService.deleteMusic(musicId, id);
    }

    @GetMapping("/getMess")
    public Mess getMess(@RequestHeader("id") Integer id) {
        return musicService.getMessage(id);
    }

    /**
     * 获取当前歌手发布的全部歌曲
     */
    @GetMapping("/my-songs")
    public Mess getAllSongsBySingerId(@RequestHeader("id") Integer id) {
        try {
            return musicService.getAllSongsBySingerId(id);
        } catch (Exception e) {
            System.err.println("获取歌手全部歌曲出错: " + e.getMessage());
            e.printStackTrace();
            return Mess.fail().setCode(500).setMessage("获取歌手全部歌曲时发生错误: " + e.getMessage());
        }
    }
}