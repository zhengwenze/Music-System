<template>
  <div class="container">
    <div class="header">
      <h2>消息中心</h2>
      <div class="header-actions">
        <el-button type="primary" @click="fresh">
          刷新
        </el-button>
        <el-button type="primary" @click="showSendDialog = true">
          发送消息
        </el-button>
        <el-button @click="handleMarkAllRead" v-if="unreadCount > 0">
          全部已读
          <span v-if="unreadCount > 0" class="badge">
            {{ unreadCount }}
          </span>
        </el-button>
      </div>
    </div>

    <div class="message-container">
      <el-empty v-if="loading" description="加载中...">
        <el-icon size="40" color="#409eff">
          <loading />
        </el-icon>
      </el-empty>
      <el-empty v-else-if="messages.length === 0" description="暂无消息">
        <el-button type="primary" @click="fetchMessages">刷新</el-button>
      </el-empty>
      <div v-else class="message-list">
        <div v-for="message in messages" :key="message.id" class="message-item"
          :class="{ 'unread': message.isRead === 0 }" @click="debouncedViewMessage(message)">
          <div class="message-header">
            <span class="message-title">{{ message.title || '无标题消息' }}</span>
            <span class="message-time">{{ formatTime(message.createTime) }}</span>
          </div>
          <div class="message-preview">{{ truncateText(message.msg || '无内容', 100) }}</div>
        </div>
      </div>
    </div>

    <!-- 消息详情对话框 -->
    <el-dialog v-model="showMessageDialog" title="消息详情" width="600px">
      <div v-if="currentMessage">
        <div class="dialog-header">
          <h3>{{ currentMessage.title || '无标题消息' }}</h3>
          <span class="dialog-time">{{ formatTime(currentMessage.createTime) }}</span>
        </div>
        <div class="dialog-content">
          <p>{{ currentMessage.msg || '无内容' }}</p>
        </div>
      </div>
      <div v-else class="empty-dialog">
        <p>消息内容加载失败</p>
      </div>
    </el-dialog>

    <!-- 发送消息对话框 -->
    <el-dialog v-model="showSendDialog" title="发送消息" width="600px" :close-on-click-modal="false">
      <el-form ref="sendFormRef" :model="sendForm" :rules="rules" label-width="80px">
        <el-form-item label="消息标题" prop="title">
          <el-input v-model="sendForm.title" placeholder="请输入消息标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="消息内容" prop="message">
          <el-input v-model="sendForm.message" type="textarea" rows="4" placeholder="请输入消息内容" maxlength="500"
            show-word-limit />
        </el-form-item>
        <el-form-item label="发送角色" prop="togroup">
          <el-select v-model="sendForm.togroup" placeholder="请选择发送角色">
            <el-option label="全体用户" value="2" />
            <el-option label="全体歌手" value="1" />
            <el-option label="特定用户" value="0" />
          </el-select>
        </el-form-item>

        <!-- 条件显示特定用户输入框：当选择特定用户时显示 -->
        <el-form-item v-if="sendForm.togroup === '0'" label="用户ID" prop="username">
          <el-input v-model="sendForm.username" placeholder="请输入用户ID" maxlength="50" show-word-limit>
            <template #prefix>
              <el-icon>
                <User />
              </el-icon>
            </template>
            <template #suffix>
              <el-icon v-if="sendForm.username" class="el-input__icon" @click="sendForm.username = ''">
                <CircleClose />
              </el-icon>
            </template>
          </el-input>
          <div class="help-text">请输入接收消息的具体用户ID</div>
        </el-form-item>

        <div class="help-text">选择'所有人'或'歌手'向对应角色的用户群组发送消息，或选择'特定用户'向单个用户ID发送消息</div>
      </el-form>
      <template #footer>
        <el-button @click="handleCancelSend">取消</el-button>
        <el-button type="primary" :loading="sending" @click="handleSendMessage">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Loading, User, CircleClose } from '@element-plus/icons-vue';
import { getMsg, getUnreadCount, markAsRead, addMsg } from '@/api/msg';

// 响应式数据
const messages = ref([]);
const unreadCount = ref(0);
const loading = ref(false);
const sending = ref(false);
const showMessageDialog = ref(false);
const showSendDialog = ref(false);
const currentMessage = ref(null);
const sendFormRef = ref(null);
const sendForm = ref({
  title: '',
  message: '',
  togroup: '',
  username: ''
});

// 表单验证规则
const rules = reactive({
  title: [
    { required: true, message: '请输入消息标题', trigger: 'blur' },
    { max: 50, message: '标题最多50个字符', trigger: 'blur' }
  ],
  message: [
    { required: true, message: '请输入消息内容', trigger: 'blur' },
    { max: 200, message: '内容最多200个字符', trigger: 'blur' }
  ],
  togroup: [
    { required: true, message: '请选择发送角色', trigger: 'change' }
  ],
  username: [
    {
      required: () => sendForm.value.togroup === '0',
      message: '请输入用户ID',
      trigger: 'blur'
    }
  ]
});

// 计算属性：判断是否有未读消息
const hasUnreadMessages = computed(() => unreadCount.value > 0);

// 防抖函数
const debounce = (fn, delay) => {
  let timer = null;
  return function (...args) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
};

// 获取消息列表
const fetchMessages = async () => {
  if (loading.value) return; // 防止重复加载

  loading.value = true;
  try {
    const response = await getMsg();
    if (response?.data?.success) {
      messages.value = Array.isArray(response.data?.data?.msg) ? response.data.data.msg : [];
      // 按createTime降序排序，使最新消息显示在最上面
      messages.value.sort((a, b) => {
        const timeA = a.createTime ? new Date(a.createTime).getTime() : 0;
        const timeB = b.createTime ? new Date(b.createTime).getTime() : 0;
        return timeB - timeA; // 降序排列
      });
      // 确保每条消息都有isRead字段（接口文档规定0=未读，1=已读）
      messages.value.forEach(msg => {
        if (msg.isRead === undefined) {
          msg.isRead = 1; // 默认已读，除非明确标记为未读
        }
      });
    } else {
      ElMessage.error(response?.data?.message || '获取消息失败，请检查网络连接');
    }
  } catch (error) {
    console.error('获取消息列表失败:', error);
    ElMessage.error('获取消息失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 刷新消息列表
const fresh = async () => {
  await fetchMessages();
  // 同时更新未读消息数量
  await fetchUnreadCount();
};

// 获取未读消息数量
const fetchUnreadCount = async () => {
  try {
    const response = await getUnreadCount();
    if (response?.data?.success) {
      unreadCount.value = parseInt(response.data?.data?.unReadMsg) || 0;
    } else {
      console.error('获取未读消息数量失败:', response?.data?.message);
    }
  } catch (error) {
    console.error('获取未读消息数量失败:', error);
  }
};

// 标记所有消息已读
const handleMarkAllRead = async () => {
  if (!hasUnreadMessages.value) return;

  try {
    const response = await markAsRead();
    if (response?.data?.success) {
      ElMessage.success('全部已读');
      // 更新本地状态
      messages.value.forEach(msg => {
        msg.isRead = 1;
      });
      unreadCount.value = 0;
    } else {
      ElMessage.error(response?.data?.message || '标记失败，请稍后重试');
    }
  } catch (error) {
    console.error('标记消息已读失败:', error);
    ElMessage.error('标记失败，请稍后重试');
  }
};

// 查看消息详情
const viewMessage = (message) => {
  if (!message) return;

  currentMessage.value = { ...message }; // 深拷贝避免直接修改源数据
  showMessageDialog.value = true;

  // 如果是未读消息，更新本地状态
  if (message.isRead === 0 && unreadCount.value > 0) {
    // 在实际应用中，这里应该调用接口标记单条消息已读
    // 但根据接口文档，只能标记全部已读，所以我们只更新本地状态
    message.isRead = 1;
    unreadCount.value = Math.max(0, unreadCount.value - 1);
  }
};

// 防抖处理的查看消息函数
const debouncedViewMessage = debounce(viewMessage, 200);

// 发送消息
const handleSendMessage = async () => {
  try {
    // 表单验证
    await sendFormRef.value.validate();

    // 构造参数对象
    const params = {
      title: sendForm.value.title.trim(),
      message: sendForm.value.message.trim(), // 使用message字段名以匹配API
      togroup: sendForm.value.togroup === '0' ? '' : sendForm.value.togroup,
      username: sendForm.value.togroup === '0' ? sendForm.value.username.trim() : null
    };

    // 验证特定用户时用户名是否填写
    if (sendForm.value.togroup === '0' && !params.username) {
      ElMessage.warning('请输入用户ID');
      return;
    }

    console.log('发送消息参数:', params);

    // 调用API发送消息 - 注意这里参数顺序和名称必须与API函数签名完全匹配
    sending.value = true;
    const res = await addMsg(params.title, params.message, params.togroup, params.username);

    if (res?.data?.success) {
      ElMessage.success('发送成功');
      sendForm.value = {
        title: '',
        message: '',
        togroup: '',
        username: ''
      };
      sendFormRef.value.resetFields();
      closeSendDialog();
      // 重新获取消息列表
      fetchMessages();
    } else {
      ElMessage.error('发送失败: ' + (res?.data?.message || '未知错误'));
    }
  } catch (error) {
    console.error('发送消息失败:', error);
    ElMessage.error('发送失败，请重试');
  } finally {
    sending.value = false;
  }
};

// 关闭发送对话框
const closeSendDialog = () => {
  showSendDialog.value = false;
};

// 取消发送
const handleCancelSend = () => {
  sendForm.value.title = '';
  sendForm.value.message = '';
  sendForm.value.togroup = '';
  sendForm.value.username = '';
  if (sendFormRef.value) {
    sendFormRef.value.resetFields();
  }
  closeSendDialog();
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '时间未知';
  try {
    const date = new Date(time);
    // 检查是否是有效日期
    if (isNaN(date.getTime())) {
      return '时间格式错误';
    }
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  } catch (error) {
    console.error('时间格式化错误:', error);
    return '时间未知';
  }
};

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text || typeof text !== 'string') return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};

// 页面加载时获取数据
onMounted(async () => {
  try {
    // 并行请求提升性能
    await Promise.all([
      fetchMessages(),
      fetchUnreadCount()
    ]);
  } catch (error) {
    console.error('页面初始化失败:', error);
  }
});
</script>

<style scoped>
.container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.container:hover {
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.header h2 {
  margin: 0;
  color: #303133;
  font-size: 20px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.notification-badge {
  margin-right: 10px;
}

/* 红色未读消息角标样式 */
.read-badge {
  .el-badge__content {
    background-color: #f56c6c !important;
    color: #fff;
  }
}

.message-container {
  min-height: 400px;
  transition: all 0.3s ease;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message-item {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #f5f5f5;
  /* 已读消息的背景色 - 灰色 */
  position: relative;
  overflow: hidden;
}

.message-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 4px;
  background-color: transparent;
  transition: all 0.3s ease;
}

.message-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.1);
  transform: translateY(-2px);
}

.message-item.unread {
  background-color: #f0f9f0;
  /* 未读消息的背景色 - 浅绿色 */
  border-color: #d9f0d9;
}

.message-item.unread::before {
  background-color: #67c23a;
  /* 未读消息的左侧边框 - 绿色 */
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  flex-wrap: wrap;
  gap: 5px;
}

.message-title {
  font-weight: 500;
  color: #606266;
  /* 已读消息标题颜色 - 深灰色 */
  font-size: 16px;
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-item.unread .message-title {
  color: #67c23a;
  /* 未读消息标题颜色 - 绿色 */
  font-weight: 600;
}

.message-preview {
  color: #909399;
  /* 已读消息内容预览颜色 - 浅灰色 */
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.message-item.unread .message-preview {
  color: #606266;
  /* 未读消息内容预览颜色 - 深灰色 */
}

.message-time {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.message-preview {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  flex-wrap: wrap;
  gap: 5px;
}

.dialog-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dialog-time {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.dialog-content {
  color: #606266;
  line-height: 1.8;
  word-break: break-word;
  padding: 10px 0;
}

.empty-dialog {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

.help-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

/* 未读消息角标样式 */
.badge {
  position: absolute;
  top: -1px;
  right: -1px;
  background-color: #ff0004;
  color: white;
  border-radius: 50%;
  min-width: 20px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  font-size: 12px;
  font-weight: 600;
  padding: 0 6px;
  transform: translate(50%, -50%);
  white-space: nowrap;
}

/* 确保按钮相对定位，使角标正确定位 */
.el-button {
  position: relative;
}

/* 调整按钮内边距，为角标留出空间 */
.el-button {
  padding-right: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 15px;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .message-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .message-title {
    width: 100%;
  }

  .dialog-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .dialog-header h3 {
    width: 100%;
  }
}
</style>
