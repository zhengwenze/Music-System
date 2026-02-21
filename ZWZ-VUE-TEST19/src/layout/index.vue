<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container" :class="{ 'is-collapse': isCollapse }">
      <div class="logo-container">
        <img src="\public\music.png" alt="音乐图标" class="logo-icon">
        <span class="title">悦享音乐</span>
      </div>
      <el-menu :default-active="activeMenu" class="sidebar-menu" :collapse="isCollapse" background-color="#304156"
        text-color="#bfcbd9" active-text-color="#ffb600" unique-opened router>
        <!-- 管理员菜单admin -->
        <template v-if="userRole === 0">
          <el-menu-item index="/admin/userManage">
            <el-icon><el-icon-user /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/singerManage">
            <el-icon><el-icon-user /></el-icon>
            <template #title>歌手管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/musicManage">
            <el-icon><el-icon-user /></el-icon>
            <template #title>歌曲管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/message">
            <el-icon><el-icon-user /></el-icon>
            <template #title>消息中心</template>
          </el-menu-item>
          <el-menu-item index="/admin/profile">
            <el-icon><el-icon-user /></el-icon>
            <template #title>个人信息</template>
          </el-menu-item>
        </template>
        <!-- 歌手菜单singer -->
        <template v-if="userRole === 1">
          <el-menu-item index="/singer/uploadMusic">
            <el-icon><el-icon-edit /></el-icon>
            <template #title>发布音乐</template>
          </el-menu-item>
          <el-menu-item index="/singer/musicManage">
            <el-icon><el-icon-collection /></el-icon>
            <template #title>歌曲管理</template>
          </el-menu-item>
          <el-menu-item index="/singer/message">
            <el-icon><el-icon-chat-dot-round /></el-icon>
            <template #title>消息中心</template>
          </el-menu-item>
          <el-menu-item index="/singer/profile">
            <el-icon><el-icon-user /></el-icon>
            <template #title>个人信息</template>
          </el-menu-item>
        </template>
        <!-- 用户菜单user -->
        <template v-if="userRole === 2">
          <el-menu-item index="/user/hotMusic" class="student-menu-item">
            <el-icon><el-icon-document /></el-icon>
            <template #title>热门推荐</template>
          </el-menu-item>
          <el-menu-item index="/user/mySongList" class="student-menu-item">
            <el-icon><el-icon-collection /></el-icon>
            <template #title>我的歌单</template>
          </el-menu-item>
          <el-menu-item index="/user/likeMusic" class="student-menu-item">
            <el-icon><el-icon-collection /></el-icon>
            <template #title>收藏音乐</template>
          </el-menu-item>
          <el-menu-item index="/user/message" class="student-menu-item">
            <el-icon><el-icon-bell /></el-icon>
            <template #title>消息中心</template>
          </el-menu-item>
          <el-menu-item index="/user/profile" class="student-menu-item">
            <el-icon><el-icon-user /></el-icon>
            <template #title>个人信息</template>
          </el-menu-item>
        </template>
      </el-menu>

      <div class="sidebar-footer">
        <el-tooltip :content="isCollapse ? '展开' : '收起'" placement="right">
          <div class="collapse-btn" @click="toggleCollapse">
            <el-icon v-if="isCollapse"><el-icon-arrow-right /></el-icon>
            <el-icon v-else><el-icon-arrow-left /></el-icon>
          </div>
        </el-tooltip>
      </div>
    </div>

    <!-- 主区域 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <div class="navbar">
        <div class="left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="right">
          <el-dropdown trigger="click">
            <div class="avatar-container">
              <!-- 用户头像显示 -->
              <div class="user-avatar">
                <img v-if="userAvatar" :src="userAvatar" alt="用户头像" class="avatar-img">
                <!-- 默认头像 -->
                <div v-else class="avatar-placeholder">{{ username.charAt(0) }}</div>
              </div>
              <span class="username">{{ roleName ? roleName + ' (' + username + ')' : username }}</span>
              <el-icon><el-icon-arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="app-main" :class="{ 'full-screen': $route.meta.fullscreen }">
        <router-view v-slot="{ Component }">
          <component :is="Component" />
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watchEffect } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import { getUserRole, getUsername, clearUserInfo, getName } from '@/utils/auth';
import { getAvatar } from '@/utils/auth'; // 导入头像相关函数
import {
  Odometer as ElIconOdometer,
  Bell as ElIconBell,
  User as ElIconUser,
  Key as ElIconKey,
  Document as ElIconDocument,
  DocumentChecked as ElIconDocumentChecked,
  Medal as ElIconMedal,
  Collection as ElIconStudy,
  QuestionFilled as ElIconQuestion,
  Edit as ElIconEdit,
  DataAnalysis as ElIconDataAnalysis,
  DataLine as ElIconDataLine,
  Collection as ElIconCollection,
  ArrowLeft as ElIconArrowLeft,
  ArrowRight as ElIconArrowRight,
  ArrowDown as ElIconArrowDown,
  ChatDotRound as ElIconChatDotRound
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();

// 侧边栏折叠状态
const isCollapse = ref(false);

// 当前用户信息
const userRole = ref(0); // 默认管理员
const username = ref(getUsername() || '管理员');
const name = ref(getName() || '');
const userAvatar = ref(getAvatar() || ''); // 用户头像，使用getAvatar函数获取

// 计算用户角色中文名
const roleName = computed(() => {
  switch (userRole.value) {
    case 0: return '管理员';
    case 1: return '歌手';
    case 2: return '用户';
    default: return '未知角色';
  }
});

// 更新用户名函数（增强版）
const updateUsername = () => {
  const newUsername = getUsername() || '管理员';
  if (newUsername !== username.value) {
    console.log(`用户名从 "${username.value}" 更新为 "${newUsername}"`);
    username.value = newUsername;
  }
};

onMounted(() => {
  // 确保获取到的角色值是数字类型
  const roleValue = getUserRole();
  userRole.value = typeof roleValue === 'string' ? parseInt(roleValue, 10) : roleValue || 0;
  console.log('当前用户角色:', userRole.value);
  console.log('初始用户名:', username.value);

  // 更新头像（使用getAvatar函数）
  userAvatar.value = getAvatar() || '';

  // 监听localStorage变化（跨标签页）
  window.addEventListener('storage', (event) => {
    console.log('检测到storage变化:', event.key, event.newValue);
    if (event.key === 'username' || event.key === null) { // null表示storage被清空
      updateUsername();
    }
    // 监听头像变化
    if (event.key === 'avatar') {
      userAvatar.value = event.newValue || '';
    }
  });
});

// 在watchEffect中也更新头像
watchEffect(() => {
  // 更频繁地检查localStorage中的用户名，缩短检查间隔
  const unwatch = setInterval(() => {
    updateUsername();
    // 同时检查头像更新
    const currentAvatar = getAvatar() || '';
    if (currentAvatar !== userAvatar.value) {
      userAvatar.value = currentAvatar;
    }
  }, 500); // 改为500ms检查一次

  return () => clearInterval(unwatch);
});

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path;
});

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清除登录状态
    clearUserInfo();

    // 跳转到登录页
    router.push('/login');
  }).catch(() => { });
};
</script>


<style scoped>
/* ========== 全局容器与背景 ========== */
.app-wrapper {
  position: relative;
  height: 100vh;
  width: 100%;
  display: flex;
  background: #ffffff;
  overflow: hidden;
}

/* ========== 侧边栏样式 - 红色顶部栏 ========== */
.sidebar-container {
  width: 240px;
  height: 100%;
  background: #ffffff;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 100;
  border-right: 1px solid #f0f0f0;
}

.sidebar-container.is-collapse {
  width: 64px;
}

/* 网易云风格红色顶部栏 */
.logo-container {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 20px;
  overflow: hidden;
  background: linear-gradient(135deg, #ec4141 0%, #c62f2f 100%);
  box-shadow: 0 2px 8px rgba(236, 65, 65, 0.3);
  position: relative;
  z-index: 10;
  border-right: 1px solid #c62f2f;
}

/* 红色渐变顶部装饰线 */
.logo-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #ff6b6b, #ec4141, #ff6b6b);
  z-index: 1;
}

.logo-icon {
  width: 36px;
  height: 36px;
  margin-right: 12px;
  border-radius: 8px;
  object-fit: cover;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.2);
  padding: 5px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.sidebar-container:hover .logo-icon {
  transform: rotate(15deg) scale(1.1);
  background: rgba(255, 255, 255, 0.3);
}

.title {
  color: #ffffff;
  font-size: 20px;
  font-weight: 700;
  white-space: nowrap;
  letter-spacing: 1px;
  transition: all 0.3s ease;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.title span {
  color: #fffacd;
}

.sidebar-container.is-collapse .title {
  opacity: 0;
  width: 0;
}

/* 侧边栏菜单样式 */
.sidebar-menu {
  flex: 1;
  border-right: none !important;
  background: transparent !important;
  padding: 15px 0;
}

/* Element Plus 菜单样式深度定制 - 白色主题 */
::v-deep(.sidebar-menu.el-menu) {
  --el-menu-bg-color: transparent;
  --el-menu-text-color: #333;
  --el-menu-hover-bg-color: rgba(236, 65, 65, 0.05);
  --el-menu-active-color: #ec4141;
}

::v-deep(.sidebar-menu .el-menu-item) {
  height: 48px;
  line-height: 48px;
  margin: 4px 12px;
  border-radius: 8px !important;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
}

::v-deep(.sidebar-menu .el-menu-item:hover) {
  background-color: rgba(236, 65, 65, 0.05) !important;
  transform: translateX(4px);
  color: #ec4141;
}

::v-deep(.sidebar-menu .el-menu-item.is-active) {
  background: rgba(236, 65, 65, 0.08) !important;
  color: #ec4141 !important;
  border-left: 3px solid #ec4141;
  font-weight: 600;
}

::v-deep(.sidebar-menu .el-icon) {
  font-size: 18px;
  margin-right: 10px;
  transition: transform 0.3s ease;
}

.sidebar-container.is-collapse ::v-deep(.sidebar-menu .el-menu-item span) {
  opacity: 0;
  width: 0;
  overflow: hidden;
}

.sidebar-container.is-collapse ::v-deep(.sidebar-menu .el-icon) {
  margin-right: 0;
  font-size: 20px;
}

/* 侧边栏折叠按钮 */
.sidebar-footer {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: center;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 50%;
  background: #f5f5f5;
  border: 1px solid #e0e0e0;
}

.collapse-btn:hover {
  background: rgba(236, 65, 65, 0.1);
  color: #ec4141;
  transform: scale(1.1);
  border-color: #ec4141;
}

/* ========== 主区域样式 ========== */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #ffffff;
}

/* 顶部导航栏 - 网易云红色 */
.navbar {
  height: 70px;
  padding: 0 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #ec4141 0%, #c62f2f 100%);
  position: relative;
  z-index: 50;
  border-bottom: none;
  box-shadow: 0 2px 8px rgba(236, 65, 65, 0.3);
}

/* 红色渐变顶部装饰线 */
.navbar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #ff6b6b, #ec4141, #ff6b6b);
  z-index: 1;
}

.navbar .left {
  flex: 1;
}

/* 面包屑导航样式 - 白色文字 */
::v-deep(.el-breadcrumb) {
  font-size: 14px;
}

::v-deep(.el-breadcrumb__inner) {
  color: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  font-weight: 500;
}

::v-deep(.el-breadcrumb__inner:hover) {
  color: #ffffff;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

::v-deep(.el-breadcrumb__separator) {
  color: rgba(255, 255, 255, 0.7);
}

::v-deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #ffffff;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* 右侧用户区域 */
.right {
  display: flex;
  align-items: center;
}

.avatar-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 25px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.avatar-container:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.user-avatar {
  width: 40px;
  height: 40px;
  margin-right: 12px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.avatar-container:hover .user-avatar {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.6);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
}

.username {
  margin-right: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

::v-deep(.el-icon) {
  color: rgba(255, 255, 255, 0.9);
  transition: color 0.3s ease;
}

.avatar-container:hover ::v-deep(.el-icon) {
  color: #ffffff;
}

/* 下拉菜单样式 */
::v-deep(.el-dropdown-menu) {
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  background: #ffffff;
}

::v-deep(.el-dropdown-menu__item) {
  padding: 12px 20px;
  font-size: 14px;
  color: #333;
  transition: all 0.3s ease;
}

::v-deep(.el-dropdown-menu__item:hover) {
  background: rgba(236, 65, 65, 0.05);
  color: #ec4141;
}

/* ========== 主内容区域 ========== */
.app-main {
  flex: 1;
  padding: 25px;
  overflow-y: auto;
  background: #ffffff;
  position: relative;
}

.app-main.full-screen {
  padding: 0;
}

/* 滚动条样式 - 红色滚动条 */
.app-main::-webkit-scrollbar {
  width: 8px;
}

.app-main::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 4px;
}

.app-main::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, #ec4141, #c62f2f);
  border-radius: 4px;
}

.app-main::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, #c62f2f, #a51c1c);
}

/* ========== 顶部栏与侧边栏顶部对齐效果 ========== */
/* 确保侧边栏顶部和主区域顶部栏高度一致并连接 */
.app-wrapper {
  position: relative;
}

.sidebar-container,
.main-container {
  position: relative;
}

/* 移除连接处的边框 */
.sidebar-container {
  border-right: 1px solid #c62f2f;
}

/* 当侧边栏折叠时的顶部栏连接效果 */
.sidebar-container.is-collapse .logo-container {
  background: linear-gradient(135deg, #ec4141 0%, #c62f2f 100%);
  box-shadow: 0 2px 8px rgba(236, 65, 65, 0.3);
  border-right: 1px solid #c62f2f;
}

/* ========== 过渡动画 ========== */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

/* ========== 响应式设计 ========== */
@media (max-width: 1200px) {
  .sidebar-container {
    width: 200px;
  }

  .sidebar-container.is-collapse {
    width: 64px;
  }

  .navbar {
    padding: 0 20px;
  }
}

@media (max-width: 768px) {
  .sidebar-container {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;
    transform: translateX(-100%);
  }

  .sidebar-container.is-collapse {
    transform: translateX(0);
    width: 240px;
  }

  .main-container {
    margin-left: 0;
  }

  .navbar {
    padding: 0 15px;
  }

  .username {
    display: none;
  }

  .avatar-container {
    padding: 8px;
  }

  .app-main {
    padding: 15px;
  }
}

@media (max-width: 480px) {
  .logo-container {
    height: 60px;
    padding: 0 15px;
  }

  .logo-icon {
    width: 30px;
    height: 30px;
  }

  .title {
    font-size: 16px;
  }

  ::v-deep(.sidebar-menu .el-menu-item) {
    height: 44px;
    line-height: 44px;
    margin: 3px 10px;
    font-size: 13px;
  }

  .navbar {
    height: 60px;
  }

  .user-avatar {
    width: 36px;
    height: 36px;
  }

  /* 移动端调整顶部红色区域 */
  .logo-container,
  .navbar {
    background: linear-gradient(135deg, #ec4141 0%, #c62f2f 100%);
  }
}
</style>