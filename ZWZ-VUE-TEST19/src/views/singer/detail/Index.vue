<template>
  <div class="singer-detail-container">
    <!-- 歌手信息卡片 -->
    <div class="singer-info-card">
      <div class="singer-avatar">
        <img :src="singerInfo.imageUrl || '/default-avatar.png'" alt="歌手头像" />
      </div>
      <div class="singer-info">
        <h2 class="singer-name">{{ singerInfo.username || '加载中...' }}</h2>
        <div class="singer-meta">
          <span class="singer-activation" :class="{ active: singerInfo.activation === 0 }">
            {{ singerInfo.activation === 0 ? '已激活' : '已禁用' }}
          </span>
          <span class="singer-join-date">
            {{ formatDate(singerInfo.createTime) }}
          </span>
        </div>
        <div class="singer-stats">
          <span class="stat-item">
            <strong>{{ musicList.length }}</strong>
            <span>音乐作品</span>
          </span>
        </div>
      </div>
    </div>

    <!-- 歌手简介 -->
    <div class="singer-bio">
      <h3>歌手简介</h3>
      <p>{{ singerInfo.about || '该歌手暂无简介' }}</p>
    </div>

    <!-- 音乐列表区域 -->
    <div class="music-list-section">
      <div class="section-header">
        <h3>音乐作品</h3>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>正在加载音乐列表...</p>
      </div>

      <!-- 错误提示 -->
      <div v-else-if="error" class="error-container">
        <p class="error-message">{{ error }}</p>
        <button @click="fetchMusicList" class="retry-button">重试</button>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!loading && musicList.length === 0" class="empty-container">
        <p>暂无音乐作品</p>
      </div>

      <!-- 音乐列表 -->
      <div v-else class="music-list">
        <div 
          v-for="music in musicList" 
          :key="music.id" 
          class="music-item"
          @click="playMusic(music)"
        >
          <div class="music-index">{{ musicList.indexOf(music) + 1 }}</div>
          <div class="music-info">
            <h4 class="music-name">{{ music.musicName }}</h4>
            <p class="music-meta">
              <span class="music-status" :class="{ active: music.status === 0 }">
                {{ music.status === 0 ? '已发布' : '已下架' }}
              </span>
              <span class="music-date">{{ formatDate(music.createTime) }}</span>
            </p>
          </div>
          <div class="music-actions">
            <button @click.stop="playMusic(music)" class="action-button play-btn">
              <i class="icon-play"></i> 播放
            </button>
            <button @click.stop="editMusic(music)" class="action-button edit-btn">
              <i class="icon-edit"></i> 编辑
            </button>
            <button @click.stop="deleteMusic(music.id)" class="action-button delete-btn">
              <i class="icon-delete"></i> 删除
            </button>
          </div>
        </div>
      </div>

      <!-- 分页控件 -->
      <div v-if="!loading && !error && total > 0" class="pagination-container">
        <div class="pagination-info">
          共 {{ total }} 条记录
        </div>
        <div class="pagination-controls">
          <button 
            @click="changePage(currentPage - 1)" 
            :disabled="currentPage === 1"
            class="page-button"
          >
            上一页
          </button>
          <span class="current-page">{{ currentPage }} / {{ totalPages }}</span>
          <button 
            @click="changePage(currentPage + 1)" 
            :disabled="currentPage >= totalPages"
            class="page-button"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 音乐播放组件 -->
    <div v-if="playingMusic" class="music-player">
      <div class="player-info">
        <h4>{{ playingMusic.musicName }}</h4>
        <p>正在播放</p>
      </div>
      <div class="player-controls">
        <button @click="pauseMusic" class="control-button">
          <i class="icon-pause"></i> 暂停
        </button>
        <button @click="stopMusic" class="control-button">
          <i class="icon-stop"></i> 停止
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getCurrentSingerInfo, getLoginSingerMusicList } from '@/api/singer'

// 状态定义
const singerInfo = ref({})
const musicList = ref([])
const loading = ref(false)
const error = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const playingMusic = ref(null)

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 获取歌手信息
const fetchSingerInfo = async () => {
  try {
    const response = await getCurrentSingerInfo()
    singerInfo.value = response
  } catch (err) {
    console.error('获取歌手信息失败:', err)
    error.value = '获取歌手信息失败'
  }
}

// 获取音乐列表
const fetchMusicList = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await getLoginSingerMusicList({
      pn: currentPage.value,
      size: pageSize.value
    })
    // 根据接口文档，response可能直接包含音乐列表或在data字段中
    if (response.data && Array.isArray(response.data.records)) {
      musicList.value = response.data.records
      total.value = response.data.total
    } else if (Array.isArray(response)) {
      musicList.value = response
      total.value = response.length
    } else {
      // 如果响应不符合预期，使用空列表
      musicList.value = []
      total.value = 0
    }
  } catch (err) {
    console.error('获取音乐列表失败:', err)
    error.value = '获取音乐列表失败'
    musicList.value = []
  } finally {
    loading.value = false
  }
}

// 切换页码
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    fetchMusicList()
  }
}

// 播放音乐
const playMusic = (music) => {
  playingMusic.value = music
  console.log('播放音乐:', music.musicName)
  // 这里应该集成实际的音乐播放逻辑
}

// 暂停音乐
const pauseMusic = () => {
  console.log('暂停音乐')
  // 这里应该集成实际的音乐暂停逻辑
}

// 停止音乐
const stopMusic = () => {
  playingMusic.value = null
  console.log('停止音乐')
  // 这里应该集成实际的音乐停止逻辑
}

// 编辑音乐
const editMusic = (music) => {
  console.log('编辑音乐:', music)
  // 这里应该跳转到编辑音乐页面或打开编辑对话框
}

// 删除音乐
const deleteMusic = async (musicId) => {
  if (confirm('确定要删除这首音乐吗？')) {
    try {
      // 调用删除音乐的API
      console.log('删除音乐:', musicId)
      // 重新获取音乐列表
      fetchMusicList()
    } catch (err) {
      console.error('删除音乐失败:', err)
      alert('删除音乐失败')
    }
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchSingerInfo()
  fetchMusicList()
})
</script>

<style scoped>
.singer-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 歌手信息卡片 */
.singer-info-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.singer-avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.singer-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.singer-info {
  flex: 1;
}

.singer-name {
  font-size: 28px;
  margin: 0 0 10px 0;
  color: #333;
}

.singer-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.singer-activation {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  background: #ff4d4f;
  color: #fff;
}

.singer-activation.active {
  background: #52c41a;
}

.singer-join-date {
  font-size: 14px;
  color: #666;
}

.singer-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-item strong {
  font-size: 24px;
  color: #333;
}

.stat-item span {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

/* 歌手简介 */
.singer-bio {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.singer-bio h3 {
  font-size: 20px;
  margin: 0 0 15px 0;
  color: #333;
}

.singer-bio p {
  font-size: 16px;
  line-height: 1.8;
  color: #666;
  margin: 0;
}

/* 音乐列表区域 */
.music-list-section {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 20px;
  margin: 0;
  color: #333;
}

/* 加载状态 */
.loading-container,
.error-container,
.empty-container {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 20px;
}

.retry-button {
  padding: 6px 16px;
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.retry-button:hover {
  background: #40a9ff;
}

/* 音乐列表 */
.music-list {
  margin-bottom: 30px;
}

.music-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.3s;
}

.music-item:hover {
  background: #fafafa;
}

.music-index {
  width: 40px;
  text-align: center;
  font-size: 16px;
  color: #999;
}

.music-info {
  flex: 1;
  margin-left: 20px;
}

.music-name {
  font-size: 16px;
  margin: 0 0 8px 0;
  color: #333;
}

.music-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin: 0;
}

.music-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  background: #ff4d4f;
  color: #fff;
}

.music-status.active {
  background: #52c41a;
}

.music-date {
  font-size: 14px;
  color: #999;
}

.music-actions {
  display: flex;
  gap: 10px;
}

.action-button {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.action-button:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.play-btn:hover {
  color: #52c41a;
  border-color: #52c41a;
}

.edit-btn:hover {
  color: #faad14;
  border-color: #faad14;
}

.delete-btn:hover {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

/* 分页控件 */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-button {
  padding: 6px 16px;
  border: 1px solid #d9d9d9;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.page-button:hover:not(:disabled) {
  color: #1890ff;
  border-color: #1890ff;
}

.page-button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.current-page {
  font-size: 14px;
  color: #333;
}

/* 音乐播放器 */
.music-player {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.1);
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1000;
}

.player-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #333;
}

.player-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.player-controls {
  display: flex;
  gap: 10px;
}

.control-button {
  padding: 6px 16px;
  border: 1px solid #d9d9d9;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.control-button:hover {
  color: #1890ff;
  border-color: #1890ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .singer-detail-container {
    padding: 10px;
  }
  
  .singer-info-card {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }
  
  .singer-avatar {
    margin-right: 0;
    margin-bottom: 20px;
  }
  
  .singer-meta {
    justify-content: center;
  }
  
  .music-item {
    flex-wrap: wrap;
  }
  
  .music-actions {
    margin-top: 10px;
    width: 100%;
    justify-content: flex-end;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 15px;
  }
}
</style>