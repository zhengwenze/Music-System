package org.zwz.mod_music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.zwz.mod_music.entity.Mess;
import org.zwz.mod_music.service.MusicService;
import org.zwz.mod_music.service.UserService;

@RestController
@RequestMapping("/music")
public class MusicController {
    // 以下是音乐模块music的接口
    @Autowired
    MusicService musicService;

    @Autowired
    UserService userService;

    // 1、推荐歌曲接口/Recommend
    @GetMapping("/Recommend")
    public Mess getRecommend() {
        return musicService.recommend();
    }

    // 2、音乐排行榜接口/RecommendList
    @GetMapping("/RecommendList")
    public Mess getRecommendList(String param) {
        return musicService.getRecommendList(param);
    }

    // 3，搜索歌曲/search，用音乐music关键词来查询音乐列表。
    @GetMapping("/search")
    public Mess search(String keyword) {
        return musicService.search(keyword);
    }

    // 4、歌曲详情接口，用音乐id来查询音乐详情。 
    @GetMapping("/detail") 
    public Mess getOne(@RequestParam Integer musicId, @RequestHeader(value = "id", required = false) Integer id) {
        if (musicId == null) {
            return Mess.fail().code(400).message("音乐ID不能为空");
        }
        return musicService.getMusic(musicId, id);
    }

    // 获取歌手推荐
    @GetMapping("RecommendSinger")
    public Mess getRecommendSinger() {
        return musicService.getRecommendSinger(null);
    }

    // 获取歌手详情，包括歌手信息和音乐列表
    @GetMapping("/singerDetail")
    public Mess singerDetail(Integer id) {
        return musicService.singerDetail(id);
    }

    // 获取最新歌曲推荐
    @GetMapping("/RecommendNewest")
    public Mess getRecommendNewest(@RequestParam Integer size) {
        return musicService.getRecommendNewest(size);
    }
}