import { Mod_msg_service } from './request';
// 发送消息接口，不准修改，不准修改，不准修改，不准修改，不准修改，不准修改，不准修改
export function addMsg(title, message, togroup, username) {
  console.log('调用发送消息API:', { title, message, togroup, username });

  // 构建请求数据对象 - 确保参数名与后端期望一致
  const formData = new URLSearchParams();
  formData.append('title', title || '消息通知'); // 添加默认标题
  formData.append('message', message || ''); // 消息内容，默认为空

  // 按照测试命令的成功模式，当需要发送给特定用户时，togroup应该为空字符串
  if (username) {
    // 如果提供了username参数，设置togroup为空字符串并添加towhose参数
    formData.append('togroup', '');
    // 确保用户ID转换为整数类型
    formData.append('towhose', parseInt(username, 10).toString());
    console.log('添加特定用户参数(towhose):', parseInt(username, 10).toString());
  } else {
    // 对于非特定用户消息，使用提供的togroup值，根据接口测试报告应该为role1、role2等
    let groupValue = togroup;
    // 根据选择转换为正确的群组标识
    if (togroup === '1') {
      groupValue = 'role1';
    } else if (togroup === '2') {
      groupValue = 'role2';
    }
    formData.append('togroup', groupValue || '');
    formData.append('towhose', ''); // 向群组发送时towhose为空字符串
  }

  console.log('发送到API的form数据:', formData.toString());

  return Mod_msg_service({
    url: 'addMsg',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('发送消息API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('发送消息API调用失败:', error);
    throw error;
  });
}

// 获取所有消息列表
export function getMsg() {
  console.log('调用获取消息列表API:');
  return Mod_msg_service({
    url: 'getMsg',
    method: 'get'
  }).then(response => {
    console.log('获取消息列表API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取消息列表API调用失败:', error);
    throw error;
  });
}

// 获取未读消息数量
export function getUnreadCount() {
  console.log('调用获取未读消息数量API:');
  return Mod_msg_service({
    url: 'getCount',
    method: 'get'
    // GET请求不需要指定Content-Type
  }).then(response => {
    console.log('获取未读消息数量API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('获取未读消息数量API调用失败:', error);
    throw error;
  });
}

// 标记消息已读
export function markAsRead() {
  console.log('调用标记消息已读API:');
  // 只需请求头中的id
  return Mod_msg_service({
    url: 'readMsg',
    method: 'post',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  }).then(response => {
    console.log('标记消息已读API返回结果:', response);
    return response;
  }).catch(error => {
    console.error('标记消息已读API调用失败:', error);
    throw error;
  });
}