package org.zwz.mod_songlist.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.zwz.mod_songlist.entity.Mess;
import org.zwz.mod_songlist.entity.ListMusic;
import org.zwz.mod_songlist.entity.Music;
import org.zwz.mod_songlist.entity.SongList;
import org.zwz.mod_songlist.mapper.ListMusicMapper;
import org.zwz.mod_songlist.mapper.SongListMapper;
import org.zwz.mod_songlist.service.SongListService;
import org.zwz.mod_songlist.utils.RedisUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 歌单表 服务实现类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
@Import(value = RedisUtils.class)
@Service
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements SongListService {

    // 注入依赖
    @Autowired
    SongListMapper songListMapper;

    @Autowired
    ListMusicMapper listMusicMapper;

    @Autowired
    RedisUtils redisUtils;

    @Override
    public Mess search(String keyword) {
        List<SongList> search = songListMapper.search(keyword);
        return Mess.success().code(20).message("响应成功").data("songList", search);
    }

    @Override
    public Mess getSongList(Integer listId, Integer id) {
        try {
            System.out.println("查询歌单详情，歌单ID: " + listId + "，用户ID: " + id);
            // 使用songListMapper的getListById方法查询歌单信息
            SongList songList = songListMapper.getListById(listId);
            System.out.println("查询结果: " + (songList != null ? songList.toString() : "null"));

            if (songList == null) {
                return Mess.fail().message("歌单已被删除");
            }

            // 如果用户ID不为空，标记当前歌单是否被该用户收藏
            if (id != null) {
                List<SongList> likeList = getLikeList(id);
                System.out.println("用户收藏列表大小: " + likeList.size());
                for (SongList likeSongList : likeList) {
                    if (likeSongList.getId().equals(listId)) {
                        songList.setIsLike(1);
                        System.out.println("歌单被用户收藏");
                        break;
                    }
                }
            }

            // 查询该歌单关联的歌曲列表
            List<Music> musicList = listMusicMapper.getMusicListByListId(listId);
            System.out.println("歌单歌曲数量: " + (musicList != null ? musicList.size() : 0));

            // 封装响应数据
            return Mess.success().data("listMessage", songList).data("musicList", musicList);
        } catch (Exception e) {
            System.out.println("查询歌单详情异常: " + e.getMessage());
            e.printStackTrace();
            return Mess.fail().message("查询失败").data("error", e.getMessage());
        }
    }

    @Override
    public Mess recommendList() {
        List<SongList> lists = songListMapper.recommendList();
        return Mess.success().data("list", lists);
    }

    @Override
    public Mess createList(SongList songlist, Integer id) {
        try {
            songlist.setCreateDate(java.time.LocalDate.now());
            songlist.setUser(id);
            return save(songlist) ? Mess.success().message("创建成功") : Mess.fail().message("创建失败");
        } catch (Exception e) {
            return Mess.fail().message(e.getMessage());
        }
    }

    @Override
    public Mess getMySongList(Integer id) {
        QueryWrapper<SongList> wrapper = new QueryWrapper<>();
        wrapper.eq("user", id);
        List<SongList> lists = list(wrapper);
        return Mess.success().data("list", lists);
    }

    @Override
    public Mess updateList(SongList songList, Integer id) {
        try {
            // 验证参数是否为空
            if (songList == null || songList.getId() == null || id == null) {
                return Mess.fail().message("歌单ID或用户ID不能为空");
            }

            // 检查歌单是否存在且属于当前用户
            QueryWrapper<SongList> wrapper = new QueryWrapper<>();
            wrapper
                    .eq("user", id)
                    .eq("id", songList.getId());

            if (count(wrapper) == 0) {
                return Mess.fail().message("歌单不存在或无权限编辑");
            }

            // 设置用户ID，确保不会修改歌单所属用户
            songList.setUser(id);
            
            // 更新歌单信息
            boolean updateResult = updateById(songList);
            if (updateResult) {
                // 清除相关缓存
                try {
                    redisUtils.remove("likelistID_".concat(String.valueOf(id)));
                } catch (Exception ex) {
                    // 忽略缓存清除异常
                    System.out.println("清除缓存异常: " + ex.getMessage());
                }
                return Mess.success().message("编辑成功");
            } else {
                return Mess.fail().message("编辑失败");
            }
        } catch (Exception e) {
            System.out.println("编辑歌单异常: " + e.getMessage());
            return Mess.fail().message("编辑错误: " + e.getMessage());
        }
    }

    @Override
    public Mess removeSongList(Integer listId, Integer id) {
        try {
            // 验证参数是否为空
            if (listId == null || id == null) {
                return Mess.fail().message("歌单ID或用户ID不能为空");
            }

            QueryWrapper<SongList> wrapper = new QueryWrapper<>();
            wrapper
                    .eq("user", id)
                    .eq("id", listId);

            // 检查歌单是否存在且属于当前用户
            if (count(wrapper) > 0) {
                // 先删除歌单与歌曲的所有关联记录
                QueryWrapper<ListMusic> listMusicWrapper = new QueryWrapper<>();
                listMusicWrapper.eq("listid", listId);
                listMusicMapper.delete(listMusicWrapper);

                // 再删除歌单本身
                boolean removeResult = remove(wrapper);
                if (removeResult) {
                    // 清除相关缓存
                    try {
                        redisUtils.remove("likelistID_".concat(String.valueOf(id)));
                    } catch (Exception ex) {
                        // 忽略缓存清除异常
                        System.out.println("清除缓存异常: " + ex.getMessage());
                    }
                    return Mess.success().message("删除成功");
                } else {
                    return Mess.fail().message("删除歌单失败");
                }
            } else {
                return Mess.fail().message("歌单不存在或无权限删除");
            }
        } catch (Exception e) {
            System.out.println("删除歌单异常: " + e.getMessage());
            // 对外键约束错误进行特殊处理
            if (e.getMessage().contains("foreign key constraint")) {
                return Mess.fail().message("删除失败：请先处理相关关联数据");
            }
            return Mess.fail().message("删除错误: " + e.getMessage());
        }
    }

    /**
     * 获取用户收藏歌单列表（含Redis缓存）
     * 
     * @param id 用户ID
     * @return 用户收藏的歌单列表
     */
    public List<SongList> getLikeList(Integer id) {
        List<SongList> list = null;
        String cacheKey = "likelistID_".concat(String.valueOf(id));

        try {
            // 先尝试从Redis中获取缓存
            list = (List<SongList>) redisUtils.get(cacheKey);
            if (list == null || list.size() == 0) {
                // 缓存不存在或为空，从数据库查询
                list = songListMapper.getLikeSongList(id);
                // 将查询结果存入Redis，有效期1分钟
                redisUtils.set(cacheKey, list, 1L, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            // 缓存操作异常，降级为直接查询数据库
            System.out.println("获取收藏列表异常: " + e.getMessage());
            list = songListMapper.getLikeSongList(id);
            // 更新Redis缓存
            try {
                redisUtils.set(cacheKey, list, 1L, TimeUnit.MINUTES);
            } catch (Exception ex) {
                // 忽略缓存更新异常
            }
        }

        return list;
    }
}