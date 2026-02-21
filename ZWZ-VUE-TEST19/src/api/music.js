import { Mod_music_service, Mod_singer_service } from './request';
import { Mod_admin_service } from './request';
import { getUserId } from '@/utils/auth';

/**
 * 搜索歌曲接口
 * @param {string} keyword - 搜索关键词
 * @returns {Promise} - 返回搜索结果的Promise对象
 */
export function searchSongs(keyword = '') {
  console.log('调用搜索歌曲API:', { keyword });
  const userId = getUserId();
  return Mod_music_service({
    // 不准加前缀
    url: '/search',
    method: 'get',
    params: { keyword: keyword },
    headers: { id: userId }
  }).then(response => {
    console.log('搜索歌曲API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('搜索歌曲API调用失败:', error);
    throw error;
  });
}

/**
 * 获取推荐歌曲接口
 * @returns {Promise} - 返回推荐歌曲列表的Promise对象
 */
export function getRecommendSongs() {
  console.log('调用获取推荐歌曲API');
  const userId = getUserId();
  return Mod_music_service({
    url: '/Recommend',
    method: 'get',
    headers: { id: userId }
  }).then(response => {
    console.log('获取推荐歌曲API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取推荐歌曲API调用失败:', error);
    throw error;
  });
}

/**
 * 获取音乐排行榜接口
 * @param {string} param - 排行类型：'hot'(热门) 或 'newest'(最新)
 * @returns {Promise} - 返回排行榜数据的Promise对象
 */
export function getMusicRecommendList(param = 'hot') {
  console.log('调用获取音乐排行榜API:', { param });
  const userId = getUserId();
  return Mod_music_service({
    url: '/RecommendList',
    method: 'get',
    params: { param: param },
    headers: { id: userId }
  }).then(response => {
    console.log('获取音乐排行榜API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取音乐排行榜API调用失败:', error);
    throw error;
  });
}

/**
 * 获取最新歌曲推荐接口
 * @param {number} size - 返回数量，默认10，最大100
 * @returns {Promise} - 返回最新歌曲列表的Promise对象
 */
export function getRecommendNewestSongs(size = 10) {
  console.log('调用获取最新歌曲推荐API:', { size });
  const userId = getUserId();
  // 限制size的最大值为100
  const validSize = Math.min(Math.max(size, 1), 100);
  return Mod_music_service({
    url: '/RecommendNewest',
    method: 'get',
    params: { size: validSize },
    headers: { id: userId }
  }).then(response => {
    console.log('获取最新歌曲推荐API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取最新歌曲推荐API调用失败:', error);
    throw error;
  });
}

/**
 * 获取推荐歌手列表接口
 * @returns {Promise} - 返回推荐歌手列表的Promise对象
 */
export function getRecommendSingers() {
  console.log('调用获取推荐歌手API');
  const userId = getUserId();
  return Mod_music_service({
    url: '/RecommendSinger',
    method: 'get',
    headers: { id: userId }
  }).then(response => {
    console.log('获取推荐歌手API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取推荐歌手API调用失败:', error);
    throw error;
  });
}

/**
 * 获取歌曲详情接口
 * @param {number} musicId - 歌曲ID
 * @returns {Promise} - 返回歌曲详情的Promise对象
 */
export function getMusicDetail(musicId) {
  console.log('调用获取歌曲详情API:', { musicId });
  const userId = getUserId();
  return Mod_music_service({
    url: '/detail',
    method: 'get',
    params: { musicId: musicId },
    headers: { id: userId }
  }).then(response => {
    console.log('获取歌曲详情API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取歌曲详情API调用失败:', error);
    throw error;
  });
}

/**
 * 获取歌手详情接口
 * @param {number} id - 歌手ID
 * @returns {Promise} - 返回歌手详情的Promise对象
 */
export function getSingerDetail(id) {
  console.log('调用获取歌手详情API:', { id });
  const userId = getUserId();
  return Mod_music_service({
    url: '/singerDetail',
    method: 'get',
    params: { id: id },
    headers: { id: userId }
  }).then(response => {
    console.log('获取歌手详情API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取歌手详情API调用失败:', error);
    throw error;
  });
}

/**
 * 获取指定歌手的歌曲列表接口
 * @param {number} singerId - 歌手ID
 * @returns {Promise} - 返回歌手歌曲列表的Promise对象
 */
export function getSingerSongs(singerId) {
  console.log('调用获取指定歌手歌曲列表API');
  return Mod_singer_service({
    url: '/singerSongs',
    method: 'get',
    params: { singerId: singerId },
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('获取指定歌手歌曲列表API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取指定歌手歌曲列表API调用失败:', error);
    throw error;
  });
}

/**
 * 分页查询音乐接口
 * @param {Object} params - 查询参数，包含page、size、keyword
 * @returns {Promise} - 返回音乐列表数据
 */
export function getMusicPage(params) {
  console.log('调用分页查询音乐API:', params);
  const { page = 1, size = 10, keyword = '' } = params;
  const userId = getUserId();
  return Mod_admin_service({
    url: `/pageMusic/${page}/${size}`,
    method: 'get',
    params: { keyword: keyword },
    headers: { id: userId }
  }).then(response => {
    console.log('分页查询音乐API返回response:', response);
    console.log('分页查询音乐API返回response.data:', response.data);
    console.log('分页查询音乐API返回response.data.data:', response.data.data);
    console.log('分页查询音乐API返回response.data.data.page:', response.data.data.page);
    // 返回完整的响应结构，与其他API函数保持一致
    return response;
  }).catch(error => {
    console.error('分页查询音乐API调用失败:', error);
    throw error;
  });
}

// 导出Mod_music对象，供组件使用
export const Mod_music = {
  searchSongs,
  getRecommendSongs,
  getMusicRecommendList,
  getRecommendNewestSongs,
  RecommendSinger: getRecommendSingers,
  getMusicDetail,
  getSingerDetail,
  getSingerSongs,
  getMusicPage
};