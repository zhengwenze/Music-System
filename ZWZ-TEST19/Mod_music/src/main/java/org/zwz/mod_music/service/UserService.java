package org.zwz.mod_music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_music.entity.Mess;
import org.zwz.mod_music.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-20
 */
public interface UserService extends IService<User> {
    // 推荐歌手，重要接口
    Mess RecommendSinger();
    // 歌手详情，重要接口
    Mess singerDetails(Integer id);

    Mess search(String keyword);
}
