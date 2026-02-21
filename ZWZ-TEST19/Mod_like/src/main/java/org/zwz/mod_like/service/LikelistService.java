package org.zwz.mod_like.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_like.entity.Likelist;
import org.zwz.mod_like.entity.Mess;

/**
 * <p>
 * 用户歌单收藏表 服务类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
public interface LikelistService extends IService<Likelist> {
    //统一使用驼峰命名法，方法名就叫getList，单词间要用大写字母隔开
    Mess getList(Integer id);

    //统一使用驼峰命名法，方法名就叫addList和removeList，单词间要用大写字母隔开
    Mess addList(Integer listId, Integer id);

    //统一使用驼峰命名法，方法名就叫addList和removeList，单词间要用大写字母隔开
    Mess removeList(Integer listId, Integer id);
}