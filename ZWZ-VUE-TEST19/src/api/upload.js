import axios from 'axios';
/**
 * 上传服务配置说明：
 * 创建专用的上传服务实例，使用相对路径让请求通过Vite代理
 * 使用相对路径 '/upload' 而不是绝对路径，确保请求通过Vite代理转发
 * upload.js,里面的音乐上传和图片上传逻辑都是对的，不准修改！！！
 * upload.js,里面的音乐上传和图片上传逻辑都是对的，不准修改！！！
 *  */
const uploadService = axios.create({
    baseURL: '/upload',   // 基础URL路径，通过Vite代理转发到实际服务
    timeout: 60000        // 上传超时时间设置为60秒，适用于大文件上传
});
// 请求拦截器
uploadService.interceptors.request.use(
    config => {
        // 构建完整的URL用于调试，方便排查请求路径问题
        const fullUrl = config.baseURL + config.url;
        // 移除默认的Content-Type设置
        // 让浏览器自动根据FormData内容设置正确的Content-Type
        if (config.headers && config.headers['Content-Type']) {
            delete config.headers['Content-Type'];
        }
        return config;  // 返回处理后的配置
    },
    error => {
        // 请求发送前的错误处理
        return Promise.reject(error);
    }
);
// 响应拦截器
uploadService.interceptors.response.use(
    response => {
        // 确保response.data存在，避免访问undefined
        const resData = response.data || {};
        // 返回完整的response对象
        // 返回完整的response对象
        // 返回完整的response对象
        return response;
    },
    error => {
        // 超时错误处理：请求超过60秒未响应
        if (error.code === 'ECONNABORTED') {
            return Promise.reject(new Error('请求超时，请检查网络连接或文件大小'));
        }
        // 网络错误处理：没有收到服务器响应
        if (!error.response) {
            return Promise.reject(new Error('网络错误，请检查网络连接'));
        }
        // 服务器错误处理：根据HTTP状态码返回友好提示
        let errorMessage = '服务器错误，请稍后重试';
        if (error.response.status === 400) {
            errorMessage = '请求参数错误';
        } else if (error.response.status === 401) {
            errorMessage = '未授权，请重新登录';
        } else if (error.response.status === 403) {
            errorMessage = '拒绝访问';
        } else if (error.response.status === 404) {
            errorMessage = '请求的资源不存在';
        } else if (error.response.status === 500) {
            errorMessage = '服务器内部错误';
        }
        return Promise.reject(new Error(errorMessage));
    }
);
/**
 * 图片上传接口upImage
 * @param {File} file - 要上传的图片文件对象
 * @returns {Promise} - 返回上传结果的Promise对象
 */
export const uploadImage = async (file) => {
    // 验证文件：确保传入的是有效的File对象
    if (!file || !(file instanceof File)) {
        throw new Error('请选择有效的图片文件');
    }
    // 创建FormData对象，用于上传文件
    const formData = new FormData();
    formData.append('file', file);  // 将文件添加到FormData中
    try {
        const response = await uploadService.post('/upImage', formData);
        const resData = response.data || {};
        const result = resData.data || resData.url || resData;
        return result;  // 返回处理后的结果
    } catch (error) {
        throw error;  // 将错误抛给调用者处理
    }
};
/**
 * 音乐上传接口upMusic
 * @param {File} file - 要上传的音乐文件对象
 * @returns {Promise} - 返回上传结果的Promise对象
 */
export const uploadMusic = async (file) => {
    // 验证文件：确保传入的是有效的File对象
    if (!file || !(file instanceof File)) {
        throw new Error('请选择有效的音乐文件');
    }
    // 创建FormData对象，用于上传文件
    const formData = new FormData();
    formData.append('file', file);  // 将文件添加到FormData中
    try {
        // 发送上传请求，使用upMusic接口
        const response = await uploadService.post('/upMusic', formData);

        // 获取响应数据，处理不同的返回数据结构
        const resData = response.data || {};

        // 尝试从不同位置获取数据：优先从data字段，然后是整个数据
        let result = resData.data || resData;

        return result;  // 返回处理后的结果
    } catch (error) {
        throw error;  // 将错误抛给调用者处理
    }
};
// 导出上传服务实例，用于在其他模块中调用
export default uploadService;