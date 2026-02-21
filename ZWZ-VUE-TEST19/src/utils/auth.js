/**
 * 认证相关的工具函数auth.js
 */
const USER_ID_KEY = 'userId'; // 用户ID存储key
const USERNAME_KEY = 'username'; // 用户名存储key
const USER_ROLE_KEY = 'userRole'; // 用户角色存储key
const USER_NAME_KEY = 'name'; // 姓名存储key,实际上没有姓名字段
const USER_EMAIL_KEY = 'email'; // 邮箱存储key
const USER_PHONE_KEY = 'phone'; // 电话存储key
const USER_ACTIVATION_KEY = 'activation'; // 激活状态存储key
const USER_AVATAR_KEY = 'avatar'; // 头像URL存储key

/**
 * 获取头像URL
 * @returns {string} 头像URL
 */
export function getAvatar() {
  return localStorage.getItem(USER_AVATAR_KEY);
}

/**
 * 设置头像URL
 * @param {string} avatarUrl - 头像URL
 */
export function setAvatar(avatarUrl) {
  return localStorage.setItem(USER_AVATAR_KEY, avatarUrl);
}

/**
 * 移除头像URL
 */
export function removeAvatar() {
  return localStorage.removeItem(USER_AVATAR_KEY);
}

/**
 * 获取token（兼容层，实际不再使用token,舍弃token，改为保存用户id）
 * 兼容层，实际不再使用token,舍弃token，改为保存用户id
 * 兼容层，实际不再使用token,舍弃token，改为保存用户id
 * @returns {string} 默认空字符串
 */
export function getToken() {
  // 不再使用token，返回空字符串作为兼容
  console.warn('getToken函数已废弃，现在使用getUserId进行认证');
  return '';
}

/**
 * 设置token（兼容层）
 * @param {string} token - token值
 */
export function setToken(token) {
  // 不再使用token，仅记录日志
  console.warn('setToken函数已废弃，现在使用saveUserInfo进行用户信息保存');
}

/**
 * 移除token（兼容层）
 */
export function removeToken() {
  // 不再使用token，仅记录日志
  console.warn('removeToken函数已废弃，现在使用clearUserInfo清除用户信息');
}

/**
 * 获取用户ID
 * @returns {number} 用户ID（数字类型）
 */
export function getUserId() {
  const id = localStorage.getItem(USER_ID_KEY);
  // 确保返回数字类型，以匹配后端接口要求
  return id ? parseInt(id, 10) : null;
}

/**
 * 设置用户ID
 * @param {string|number} userId - 用户ID
 */
export function setUserId(userId) {
  return localStorage.setItem(USER_ID_KEY, userId.toString());
}

/**
 * 移除用户ID
 */
export function removeUserId() {
  return localStorage.removeItem(USER_ID_KEY);
}

/**
 * 获取邮箱
 * @returns {string} 邮箱
 */
export function getEmail() {
  return localStorage.getItem(USER_EMAIL_KEY);
}

/**
 * 设置邮箱
 * @param {string} email - 邮箱
 */
export function setEmail(email) {
  return localStorage.setItem(USER_EMAIL_KEY, email);
}

/**
 * 移除邮箱
 */
export function removeEmail() {
  return localStorage.removeItem(USER_EMAIL_KEY);
}

/**
 * 获取电话
 * @returns {string} 电话
 */
export function getPhone() {
  return localStorage.getItem(USER_PHONE_KEY);
}

/**
 * 设置电话
 * @param {string} phone - 电话
 */
export function setPhone(phone) {
  return localStorage.setItem(USER_PHONE_KEY, phone);
}

/**
 * 移除电话
 */
export function removePhone() {
  return localStorage.removeItem(USER_PHONE_KEY);
}

/**
 * 获取用户激活状态
 * @returns {boolean|string} 激活状态
 */
export function getActivation() {
  const activation = localStorage.getItem(USER_ACTIVATION_KEY);
  return activation === 'true' ? true : activation === 'false' ? false : activation;
}

/**
 * 设置用户激活状态
 * @param {boolean|string} activation - 激活状态
 */
export function setActivation(activation) {
  return localStorage.setItem(USER_ACTIVATION_KEY, activation.toString());
}

/**
 * 移除用户激活状态
 */
export function removeActivation() {
  return localStorage.removeItem(USER_ACTIVATION_KEY);
}

/**
 * 获取用户名
 * @returns {string} 用户名
 */
export function getUsername() {
  return localStorage.getItem(USERNAME_KEY);
}

/**
 * 设置用户名
 * @param {string} username - 用户名
 */
export function setUsername(username) {
  return localStorage.setItem(USERNAME_KEY, username);
}

/**
 * 移除用户名
 */
export function removeUsername() {
  return localStorage.removeItem(USERNAME_KEY);
}

/**
 * 获取用户真实姓名
 * @returns {string} 用户姓名
 */
export function getName() {
  return localStorage.getItem(USER_NAME_KEY);
}

/**
 * 设置用户真实姓名
 * @param {string} name - 用户姓名
 */
export function setName(name) {
  return localStorage.setItem(USER_NAME_KEY, name);
}

/**
 * 移除用户真实姓名
 */
export function removeName() {
  return localStorage.removeItem(USER_NAME_KEY);
}

/**
 * 获取用户角色
 * @returns {number} 用户角色（数字类型）
 */
export function getUserRole() {
  const roleValue = localStorage.getItem(USER_ROLE_KEY);
  // 确保返回数字类型，与数据库中的int类型保持一致
  return roleValue ? parseInt(roleValue, 10) : 0;
}

/**
 * 设置用户角色
 * @param {string} role - 用户角色
 */
export function setUserRole(role) {
  return localStorage.setItem(USER_ROLE_KEY, role);
}

/**
 * 移除用户角色
 */
export function removeUserRole() {
  return localStorage.removeItem(USER_ROLE_KEY);
}

/**
 * 清除所有用户信息
 */
export function clearUserInfo() {
  removeUserId();
  removeUsername();
  removeUserRole();
  removeName();
  removeEmail();
  removePhone();
  removeActivation(); // 添加移除激活状态
  removeAvatar(); // 添加移除头像URL
  console.log('用户信息已清除');
}

/**
 * 保存用户信息
 * @param {Object} userInfo - 用户信息对象
 * @param {number} userInfo.userId - 用户ID
 * @param {string} userInfo.username - 用户名
 * @param {string} userInfo.role - 用户角色
 * @param {string} userInfo.name - 用户真实姓名
 * @param {string} userInfo.email - 邮箱
 * @param {string} userInfo.phone - 电话
 * @param {boolean} userInfo.activation - 激活状态
 * @param {string} userInfo.image_url - 头像URL
 */
export function saveUserInfo(userInfo) {
  try {
    if (!userInfo || typeof userInfo !== 'object') {
      console.error('保存用户信息失败: 参数必须是有效的用户信息对象');
      return false;
    }

    // 保存用户ID（允许为数字类型，包括0）
    if (userInfo.userId !== undefined && userInfo.userId !== null) {
      setUserId(userInfo.userId);
    } else if (userInfo.id !== undefined && userInfo.id !== null) {
      // 兼容id字段
      setUserId(userInfo.id);
    }

    // 保存用户名
    if (userInfo.username !== undefined) {
      setUsername(userInfo.username);
    }

    // 保存用户角色
    if (userInfo.role !== undefined && userInfo.role !== null) {
      setUserRole(userInfo.role);
    }

    // 保存邮箱
    if (userInfo.email !== undefined) {
      setEmail(userInfo.email);
    }

    // 保存电话
    if (userInfo.phone !== undefined) {
      setPhone(userInfo.phone);
    }

    // 保存激活状态
    if (userInfo.activation !== undefined && userInfo.activation !== null) {
      setActivation(userInfo.activation);
    }

    // 保存头像URL
    if (userInfo.image_url) {
      setAvatar(userInfo.image_url);
    }

    console.log('用户信息保存成功');
    return true;
  } catch (error) {
    console.error('保存用户信息失败:', error);
    return false;
  }
}

/**
 * 检查并打印当前用户的认证信息
 * 用于调试
 */
export function debugAuthInfo() {
  try {
    // 获取各项用户信息
    const userId = getUserId();
    const username = getUsername();
    const role = getUserRole();
    const email = getEmail();
    const phone = getPhone();
    const activation = getActivation();
    
    // 获取本地存储中的所有用户相关信息
    const storedUserId = localStorage.getItem(USER_ID_KEY);
    const storedUsername = localStorage.getItem(USERNAME_KEY);
    const storedRole = localStorage.getItem(USER_ROLE_KEY);
    const storedEmail = localStorage.getItem(USER_EMAIL_KEY);
    const storedPhone = localStorage.getItem(USER_PHONE_KEY);
    const storedActivation = localStorage.getItem(USER_ACTIVATION_KEY);
    
    // 构建详细的认证信息对象
    const authDetails = {
      userId,
      username,
      role,
      email,
      phone,
      activation,
      storedUserId,
      storedUsername,
      storedRole,
      storedEmail,
      storedPhone,
      storedActivation
    };
    
    // 输出格式化的认证信息到控制台
    console.log('=== 认证信息详情 ===');
    console.log('用户ID:', userId, '(存储值:', storedUserId, ')');
    console.log('用户名:', username, '(存储值:', storedUsername, ')');
    console.log('用户角色:', role, '(存储值:', storedRole, ')');
    console.log('邮箱:', email, '(存储值:', storedEmail, ')');
    console.log('电话:', phone, '(存储值:', storedPhone, ')');
    console.log('激活状态:', activation, '(存储值:', storedActivation, ')');
    
    // 检查是否已登录
    const isLoggedInCheck = !!userId && !!username;
    console.log('是否已登录:', isLoggedInCheck ? '是' : '否');
    
    // 检查信息完整性
    const isComplete = isLoggedInCheck && role !== null && role !== undefined;
    console.log('信息是否完整:', isComplete ? '是' : '否');
    
    // 返回结构化的认证状态信息
    return {
      isLoggedIn: isLoggedInCheck,
      isComplete,
      authDetails
    };
  } catch (error) {
    console.error('调试认证信息时发生错误:', error);
    return {
      isLoggedIn: false,
      isComplete: false,
      authDetails: null,
      error: error.message
    };
  }
}

/**
 * 检查是否已登录
 * @returns {boolean} 是否已登录
 */
export function isLoggedIn() {
  const userId = getUserId();
  // 修改：明确检查userId是否为undefined或null，允许0值
  return userId !== undefined && userId !== null;
}

/**
 * 检查是否有权限访问路由
 * @param {Object} route - 路由对象
 * @returns {boolean} 是否有权限访问
 */
export function hasPermission(route) {
  const userRole = getUserRole();

  // 如果路由没有meta或者meta.roles，则所有人都可以访问
  if (!route.meta || !route.meta.roles) {
    return true;
  }

  // 修改：检查userRole是否为undefined或null，允许0值
  if (userRole === undefined || userRole === null) {
    return false;
  }

  // 检查用户角色是否在路由允许的角色列表中
  return route.meta.roles.includes(userRole);
}

/**
 * 特殊页面的用户验证
 * 用于学生考试列表和成绩列表页面
 */
export function ensureTokenForSpecialPages() {
  const userId = getUserId();
  const username = getUsername();
  const role = getUserRole();

  console.log('特殊页面用户验证检查');
  console.log(`用户ID: ${userId !== undefined && userId !== null ? '存在' : '不存在'}`);
  console.log(`用户名: ${username ? '存在' : '不存在'}`);
  console.log(`角色: ${role !== undefined && role !== null ? '存在' : '不存在'}`);

  // 修改：确保用户信息完整，正确处理数值类型0
  if (userId === undefined || userId === null || !username || role === undefined || role === null) {
    console.log('用户信息不完整，需要重新登录');
    return false;
  }

  return true;
} 