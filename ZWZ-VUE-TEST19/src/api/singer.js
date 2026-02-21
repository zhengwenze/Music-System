import { Mod_singer_service, Mod_admin_service } from '@/api/request';
import { getUserId } from '@/utils/auth';
/**
 * 获取所有歌手信息
 * @returns {Promise} - 返回Promise对象
 */
export const getAllSingers = () => {
  console.log('调用获取所有歌手信息API');
  return Mod_singer_service({
    url: '/all',
    method: 'get',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('获取所有歌手信息API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取所有歌手信息API调用失败:', error);
    throw error;
  });
};

/**
 * 根据ID获取指定歌手信息
 * @param {number} id - 歌手ID
 * @returns {Promise} - 返回Promise对象
 */
export const getSingerById = (id) => {
  console.log('调用根据ID获取歌手信息API:', { id });
  return Mod_singer_service({
    url: `/${id}`,
    method: 'get',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('根据ID获取歌手信息API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('根据ID获取歌手信息API调用失败:', error);
    throw error;
  });
};

/**
 * 根据用户名获取歌手信息
 * @param {string} username - 歌手用户名
 * @returns {Promise} - 返回Promise对象
 */
export const getSingerByUsername = (username) => {
  console.log('调用根据用户名获取歌手信息API:', { username });
  return Mod_singer_service({
    url: `/username/${username}`,
    method: 'get',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('根据用户名获取歌手信息API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('根据用户名获取歌手信息API调用失败:', error);
    throw error;
  });
};

/**
 * 获取所有激活歌手信息
 * @returns {Promise} - 返回Promise对象
 */
export const getAllActiveSingers = () => {
  console.log('调用获取所有激活歌手信息API');
  return Mod_singer_service({
    url: '/active/all',
    method: 'get',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('获取所有激活歌手信息API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取所有激活歌手信息API调用失败:', error);
    throw error;
  });
};

/**
 * 获取消息信息
 * @returns {Promise} - 返回Promise对象
 */
export const getMessageInfo = () => {
  console.log('调用获取消息信息API');
  return Mod_singer_service({
    url: '/message',
    method: 'get',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('获取消息信息API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取消息信息API调用失败:', error);
    throw error;
  });
};

/**
 * 分页获取歌手自己的音乐列表
 * @param {number} current - 当前页码
 * @param {number} size - 每页大小
 * @param {string} keyword - 搜索关键词（可选）
 * @returns {Promise} - 返回Promise对象
 */
export const pageMusic = (current = 1, size = 10, keyword = '') => {
  console.log('调用歌手音乐分页查询API:', {
    current,
    size,
    keyword
  });
  return Mod_singer_service({
    url: `/pageMusic/${current}/${size}`,
    method: 'get',
    params: {
      keyword: keyword || undefined
    },
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('歌手音乐分页查询API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('歌手音乐分页查询API调用失败:', error);
    throw error;
  });
};

/**
 * 冻结音乐
 * @param {number} musicId - 音乐ID
 * @returns {Promise} - 返回Promise对象
 */
export const freezeMusic = (musicId) => {
  console.log('调用歌手冻结音乐API:', { musicId });
  return Mod_singer_service({
    url: '/freezeMusic',
    method: 'post',
    data: {
      musicId
    },
    headers: {
      'Content-Type': 'application/json'
    }
  }).then(response => {
    console.log('歌手冻结音乐API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('歌手冻结音乐API调用失败:', error);
    throw error;
  });
};

/**
 * 解冻音乐
 * @param {number} musicId - 音乐ID
 * @returns {Promise} - 返回Promise对象
 */
export const unfreezeMusic = (musicId) => {
  console.log('调用歌手解冻音乐API:', { musicId });
  return Mod_singer_service({
    url: '/unfreezeMusic',
    method: 'post',
    data: {
      musicId
    },
    headers: {
      'Content-Type': 'application/json'
    }
  }).then(response => {
    console.log('歌手解冻音乐API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('歌手解冻音乐API调用失败:', error);
    throw error;
  });
};

/**
 * 添加音乐
 * @param {Object} musicData - 音乐数据，包含musicName, musicUrl, singerUsername等字段
 * @param {number} id - 用户ID
 * @returns {Promise} - 返回Promise对象
 */
export const addMusic = (musicData, id) => {
  console.log('调用歌手添加音乐API:', musicData, '用户ID:', id);
  return Mod_singer_service({
    url: '/addMusic',
    method: 'post',
    data: musicData,
    headers: {
      'Content-Type': 'application/json',
      'id': id
    }
  }).then(response => {
    console.log('歌手添加音乐API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('歌手添加音乐API调用失败:', error);
    throw error;
  });
};

/**
 * 更新音乐信息（预留接口）
 * @param {Object} musicData - 音乐数据
 * @returns {Promise} - 返回Promise对象
 */
export const updateMusic = (musicData) => {
  console.log('调用歌手更新音乐API:', musicData);
  
  // 实现选择性更新：只发送非空字段
  const filteredMusicData = {};
  for (const key in musicData) {
    if (musicData.hasOwnProperty(key) && musicData[key] !== null && musicData[key] !== undefined) {
      filteredMusicData[key] = musicData[key];
    }
  }
  
  // 获取用户ID并添加到请求头
  const userId = getUserId();
  
  return Mod_admin_service.post('/editMusic', filteredMusicData, {
    headers: {
      'id': userId // 添加用户ID到请求头
    }
  }).then(response => {
    console.log('歌手更新音乐API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('歌手更新音乐API调用失败:', error);
    throw error;
  });
};

/**
 * 获取音乐详情（预留接口）
 * @param {number} musicId - 音乐ID
 * @returns {Promise} - 返回Promise对象
 */
export const getMusicDetail = (musicId) => {
  console.log('调用歌手获取音乐详情API:', { musicId });
  return Mod_singer_service({
    url: `/getMusic/${musicId}`,
    method: 'get',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('歌手获取音乐详情API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('歌手获取音乐详情API调用失败:', error);
    throw error;
  });
};

/**
 * 更新歌手信息
 * @param {Object} singerData - 歌手信息数据
 * @returns {Promise} - 返回Promise对象
 */
export const updateSingerInfo = (singerData) => {
  console.log('调用更新歌手信息API:', singerData);
  return Mod_singer_service({
    url: '/update',
    method: 'post',
    data: singerData,
    headers: {
      'Content-Type': 'application/json'
    }
  }).then(response => {
    console.log('更新歌手信息API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('更新歌手信息API调用失败:', error);
    throw error;
  });
};

// 添加获取登录歌手音乐列表的函数
/**
 * 获取登录歌手的音乐列表
 * @param {Object} params - 查询参数
 * @param {number} params.pn - 页码，默认1
 * @param {number} params.size - 每页大小，默认10
 * @returns {Promise} - 返回Promise对象
 */
export const getLoginSingerMusicList = (params = { pn: 1, size: 10 }) => {
  console.log('调用获取登录歌手音乐列表API', params);
  return Mod_singer_service({
    url: '/pageMusic/' + params.pn + '/' + params.size,
    method: 'get',
    params: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('获取登录歌手音乐列表API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取登录歌手音乐列表API调用失败:', error);
    throw error;
  });
};

// 添加获取当前登录歌手信息的函数
/**
 * 获取当前登录歌手信息
 * @returns {Promise} - 返回Promise对象
 */
export const getCurrentSingerInfo = () => {
  console.log('调用获取当前登录歌手信息API');
  return Mod_singer_service({
    url: '/current',
    method: 'get',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('获取当前登录歌手信息API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取当前登录歌手信息API调用失败:', error);
    throw error;
  });
};

/**
 * 获取登录歌手的所有歌曲
 * @returns {Promise} - 返回Promise对象
 */
export const getMySongs = () => {
  console.log('调用获取登录歌手的所有歌曲API');
  return Mod_singer_service({
    url: '/my-songs',
    method: 'get',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('获取登录歌手的所有歌曲API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取登录歌手的所有歌曲API调用失败:', error);
    throw error;
  });
};

/**
 * 删除歌曲
 * @param {number} musicId - 音乐ID
 * @returns {Promise} - 返回Promise对象
 */
export const deleteSong = (musicId) => {
  console.log('调用删除歌曲API:', { musicId });
  return Mod_singer_service({
    url: '/delete',
    method: 'post',
    params: {
      musicId
    },
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('删除歌曲API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('删除歌曲API调用失败:', error);
    throw error;
  });
};