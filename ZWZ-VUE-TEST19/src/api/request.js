import axios from 'axios';
import { ElMessage } from 'element-plus';
import { getUserId, clearUserInfo } from '@/utils/auth';

const serviceConfigs = {
    admin: {
        baseURL: '/api/admin',
        timeout: 10000,
        withCredentials: false
    },
    like: {
        baseURL: '/api/like',
        timeout: 10000,
        withCredentials: false
    },
    login: {
        baseURL: '/api/login',
        timeout: 10000,
        withCredentials: false
    },
    msg: {
        baseURL: '/api/msg',
        timeout: 10000,
        withCredentials: false
    },
    music: {
        baseURL: '/api/music',
        timeout: 10000,
        withCredentials: false
    },
    setting: {
        baseURL: '/api/setting',
        timeout: 10000,
        withCredentials: false
    },
    singer: {
        baseURL: '/api/singer',
        timeout: 10000,
        withCredentials: false
    },
    songList: {
        baseURL: '/api/songList',
        timeout: 10000,
        withCredentials: false
    },
    upload: {
        baseURL: '/api/upload',
        timeout: 30000,
        withCredentials: false
    },
    user: {
        baseURL: '/api/login',
        timeout: 10000,
        withCredentials: false
    }
};

const createService = (config) => {
    const service = axios.create(config);
    
    service.interceptors.request.use(
        (config) => {
            const userId = getUserId();
            if (userId) {
                config.headers['id'] = userId;
            }
            return config;
        },
        (error) => {
            return Promise.reject(error);
        }
    );
    
    service.interceptors.response.use(
        (response) => {
            return response;
        },
        (error) => {
            if (error.response) {
                switch (error.response.status) {
                    case 401:
                        ElMessage.error('登录已过期，请重新登录');
                        clearUserInfo();
                        window.location.href = '/login';
                        break;
                    case 403:
                        ElMessage.error('没有权限访问');
                        break;
                    case 404:
                        ElMessage.error('请求的资源不存在');
                        break;
                    case 500:
                        ElMessage.error('服务器错误');
                        break;
                    default:
                        ElMessage.error(error.message || '请求失败');
                }
            } else {
                ElMessage.error('网络连接异常，请检查网络');
            }
            return Promise.reject(error);
        }
    );
    
    return service;
};

const Mod_admin_service = createService(serviceConfigs.admin);
const Mod_like_service = createService(serviceConfigs.like);
const Mod_login_service = createService(serviceConfigs.login);
const Mod_msg_service = createService(serviceConfigs.msg);
const Mod_music_service = createService(serviceConfigs.music);
const Mod_setting_service = createService(serviceConfigs.setting);
const Mod_singer_service = createService(serviceConfigs.singer);
const Mod_songList_service = createService(serviceConfigs.songList);
const Mod_upload_service = createService(serviceConfigs.upload);

export {
    Mod_admin_service,
    Mod_like_service,
    Mod_login_service,
    Mod_msg_service,
    Mod_music_service,
    Mod_setting_service,
    Mod_singer_service,
    Mod_songList_service,
    Mod_upload_service
};

export default serviceConfigs;
