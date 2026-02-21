package org.zwz.mod_admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zwz.mod_admin.entity.Mess;
import org.zwz.mod_admin.entity.Music;

public interface MusicService extends IService<Music> {
    // 在MusicService中添加歌曲分页查询，歌曲冻结以及解冻方法，并值MusicServicelmpl中添加对应的实现方法。
    // 统一使用驼峰命名法，第一个单词小写，后面相连的单词间要用大写字母隔开
    Mess searchMusic(Integer pn, Integer size, String keyword);

    Mess freezeMusic(Integer id);

    Mess unFreezeMusic(Integer id);
    
    // 编辑歌曲信息
    Mess editMusic(Music music);
}