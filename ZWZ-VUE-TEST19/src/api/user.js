import { Mod_login_service } from './request';      // 登录微服务实例
import { Mod_setting_service } from './request';    // 设置微服务实例
import { Mod_upload_service } from './request';     // 上传微服务实例
import { getUserId } from '../utils/auth';          // 获取当前登录用户ID的工具函数
/**
 * 登录接口
 * @param {Object} data - 登录数据，包含用户名和密码
 * @returns {Promise} - 登录请求的Promise对象
 * @note 路径不准修改，已通过代理配置到正确后端地址
 */
// 注意：代理配置已处理路径拼接，最终请求: http://localhost:8083/login/login
const login = (data) => {
    return Mod_login_service.post('/login', data);
};
/**
 * 用户注册接口
 * @param {Object} data - 注册数据，包含用户基本信息
 * @returns {Promise} - 注册请求的Promise对象
 * @note 路径不准修改，避免与登录接口路径冲突
 * 使用'/register'路径，避免与baseURL重复拼接
 */
const register = (data) => {
    return Mod_login_service.post('/register', data);
};

/**
 * 获取用户详细信息
 * @param {string} userId - 用户ID
 * @returns {Promise} - 用户信息请求的Promise对象
 * @note 路径不准修改，避免路径重复问题
 */
const getUserInfo = (userId) => {
    return Mod_login_service.get('/getInfo', {
        params: { userId }
    });
};

/**
 * 更新用户信息接口
 * @param {Object} userInfo - 更新后的用户信息
 * @returns {Promise} - 更新请求的Promise对象
 * @note 路径不准修改，避免与其他接口路径冲突
 * 移除'/setting'前缀，因为Mod_setting_service的baseURL已经是'/setting'
 * 最终请求路径: '/setting' + '/setMessage' = '/setting/setMessage'
 */
const updateUserInfo = (userInfo) => {
    const userId = getUserId();
    return Mod_setting_service.post('/setMessage', userInfo, {
        headers: {
            'id': userId
        }
    });
};

/**
 * 更新用户头像 - 与后端setIcon接口匹配
 * @param {string} iconUrl - 头像图片URL地址
 * @returns {Promise} - 头像更新请求的Promise对象
 */
const updateUserAvatar = (iconUrl) => {
    const userId = getUserId();
    // 移除'/setting'前缀，因为Mod_setting_service的baseURL已经是'/setting'
    // 最终请求路径: '/setting' + '/setIcon' = '/setting/setIcon'
    // 根据后端接口定义，使用params传递iconUrl参数
    return Mod_setting_service.post('/setIcon', null, {
        params: {
            iconUrl: String(iconUrl)  // 确保转换为字符串
        },
        headers: {
            'id': userId
        }
    });
};

/**
 * 密码更新 - 确保与后端参数传递方式匹配
 * @param {string} oldPassword - 旧密码
 * @param {string} newPassword - 新密码
 * @returns {Promise} - 密码更新请求的Promise对象
 * @note 路径不准修改，避免与其他接口路径冲突
 * 移除'/setting'前缀，因为Mod_setting_service的baseURL已经是'/setting'
 * 最终请求路径: '/setting' + '/setPassword' = '/setting/setPassword'
 * 注意：后端使用@RequestParam接收密码参数，需要使用params而不是body
 * 必须使用auth.js中的getUserId函数获取用户ID
 */
const updatePassword = (oldPassword, newPassword) => {
    const userId = getUserId();
    return Mod_setting_service.post('/setPassword', {}, {
        params: {
            oldPassword: oldPassword,
            newPassword: newPassword
        },
        headers: {
            'id': userId
        }
    });
};

/**
 * 上传图片 - 使用上传微服务
 * @param {File} file - 要上传的图片文件
 * @returns {Promise<string>} - 解析为图片URL的Promise
 * @note 路径不准修改，避免与其他接口路径冲突
 * 创建FormData对象用于文件上传
 * 移除'/upload'前缀，因为Mod_upload_service的baseURL已经是'/upload'
 * 最终请求路径: '/upload' + '/upImage' = '/upload/upImage'（通过代理到http://localhost:8089/upload/upImage）
 * 不需要手动设置Content-Type，axios会自动处理FormData的Content-Type
 * 使用Mod_upload_service调用上传接口
 */
const uploadImage = (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return Mod_upload_service.post('/upImage', formData).then(response => {
        console.log('图片上传响应:', response);
        // 确保正确访问响应数据结构
        const data = response.data || response;
        // 确保返回拼接好路径的图片url
        // 根据实际接口URL在data.data.url中
        if (data && (data.success === true || data.code === 20) && data.data?.url) {
            return Promise.resolve(data.data.url);
        }
        return Promise.reject(new Error(data?.message || '图片上传失败'));
    }).catch(error => {
        console.error('图片上传失败:', error);
        // 提供更详细的错误信息，特别是关于CORS问题
        const errorMessage = error.response?.status === 0 ?
            '网络连接异常，请确认上传服务是否正在运行在8089端口，或检查CORS配置' :
            error.message;
        return Promise.reject(new Error(errorMessage));
    });
};

/**
 * 上传并设置用户图片 - 结合上传和设置功能
 * @param {File} file - 要上传并设置为头像的图片文件
 * @returns {Promise<Object>} - 包含操作结果的对象
 */
const setUserImage = async (file) => {
    try {
        // 1. 先调用uploadImage上传图片获取URL
        const imageUrl = await uploadImage(file);

        // 2. 然后调用updateUserAvatar将URL存储到setting模块
        const result = await updateUserAvatar(imageUrl);

        // 3. 返回处理结果
        return {
            success: true,
            imageUrl: imageUrl,
            response: result
        };
    } catch (error) {
        console.error('设置用户图片失败:', error);
        return {
            success: false,
            message: error.message || '设置用户图片失败'
        };
    }
};

// 导出api，很重要
export {
    login,
    register,
    getUserInfo,
    updateUserInfo,
    updateUserAvatar,
    updatePassword,
    uploadImage,
    setUserImage
};

export default {
    login,
    register,
    getUserInfo,
    updateUserInfo,
    updateUserAvatar,
    updatePassword,
    uploadImage,
    setUserImage
};