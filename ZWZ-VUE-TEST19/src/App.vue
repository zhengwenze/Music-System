<script setup>
import { ref, provide, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getUserInfo } from '@/api/user';
import { getUserId, saveUserInfo, clearUserInfo, debugAuthInfo, isLoggedIn } from '@/utils/auth';
// 全局加载状态
const globalLoading = ref(false);
// 提供全局加载状态给子组件
provide('globalLoading', globalLoading);
// 使用路由
const router = useRouter();
const route = useRoute();
// 检查登录状态
const checkLoginStatus = async () => {
  const userId = getUserId();
  console.log('应用启动，检查登录状态，当前userId:', userId);
  // 调试当前认证信息
  const authStatus = debugAuthInfo();
  console.log('认证状态完整性:', authStatus.isComplete ? '完整' : '不完整');
  if (userId) {
    try {
      // 尝试获取用户信息，验证用户ID有效性，传递userId参数
      const response = await getUserInfo(userId);
      // 添加响应数据结构验证
      if (response && response.data && (response.data.success || response.data.code === 20) && response.data.data && response.data.data.user) {
        console.log('用户ID有效，用户已登录，获取到的用户信息:', response.data.data.user);
        // 更新本地存储的用户信息，确保数据最新
        saveUserInfo({
          userId: response.data.data.user.id,
          username: response.data.data.user.username,
          role: response.data.data.user.role || 2,
          email: response.data.data.user.email || '',
          phone: response.data.data.user.phone || '',
          activation: response.data.data.user.activation || 0,
          image_url: response.data.data.user.image_url || ''
        });
      } else {
        console.error('获取用户信息数据结构不正确:', response);
        // 清除无效的用户信息
        clearUserInfo();

        // 如果当前不在登录页，则显示提示并跳转
        if (router.currentRoute.value.path !== '/login' &&
          router.currentRoute.value.path !== '/register') {
          ElMessage.error('登录已过期，请重新登录');
          router.push('/login');
        }
      }
    } catch (error) {
      console.error('用户ID验证失败:', error);
      // 清除无效的用户信息
      clearUserInfo();

      // 如果当前不在登录页，则显示提示并跳转
      if (router.currentRoute.value.path !== '/login' &&
        router.currentRoute.value.path !== '/register') {
        ElMessage.error('登录已过期，请重新登录');
        router.push('/login');
      }
    }
  } else {
    console.log('未找到用户ID，用户未登录');
    // 如果当前不在公共页面，则跳转到登录页
    if (router.currentRoute.value.path !== '/login' &&
      router.currentRoute.value.path !== '/register') {
      router.push('/login');
    }
  }
};

// 组件挂载时检查登录状态
onMounted(() => {
  console.log('应用挂载，当前路由:', route.path);

  // 检查认证状态
  const authInfo = debugAuthInfo();

  checkLoginStatus();
});
</script>

<template>
  <!-- 全局加载指示器 -->
  <div v-if="globalLoading" class="global-loading-container">
    <div class="global-loading-spinner"></div>
  </div>

  <router-view v-slot="{ Component }">
    <transition name="fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 全局加载指示器样式 */
.global-loading-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.global-loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>