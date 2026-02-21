import axios from 'axios';
import { ElMessage } from 'element-plus';
import { getUserId, clearUserInfo } from '@/utils/auth';
// 微服务URL配置（已修改为 /api 前缀以区分前端路由和API请求）
const serviceConfigs = {
    admin: {
        baseURL: '/api/admin', // 相对路径，将通过Vite代理到http://localhost:8081
        timeout: 10000,
        withCredentials: false
    },
    like: {
        baseURL: '/api/like', // 相对路径，将通过Vite代理到http://localhost:8082
        timeout: 10000,
        withCredentials: false
    },
    login: {
        baseURL: '/api/login', // 相对路径，将通过Vite代理到http://localhost:8083
        timeout: 10000,
        withCredentials: false
    },
    msg: {
        baseURL: '/api/msg', // 相对路径，将通过Vite代理到http://localhost:8084
        timeout: 10000,
        withCredentials: false
    },
    music: {
        baseURL: '/api/music', // 相对路径，将通过Vite代理到http://localhost:8085
        timeout: 10000,
        withCredentials: false
    },
    setting: {
        baseURL: '/api/setting', // 相对路径，将通过Vite代理到http://localhost:8086
        timeout: 10000,
        withCredentials: false
    },
    singer: {
        baseURL: '/api/singer', // 相对路径，将通过Vite代理到http://localhost:8087
        timeout: 10000,
        withCredentials: false
    },
    songList: {
        baseURL: '/api/songList', // 相对路径，将通过Vite代理到http://localhost:8088
        timeout: 10000,
        withCredentials: false
    },
    upload: {
        baseURL: '/api/upload', // 相对路径，将通过Vite代理到http://localhost:8089
        timeout: 30000,
        withCredentials: false
    },
    user: {
        baseURL: '/api/login', // 相对路径，将通过Vite代理到http://localhost:8083
        timeout: 10000,
        withCredentials: false
    }
};

export default serviceConfigs;
