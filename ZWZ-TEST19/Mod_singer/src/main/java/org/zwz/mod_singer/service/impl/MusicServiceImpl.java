package org.zwz.mod_singer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zwz.mod_music.entity.Mess;
import org.zwz.mod_singer.entity.Music;
import org.zwz.mod_singer.entity.Singer;
import org.zwz.mod_singer.mapper.MusicMapper;
import org.zwz.mod_singer.service.MusicService;
import org.zwz.mod_singer.service.SingerService;

import java.time.LocalDate;
import java.util.List;

/**
 * 音乐服务实现类MusicServiceImpl继承自MusicService接口
 */
@Service
public class MusicServiceImpl implements MusicService {

    @Autowired
    private SingerService singerService;

    @Autowired
    private MusicMapper musicMapper;

    @Override
    public Mess getMusic(String keyword, Integer id, Integer pn, Integer size) {
        try {
            // 参数验证
            if (id == null) {
                return Mess.fail().setCode(400).setMessage("歌手ID不能为空");
            }
            if (pn == null || pn <= 0) {
                pn = 1;
            }
            if (size == null || size <= 0 || size > 100) {
                size = 10;
            }
            
            // 构建查询条件
            QueryWrapper<Music> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("from_singer", id); // 只查询当前歌手发布的歌曲
            
            // 添加关键字搜索条件
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.like("music_name", keyword.trim())
                           .or()
                           .like("singer_username", keyword.trim())
                           .or()
                           .like("tags", keyword.trim());
            }
            
            // 按创建时间倒序排列
            queryWrapper.orderByDesc("create_time");
            
            // 执行分页查询
            long total = musicMapper.selectCount(queryWrapper);
            int pages = (int) Math.ceil((double) total / size);
            int offset = (pn - 1) * size;
            List<Music> records = musicMapper.selectList(queryWrapper.last("LIMIT " + offset + "," + size));
            
            // 构建分页结果
            PageResult pageResult = new PageResult(total, size, pn, pages, records);
            
            return Mess.success().setCode(200).setMessage("获取歌曲列表成功").data("page", pageResult);
        } catch (Exception e) {
            System.err.println("获取音乐列表出错: " + e.getMessage());
            e.printStackTrace();
            return Mess.fail().setCode(500).setMessage("获取音乐列表时发生错误: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mess addMusic(Music music, Integer id) {
        try {
            // 参数验证
            if (music == null) {
                return Mess.fail().setCode(400).setMessage("音乐信息不能为空");
            }

            if (id == null) {
                return Mess.fail().setCode(400).setMessage("用户ID不能为空");
            }

            // 设置音乐的创建者ID
            music.setFromSinger(id);
            // 初始化必要字段
            music.setCreateTime(LocalDate.now());
            music.setListenNumb(0);
            music.setActivation(0); // 0表示激活状态

            // 确保歌手用户名不为空
            if (music.getSingerUsername() == null || music.getSingerUsername().trim().isEmpty()) {
                // 如果前端没有提供歌手用户名，尝试从歌手信息中获取
                Singer singer = singerService.getSingerById(id);
                if (singer != null) {
                    music.setSingerUsername(singer.getUsername());
                } else {
                    return Mess.fail().setCode(500).setMessage("无法获取歌手信息");
                }
            }

            // 设置其他必要字段的默认值
            if (music.getMusicName() == null) {
                return Mess.fail().setCode(400).setMessage("歌曲名称不能为空");
            }
            if (music.getMusicUrl() == null) {
                return Mess.fail().setCode(400).setMessage("歌曲URL不能为空");
            }

            // 保存音乐信息
            boolean save = musicMapper.insert(music) > 0;
            if (save) {
                return Mess.success().setCode(200).setMessage("添加音乐成功");
            } else {
                return Mess.fail().setCode(500).setMessage("添加音乐失败");
            }
        } catch (Exception e) {
            System.err.println("添加音乐出错: " + e.getMessage());
            e.printStackTrace();
            return Mess.fail().setCode(500).setMessage("添加音乐时发生错误: " + e.getMessage());
        }
    }

    @Override
    public Mess updateMusic(Music music, Integer id) {
        try {
            // 参数验证
            if (music == null || music.getMusicId() == null || id == null) {
                return Mess.fail().setCode(400).setMessage("音乐信息和歌手ID不能为空");
            }
            
            // 查找音乐是否存在
            Music existingMusic = musicMapper.selectById(music.getMusicId());
            if (existingMusic == null) {
                return Mess.fail().setCode(404).setMessage("音乐不存在");
            }
            
            // 验证当前歌手是否为歌曲的创建者
            if (!existingMusic.getFromSinger().equals(id)) {
                return Mess.fail().setCode(403).setMessage("无权更新他人发布的歌曲");
            }
            
            // 确保不允许修改关键字段
            music.setFromSinger(id); // 保持创建者ID不变
            music.setCreateTime(existingMusic.getCreateTime()); // 保持创建时间不变
            
            // 执行更新操作
            boolean success = musicMapper.updateById(music) > 0;
            if (success) {
                return Mess.success().setCode(200).setMessage("歌曲更新成功");
            } else {
                return Mess.fail().setCode(500).setMessage("歌曲更新失败");
            }
        } catch (Exception e) {
            System.err.println("更新音乐出错: " + e.getMessage());
            e.printStackTrace();
            return Mess.fail().setCode(500).setMessage("更新音乐时发生错误: " + e.getMessage());
        }
    }

    @Override
    public Mess deleteMusic(Integer musicId, Integer id) {
        try {
            // 参数验证
            if (musicId == null || id == null) {
                return Mess.fail().setCode(400).setMessage("音乐ID和歌手ID不能为空");
            }
            
            // 查找音乐是否存在
            Music music = musicMapper.selectById(musicId);
            if (music == null) {
                return Mess.fail().setCode(404).setMessage("音乐不存在");
            }
            
            // 验证当前歌手是否为歌曲的创建者
            if (!music.getFromSinger().equals(id)) {
                return Mess.fail().setCode(403).setMessage("无权删除他人发布的歌曲");
            }
            
            // 执行删除操作
            boolean success = musicMapper.deleteById(musicId) > 0;
            if (success) {
                return Mess.success().setCode(200).setMessage("歌曲删除成功");
            } else {
                return Mess.fail().setCode(500).setMessage("歌曲删除失败");
            }
        } catch (Exception e) {
            System.err.println("删除音乐出错: " + e.getMessage());
            e.printStackTrace();
            return Mess.fail().setCode(500).setMessage("删除音乐时发生错误: " + e.getMessage());
        }
    }

    @Override
    public Mess getMessage(Integer id) {
        return Mess.success().setCode(200).setMessage("获取消息成功");
    }

    @Override
    public Mess getAllSongsBySingerId(Integer id) {
        try {
            // 参数验证
            if (id == null) {
                return Mess.fail().setCode(400).setMessage("歌手ID不能为空");
            }
            
            // 构建查询条件
            QueryWrapper<Music> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("from_singer", id); // 只查询当前歌手发布的歌曲
            
            // 按创建时间倒序排列
            queryWrapper.orderByDesc("create_time");
            
            // 查询所有歌曲
            List<Music> songs = musicMapper.selectList(queryWrapper);
            
            return Mess.success().setCode(200).setMessage("获取全部歌曲成功").data("songs", songs);
        } catch (Exception e) {
            System.err.println("获取全部歌曲出错: " + e.getMessage());
            e.printStackTrace();
            return Mess.fail().setCode(500).setMessage("获取全部歌曲时发生错误: " + e.getMessage());
        }
    }

    /**
     * 分页结果类
     */
    public static class PageResult {
        private long total;
        private int size;
        private int current;
        private int pages;
        private Object records;

        public PageResult(long total, int size, int current, int pages, Object records) {
            this.total = total;
            this.size = size;
            this.current = current;
            this.pages = pages;
            this.records = records;
        }

        // Getters and setters
        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public Object getRecords() {
            return records;
        }

        public void setRecords(Object records) {
            this.records = records;
        }
    }
}