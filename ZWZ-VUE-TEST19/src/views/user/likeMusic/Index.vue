<template>
  <div class="like-music-container">
    <div class="page-header">
      <h1>我的收藏音乐</h1>
    </div>
    <div class="content-wrapper">
      <!-- 错误状态 -->
      <div v-if="error" class="error-container">
        <p>{{ error }}</p>
        <button @click="loadLikedMusic">重新加载。。。</button>
      </div>
      <!-- 空状态 -->
      <div v-else-if="likedMusicList.length === 0" class="empty-container">
        <p>还没收藏任何音乐。。。</p>
      </div>
      <!-- 音乐列表 -->
      <div v-else class="music-list">
        <div v-for="(music, index) in likedMusicList" :key="music.musicId" class="music-item">
          <!-- 封面图片 -->
          <div class="music-cover-wrapper" @click="playMusic(music)">
            <img v-bind:src="music.imageUrl" alt="音乐封面" :title="music.musicName || '未知歌曲'"
              @error="handleImageError($event)">
          </div>

          <!-- 音乐信息 -->
          <div class="music-info">
            <h3 class="music-title">{{ music.musicName || '未知歌曲' }}</h3>
            <div class="music-meta">
              <span>{{ music.musicSinger || music.singerUsername || '未知歌手' }}</span>
              <span>{{ formatTime(music.timeLength) }}</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="music-actions">
            <button class="unlike-btn" @click="handleUnlike(music.musicId, index)">
              取消收藏
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 音乐播放器组件 -->
    <MusicPlayer v-if="currentMusic.url" :audio-url="currentMusic.url" :autoplay="true" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getMyLike, removeLike } from '@/api/mylike.js';
import { defineAsyncComponent } from 'vue';
const MusicPlayer = defineAsyncComponent(() => import('@/views/component/musicPlayer.vue'));
import { ElMessage } from 'element-plus';

// 状态管理
const likedMusicList = ref([]);
const error = ref('');
const currentMusic = ref({ url: '' });

// 生命周期钩子
onMounted(() => {
  // 加载收藏音乐
  loadLikedMusic();
});

// 加载收藏音乐列表
const loadLikedMusic = async () => {
  error.value = '';

  try {
    const response = await getMyLike();

    // 验证数据格式
    if (Array.isArray(response)) {
      likedMusicList.value = response;
    } else {
      console.error('数据格式错误:', response);
      error.value = '数据格式错误';
    }
  } catch (err) {
    console.error('加载收藏音乐失败:', err);
    error.value = '加载失败，请稍后重试';
  }
};

// 播放音乐
const playMusic = (music) => {
  // 检查歌曲是否被冻结
  if (music.activation === 0) {
    console.log('歌曲已被冻结:', music.musicName);
    ElMessage.error('这个歌曲已被下架，无法播放！');
    return;
  }

  currentMusic.value = {
    url: music.musicUrl || '',
    id: music.musicId,
    name: music.musicName
  };
};

// 取消收藏
const handleUnlike = async (musicId, index) => {
  try {
    const response = await removeLike(musicId);

    // 检查是否取消成功
    if (response.code === 20 || response.success === true) {
      // 从列表中移除
      likedMusicList.value.splice(index, 1);
      console.log('取消收藏成功');
    } else {
      console.error('取消收藏失败:', response);
      alert('取消收藏失败: ' + (response.message || '未知错误'));
    }
  } catch (err) {
    console.error('取消收藏请求失败:', err);
    alert('取消收藏失败，请稍后重试');
  }
};

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = '/default-music-cover.png'; // 默认封面图
};

// 格式化时间
const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '0:00';

  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs.toString().padStart(2, '0')}`;
};
</script>

<style scoped>
/* ========== 主容器与背景 ========== */
.like-music-container {
  padding: 30px;
  background: linear-gradient(180deg, #f9f9f9 0%, #f0f0f0 100%);
  min-height: 100vh;
  padding-bottom: 100px;
  /* 为底部播放器预留空间 */
}

/* ========== 页面标题 ========== */
.page-header {
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  text-align: left;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0;
  position: relative;
  padding-left: 15px;
}

.page-header h1::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(180deg, #ec4141 0%, #c62f2f 100%);
  border-radius: 2px;
}

/* ========== 内容包装器 ========== */
.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
}

/* ========== 状态提示（错误、空状态） ========== */
.error-container,
.empty-container {
  text-align: center;
  padding: 80px 20px;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.empty-container p {
  font-size: 16px;
  color: #999;
  margin: 0;
}

.error-container p {
  font-size: 16px;
  color: #f56c6c;
  margin-bottom: 20px;
}

.error-container button {
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  color: white;
  border: none;
  border-radius: 20px;
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.error-container button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(236, 65, 65, 0.3);
}

/* ========== 音乐列表 - 卡片网格布局 ========== */
.music-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 25px;
}

/* ========== 音乐卡片项 ========== */
.music-item {
  background-color: #fff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #f0f0f0;
  position: relative;
  /* 遵循一卡一内容的设计规则，将歌曲所有信息和操作整合在卡片内[citation:10] */
}

.music-item:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
  border-color: rgba(236, 65, 65, 0.2);
}

/* 封面图片容器 */
.music-cover-wrapper {
  width: 100%;
  height: 260px;
  overflow: hidden;
  position: relative;
  background-color: #f5f5f5;
}

.music-cover-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.8s ease;
}

.music-item:hover .music-cover-wrapper img {
  transform: scale(1.08);
}

/* 封面上的播放遮罩/图标（模仿黑胶唱片播放感[citation:5]） */
.music-cover-wrapper::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 48px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.music-item:hover .music-cover-wrapper::after {
  opacity: 1;
  content: '▶';
}

/* 音乐信息区域 */
.music-info {
  padding: 20px;
}

.music-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.4;
  /* 确保标题在卡片内清晰展示，这是卡片的基础信息[citation:2] */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.8em;
}

.music-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #999;
}

/* 操作按钮区域 */
.music-actions {
  padding: 0 20px 20px;
  text-align: center;
}

.unlike-btn {
  width: 100%;
  padding: 10px;
  background-color: transparent;
  color: #ec4141;
  border: 1px solid #ffcdd2;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.unlike-btn:hover {
  background-color: rgba(236, 65, 65, 0.05);
  border-color: #ec4141;
  transform: translateY(-1px);
}

/* ========== 响应式设计 ========== */
@media (max-width: 1200px) {
  .music-list {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .like-music-container {
    padding: 20px 15px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .music-list {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 15px;
  }

  .music-cover-wrapper {
    height: 200px;
  }
}

@media (max-width: 480px) {
  .music-list {
    grid-template-columns: 1fr;
    /* 在小屏幕上切换为单列列表布局，信息展示更清晰[citation:6] */
  }

  .page-header h1 {
    font-size: 20px;
    text-align: center;
    padding-left: 0;
  }

  .page-header h1::before {
    display: none;
  }
}
</style>