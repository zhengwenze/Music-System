<template>
  <div class="hot-music-container">
    <h2 class="page-title">热门推荐</h2>
    <!-- 音乐播放器组件 -->
    <music-player v-if="currentPlayingUrl" :audio-url="currentPlayingUrl" :autoplay="true" />
    <!-- 排行榜类型切换 -->
    <div class="rank-tabs">
      <el-tabs v-model="activeRankTab" @tab-click="handleRankTabChange">
        <el-tab-pane label="热门排行榜" name="hot">
          <div class="rank-content">
            <!-- 加载状态 -->
            <div v-if="loading" class="loading-container">
              <el-loading v-loading="loading" text="加载中..." />
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="error-container">
              <el-empty description="加载失败" :image-size="120" />
              <el-button type="primary" @click="loadRankMusic('hot')">重新加载</el-button>
            </div>

            <!-- 空状态 -->
            <div v-else-if="hotRankList.length === 0" class="empty-container">
              <el-empty description="暂无热门音乐" :image-size="120" />
            </div>

            <!-- 排行榜音乐列表 -->
            <div v-else class="music-list">
              <div v-for="(music, index) in hotRankList" :key="music.musicId || index" class="music-item"
                @click="playMusic(music)">
                <!-- 排名 -->
                <div class="rank-number" :class="{ 'top3': index < 3 }">
                  {{ index + 1 }}
                </div>

                <!-- 音乐封面 -->
                <div class="music-cover" @click.stop="playMusic(music)">
                  <img :src="music.img || defaultCover" :alt="music.musicName" class="cover-image">
                  <div class="play-icon">
                    <i class="el-icon-play"></i>
                  </div>
                </div>

                <!-- 音乐信息 -->
                <div class="music-info">
                  <h3 class="music-name">{{ music.musicName || '未知歌曲' }}</h3>
                  <p class="music-artist">{{ music.singer || '未知歌手' }}</p>
                  <p class="music-stats">{{ formatListenCount(music.listenNumb) }} 次播放</p>
                </div>

                <!-- 操作按钮 -->
                <div class="music-actions">
                  <el-button type="text" :icon="music.isLike === 1 ? 'el-icon-star-on' : 'el-icon-star-off'"
                    @click.stop="handleLike(music)" :class="{ 'liked': music.isLike === 1 }" :disabled="isLiking">
                    {{ music.isLike === 1 ? '已收藏' : '收藏' }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="最新排行榜" name="newest">
          <div class="rank-content">
            <!-- 加载状态 -->
            <div v-if="loading" class="loading-container">
              <el-loading v-loading="loading" text="加载中..." />
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="error-container">
              <el-empty description="加载失败" :image-size="120" />
              <el-button type="primary" @click="loadRankMusic('newest')">重新加载</el-button>
            </div>

            <!-- 空状态 -->
            <div v-else-if="newestRankList.length === 0" class="empty-container">
              <el-empty description="暂无最新音乐" :image-size="120" />
            </div>

            <!-- 排行榜音乐列表 -->
            <div v-else class="music-list">
              <div v-for="(music, index) in newestRankList" :key="music.musicId || index" class="music-item"
                @click="playMusic(music)">
                <!-- 排名 -->
                <div class="rank-number" :class="{ 'top3': index < 3 }">
                  {{ index + 1 }}
                </div>

                <!-- 音乐封面 -->
                <div class="music-cover" @click.stop="playMusic(music)">
                  <img :src="music.img || defaultCover" :alt="music.musicName" class="cover-image">
                  <div class="play-icon">
                    <i class="el-icon-play"></i>
                  </div>
                </div>

                <!-- 音乐信息 -->
                <div class="music-info">
                  <h3 class="music-name">{{ music.musicName || '未知歌曲' }}</h3>
                  <p class="music-artist">{{ music.singer || '未知歌手' }}</p>
                  <p class="music-stats">{{ formatDate(music.createTime) }}</p>
                </div>

                <!-- 操作按钮 -->
                <div class="music-actions">
                  <el-button type="text" :icon="music.isLike === 1 ? 'el-icon-star-on' : 'el-icon-star-off'"
                    @click.stop="handleLike(music)" :class="{ 'liked': music.isLike === 1 }" :disabled="isLiking">
                    {{ music.isLike === 1 ? '已收藏' : '收藏' }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="音乐搜索" name="search">
          <div class="search-content">
            <!-- 搜索栏 -->
            <div class="search-bar">
              <el-input v-model="searchKeyword" placeholder="搜索歌曲名称" clearable @keyup.enter="handleSearch">
                <template #append>
                  <el-button @click="handleSearch">搜索</el-button>
                </template>
              </el-input>
            </div>

            <!-- 加载状态 -->
            <div v-if="searchLoading" class="loading-container">
              <el-loading v-loading="searchLoading" text="加载中..." />
            </div>

            <!-- 错误状态 -->
            <div v-else-if="searchError" class="error-container">
              <el-empty description="加载失败" :image-size="120" />
              <el-button type="primary" @click="getMusicSearchList">重新加载</el-button>
            </div>

            <!-- 空状态 -->
            <div v-else-if="searchMusicList.length === 0" class="empty-container">
              <el-empty description="暂无搜索结果" :image-size="120" />
            </div>

            <!-- 音乐列表 -->
            <div v-else class="music-list">
              <div v-for="(music, index) in searchMusicList" :key="music.musicId || index" class="music-item"
                @click="playMusic(music)">
                <!-- 排名 -->
                <div class="rank-number">
                  {{ index + 1 }}
                </div>

                <!-- 音乐封面 -->
                <div class="music-cover" @click.stop="playMusic(music)">
                  <img :src="music.img || defaultCover" :alt="music.musicName" class="cover-image">
                  <div class="play-icon">
                    <i class="el-icon-play"></i>
                  </div>
                </div>

                <!-- 音乐信息 -->
                <div class="music-info">
                  <h3 class="music-name">{{ music.musicName || '未知歌曲' }}</h3>
                  <p class="music-artist">{{ music.singer || '未知歌手' }}</p>
                  <p class="music-stats">{{ formatListenCount(music.listenNumb) }} 次播放</p>
                </div>

                <!-- 操作按钮 -->
                <div class="music-actions">
                  <el-button type="text" :icon="music.isLike === 1 ? 'el-icon-star-on' : 'el-icon-star-off'"
                    @click.stop="handleLike(music)" :class="{ 'liked': music.isLike === 1 }" :disabled="isLiking">
                    {{ music.isLike === 1 ? '已收藏' : '收藏' }}
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 分页组件 -->
            <div class="pagination-container">
              <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="totalMusicCount"
                v-model:current-page="currentPage" v-model:page-size="pageSize" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :page-sizes="[5, 10, 20, 50, 100]" />
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="推荐歌手" name="singer">
          <div class="singer-content">
            <!-- 加载状态 -->
            <div v-if="singerLoading" class="loading-container">
              <el-loading v-loading="singerLoading" text="加载中..." />
            </div>

            <!-- 错误状态 -->
            <div v-else-if="singerError" class="error-container">
              <el-empty description="加载失败" :image-size="120" />
              <el-button type="primary" @click="getRecommendSingersList">重新加载</el-button>
            </div>

            <!-- 空状态 -->
            <div v-else-if="recommendSingers.length === 0" class="empty-container">
              <el-empty description="暂无推荐歌手" :image-size="120" />
            </div>

            <!-- 歌手列表 -->
            <div v-else class="singer-list">
              <div v-for="singer in recommendSingers" :key="singer.id" class="singer-item">
                <!-- 歌手头像 -->
            <div class="singer-avatar" @click="openSingerSongPopup(singer.id, singer.username)">
              <img :src="processImageUrl(singer.avatar)" :alt="singer.username" class="avatar-image">
            </div>

                <!-- 歌手信息 -->
                <div class="singer-info">
                  <h3 class="singer-name">{{ singer.username || '未知歌手' }}</h3>
                  <p class="singer-about">{{ singer.about || '暂无简介' }}</p>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 歌手歌曲列表弹窗 -->
    <el-dialog v-model="singerSongDialogVisible" :title="singerSongDialogTitle" width="800px">
      <el-table :data="singerSongList" v-loading="singerSongLoading" style="margin-top: 20px; margin-left: auto; margin-right: auto;"
        :default-sort="{ prop: 'musicId', order: 'ascending' }">
        <el-table-column prop="musicId" label="ID" width="50" />
        <el-table-column prop="musicName" label="音乐名称" width="150" />
        <el-table-column label="音乐封面" width="150">
          <template #default="{ row }">
            <el-image v-if="row.imageUrl" :src="row.imageUrl" :preview-src-list="[row.imageUrl]" fit="cover"
              style="width: 80px; height: 80px; border-radius: 4px;" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="歌手" width="120">
          <template #default="{ row }">
            {{ row.singerUsername || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="tags" label="标签" width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
      </el-table>

      <!-- 分页 -->
      <el-pagination v-model:current-page="singerSongCurrentPage" v-model:page-size="singerSongPageSize" :page-sizes="[5, 10, 20, 50, 100]"
        :total="singerSongTotal" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSingerSongSizeChange"
        @current-change="handleSingerSongCurrentChange" style="margin-top: 20px; justify-content: center;" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { ElMessage, ElTabs, ElTabPane } from 'element-plus';
import { getMyLike, addLike, removeLike } from '@/api/mylike';
import { getMusicRecommendList, getMusicPage, getRecommendSingers, getSingerSongs } from '@/api/music';
import { defineAsyncComponent } from 'vue';
const MusicPlayer = defineAsyncComponent(() => import('@/views/component/musicPlayer.vue'));

// 响应式数据
const hotRankList = ref([]);
const newestRankList = ref([]);
const loading = ref(false);
const error = ref('');
const currentPlayingUrl = ref('');
const defaultCover = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
const likedMusicIds = ref(new Set());
const isLiking = ref(false); // 防止重复点击收藏按钮
const activeRankTab = ref('hot'); // 默认显示热门排行榜

// 推荐歌手相关数据
const recommendSingers = ref([]);
const singerLoading = ref(false);
const singerError = ref('');

// 音乐搜索相关数据
const searchMusicList = ref([]);
const searchLoading = ref(false);
const searchError = ref('');
const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const totalMusicCount = ref(0);

// 歌手歌曲列表弹窗相关数据
const singerSongDialogVisible = ref(false);
const singerSongDialogTitle = ref('');
const singerSongList = ref([]);
const singerSongLoading = ref(false);
const singerSongCurrentPage = ref(1);
const singerSongPageSize = ref(5);
const singerSongTotal = ref(0);
const currentSingerId = ref(null);

// 处理图片URL，确保正确显示
const processImageUrl = (url) => {
  if (!url) return defaultCover;

  // 如果已经是完整URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }

  // 处理相对路径，根据API文档，可能需要添加相应的前缀
  if (url.startsWith('/')) {
    return url;
  }

  return `/music/${url}`;
};

// 处理音乐URL，确保正确播放
const processMusicUrl = (url) => {
  if (!url) return '';

  // 如果已经是完整URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }

  // 处理相对路径
  if (url.startsWith('/')) {
    return url;
  }

  return `/music/${url}`;
};

// 格式化播放次数
const formatListenCount = (count) => {
  if (typeof count !== 'number') return '0';
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万';
  }
  return count.toString();
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  try {
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  } catch (error) {
    return dateString;
  }
};

// 加载排行榜音乐数据
const loadRankMusic = async (type = 'hot') => {
  loading.value = true;
  error.value = '';

  try {
    // 异步加载用户收藏列表，不阻塞主数据加载
    loadLikedMusicIds();

    // 调用API获取音乐排行榜，确保使用正确的参数
    const response = await getMusicRecommendList(type);
    // 输出完整的response结构以便快速分析数据路径
    console.log(`=== ${type}排行榜API完整响应结构 ===`, JSON.stringify(response, null, 2));
    // 全面的响应数据验证和处理 - 根据用户反馈和API文档调整
    let musicList = response.data.data.list;
    if (musicList.length > 0) {
      // 转换数据格式并标记已收藏状态
      const processedList = musicList.map((music, index) => {
        // 确保music是对象
        if (typeof music !== 'object' || music === null) {
          return null;
        }

        // 获取歌曲ID的多种可能字段名
        const musicId = music.musicId || music.id || music.music_id;

        // 如果没有有效的音乐ID，跳过该项
        if (!musicId) {
          return null;
        }

        // 处理收藏状态
        const isLiked = (music.isLike !== undefined && (music.isLike === 0 || music.isLike === 1)) ? music.isLike :
          (music.is_liked !== undefined && (music.is_liked === 0 || music.is_liked === 1)) ? music.is_liked :
            (likedMusicIds.value.has(musicId) ? 1 : 0);

        // 提取图片和音乐URL
        const imageUrl = music.imageUrl || music.img || music.image || music.cover;
        const musicUrl = music.musicUrl || music.url;

        // 根据API文档提取所有可能的歌曲信息字段
        const processedMusic = {
          musicId: musicId,
          musicName: music.musicName || music.name || music.title || '未知歌曲',
          singer: music.singer || music.artist || music.singerName || '未知歌手',
          img: processImageUrl(imageUrl),
          url: processMusicUrl(musicUrl),
          listenNumb: typeof music.listenNumb === 'number' ? music.listenNumb :
            typeof music.listenCount === 'number' ? music.listenCount : 0,
          createTime: music.createTime || music.time || '',
          isLike: isLiked,
          activation: music.activation || 1, // 默认1表示正常，0表示冻结
          album: music.album || '',
          fromSinger: music.fromSinger || null
        };

        return processedMusic;
      }).filter(item => item !== null && item.activation === 1); // 过滤无效数据和下架歌曲

      // 根据类型更新对应的排行榜数据
      if (type === 'hot') {
        hotRankList.value = processedList;
      } else {
        newestRankList.value = processedList;
      }
    } else {
      // 即使没有数据也不报错，而是显示空状态
      if (type === 'hot') {
        hotRankList.value = [];
      } else {
        newestRankList.value = [];
      }
    }
  } catch (err) {
    error.value = '加载失败，请稍后重试';
    ElMessage.error(`加载${type === 'hot' ? '热门' : '最新'}音乐失败`);
    // 提供模拟数据作为备份
    const mockData = [];
    if (type === 'hot') {
      hotRankList.value = mockData;
    } else {
      newestRankList.value = mockData;
    }
  } finally {
    loading.value = false;
  }
};
// 加载用户收藏的音乐ID列表
const loadLikedMusicIds = async () => {
  try {
    const response = await getMyLike();
    // 输出完整的收藏列表API响应结构
    console.log('=== 收藏列表API完整响应结构 ===', JSON.stringify(response, null, 2));
    // 重置收藏ID集合
    likedMusicIds.value = new Set();
    // 根据API响应格式处理数据
    // 情况1: 直接返回的是收藏列表数组
    if (Array.isArray(response)) {
      response.forEach(item => {
        const id = item?.id || item?.musicId;
        if (id) {
          likedMusicIds.value.add(id);
        }
      });
    }
    // 情况2: 响应对象包含code和data字段
    else if (response && response.code === 20 && Array.isArray(response.data)) {
      response.data.forEach(item => {
        const id = item?.id || item?.musicId;
        if (id) {
          likedMusicIds.value.add(id);
        }
      });
    }
    // 情况3: 响应对象有嵌套的data.likelist结构
    else if (response && response.data && response.data.data && Array.isArray(response.data.data.likelist)) {
      response.data.data.likelist.forEach(item => {
        const id = item?.id || item?.musicId;
        if (id) {
          likedMusicIds.value.add(id);
        }
      });
    }
    // 情况4: 响应对象直接包含success和data字段
    else if (response && response.success && Array.isArray(response.data)) {
      response.data.forEach(item => {
        const id = item?.id || item?.musicId;
        if (id) {
          likedMusicIds.value.add(id);
        }
      });
    }
  } catch (error) {
    // 收藏列表加载失败不影响主功能，静默处理
    likedMusicIds.value = new Set();
  }
};
// 获取音乐搜索列表
const getMusicSearchList = async () => {
  searchLoading.value = true;
  searchError.value = '';
  try {
    // 异步加载用户收藏列表，不阻塞主数据加载
    loadLikedMusicIds();
    // 调用API获取分页音乐数据
    const response = await getMusicPage({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    });
    // 输出完整的搜索API响应结构
    console.log('=== 音乐搜索API完整响应结构 ===', JSON.stringify(response, null, 2));

    // 处理API响应数据
    let musicList = [];
    let total = 0;
    musicList = response.data.data.page.records;
    total = response.data.data.page.total || 0;
    // 转换数据格式并标记已收藏状态
    const processedList = musicList.map((music, index) => {
      if (typeof music !== 'object' || music === null) {
        return null;
      }
      // 获取歌曲ID
      const musicId = music.musicId || music.id || music.music_id;
      if (!musicId) {
        return null;
      }
      // 处理收藏状态
      const isLiked = (music.isLike !== undefined && (music.isLike === 0 || music.isLike === 1)) ? music.isLike :
        (music.is_liked !== undefined && (music.is_liked === 0 || music.is_liked === 1)) ? music.is_liked :
          (likedMusicIds.value.has(musicId) ? 1 : 0);
      // 提取图片和音乐URL
      const imageUrl = music.imageUrl || music.img || music.image || music.cover;
      const musicUrl = music.musicUrl || music.url;
      return {
        musicId: musicId,
        musicName: music.musicName || music.name || music.title || '未知歌曲',
        singer: music.singer || music.artist || music.singerName || '未知歌手',
        img: processImageUrl(imageUrl),
        url: processMusicUrl(musicUrl),
        listenNumb: typeof music.listenNumb === 'number' ? music.listenNumb :
          typeof music.listenCount === 'number' ? music.listenCount : 0,
        createTime: music.createTime || music.time || '',
        isLike: isLiked,
        activation: music.activation || 1, // 默认1表示正常，0表示冻结
        album: music.album || ''
      };
    }).filter(item => item !== null); // 过滤无效数据
    searchMusicList.value = processedList;
    totalMusicCount.value = total;
  } catch (err) {
    searchError.value = '加载失败，请稍后重试';
    ElMessage.error('加载音乐搜索列表失败');
    searchMusicList.value = [];
    totalMusicCount.value = 0;
  } finally {
    searchLoading.value = false;
  }
};

// 播放音乐
const playMusic = (music) => {
  if (!music.url) {
    ElMessage.warning('该歌曲暂无播放链接');
    return;
  }
  // 检查歌曲是否被冻结
  if (music.activation === 0) {
    ElMessage.error('这个歌曲已被下架，无法播放！');
    return;
  }
  currentPlayingUrl.value = music.url;

  // 更新播放次数统计（实际应该调用API）
  if (music.listenNumb !== undefined) {
    music.listenNumb += 1;
  }
  ElMessage.success(`正在播放: ${music.musicName}`);
};

// 处理收藏/取消收藏
const handleLike = async (music) => {
  // 防止重复点击
  if (isLiking.value) return;

  isLiking.value = true;

  try {
    const musicId = music.musicId;
    if (!musicId) {
      ElMessage.error('无效的音乐信息');
      return;
    }

    if (music.isLike !== 1) {
      // 收藏歌曲
      // 直接传递musicId值，而不是对象
      const addResponse = await addLike(musicId);

      // 检查不同的响应格式
      const isSuccess = (addResponse.code === 20) ||
        (addResponse.code === 200) ||
        (addResponse && addResponse.success);

      if (isSuccess) {
        music.isLike = 1;
        likedMusicIds.value.add(musicId);
        ElMessage.success('收藏成功');
      } else {
        ElMessage.error('收藏失败，请稍后重试');
      }
    } else {
      // 取消收藏
      // 直接传递musicId值，而不是对象
      const removeResponse = await removeLike(musicId);

      // 检查不同的响应格式
      const isSuccess = (removeResponse.code === 20) ||
        (removeResponse.code === 200) ||
        (removeResponse && removeResponse.success);

      if (isSuccess) {
        music.isLike = 0;
        likedMusicIds.value.delete(musicId);
        ElMessage.success('取消收藏成功');
      } else {
        ElMessage.error('取消收藏失败，请稍后重试');
      }
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试');
  } finally {
    isLiking.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  // 搜索时重置到第一页
  currentPage.value = 1;
  getMusicSearchList();
};

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1; // 重置到第一页
  getMusicSearchList();
};

// 处理页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  getMusicSearchList();
};

// 获取推荐歌手列表
const getRecommendSingersList = async () => {
  singerLoading.value = true;
  singerError.value = '';

  // 重置歌手列表，确保状态正确
  recommendSingers.value = [];

  try {
    const response = await getRecommendSingers();
    // 输出完整的推荐歌手API响应结构
    console.log('=== 推荐歌手API完整响应结构 ===', JSON.stringify(response, null, 2));
    // 处理API响应数据
    let singerList = response.data.data.list;
    // 处理歌手头像URL
    const processedSingers = singerList.map((singer) => {
      return {
        id: singer.id,
        username: singer.username || '未知歌手',
        role: singer.role,
        avatar: singer.imageUrl,
        about: singer.about
      };
    });

    recommendSingers.value = processedSingers;
  } catch (error) {
    singerError.value = '加载推荐歌手失败';
    ElMessage.error('加载推荐歌手失败，请稍后重试');

    // 提供模拟数据作为备份
    recommendSingers.value = [
      {
        id: 1,
        username: '周杰伦',
        role: '1',
        avatar: 'https://example.com/avatar/1.jpg',
        about: '华语流行音乐天王'
      },
      {
        id: 2,
        username: '陈奕迅',
        role: '1',
        avatar: 'https://example.com/avatar/2.jpg',
        about: '香港流行音乐代表人物'
      }
    ];
  } finally {
    singerLoading.value = false;
  }
};

// 处理标签页切换
const handleRankTabChange = (tab) => {
  // 使用activeRankTab.value获取当前激活的标签页名称，确保兼容性
  const type = activeRankTab.value;

  if (type === 'hot' || type === 'newest') {
    // 每次切换到热门排行榜或最新排行榜标签时都重新加载数据
    loadRankMusic(type);
  } else if (type === 'search') {
    // 切换到搜索标签时加载第一页数据
    currentPage.value = 1;
    getMusicSearchList();
  } else if (type === 'singer') {
    // 如果推荐歌手列表为空，则加载数据
    if (recommendSingers.value.length === 0) {
      getRecommendSingersList();
    }
  }
};

// 添加watch监听activeRankTab变化，确保可靠检测标签页切换
watch(activeRankTab, (newType, oldType) => {
  // 当切换到不同标签时，确保加载对应的数据
  if (newType === 'newest') {
    // 切换到最新排行榜时重新加载数据
    loadRankMusic('newest');
  } else if (newType === 'search') {
    // 切换到搜索标签时加载第一页数据
    currentPage.value = 1;
    getMusicSearchList();
  } else if (newType === 'singer') {
    // 如果推荐歌手列表为空，则加载数据
    if (recommendSingers.value.length === 0) {
      getRecommendSingersList();
    }
  }
});

// 组件挂载时加载热门排行榜数据
onMounted(() => {
  loadRankMusic('hot');
});
</script>

<style scoped>
.hot-music-container {
  padding: 20px 30px;
  background: linear-gradient(180deg, #f9f9f9 0%, #f0f0f0 100%);
  min-height: 100vh;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 25px;
  color: #333;
  position: relative;
  padding-left: 15px;
}

.page-title::before {
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

.rank-tabs {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.rank-tabs ::v-deep(.el-tabs__header) {
  margin: 0;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  padding: 0 20px;
}

.rank-tabs ::v-deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #f0f0f0;
}

.rank-tabs ::v-deep(.el-tabs__item) {
  height: 50px;
  line-height: 50px;
  font-size: 16px;
  font-weight: 500;
  color: #666;
  padding: 0 20px;
  transition: all 0.3s ease;
}

.rank-tabs ::v-deep(.el-tabs__item:hover) {
  color: #333;
}

.rank-tabs ::v-deep(.el-tabs__item.is-active) {
  color: #ec4141;
  font-weight: 600;
}

.rank-tabs ::v-deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 2px;
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
}

.rank-content,
.search-content {
  padding: 25px 30px;
}

.loading-container,
.error-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  min-height: 400px;
}

.error-container button {
  margin-top: 20px;
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  border: none;
  border-radius: 20px;
  padding: 8px 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.error-container button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(236, 65, 65, 0.3);
}

.music-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.music-item {
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 10px;
  padding: 15px 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  border: 1px solid transparent;
}

.music-item:hover {
  background-color: #fafafa;
  border-color: #f0f0f0;
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.rank-number {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  color: #999;
  margin-right: 20px;
  flex-shrink: 0;
  background: #f5f5f5;
  border-radius: 6px;
}

.rank-number.top3 {
  color: #fff;
  background: linear-gradient(135deg, #ff5a5a 0%, #ec4141 100%);
  font-size: 16px;
}

.rank-number.top3:nth-child(1) {
  background: linear-gradient(135deg, #ff5a5a 0%, #ec4141 100%);
}

.rank-number.top3:nth-child(2) {
  background: linear-gradient(135deg, #ff8a5a 0%, #ff6b3b 100%);
}

.rank-number.top3:nth-child(3) {
  background: linear-gradient(135deg, #ffaa5a 0%, #ff8b3b 100%);
}

/* 音乐封面容器 - 修改原有 .music-cover 规则 */
.music-cover {
  position: relative;
  width: 70px;
  height: 70px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 20px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background-color: #f5f5f5;
  /* 添加背景色，用于图片加载失败时 */
}

/* 封面图片 - 修改原有 .cover-image 规则 */
.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.8s ease;
  /* 延长过渡时间，使缩放更平滑 */
}

/* 封面悬停效果：图片放大 */
.music-item:hover .cover-image {
  transform: scale(1.08);
}

/* --- 推荐歌手列表样式 --- */
.singer-content {
  padding: 25px 30px;
}

.singer-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.singer-item {
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid transparent;
}

.singer-item:hover {
  background-color: #fafafa;
  border-color: #f0f0f0;
  transform: translateY(-4px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
}

.singer-avatar {
  position: relative;
  width: 100%;
  padding-top: 100%;
  /* 1:1 Aspect Ratio */
  overflow: hidden;
}

.singer-avatar img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.singer-item:hover .singer-avatar img {
  transform: scale(1.1);
}

.singer-info {
  padding: 15px 20px;
  text-align: center;
}

.singer-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.singer-about {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* --- 核心：新增播放按钮遮罩层样式 --- */
.music-cover::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  /* 黑色半透明遮罩 */
  background: rgba(0, 0, 0, 0.4);
  /* 使用 flex 居中播放图标 */
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  /* 播放图标大小 */
  /* 初始状态隐藏 */
  opacity: 0;
  transition: opacity 0.3s ease;
}

/* 悬停时：显示遮罩层和播放图标 */
.music-item:hover .music-cover::after {
  opacity: 1;
  content: '▶';
  /* 显示播放三角图标 */
}

.music-item:hover .play-icon {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1.1);
}

.music-info {
  flex: 1;
  overflow: hidden;
  padding-right: 15px;
}

.music-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s ease;
}

.music-item:hover .music-name {
  color: #ec4141;
}

.music-artist {
  font-size: 14px;
  color: #666;
  margin: 0 0 5px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.music-stats {
  font-size: 13px;
  color: #999;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 5px;
}

.music-stats::before {
  content: '▶';
  font-size: 10px;
  opacity: 0.6;
}

.music-actions {
  margin-left: 15px;
  flex-shrink: 0;
}

.music-actions .el-button {
  width: 40px;
  height: 40px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
  transition: all 0.3s ease;
  font-size: 20px;
  background: transparent;
  border: 1px solid #f0f0f0;
  border-radius: 50%;
}

.music-actions .el-button:hover {
  color: #ec4141;
  background: rgba(236, 65, 65, 0.05);
  border-color: rgba(236, 65, 65, 0.3);
  transform: scale(1.1);
}

.music-actions .el-button.liked {
  color: #ec4141;
}

.music-actions .el-button.liked:hover {
  background: rgba(236, 65, 65, 0.1);
}

.search-bar {
  margin-bottom: 25px;
}

.search-bar ::v-deep(.el-input-group__append) {
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  border: none;
  border-radius: 0 4px 4px 0;
}

.search-bar ::v-deep(.el-input-group__append .el-button) {
  color: #fff;
  font-weight: 500;
}

.search-bar ::v-deep(.el-input__wrapper) {
  border-radius: 4px 0 0 4px;
  box-shadow: 0 0 0 1px #f0f0f0;
}

.search-bar ::v-deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #ec4141;
}

.search-bar ::v-deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #ec4141;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.pagination-container ::v-deep(.el-pagination) {
  padding: 20px 0;
}

.pagination-container ::v-deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  border-radius: 4px;
}

.pagination-container ::v-deep(.el-pagination.is-background .el-pager li:not(.is-disabled):hover) {
  color: #ec4141;
}

.pagination-container ::v-deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active:hover) {
  color: #fff;
}

@media (max-width: 768px) {
  .hot-music-container {
    padding: 15px;
  }

  .page-title {
    font-size: 22px;
    margin-bottom: 20px;
  }

  .rank-content,
  .search-content {
    padding: 15px;
  }

  .music-item {
    padding: 12px 15px;
  }

  .rank-number {
    width: 28px;
    height: 28px;
    font-size: 14px;
    margin-right: 15px;
  }

  .rank-number.top3 {
    font-size: 14px;
  }


  .play-icon {
    width: 32px;
    height: 32px;
    font-size: 16px;
  }

  .music-name {
    font-size: 15px;
  }

  .music-artist {
    font-size: 13px;
  }

  .music-stats {
    font-size: 12px;
  }

  .music-actions .el-button {
    width: 36px;
    height: 36px;
    font-size: 18px;
  }
}

/* 推荐歌手相关样式 */
.singer-content {
  padding: 20px 0;
}

.singer-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.singer-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #fff;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.singer-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.singer-avatar {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.singer-item:hover .avatar-image {
  transform: scale(1.05);
}

.singer-info {
  text-align: center;
  width: 100%;
}

.singer-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.singer-about {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.loading-container,
.error-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.error-container .el-button {
  margin-top: 20px;
}
</style>