/**
 * 特殊页面调试工具
 * 用于诊断和修复学生考试列表和成绩列表页面的问题
 */
import { getToken, setToken, clearUserInfo, debugAuthInfo } from './auth';
import axios from 'axios';

/**
 * 诊断特殊页面问题
 * @returns {Object} 诊断结果
 */
export function diagnoseSpecialPages() {
  console.group('特殊页面问题诊断');
  
  // 检查认证状态
  const authInfo = debugAuthInfo();
  
  // 检查localStorage中是否有其他可能的token
  const localStorageKeys = Object.keys(localStorage);
  const possibleTokenKeys = localStorageKeys.filter(key => 
    key.toLowerCase().includes('token') || 
    key.toLowerCase().includes('auth') || 
    key.toLowerCase().includes('jwt')
  );
  
  console.log('可能存储token的键:', possibleTokenKeys);
  
  // 检查是否存在CORS问题
  console.log('检查CORS配置...');
  const corsStatus = {
    viteProxyConfigured: true, // 假设已配置
    backendCorsConfigured: 'unknown' // 需要实际测试
  };
  
  // 检查请求头
  console.log('检查请求头配置...');
  const headers = {
    'Authorization': authInfo.token ? `Bearer ${authInfo.token}` : '未设置',
    'Content-Type': 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  };
  
  console.log('当前请求头配置:', headers);
  
  // 诊断结果
  const diagnosis = {
    authStatus: authInfo,
    possibleTokenKeys,
    corsStatus,
    headers,
    recommendations: []
  };
  
  // 生成建议
  if (!authInfo.token) {
    diagnosis.recommendations.push('未找到有效token，需要重新登录');
  }
  
  if (possibleTokenKeys.length > 1) {
    diagnosis.recommendations.push('检测到多个可能的token存储位置，可能导致混淆');
  }
  
  console.log('诊断完成，建议:', diagnosis.recommendations);
  console.groupEnd();
  
  return diagnosis;
}

/**
 * 修复特殊页面问题
 * @returns {boolean} 是否成功修复
 */
export async function fixSpecialPages() {
  console.group('特殊页面问题修复');
  
  // 获取诊断结果
  const diagnosis = diagnoseSpecialPages();
  
  // 尝试修复token问题
  if (!diagnosis.authStatus.token) {
    console.log('尝试从其他存储位置恢复token...');
    
    // 检查其他可能的token存储位置
    for (const key of diagnosis.possibleTokenKeys) {
      if (key === 'token') continue; // 跳过已检查的标准位置
      
      const possibleToken = localStorage.getItem(key);
      if (possibleToken) {
        console.log(`从${key}恢复token`);
        // 清理可能的Bearer前缀
        const cleanToken = possibleToken.startsWith('Bearer ') 
          ? possibleToken.substring(7) 
          : possibleToken;
        setToken(cleanToken);
        console.log('token已恢复');
        break;
      }
    }
  }
  
  // 再次检查token
  const token = getToken();
  if (!token) {
    console.log('无法恢复token，修复失败');
    console.groupEnd();
    return false;
  }
  
  // 测试token有效性
  try {
    console.log('测试token有效性...');
    const response = await axios.get('/api/user/info', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    console.log('Token有效，响应状态:', response.status);
    console.log('修复成功');
    console.groupEnd();
    return true;
  } catch (error) {
    console.error('Token无效，修复失败:', error);
    console.groupEnd();
    return false;
  }
}

// 导出调试对象到全局，方便在浏览器控制台使用
window.specialPagesDebug = {
  diagnose: diagnoseSpecialPages,
  fix: fixSpecialPages
};

export default {
  diagnose: diagnoseSpecialPages,
  fix: fixSpecialPages
}; 