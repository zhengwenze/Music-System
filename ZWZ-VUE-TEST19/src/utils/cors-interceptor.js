import axios from 'axios';

// 创建axios拦截器中间件，用于解决跨域问题
// 这个文件不会修改request.js中的绝对路径配置，而是通过拦截器自动转换请求路径

/**
 * 跨域请求拦截器
 * 拦截绝对路径的请求并通过代理发送，避免浏览器的跨域限制
 */
const corsInterceptor = {
  // 请求拦截器
  request: {
    handler: (config) => {
      // 检查请求是否使用了绝对路径（http://或https://开头）
      if (config.url && (config.url.startsWith('http://') || config.url.startsWith('https://'))) {
        // 微服务URL映射表，将绝对路径映射到代理路径
        const urlMappings = {
          'http://localhost:8081': '/admin',
          'http://localhost:8082': '/like',
          'http://localhost:8083': '/login',
          'http://localhost:8084': '/msg',
          'http://localhost:8085': '/music',
          'http://localhost:8086': '/setting',
          'http://localhost:8087': '/singer',
          'http://localhost:8088': '/songList',
          'http://localhost:8089': '/upload'
        };

        // 检查URL是否匹配微服务URL模式
        for (const [absoluteUrl, proxyPath] of Object.entries(urlMappings)) {
          if (config.url.startsWith(absoluteUrl)) {
            // 转换为代理路径，保持请求路径的其余部分不变
            config.url = config.url.replace(absoluteUrl, proxyPath);
            console.log(`跨域拦截器: 将请求从 ${absoluteUrl} 转换为代理路径 ${proxyPath}`);
            break;
          }
        }
      }
      return config;
    },
    errorHandler: (error) => {
      return Promise.reject(error);
    }
  }
};

/**
 * 注册跨域请求拦截器
 * 为所有axios实例添加跨域请求处理
 */
export function registerCorsInterceptor() {
  // 为axios默认实例添加拦截器
  axios.interceptors.request.use(
    corsInterceptor.request.handler,
    corsInterceptor.request.errorHandler
  );
  console.log('跨域请求拦截器已注册');
}

/**
 * 为特定的axios实例添加跨域拦截器
 * @param {axios.AxiosInstance} instance - axios实例
 */
export function addCorsInterceptorToInstance(instance) {
  instance.interceptors.request.use(
    corsInterceptor.request.handler,
    corsInterceptor.request.errorHandler
  );
  console.log('已为axios实例添加跨域请求拦截器');
}

export default corsInterceptor;