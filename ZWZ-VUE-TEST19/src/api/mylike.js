import { Mod_like_service } from './request';
// 收藏音乐/收藏歌单接口
// 查询我的全部收藏歌曲
export const getMyLike = async () => {
    try {
        console.log('getMyLike: 查询我的全部收藏歌曲');
        // 添加用户ID请求头
        const headers = { 'id': '1' };
        const response = await Mod_like_service({
            url: '/getMyLike',
            method: 'get',
            headers
        });
        console.log('getMyLike: 响应数据:', response.data);
        // 根据接口测试结果，返回data中的likelist字段
        return response.data.data?.likelist || [];
    } catch (error) {
        console.error('getMyLike: 错误:', error);
        throw error;
    }
};

// 查询我的收藏歌单
export const getLikeList = async () => {
    try {
        console.log('getLikeList: 查询我的收藏歌单');
        // 添加用户ID请求头
        const headers = { 'id': '1' };
        const response = await Mod_like_service({
            url: '/getLikeList',
            method: 'get',
            headers
        });
        console.log('getLikeList: 完整响应:', response);
        console.log('getLikeList: 响应数据:', response.data);
        // 返回完整的response对象
        return response;
    } catch (error) {
        console.error('getLikeList: 错误:', error);
        if (error.response) {
            console.error('getLikeList: 错误响应:', error.response);
            console.error('getLikeList: 错误响应数据:', error.response.data);
            console.error('getLikeList: 错误响应状态:', error.response.status);
        }
        throw error;
    }
};

// 添加收藏歌曲
export const addLike = async (musicId) => {
    try {
        console.log('addLike: 添加收藏歌曲', { musicId });
        // 添加用户ID请求头
        const headers = { 'id': '1' };
        const response = await Mod_like_service({
            url: `/add?musicId=${musicId}`,
            method: 'post',
            headers
        });
        console.log('addLike: 响应数据:', response.data);
        return response.data;
    } catch (error) {
        console.error('addLike: 错误:', error);
        throw error;
    }
};

// 移除收藏歌曲
export const removeLike = async (musicId) => {
    try {
        console.log('removeLike: 移除收藏歌曲', { musicId });
        // 添加用户ID请求头
        const headers = { 'id': '1' };
        const response = await Mod_like_service({
            url: `/remove?musicId=${musicId}`,
            method: 'post',
            headers
        });
        console.log('removeLike: 响应数据:', response.data);
        return response.data;
    } catch (error) {
        console.error('removeLike: 错误:', error);
        throw error;
    }
};

// 添加收藏歌单
export const addLikeList = async (listId) => {
    try {
        console.log('addLikeList: 添加收藏歌单', { listId });
        // 添加用户ID请求头
        const headers = { 'id': '1' };
        const response = await Mod_like_service({
            url: `/addList?listId=${listId}`,
            method: 'post',
            headers
        });
        console.log('addLikeList: 响应数据:', response.data);
        return response.data;
    } catch (error) {
        console.error('addLikeList: 错误:', error);
        throw error;
    }
};

// 移除收藏歌单
export const removeLikeList = async (listId) => {
    try {
        console.log('removeLikeList: 移除收藏歌单', { listId });
        // 添加用户ID请求头
        const headers = { 'id': '1' };
        const response = await Mod_like_service({
            url: `/removeList?listId=${listId}`,
            method: 'post',
            headers
        });
        console.log('removeLikeList: 响应数据:', response.data);
        return response.data;
    } catch (error) {
        console.error('removeLikeList: 错误:', error);
        throw error;
    }
};

// 获取单个歌单详情及收藏状态
export const getSongListDetailWithLikeStatus = async (listId) => {
    try {
        console.log('getSongListDetailWithLikeStatus: 获取单个歌单详情及收藏状态', { listId });
        // 添加用户ID请求头
        const headers = { 'id': '1' };
        const response = await Mod_like_service({
            url: `/songList/detail?listId=${listId}`,
            method: 'get',
            headers
        });
        console.log('getSongListDetailWithLikeStatus: 响应数据:', response.data);
        return response.data;
    } catch (error) {
        console.error('getSongListDetailWithLikeStatus: 错误:', error);
        throw error;
    }
};