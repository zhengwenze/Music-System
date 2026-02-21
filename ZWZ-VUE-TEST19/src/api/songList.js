import { Mod_songList_service } from './request';
import { getUserId } from '@/utils/auth';
/**
 * 查询歌单接口
 * @param {string} keyword - 搜索关键词
 * @returns {Promise} - 返回查询结果的Promise对象
 */
export function searchSongList(keyword = '') {
  console.log('调用查询歌单API:', { keyword });
  return Mod_songList_service({
    url: '/songList/search',//不准修改
    method: 'get',
    params: { keyword }
  }).then(response => {
    console.log('查询歌单API返回结果:', response);
    // 修改：返回完整的response对象，而仅仅是response.data
    return response;
  }).catch(error => {
    console.error('查询歌单API调用失败:', error);
    throw error;
  });
}

/**
 * 查看歌单详情接口，需要参数《歌单ID》
 * @param {number} id - 歌单ID
 * @returns {Promise} - 返回歌单详情的Promise对象
 */
export function selectSongList(id) {
  console.log('调用查看歌单详情API:', { id });
  // 根据后端测试，查看歌单接口不需要userId请求头
  return Mod_songList_service({
    url: '/songList/select', //不准修改
    method: 'get',
    params: { songListId: id }
  }).then(response => {
    console.log('查看歌单详情API返回结果:', response);
    // 修改：返回完整的response对象，而仅仅是response.data
    return response;
  }).catch(error => {
    console.error('查看歌单详情API调用失败:', error);
    throw error;
  });
}

/**
 * 获取推荐歌单接口，无需参数
 * @returns {Promise} - 返回推荐歌单列表的Promise对象
 */
export function getRecommendSongList() {
  console.log('调用获取推荐歌单API');
  return Mod_songList_service({
    url: '/songList/recommendList', //不准修改
    method: 'get'
  }).then(response => {
    console.log('获取推荐歌单API返回结果:', response);
    // 修改：返回完整的response对象，而仅仅是response.data
    return response;
  }).catch(error => {
    console.error('获取推荐歌单API调用失败:', error);
    throw error;
  });
}

/**
 * 创建歌单接口
 * @param {Object} data - 歌单创建数据
 * @param {string} data.name - 歌单名称
 * @param {string} data.description - 歌单简介
 * @param {string} data.image - 歌单封面
 * @returns {Promise} - 返回创建结果的Promise对象
 */
export function createSongList(data) {
  console.log('调用创建歌单API:', data);
  // 获取用户ID，添加到请求头
  const userId = getUserId();
  // 只保留API文档要求的字段
  const requestData = {
    name: data.name,
    description: data.description,
    image: data.image
  };
  return Mod_songList_service({
    url: '/songList/create', //不准修改
    method: 'post',
    data: requestData,
    headers: { id: userId }
  }).then(response => {
    console.log('创建歌单API返回结果:', response);
    // 返回完整的response对象
    return response;
  }).catch(error => {
    console.error('创建歌单API调用失败:', error);
    throw error;
  });
}

/**
 * 获取我的歌单接口
 * @param {number|string} userId - 用户ID
 * @returns {Promise} - 返回我的歌单列表的Promise对象
 */
export function getMySongList(userId) {
  console.log('调用获取我的歌单API:', { userId });
  // 确保userId是字符串类型，避免可能的类型转换问题
  const currentUserId = String(getUserId() || userId || '1');
  console.log('实际使用的用户ID:', currentUserId);

  // 修正：添加userId请求头，与其他成功函数保持一致
  return Mod_songList_service({
    url: '/songList/getMySongList', //不准修改
    method: 'get',
    params: { userId: currentUserId },
    headers: { id: currentUserId } // 添加userId请求头
  }).then(response => {
    console.log('获取我的歌单API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取我的歌单API调用失败:', error);
    throw error;
  });
}

/**
 * 添加歌曲到歌单接口
 * @param {number} songId - 歌曲ID
 * @param {number} songListId - 歌单ID
 * @returns {Promise} - 返回添加结果的Promise对象
 */
export function addSongToSongList(songId, songListId) {
  console.log('调用添加歌曲到歌单API:', { songId, songListId });
  // 获取用户ID，添加到请求头
  const userId = getUserId();
  return Mod_songList_service({
    url: '/songList/addSong', //不准修改
    method: 'post',
    params: { songId, songListId },
    headers: { id: userId }
  }).then(response => {
    console.log('添加歌曲到歌单API返回结果:', response);
    // 修改：返回完整的response对象，而仅仅是response.data
    return response;
  }).catch(error => {
    console.error('添加歌曲到歌单API调用失败:', error);
    throw error;
  });
}

/**
 * 从歌单移除歌曲接口
 * @param {number} songId - 歌曲ID
 * @param {number} songListId - 歌单ID
 * @returns {Promise} - 返回移除结果的Promise对象
 */
export function removeSongFromSongList(songId, songListId) {
  console.log('调用从歌单移除歌曲API:', { songId, songListId });
  // 获取用户ID，添加到请求头
  const userId = getUserId();
  return Mod_songList_service({
    url: '/songList/removeSong', //不准修改
    method: 'post',
    params: { songId, songListId },
    headers: { id: userId }
  }).then(response => {
    console.log('从歌单移除歌曲API返回结果:', response);
    // 修改：返回完整的response对象
    return response;
  }).catch(error => {
    console.error('从歌单移除歌曲API调用失败:', error);
    throw error;
  });
}

/**
 * 更新歌单接口
 * @param {number} id - 歌单ID
 * @param {Object} data - 歌单更新数据
 * @param {string} data.name - 歌单名称
 * @param {string} data.description - 歌单简介
 * @param {string} data.image - 歌单封面
 * @param {string} data.tags - 歌单标签
 * @returns {Promise} - 返回更新结果的Promise对象
 */
export function updateSongList(id, data) {
  console.log('调用更新歌单API:', { id, data });
  // 获取用户ID，添加到请求头
  const userId = getUserId();
  // 只保留必要的字段，并添加歌单ID
  const requestData = {
    id: Number(id), // 歌单ID，根据接口文档格式需要是数字类型
    name: data.name,
    message: data.description, // 根据接口文档，描述字段应该是message
    image: data.image,
    tags: data.tags
  };
  return Mod_songList_service({
    url: '/songList/update',
    method: 'PUT', // 使用大写的HTTP方法
    data: requestData,
    headers: { 
      id: userId, // 用户ID
      'Content-Type': 'application/json' // 确保内容类型正确
    }
  }).then(response => {
    console.log('更新歌单API返回结果:', response);
    // 返回完整的response对象
    return response;
  }).catch(error => {
    console.error('更新歌单API调用失败:', error);
    throw error;
  });
}

/**
 * 删除歌单接口
 * @param {number} id - 歌单ID
 * @param {number} userId - 用户ID
 * @returns {Promise} - 返回删除结果的Promise对象
 */
export function removeSongList(id, userId) {
  console.log('调用删除歌单API:', { id, userId });
  // 获取用户ID，添加到请求头和查询参数
  const currentUserId = userId || getUserId();
  return Mod_songList_service({
    url: '/songList/removeSongList', //不准修改
    method: 'post',
    params: { id, userId: currentUserId },
    headers: { id: currentUserId }
  }).then(response => {
    console.log('删除歌单API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('删除歌单API调用失败:', error);
    throw error;
  });
}

/**
 * 获取歌单中的歌曲列表接口
 * @param {number} songListId - 歌单ID
 * @returns {Promise} - 返回歌单歌曲列表的Promise对象
 */
export function getSongListSongs(songListId) {
  console.log('调用获取歌单歌曲列表API:', { songListId });
  // 获取用户ID，添加到请求头
  const userId = getUserId();
  return Mod_songList_service({
    url: '/songList/getSongListSongs', // 根据接口规范的URL
    method: 'get',
    params: { songListId },
    headers: { id: userId }
  }).then(response => {
    console.log('获取歌单歌曲列表API返回结果:', response);
    // 返回完整的response对象
    return response;
  }).catch(error => {
    console.error('获取歌单歌曲列表API调用失败:', error);
    throw error;
  });
}