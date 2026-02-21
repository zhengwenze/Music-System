import axios from 'axios';
import { ElMessage } from 'element-plus';
import { getUserId, clearUserInfo } from '@/utils/auth';
// 微服务URL配置（已修改为相对路径以使用Vite代理,不要再修改了)
// 微服务URL配置（已修改为相对路径以使用Vite代理,不要再修改了)
const serviceConfigs = {
    admin: {
        baseURL: '/admin', // 相对路径，将通过Vite代理到http://localhost:8081
        timeout: 10000,
        withCredentials: false
    },
    like: {
        baseURL: '/mylike', // 相对路径，将通过Vite代理到http://localhost:8082，修改为'/mylike'以匹配后端路径
        timeout: 10000,
        withCredentials: false
    },
    login: {
        baseURL: '/login', // 相对路径，将通过Vite代理到http://localhost:8083
        timeout: 10000,
        withCredentials: false
    },
    msg: {
        baseURL: '/msg', // 相对路径，将通过Vite代理到http://localhost:8084
        timeout: 10000,
        withCredentials: false
    },
    music: {
        baseURL: '/music', // 相对路径，将通过Vite代理到http://localhost:8085
        timeout: 10000,
        withCredentials: false
    },
    setting: {
        baseURL: '/setting', // 相对路径，将通过Vite代理到http://localhost:8086
        timeout: 10000,
        withCredentials: false
    },
    singer: {
        baseURL: '/singer', // 相对路径，将通过Vite代理到http://localhost:8087
        timeout: 10000,
        withCredentials: false
    },
    songList: {
        baseURL: '/songList', // 相对路径，将通过Vite代理到http://localhost:8088
        timeout: 10000,
        withCredentials: false
    },
    upload: {
        baseURL: '/upload', // 相对路径，将通过Vite代理到http://localhost:8089
        timeout: 30000,
        withCredentials: false
    }
};
// 通用请求拦截器函数
const setupRequestInterceptor = (instance) => {
    instance.interceptors.request.use(
        config => {
            // 从auth模块获取userId
            const userId = getUserId();
            console.log(`请求URL: ${config.url}, 方法: ${config.method}`);

            // 更新登录请求判断逻辑，适配修改后的URL路径
            const isLoginRequest = config.url === '/login' && config.method === 'post';

            if (userId && !config.headers['id'] && !isLoginRequest) {
                // 只有在没有设置id头且不是登录请求的情况下才添加
                config.headers['id'] = userId;
                console.log('请求头添加id:', userId);
            } else if (config.headers['id']) {
                console.log('请求头已包含id，使用现有值:', config.headers['id']);
            } else if (!isLoginRequest) {
                console.log('未找到userId，请求未携带用户标识');
            }
            return config;
        },
        error => {
            console.error('请求错误:', error);
            return Promise.reject(error);
        }
    );
};

// 通用响应拦截器函数
// request.js中响应拦截器返回的是完整的response对象，包含data、headers等信息
const setupResponseInterceptor = (instance) => {
    instance.interceptors.response.use(
        response => {
            const res = response.data;
            console.log('response:', res);
            console.log('response.data:', res.data);
            console.log('response.data.data:', res.data.data);
            // 简化条件判断：只要code为20或success为true就视为成功
            if (res.code === 20 || res.success === true) {
                return response; // 修改：返回完整的response对象
            } else {
                // 失败时提供更详细的错误信息
                const errorMsg = res.message || '请求失败';
                console.error('业务逻辑错误:', errorMsg);

                // 登录相关错误特殊处理，更新URL匹配逻辑
                if (response.config.url === '/login' && response.config.method === 'post') {
                    // 对于登录失败，返回带有data属性的对象，保持与成功响应格式一致
                    return {
                        data: {
                            success: false,
                            code: res.code || 500,
                            message: errorMsg
                        }
                    };
                }

                ElMessage({
                    message: errorMsg,
                    type: 'error',
                    duration: 5 * 1000
                });

                // 401: 未登录或token过期
                if (res.code === 401) {
                    // 清除用户信息并跳转到登录页
                    clearUserInfo();

                    ElMessage.error('登录已过期，请重新登录');

                    setTimeout(() => {
                        window.location.href = '/login';
                    }, 1500);
                }

                return Promise.reject(new Error(errorMsg));
            }
        },
        error => {
            console.error('响应错误:', error);

            let errorMessage = '网络请求失败';

            // 处理HTTP错误状态码
            if (error.response) {
                const status = error.response.status;
                const errorData = error.response.data;

                // 尝试从响应数据中获取错误信息
                if (errorData && errorData.message) {
                    errorMessage = errorData.message;
                } else {
                    // 根据状态码设置默认错误信息
                    switch (status) {
                        case 400:
                            errorMessage = '请求参数错误';
                            break;
                        case 401:
                            errorMessage = '未授权，请重新登录';
                            // 清除用户信息并跳转到登录页
                            clearUserInfo();

                            setTimeout(() => {
                                window.location.href = '/login';
                            }, 1500);
                            break;
                        case 403:
                            errorMessage = '拒绝访问';
                            break;
                        case 404:
                            errorMessage = '请求地址不存在';
                            break;
                        case 500:
                            errorMessage = '服务器内部错误';
                            break;
                        default:
                            errorMessage = `连接错误(${status})`;
                    }
                }

                // 对于登录请求的错误，返回更明确的信息
                if (error.config && error.config.url === '/login' && error.config.method === 'post') {
                    // 登录请求的错误信息特殊处理，确保不会出现null错误
                    return Promise.reject({
                        ...error,
                        message: `登录失败，${errorMessage || '内部错误'}`
                    });
                }
            } else if (error.code === 'ERR_NETWORK') {
                errorMessage = '网络连接异常，请检查后端服务是否正常运行';
            } else if (error.code === 'ECONNABORTED') {
                errorMessage = '请求超时，请稍后重试';
            }

            ElMessage({
                message: errorMessage,
                type: 'error',
                duration: 5 * 1000
            });

            return Promise.reject({
                ...error,
                message: errorMessage
            });
        }
    );
};

// 创建axios实例的工厂函数
const createService = (config) => {
    // 创建axios实例
    const instance = axios.create({
        baseURL: config.baseURL || config.path,
        timeout: config.timeout || 10000,
        withCredentials: config.withCredentials || false
    });

    // 为每个服务实例单独设置拦截器
    setupRequestInterceptor(instance);
    setupResponseInterceptor(instance);

    return instance;
};

const services = {};

// 创建所有服务实例
for (const [serviceName, config] of Object.entries(serviceConfigs)) {
    services[`Mod_${serviceName}_service`] = createService(config);
}

// 创建默认service实例
const service = createService({
    baseURL: '/', // 默认基础路径
    timeout: 10000,
    withCredentials: false
});

// 为默认service添加拦截器
// setupRequestInterceptor(service);
// setupResponseInterceptor(service);
// 注意：拦截器已经在createService中设置，不需要重复设置
// 解构并导出所有服务
export const {
    Mod_admin_service,
    Mod_like_service,
    Mod_login_service,
    Mod_msg_service,
    Mod_music_service,
    Mod_setting_service,
    Mod_singer_service,
    Mod_songList_service,
    Mod_upload_service
} = services;

// 导出微服务URL常量,能使用service尽量使用service
export const Mod_admin_URL = 'http://localhost:8081';
export const Mod_like_URL = 'http://localhost:8082';
export const Mod_login_URL = 'http://localhost:8083';
export const Mod_msg_URL = 'http://localhost:8084';
export const Mod_music_URL = 'http://localhost:8085';
export const Mod_setting_URL = 'http://localhost:8086';
export const Mod_singer_URL = 'http://localhost:8087';
export const Mod_songList_URL = 'http://localhost:8088';
export const Mod_upload_URL = 'http://localhost:8089';
// 导出默认service,尽量使用service
export default service;