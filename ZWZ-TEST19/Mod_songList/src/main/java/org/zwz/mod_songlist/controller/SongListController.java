package org.zwz.mod_songlist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zwz.mod_songlist.entity.Mess;
import org.zwz.mod_songlist.entity.SongList;
import org.zwz.mod_songlist.service.ListMusicService;
import org.zwz.mod_songlist.service.SongListService;

/**
 * <p>
 * 歌单表 前端控制器
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
@RestController
@RequestMapping("/songList")
public class SongListController {
//    自动依赖注入。
    @Autowired
    SongListService songListService;

    @Autowired
    ListMusicService listMusicService;

    /**
     * 查询歌单，参数要求：keyword（可选）
     */
    @GetMapping("/search")
    public Mess search(String keyword) {
        return songListService.search(keyword);
    }

    /**
     * 查看歌单详情，参数要求：songListId（必填）代表歌单id，id字段代表用户id
     */
    @GetMapping("/select")
    public Mess select(@RequestParam("songListId") Integer songListId, @RequestHeader(value = "id", required = false) Integer userId) {
        return songListService.getSongList(songListId, userId);
    }

    /**
     * 获取推荐歌单列表
     */
    @GetMapping("/recommendList")
    public Mess recommendList() {
        return songListService.recommendList();
    }

    /**
     * 创建歌单
     */
    @PostMapping("/create")
    public Mess create(@RequestBody SongList songList, @RequestHeader("id") String userIdStr) {
        try {
            Integer userId = Integer.parseInt(userIdStr);
            return songListService.createList(songList, userId);
        } catch (Exception e) {
            return Mess.fail().message("创建失败：" + e.getMessage());
        }
    }

    /**
     * 编辑歌单
     */
    @PutMapping("/update")
    public Mess update(@RequestBody SongList songList, @RequestHeader("id") String userIdStr) {
        try {
            // 验证用户ID
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return Mess.fail().message("用户ID不能为空");
            }
            
            Integer userId;
            try {
                userId = Integer.parseInt(userIdStr);
            } catch (NumberFormatException e) {
                return Mess.fail().message("用户ID格式错误");
            }
            
            // 验证歌单ID
            if (songList.getId() == null) {
                return Mess.fail().message("歌单ID不能为空");
            }
            
            // 调用服务层方法编辑歌单
            return songListService.updateList(songList, userId);
        } catch (Exception e) {
            return Mess.fail().message("编辑失败: " + e.getMessage());
        }
    }

    /**
     * 查看我的歌单列表
     */
    @GetMapping("/getMySongList")
    public Mess getMySongList(@RequestHeader("id") String userIdStr) {
        try {
            // 验证用户ID是否为空
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return Mess.fail().message("用户ID不能为空");
            }
            
            // 解析用户ID
            Integer userId;
            try {
                userId = Integer.parseInt(userIdStr);
                if (userId <= 0) {
                    return Mess.fail().message("用户ID无效");
                }
            } catch (NumberFormatException e) {
                return Mess.fail().message("用户ID格式错误");
            }
            
            // 调用服务层方法获取歌单列表
            Mess result = songListService.getMySongList(userId);
            return result;
        } catch (Exception e) {
            System.out.println("获取我的歌单列表异常: " + e.getMessage());
            return Mess.fail().message("获取失败：" + e.getMessage());
        }
    }

    /**
     * 向歌单添加歌曲
     */
    @PostMapping("/addSong")
    public Mess addSong(Integer songId, Integer songListId) {
        try {
            // 验证参数
            if (songId == null || songListId == null) {
                return Mess.fail().message("歌曲ID或歌单ID不能为空");
            }
            
            return listMusicService.addSong(songId, songListId);
        } catch (Exception e) {
            return Mess.fail().message("添加失败：" + e.getMessage());
        }
    }

    /**
     * 从歌单移除歌曲
     */
    @PostMapping("/removeSong")
    public Mess removeSong(Integer songId, Integer songListId, @RequestHeader("id") String userIdStr) {
        try {
            // 验证用户身份
            if (userIdStr == null) {
                return Mess.fail().message("用户未登录");
            }
            
            // 验证参数
            if (songId == null || songListId == null) {
                return Mess.fail().message("歌曲ID或歌单ID不能为空");
            }
            
            // 调用服务层方法移除歌曲
            return listMusicService.removeSong(songId, songListId);
        } catch (Exception e) {
            return Mess.fail().message("移除失败: " + e.getMessage());
        }
    }

    /**
     * 删除歌单
     */
    @PostMapping("/removeSongList")
    public Mess removeSongList(Integer id, @RequestHeader("id") String userIdStr) {
        try {
            // 验证参数
            if (id == null) {
                return Mess.fail().message("歌单ID不能为空");
            }
            
            // 获取用户ID
            Integer userId = Integer.parseInt(userIdStr);
            
            // 调用服务层方法删除歌单
            return songListService.removeSongList(id, userId);
        } catch (Exception e) {
            return Mess.fail().message("删除失败: " + e.getMessage());
        }
    }
}