package org.zwz.mod_like.service;

import org.zwz.mod_like.entity.Mess;
import org.zwz.mod_like.entity.MyLike;

/**
 * <p>
 * 用户喜欢表 服务类
 * </p>
 *
 * @author 郑文泽
 * @since 2025-12-22
 */
public interface MyLikeService {
    //统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开
    //统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开
    // 统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开

    Mess addLike(MyLike myLike);

    Mess addLike(Integer musicId, Integer userId);

    Mess removeLike(Integer musicId, Integer userId);

    Mess getMyLikes(Integer userId);

    Mess checkMusicIsLiked(Integer musicId, Integer userId);

    Mess checkSongListIsLiked(Integer songListId, Integer userId);

    Mess getLikeCount(Integer userId);
}

