package org.zwz.mod_like.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zwz.mod_like.entity.Mess;
import org.zwz.mod_like.service.LikelistService;
import org.zwz.mod_like.service.MusicService;
import org.zwz.mod_like.service.MyLikeService;
import org.zwz.mod_like.service.SongListService;

/**
 * <p>
 * 歌曲收藏、歌单收藏控制器，负责歌曲收藏相关操作
 * mod_like模块下只能有这一个控制器，负责处理所有与歌曲收藏、歌单收藏相关的请求
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
@RestController
@RequestMapping("/mylike")
public class LikeController {

    @Autowired
    MusicService musicService;

    @Autowired
    LikelistService likeListService;

    @Autowired
    MyLikeService mylikeService;

    @Autowired
    SongListService songListService;

    /**
     * 获取单个歌单详情及收藏状态
     * @param listId 歌单ID
     * @param userId 用户ID（从请求头获取）
     * @return 歌单详情及收藏状态
     */
    @GetMapping("/songList/detail")
    public Mess getSongListDetail(@RequestParam Integer listId, @RequestHeader("id") Integer userId) {
        return songListService.getSongListDetail(listId, userId);
    }

    //查询我的全部收藏歌曲接口方法
    @GetMapping("/getMyLike")
    public Mess getMylike(@RequestHeader("id") Integer id) {
        return mylikeService.getMyLikes(id);
    }

    //查询我的收藏歌单接口方法
    @GetMapping("/getLikeList")
    public Mess getLikeList(@RequestHeader("id") Integer id) {
        return likeListService.getList(id);
    }

    //添加收藏歌曲接口方法
    @PostMapping("/add")
    public Mess addLike(@RequestHeader("id") Integer id, @RequestParam Integer musicId) {
        return mylikeService.addLike(musicId, id);
    }

    //移除收藏歌曲接口方法
    @PostMapping("/remove")
    public Mess remove(@RequestHeader("id") Integer id, @RequestParam Integer musicId) {
        return mylikeService.removeLike(musicId, id);
    }

    //添加收藏歌单接口方法
    @PostMapping("/addList")
    public Mess addList(@RequestHeader("id") Integer id, @RequestParam Integer listId) {
        return likeListService.addList(listId, id);
    }

    //移除收藏歌单接口方法
    @PostMapping("/removeList")
    public Mess removeList(@RequestHeader("id") Integer id, @RequestParam Integer listId) {
        return likeListService.removeList(listId, id);
    }
}