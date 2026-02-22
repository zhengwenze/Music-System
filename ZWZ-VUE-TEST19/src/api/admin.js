import axios from 'axios';
import { ElMessage } from 'element-plus';
import { Mod_setting_service, Mod_admin_service } from './request';
import { getUserId } from '@/utils/auth';
const uploadService = axios.create({
    baseURL: '/upload',
    timeout: 30000,
    withCredentials: false // 确保CORS跨域配置正确
});
// ============================= 请求拦截器 =============================
uploadService.interceptors.request.use(
    config => {
        console.log(`上传请求URL: ${config.url}, 方法: ${config.method}`);
        if (config.headers['Content-Type'] === 'multipart/form-data') {
            delete config.headers['Content-Type'];
        }
        return config;
    },
    error => {
        console.error('上传请求错误:', error);
        return Promise.reject(error);
    }
);

// ============================= 响应拦截器 =============================
uploadService.interceptors.response.use(
    response => {
        const res = response.data;
        console.log('上传响应数据:', res);
        // 简化条件判断：只要code为20或success为true就视为成功
        if (res.code === 20 || res.success === true) {
            return response.data;  // 返回响应数据
        } else {
            // 失败时提供更详细的错误信息
            const errorMsg = res.message || '文件上传失败';
            console.error('上传业务逻辑错误:', errorMsg);

            ElMessage({
                message: errorMsg,
                type: 'error',
                duration: 5 * 1000
            });

            return Promise.reject(new Error(errorMsg));
        }
    },
    error => {
        // 处理超时错误
        if (error.code === 'ECONNABORTED') {
            const enhancedError = new Error('上传超时，请检查网络连接或后端服务状态');
            enhancedError.code = error.code;
            enhancedError.originalError = error;
            throw enhancedError;
        }
        // 处理网络错误
        else if (error.code === 'ERR_NETWORK') {
            const enhancedError = new Error('网络连接异常，请确认上传服务是否正在运行在8089端口');
            enhancedError.code = error.code;
            enhancedError.originalError = error;
            throw enhancedError;
        }

        // 其他错误保持原样抛出
        console.error('上传响应错误:', error);
        return Promise.reject(error);
    }
);

/**
 * 上传图片文件接口（使用8089端口微服务）
 * @param {File} file - 要上传的图片文件对象
 * @returns {Promise} - 返回上传结果的Promise对象，成功时返回包含url的data对象
 * @note 上传成功后会返回拼接好路径的图片URL: http://localhost:8089/file/image/xxx.jpg
 */
export function uploadImage(file) {
    const formData = new FormData();  // 创建FormData对象用于文件上传
    formData.append('file', file);    // 将图片文件添加到FormData中
    return uploadService({
        url: '/upImage',              // 接口路径
        method: 'post',               // POST请求
        data: formData                // 请求体为FormData对象
    }).then(response => {
        // 直接返回response.data中的url字符串，而不是整个response对象
        console.log('图片上传成功，返回数据:', response);

        if (response && response.data && response.data.url) {
            return response.data.url;  // 返回图片URL字符串
        }

        throw new Error('返回数据中未找到图片URL');
    });
}

// 管理员更新用户信息接口在函数定义时已直接导出

/**
 * 管理员更新用户信息接口
 * @param {string} userId - 目标用户ID
 * @param {Object} userInfo - 更新的用户信息
 * @returns {Promise} - 更新请求的Promise对象
 * @note 允许管理员指定目标用户ID，解决管理员编辑用户时总是修改自己信息的问题
 */
export function updateUserInfoByAdmin(userId, userInfo) {
    return Mod_setting_service.post('/setMessage', userInfo, {
        headers: {
            'id': userId // 使用传入的目标用户ID，而不是当前登录用户ID
        }
    });
}

// ============================= 音乐管理相关API =============================

/**
 * 获取音乐列表（分页）
 * @param {Object} params - 查询参数，包含page、size等
 * @returns {Promise} - 返回音乐列表数据
 */
export function getMusicList(params) {
    // 后端接口是/pageMusic/{current}/{size}，需要转换参数格式
    const { page, size, name } = params;
    return Mod_admin_service.get(`/pageMusic/${page}/${size}`, {
        params: { keyword: name }
    });
}

/**
 * 冻结音乐
 * @param {string} musicId - 音乐ID
 * @returns {Promise} - 返回冻结操作的结果
 */
export function freezeMusic(musicId) {
    // 修正：使用URL参数传递id，与后端保持一致
    return Mod_admin_service.post('/freezeMusic', {}, {
        params: { id: musicId }
    });
}

/**
 * 解冻音乐
 * @param {string} musicId - 音乐ID
 * @returns {Promise} - 返回解冻操作的结果
 */
export function unfreezeMusic(musicId) {
    // 修正：使用URL参数传递id，与后端保持一致
    return Mod_admin_service.post('/unfreezeMusic', {}, {
        params: { id: musicId }
    });
}

/**
 * 添加音乐
 * @param {Object} musicData - 音乐数据
 * @returns {Promise} - 返回添加操作的结果
 */
export function addMusic(musicData) {
    return Mod_admin_service.post('/addmusic', musicData);
}

/**
 * 管理员端，编辑音乐功能。
 * @param {Object} musicData - 音乐数据
 * @returns {Promise} - 返回编辑操作的结果
 */
export function editMusic(musicData) {
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
    });
}

// 导出上传服务实例
export default uploadService;