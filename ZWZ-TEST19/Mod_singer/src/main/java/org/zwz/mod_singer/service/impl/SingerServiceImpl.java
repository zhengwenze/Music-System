package org.zwz.mod_singer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zwz.mod_singer.entity.Singer;
import org.zwz.mod_singer.mapper.SingerMapper;
import org.zwz.mod_singer.service.SingerService;

import java.util.List;

/**
 * 歌手服务实现类
 */
@Service
@RequiredArgsConstructor
public class SingerServiceImpl implements SingerService {

    private final SingerMapper singerMapper;

    @Override
    public List<Singer> getAllSingers() {
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", 1); // 角色为1表示歌手
        return singerMapper.selectList(queryWrapper);
    }

    @Override
    public Singer getSingerById(Integer id) {
        Singer singer = singerMapper.selectById(id);
        if (singer != null && singer.getRole() == 1) {
            return singer;
        }
        return null;
    }

    @Override
    public Singer getSingerByUsername(String username) {
        return singerMapper.selectSingerByUsername(username);
    }

    @Override
    public List<Singer> getActiveSingers() {
        return singerMapper.selectActiveSingers();
    }

    @Override
    public boolean updateSinger(Singer singer) {
        // 确保只能更新歌手信息，并避免空指针异常
        if (singer == null || singer.getRole() != 1) {
            System.out.println("Invalid singer data: singer is null or role is not 1");
            return false;
        }

        try {
            System.out.println("Attempting to update singer with id: " + singer.getId());
            int result = singerMapper.updateById(singer);
            System.out.println("Update result: " + result);
            return result > 0;
        } catch (Exception e) {
            System.out.println("Error updating singer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean activateSinger(Integer id) {
        Singer singer = getSingerById(id);
        if (singer != null) {
            singer.setActivation(0); // 0表示激活
            return singerMapper.updateById(singer) > 0;
        }
        return false;
    }

    @Override
    public boolean deactivateSinger(Integer id) {
        Singer singer = getSingerById(id);
        if (singer != null) {
            singer.setActivation(1); // 1表示禁用
            return singerMapper.updateById(singer) > 0;
        }
        return false;
    }
}