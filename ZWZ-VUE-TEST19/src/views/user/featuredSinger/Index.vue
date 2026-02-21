<template>
  <div class="container">
    <h2>精选歌手</h2>
    <div v-if="loading" class="loading">
      加载中...
    </div>
    <div v-else-if="error" class="error">
      {{ error }}
    </div>
    <div v-else-if="singerList.length > 0" class="singer-list">
      <div 
        v-for="singer in singerList" 
        :key="singer.id" 
        class="singer-item"
      >
        <img 
          :src="processAvatarUrl(singer.avatar)" 
          :alt="singer.username" 
          class="singer-avatar"
        />
        <div class="singer-info">
          <h3 class="singer-name">{{ singer.username }}</h3>
          <p class="singer-about">{{ singer.about || '暂无简介' }}</p>
        </div>
      </div>
    </div>
    <div v-else class="empty">
      暂无歌手信息
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Mod_music } from '@/api/music';

const singerList = ref([]);
const loading = ref(false);
const error = ref('');

// 处理头像URL
const processAvatarUrl = (avatar) => {
  if (!avatar) return '/music.ico'; // 使用默认图标作为备用
  // 如果是绝对路径则直接返回，否则添加public前缀
  return avatar.startsWith('http') || avatar.startsWith('/') ? avatar : `/${avatar}`;
};

// 加载推荐歌手列表
const loadRecommendSingers = async () => {
  loading.value = true;
  error.value = '';
  try {
    console.log('开始加载推荐歌手列表...');
    const response = await Mod_music.RecommendSinger();
    console.log('推荐歌手数据响应:', response);
    
    // 增强的响应格式处理逻辑
    // 1. 检查响应是否有效
    if (!response) {
      throw new Error('未收到服务器响应');
    }
    
    // 2. 检查成功状态码
    if (response.code === 200 || response.success === true) {
      // 3. 多种数据格式兼容处理
      let singersData = [];
      
      // 主要格式：response.data.singers
      if (response.data && Array.isArray(response.data.singers)) {
        singersData = response.data.singers;
        console.log('使用格式: response.data.singers');
      }
      // 备用格式1：response.data
      else if (Array.isArray(response.data)) {
        singersData = response.data;
        console.log('使用格式: response.data (直接数组)');
      }
      // 备用格式2：检查是否有其他可能的数据路径
      else if (response.list && Array.isArray(response.list)) {
        singersData = response.list;
        console.log('使用格式: response.list');
      }
      // 备用格式3：检查嵌套更深的结构
      else if (response.data && response.data.data && Array.isArray(response.data.data)) {
        singersData = response.data.data;
        console.log('使用格式: response.data.data');
      }
      // 数据格式不匹配
      else {
        console.error('数据格式无法识别:', response);
        throw new Error('服务器返回的数据格式无法识别');
      }
      
      // 4. 数据验证和过滤
      if (singersData.length === 0) {
        console.warn('没有获取到歌手数据');
        singerList.value = [];
      } else {
        // 过滤和规范化数据
        singerList.value = singersData.filter(singer => singer && singer.id !== undefined)
          .map(singer => ({
            id: singer.id,
            username: singer.username || singer.name || '未知歌手',
            avatar: singer.avatar || '',
            about: singer.about || singer.description || ''
          }));
        console.log('成功加载歌手数量:', singerList.value.length);
      }
    } else {
      // 处理错误状态码
      const errorMsg = response.message || response.msg || '获取推荐歌手失败';
      console.error('API错误:', errorMsg, '状态码:', response.code);
      throw new Error(errorMsg);
    }
  } catch (err) {
    console.error('加载推荐歌手失败:', err);
    error.value = err.message || '加载失败，请稍后重试';
    
    // 移除模拟数据调用，按要求不使用模拟数据
  } finally {
    loading.value = false;
    console.log('推荐歌手加载完成');
  }
};

onMounted(() => {
  console.log('精选歌手页面加载完成');
  loadRecommendSingers();
});
</script>

<style scoped>
.container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 20px;
  font-weight: 500;
}

p {
  color: #606266;
  font-size: 14px;
}
</style>