/**
 * Token调试工具
 * 用于检查和修复token问题
 */
import { getToken, setToken, getUserRole, getUsername, debugAuthInfo } from './auth';
import axios from 'axios';

/**
 * 检查token是否有效
 * @returns {Promise<boolean>} token是否有效
 */
export async function checkTokenValidity() {
  const token = getToken();
  if (!token) {
    console.error('没有找到token');
    return false;
  }

  try {
    // 尝试发送请求验证token
    const response = await axios.get('/api/user/info', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    console.log('Token验证响应:', response);
    return response.status === 200;
  } catch (error) {
    console.error('Token验证失败:', error);
    return false;
  }
}

/**
 * 修复常见的token问题
 */
export function fixTokenIssues() {
  const token = getToken();
  const username = getUsername();
  const role = getUserRole();
  
  console.log('开始修复token问题...');
  
  // 检查token格式
  if (token && !token.startsWith('ey')) {
    console.warn('Token格式可能不正确，尝试修复...');
    // 尝试解析JSON格式的token
    try {
      const parsedToken = JSON.parse(token);
      if (parsedToken.token && typeof parsedToken.token === 'string') {
        console.log('从JSON中提取token');
        setToken(parsedToken.token);
      }
    } catch (e) {
      console.error('无法解析token:', e);
    }
  }
  
  // 检查localStorage中是否存在旧格式的token
  const oldToken = localStorage.getItem('Authorization');
  if (oldToken && (!token || token.length < 10)) {
    console.log('发现旧格式token，迁移到新格式');
    setToken(oldToken.replace('Bearer ', ''));
    localStorage.removeItem('Authorization');
  }
  
  // 输出修复后的状态
  console.log('修复完成，当前认证状态:');
  return debugAuthInfo();
}

/**
 * 强制设置token（用于调试）
 * @param {string} token - 要设置的token
 * @param {string} username - 要设置的用户名
 * @param {string} role - 要设置的角色
 */
export function forceSetToken(token, username = 'student1', role = 'student') {
  if (!token) {
    console.error('token不能为空');
    return;
  }
  
  setToken(token);
  
  if (username) {
    localStorage.setItem('username', username);
  }
  
  if (role) {
    localStorage.setItem('userRole', role);
  }
  
  console.log('已强制设置token:', token);
  return debugAuthInfo();
}

// 导出一个全局可访问的调试对象
window.tokenDebug = {
  checkTokenValidity,
  fixTokenIssues,
  forceSetToken,
  debugAuthInfo
};

export default {
  checkTokenValidity,
  fixTokenIssues,
  forceSetToken
}; 