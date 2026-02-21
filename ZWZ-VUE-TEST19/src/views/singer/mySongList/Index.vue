<template>
  <div class="container" key="songlist-container">
    <div class="header">
      <h2>我的歌单</h2>
      <div class="header-actions">
        <el-button type="primary" @click="showCreateDialog = true">创建歌单</el-button>
      </div>
    </div>
    <!-- 搜索、重置、刷新区域 -->
    <div class="search-section">
      <el-input v-model="searchKeyword" placeholder="搜索歌单" prefix-icon="el-icon-search" clearable class="search-input"
        @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="primary" @click="handleReset">重置</el-button>
      <el-button type="primary" @click="handleRefresh">刷新</el-button>
    </div>
    <!-- 标签页切换 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="我的歌单" name="mySongList">
        <div v-if="mySongLists.length > 0" class="songlist-grid">
          <div v-for="list in mySongLists" :key="list.id" class="songlist-item" @click="showSongListDetail(list.id)">
            <div class="songlist-cover">
              <img :src="list.image || list.titleImg || defaultCover" :alt="list.name">
            </div>
            <div class="songlist-info">
              <h3 class="songlist-name">{{ list.name }}</h3>
              <p class="songlist-desc">{{ list.message || '暂无简介' }}</p>
            </div>
            <div class="songlist-actions">
              <button @click.stop="editSongList(list)">编辑</button>
              <button @click.stop="deleteSongList(list.id)">删除</button>
              <button @click.stop="handleLikeList(list)" :disabled="isLikingList" :class="{'is-liked': (list.isLike || likedSongListIds.has(list.id))}">
                {{ (list.isLike || likedSongListIds.has(list.id)) ? '已收藏' : '收藏' }}
              </button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="推荐歌单" name="recommendList">
        <div v-if="recommendSongLists.length > 0" class="songlist-grid">
          <div v-for="list in recommendSongLists" :key="list.id" class="songlist-item" @click="showSongListDetail(list.id)">
            <div class="songlist-cover">
              <img :src="list.image || list.titleImg || defaultCover" :alt="list.name">
            </div>
            <div class="songlist-info">
              <h3 class="songlist-name">{{ list.name }}</h3>
              <p class="songlist-desc">{{ list.message || '暂无简介' }}</p>
            </div>
            <div class="songlist-actions">
              <button @click.stop="handleLikeList(list)" :disabled="isLikingList" :class="{'is-liked': (list.isLike || likedSongListIds.has(list.id))}">
                {{ (list.isLike || likedSongListIds.has(list.id)) ? '已收藏' : '收藏' }}
              </button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="收藏歌单" name="likeSongList">
        <div v-if="likeSongLists.length > 0" class="songlist-grid">
          <div v-for="list in likeSongLists" :key="list.id" class="songlist-item" @click="showSongListDetail(list.id)">
            <div class="songlist-cover">
              <img :src="list.image || list.titleImg || defaultCover" :alt="list.name">
            </div>
            <div class="songlist-info">
              <h3 class="songlist-name">{{ list.name }}</h3>
              <p class="songlist-desc">{{ list.message || '暂无简介' }}</p>
            </div>
            <div class="songlist-actions">
              <button @click.stop="handleLikeList(list)" :disabled="isLikingList" :class="{'is-liked': (list.isLike || likedSongListIds.has(list.id))}">
                {{ (list.isLike || likedSongListIds.has(list.id)) ? '已收藏' : '收藏' }}
              </button>
            </div>
          </div>
        </div>
        <div v-else class="empty-like-lists">
          <p>暂无收藏歌单</p>
        </div>
      </el-tab-pane>

      <el-tab-pane label="搜索结果" name="searchResult">
        <div v-if="searchResults.length > 0" class="songlist-grid">
          <div v-for="list in searchResults" :key="list.id" class="songlist-item" @click="showSongListDetail(list.id)">
            <div class="songlist-cover">
              <img :src="list.image || list.titleImg || defaultCover" :alt="list.name">
            </div>
            <div class="songlist-info">
              <h3 class="songlist-name">{{ list.name }}</h3>
              <p class="songlist-desc">{{ list.message || '暂无简介' }}</p>
            </div>
            <div class="songlist-actions">
              <button @click.stop="handleLikeList(list)" :disabled="isLikingList" :class="{'is-liked': (list.isLike || likedSongListIds.has(list.id))}">
                {{ (list.isLike || likedSongListIds.has(list.id)) ? '已收藏' : '收藏' }}
              </button>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 歌单详情对话框 -->
    <!-- 歌单详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="歌单详情" width="800px">
      <div v-if="currentSongList" class="songlist-detail">
        <div class="detail-header">
          <img :src="currentSongList.image || currentSongList.titleImg || defaultCover" :alt="currentSongList.name"
            class="detail-cover">
          <div class="detail-info">
            <h2>{{ currentSongList.name }}</h2>
            <p>创建时间: {{ formatDate(currentSongList.createDate) }}</p>
            <p>{{ currentSongList.message || '暂无简介' }}</p>
          </div>
        </div>
        <div class="detail-content">
          <h3>歌曲列表</h3>
          <el-table :data="currentSongList.songList || []" style="width: 100%">
            <el-table-column label="操作" width="80" align="center">
              <template #default="scope">
                <el-button type="danger" size="small" @click="removeSong(scope.row, scope.$index)"
                  confirm-type="danger">移除</el-button>
              </template>
            </el-table-column>
            <el-table-column label="歌曲名称" min-width="100">
              <template #default="scope">
                {{ scope.row.musicName || '未知歌曲' }}
              </template>
            </el-table-column>
            <el-table-column label="封面" min-width="100">
              <template #default="scope">
                <img v-if="scope.row.imageUrl" :src="scope.row.imageUrl" :alt="scope.row.musicName"
                  class="song-cover-small">
                <span v-else>暂无封面</span>
              </template>
            </el-table-column>
            <el-table-column label="歌手" min-width="120">
              <template #default="scope">
                {{ scope.row.singerUsername || '未知歌手' }}
              </template>
            </el-table-column>
            <el-table-column label="创建时间" min-width="150">
              <template #default="scope">
                {{ scope.row.createTime || '未知' }}
              </template>
            </el-table-column>
            <el-table-column label="" width="80" align="center">
              <template #default="scope">
                <!-- {{ formatDuration(scope.row.timeLength) }} -->
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div v-else>
        <el-skeleton :rows="6" animated />
      </div>
    </el-dialog>

    <!-- 创建/编辑歌单对话框 -->
    <el-dialog v-model="showCreateDialog" :title="editingList ? '编辑歌单' : '创建歌单'" width="500px">
      <el-form :model="songListForm" :rules="rules" ref="songListFormRef">
        <el-form-item label="歌单名称" prop="name">
          <el-input v-model="songListForm.name" placeholder="请输入歌单名称"></el-input>
        </el-form-item>
        <el-form-item label="歌单简介" prop="message">
          <el-input v-model="songListForm.message" placeholder="请输入歌单简介" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="歌单标签" prop="tags">
          <el-input v-model="songListForm.tags" placeholder="请输入标签，用逗号分隔"></el-input>
        </el-form-item>
        <el-form-item label="歌单封面">
          <el-upload class="avatar-uploader" :http-request="customImageUpload" :before-upload="handleBeforeImageUpload"
            :show-file-list="false" action="#" accept="image/*" :auto-upload="true">
            <img v-if="songListForm.image" :src="songListForm.image" class="avatar">
            <div v-else class="upload-placeholder">
              <i class="el-icon-plus avatar-uploader-icon"></i>
              <span class="upload-text">上传封面</span>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitSongList">确定</el-button>
      </template>
    </el-dialog>
  </div>

  <!-- 歌单详情弹窗 -->
  <el-dialog v-model="songListDetailVisible" :title="selectedSongList.name || '歌单详情'" width="800px"
    :before-close="handleCloseDetail">
    <template #header>
      <div class="dialog-header">
        <span>{{ selectedSongList.name || '歌单详情' }}</span>
        <el-button type="primary" size="small" @click="openAddSongDialog">添加歌曲</el-button>
      </div>
    </template>
    <div class="songlist-detail-container">
      <!-- 歌单基本信息 -->
      <div class="songlist-detail-header">
        <div class="songlist-cover-large">
          <img :src="selectedSongList.image || selectedSongList.titleImg || defaultCover" :alt="selectedSongList.name"
            class="cover-img">
        </div>
        <div class="songlist-info-large">
          <h2 class="songlist-name-large">{{ selectedSongList.name || '未知歌单' }}</h2>
          <p class="songlist-desc-large">{{ selectedSongList.message || '暂无简介' }}</p>
          <div class="songlist-stats">
            <span>歌曲数量：{{ songListSongs.length }}</span>
          </div>
        </div>
      </div>

      <h3 class="section-title">歌曲列表</h3>
      <!-- 添加搜索功能 -->
      <div class="song-search-box">
        <el-input v-model="currentSongListSearchKeyword" placeholder="搜索歌单中的歌曲" prefix-icon="el-icon-search" clearable
          @keyup.enter="searchCurrentSongList">
          <template #append>
            <el-button @click="searchCurrentSongList">搜索</el-button>
            <!-- <el-button @click="resetSongListSearch">重置</el-button> -->
          </template>
        </el-input>
      </div>
      <div v-if="songListSongs.length > 0" class="songs-list">
        <el-table :data="filteredSongListSongs" stripe style="width: 100%" @selection-change="handleSongSelectionChange"
          :row-class-name="(row, index) => {
            if (row.activation === 0) return 'frozen-row';
            return index % 2 === 0 ? 'even-row' : 'odd-row';
          }">
          <el-table-column prop="id" label="#" width="60" align="center"></el-table-column>
          <el-table-column label="歌曲名" min-width="200">
            <template #default="scope">
              <div class="song-item">
                <img v-if="scope.row.imageUrl" :src="scope.row.imageUrl" :alt="scope.row.musicName"
                  class="song-cover-small" @click="playMusic(scope.row)">
                <img v-else :src="defaultCover" alt="默认封面" class="song-cover-small" @click="playMusic(scope.row)">
                <span class="song-name">{{ scope.row.musicName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="歌手" min-width="120">
            <template #default="scope">
              {{ scope.row.singerUsername || '未知歌手' }}
            </template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="150">
            <template #default="scope">
              {{ scope.row.createTime || '未知' }}
            </template>
          </el-table-column>
          <el-table-column label="收藏" width="100" align="center">
            <template #default="scope">
              <el-button type="primary" size="small"
                :icon="likedMusicIds.has(scope.row.musicId) ? 'el-icon-star-on' : 'el-icon-star-off'"
                @click="handleLike(scope.row)" :disabled="isLiking">
                {{ likedMusicIds.has(scope.row.musicId) ? '已收藏' : '收藏' }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="" width="80" align="center">
            <template #default="scope">
              <!-- {{ formatDuration(scope.row.timeLength) }} -->
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else-if="currentSongListSearchKeyword && songListSongs.length > 0" class="empty-search-results">
        <el-empty description="未找到匹配的歌曲">
          <el-button type="primary" @click="resetSongListSearch">重置搜索</el-button>
        </el-empty>
      </div>
      <div v-else class="empty-songs">
        <p>暂无歌曲</p>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleCloseDetail">关闭</el-button>
    </template>
  </el-dialog>

  <!-- 歌曲搜索对话框 -->
  <el-dialog v-model="addSongDialogVisible" title="搜索歌曲添加到歌单" width="800px" :before-close="handleCloseAddSongDialog">
    <div class="song-search-container">
      <!-- 搜索区域 -->
      <div class="search-section">
        <el-input v-model="songSearchKeyword" placeholder="搜索歌曲" prefix-icon="el-icon-search" clearable
          @keyup.enter="searchMusic">
          <template #append>
            <el-button type="primary" @click="searchMusic">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 歌曲列表 -->
      <div class="songs-search-results">
        <el-table :data="searchSongsResults" stripe style="width: 100%" @selection-change="handleSongSelectionChange"
          :row-key="row => row.musicId">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="musicId" label="#" width="60" align="center"></el-table-column>
          <el-table-column label="歌曲名" min-width="200">
            <template #default="scope">
              <div class="song-item">
                <img v-if="scope.row.imageUrl" :src="scope.row.imageUrl" :alt="scope.row.musicName"
                  class="song-cover-small">
                <img v-else :src="defaultCover" alt="默认封面" class="song-cover-small">
                <span class="song-name">{{ scope.row.musicName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="歌手" min-width="120">
            <template #default="scope">
              {{ scope.row.singerUsername || '未知歌手' }}
            </template>
          </el-table-column>
          <el-table-column label="专辑" min-width="150">
            <template #default="scope">
              {{ scope.row.musicAlbum || '未知专辑' }}
            </template>
          </el-table-column>
          <el-table-column label="" width="80" align="center">
            <template #default="scope">
              <!-- {{ formatDuration(scope.row.timeLength) }} -->
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleCloseAddSongDialog">取消</el-button>
      <el-button type="primary" @click="addSelectedSongsToSongList">添加选中歌曲</el-button>
    </template>
  </el-dialog>
  <!-- 音乐播放器组件 -->
  <MusicPlayer v-if="showPlayer" :audio-url="currentAudioUrl" :autoplay="true" ref="musicPlayerRef" />
</template>
<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  getMySongList,
  createSongList,
  updateSongList,
  removeSongList,
  selectSongList,
  searchSongList,
  getRecommendSongList,
  addSongToSongList,
  removeSongFromSongList
} from '@/api/songList';
import { searchSongs } from '@/api/music';
import { uploadImage } from '@/api/upload';
// 导入音乐收藏相关 API
import { getMyLike, addLike, removeLike, getLikeList, addLikeList, removeLikeList, getSongListDetailWithLikeStatus } from '@/api/mylike.js';
// 导入音乐播放器组件
import MusicPlayer from '@/views/component/musicPlayer.vue';

// 响应式数据
const activeTab = ref('mySongList');
const searchKeyword = ref('');
const hasSearched = ref(false);
const mySongLists = ref([]);
const recommendSongLists = ref([]);
const searchResults = ref([]);
const showDetailDialog = ref(false);
const showCreateDialog = ref(false);
const currentSongList = ref(null);
const editingList = ref(null);
const defaultCover = '/music.png';
const songListDetailVisible = ref(false);
const selectedSongList = ref({});
const songListSongs = ref([]);
const addSongDialogVisible = ref(false);
const songSearchKeyword = ref('');
const searchSongsResults = ref([]);
const selectedSongs = ref([]);
const currentSongListId = ref(null);
// 添加歌单详情搜索相关
const currentSongListSearchKeyword = ref('');
const filteredSongListSongs = ref([]);
// 音乐播放相关
const currentAudioUrl = ref('');
const showPlayer = ref(false);
const musicPlayerRef = ref(null);
// 音乐收藏相关
const likedMusicIds = ref(new Set());
const isLiking = ref(false);
const loadingLikes = ref(false);

// 歌单收藏相关
const likedSongListIds = ref(new Set());
const likeSongLists = ref([]);
const isLikingList = ref(false);
const loadingLikeSongLists = ref(false);

// 加载用户收藏列表
const loadLikedMusic = async () => {
  loadingLikes.value = true;
  try {
    const response = await getMyLike();
    if (Array.isArray(response)) {
      likedMusicIds.value = new Set(response.map(music => music.musicId));
    } else if (response.data && Array.isArray(response.data.data?.likelist)) {
      likedMusicIds.value = new Set(response.data.data.likelist.map(music => music.musicId));
    } else if (response.data && Array.isArray(response.data?.likelist)) {
      likedMusicIds.value = new Set(response.data.likelist.map(music => music.musicId));
    } else {
      console.error('获取收藏列表数据格式错误:', response);
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error);
  } finally {
    loadingLikes.value = false;
  }
};

// 加载用户收藏歌单列表
const loadLikeSongLists = async () => {
  console.log('开始加载收藏歌单列表...');
  loadingLikeSongLists.value = true;
  try {
    const response = await getLikeList();
    console.log('获取收藏歌单列表响应:', response);
    console.log('响应类型:', typeof response);
    console.log('响应结构:', JSON.stringify(response, null, 2));

    // 检查响应数据结构
    if (response && response.data) {
      console.log('响应data:', response.data);
      console.log('响应data.code:', response.data.code);
      console.log('响应data.success:', response.data.success);
      console.log('响应data.data:', response.data.data);
      console.log('响应data.data.likeSongList:', response.data.data?.likeSongList);
    }

    if (response.data && (response.data.success || response.data.code === 20)) {
      const likeLists = response.data.data?.likeSongList || [];
      console.log('解析出的likeLists:', likeLists);
      console.log('likeLists长度:', likeLists.length);
      likeSongLists.value = likeLists;
      console.log('更新后likeSongLists.value:', likeSongLists.value);
      console.log('更新后likeSongLists.value长度:', likeSongLists.value.length);

      // 更新收藏歌单ID集合
      const newLikeSongListIds = new Set(likeLists.map(list => list.id));
      console.log('新的likedSongListIds:', [...newLikeSongListIds]);
      likedSongListIds.value = newLikeSongListIds;

      // 设置每个歌单的isLike状态
      likeLists.forEach(list => {
        list.isLike = 1;
      });
    } else {
      console.error('获取收藏歌单列表失败:', response);
      ElMessage.error('获取收藏歌单列表失败');
    }
  } catch (error) {
    console.error('加载收藏歌单列表失败:', error);
    console.error('错误类型:', typeof error);
    console.error('错误详细信息:', error);
    if (error.response) {
      console.error('错误响应:', error.response);
      console.error('错误响应数据:', error.response.data);
      console.error('错误响应状态:', error.response.status);
    }
    ElMessage.error('加载收藏歌单列表失败');
  } finally {
    loadingLikeSongLists.value = false;
    console.log('收藏歌单列表加载完成');
  }
};

// 处理歌单收藏/取消收藏
const handleLikeList = async (songList) => {
  console.log('开始处理歌单收藏操作...');
  console.log('当前歌单:', songList);
  console.log('当前isLikingList状态:', isLikingList.value);

  if (isLikingList.value) return;
  isLikingList.value = true;

  try {
    const listId = songList.id;
    console.log('歌单ID:', listId);
    console.log('当前收藏状态(likedSongListIds.has(listId)):', likedSongListIds.value.has(listId));
    console.log('当前收藏状态(songList.isLike):', songList.isLike);

    if (!likedSongListIds.value.has(listId)) {
      // 收藏歌单
      console.log('执行收藏歌单操作...');
      const addResponse = await addLikeList(listId);
      console.log('收藏歌单响应:', addResponse);

      const isSuccess = addResponse.code === 20 || addResponse.success || addResponse.code === 200;
      console.log('收藏操作是否成功:', isSuccess);

      if (isSuccess) {
        songList.isLike = 1;
        likedSongListIds.value.add(listId);
        console.log('更新后songList.isLike:', songList.isLike);
        console.log('更新后likedSongListIds:', [...likedSongListIds.value]);
        ElMessage.success('收藏成功');
      } else {
        console.error('收藏失败:', addResponse);
        ElMessage.error(addResponse.message || '收藏失败，请稍后重试');
      }
    } else {
      // 取消收藏
      console.log('执行取消收藏歌单操作...');
      const removeResponse = await removeLikeList(listId);
      console.log('取消收藏歌单响应:', removeResponse);

      const isSuccess = removeResponse.code === 20 || removeResponse.success || removeResponse.code === 200;
      console.log('取消收藏操作是否成功:', isSuccess);

      if (isSuccess) {
        songList.isLike = 0;
        likedSongListIds.value.delete(listId);
        console.log('更新后songList.isLike:', songList.isLike);
        console.log('更新后likedSongListIds:', [...likedSongListIds.value]);
        ElMessage.success('取消收藏成功');

        // 如果当前在收藏歌单标签页，需要从列表中移除该歌单
        if (activeTab.value === 'likeSongList') {
          console.log('当前在收藏歌单标签页，移除已取消收藏的歌单');
          likeSongLists.value = likeSongLists.value.filter(list => list.id !== listId);
          console.log('更新后likeSongLists:', likeSongLists.value);
        }
      } else {
        console.error('取消收藏失败:', removeResponse);
        ElMessage.error(removeResponse.message || '取消收藏失败，请稍后重试');
      }
    }
  } catch (error) {
    console.error('处理歌单收藏请求失败:', error);
    console.error('错误类型:', typeof error);
    console.error('错误详细信息:', error);
    if (error.response) {
      console.error('错误响应:', error.response);
      console.error('错误响应数据:', error.response.data);
      console.error('错误响应状态:', error.response.status);
    }
    ElMessage.error('操作失败，请稍后重试');
  } finally {
    isLikingList.value = false;
    console.log('歌单收藏操作完成，isLikingList重置为false');
  }
};

// 播放音乐
const playMusic = (song) => {
  if (!song || !song.musicUrl) {
    ElMessage.warning('该歌曲暂无音频链接');
    return;
  }

  // 检查歌曲是否被冻结
  if (song.activation === 0) {
    console.log('歌曲已被冻结:', song.musicName || song.name);
    ElMessage.error('这个歌曲已被下架，无法播放！');
    return;
  }

  // 设置音频URL
  currentAudioUrl.value = song.musicUrl;
  // 显示播放器
  showPlayer.value = true;

  // 如果播放器已经加载完成，直接调用播放方法
  if (musicPlayerRef.value && musicPlayerRef.value.play) {
    musicPlayerRef.value.play();
  }
};

// 处理收藏/取消收藏
const handleLike = async (music) => {
  if (isLiking.value) return;
  isLiking.value = true;
  try {
    const musicId = music.musicId;
    if (!likedMusicIds.value.has(musicId)) {
      // 收藏歌曲
      const addResponse = await addLike(musicId);
      const isSuccess = addResponse.code === 20 || addResponse.success || addResponse.code === 200;
      if (isSuccess) {
        music.isLike = 1;
        likedMusicIds.value.add(musicId);
        ElMessage.success('收藏成功');
      } else {
        ElMessage.error('收藏失败，请稍后重试');
      }
    } else {
      // 取消收藏
      const removeResponse = await removeLike(musicId);
      const isSuccess = removeResponse.code === 20 || removeResponse.success || removeResponse.code === 200;
      if (isSuccess) {
        music.isLike = 0;
        likedMusicIds.value.delete(musicId);
        ElMessage.success('取消收藏成功');
      } else {
        ElMessage.error('取消收藏失败，请稍后重试');
      }
    }
  } catch (error) {
    console.error('处理收藏请求失败:', error);
    ElMessage.error('操作失败，请稍后重试');
  } finally {
    isLiking.value = false;
  }
};


// 搜索当前歌单中的歌曲
const searchCurrentSongList = () => {
  const keyword = currentSongListSearchKeyword.value.trim().toLowerCase();
  if (!keyword) {
    filteredSongListSongs.value = [...songListSongs.value];
    return;
  }

  // 过滤歌曲列表，根据歌曲名、歌手名或专辑名进行搜索
  filteredSongListSongs.value = songListSongs.value.filter(song => {
    return (
      (song.name && song.name.toLowerCase().includes(keyword)) ||
      (song.singerName && song.singerName.toLowerCase().includes(keyword)) ||
      (song.albumName && song.albumName.toLowerCase().includes(keyword))
    );
  });
};

// 重置歌单搜索
const resetSongListSearch = () => {
  currentSongListSearchKeyword.value = '';
  filteredSongListSongs.value = [...songListSongs.value];
};

// 显示添加歌曲对话框
const openAddSongDialog = () => {
  currentSongListId.value = selectedSongList.value.id;
  addSongDialogVisible.value = true;
};

// 关闭添加歌曲对话框
const handleCloseAddSongDialog = () => {
  addSongDialogVisible.value = false;
  songSearchKeyword.value = '';
  searchSongsResults.value = [];
  selectedSongs.value = [];
};

// 歌单信息存储
const songListForm = reactive({
  name: '',
  message: '',
  tags: '',
  image: ''
});

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入歌单名称', trigger: 'blur' },
    { min: 1, max: 50, message: '歌单名称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  message: [
    { max: 200, message: '歌单简介最多200个字符', trigger: 'blur' }
  ],
  tags: [
    { max: 100, message: '标签最多100个字符', trigger: 'blur' }
  ]
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return '0:00';
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs < 10 ? '0' + secs : secs}`;
};

// 搜索歌曲
const searchMusic = async () => {
  if (!songSearchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }

  try {
    const response = await searchSongs(songSearchKeyword.value);
    if (response.data.success || response.data.code === 20) {
      searchSongsResults.value = response.data.data.list || [];
    } else {
      ElMessage.error(response.data.message || '搜索歌曲失败');
      searchSongsResults.value = [];
    }
  } catch (error) {
    console.error('搜索歌曲失败:', error);
    ElMessage.error('搜索失败，请稍后重试');
    searchSongsResults.value = [];
  }
};

// 处理歌曲选择
const handleSongSelectionChange = (selection) => {
  selectedSongs.value = selection;
};

// 将选中歌曲添加到歌单
const addSelectedSongsToSongList = async () => {
  if (selectedSongs.value.length === 0) {
    ElMessage.warning('请先选择要添加的歌曲');
    return;
  }

  if (!currentSongListId.value) {
    ElMessage.error('歌单ID无效');
    return;
  }

  try {
    let successCount = 0;
    let failedCount = 0;
    const failedSongs = [];

    // 批量添加歌曲到歌单
    for (const song of selectedSongs.value) {
      try {
        // 使用musicId代替id，与API返回结构一致
        const response = await addSongToSongList(song.musicId, currentSongListId.value);
        if (response.data.success || response.data.code === 20) {
          successCount++;
        } else {
          failedCount++;
          failedSongs.push(song.musicName);
        }
      } catch (error) {
        failedCount++;
        failedSongs.push(song.musicName);
        console.error(`添加歌曲 ${song.musicName} 到歌单失败:`, error);
      }
    }

    // 显示添加结果
    if (successCount > 0) {
      ElMessage.success(`成功添加 ${successCount} 首歌曲到歌单`);
    }
    if (failedCount > 0) {
      ElMessage.warning(`有 ${failedCount} 首歌曲添加失败: ${failedSongs.join(', ')}`);
    }

    // 关闭对话框
    handleCloseAddSongDialog();

    // 刷新歌单详情，重新加载歌曲列表
    if (successCount > 0) {
      await showSongListDetail(currentSongListId.value);
    }
  } catch (error) {
    console.error('添加歌曲到歌单失败:', error);
    ElMessage.error('操作失败，请稍后重试');
  }
};

// 处理关闭详情弹窗
const handleCloseDetail = () => {
  songListDetailVisible.value = false;
  selectedSongList.value = {};
  songListSongs.value = [];
};

// 生命周期
onMounted(() => {
  loadMySongList();
  loadRecommendSongList();
  loadLikeSongLists();
});

// 加载我的歌单
const loadMySongList = async () => {
  try {
    // 从auth工具获取真实用户ID
    const { getUserId } = await import('@/utils/auth.js');
    const userId = getUserId();
    console.log('正在获取用户ID为', userId, '的歌单');

    // 确保userId存在
    if (!userId) {
      console.warn('无法获取用户ID');
      ElMessage.warning('请先登录');
      return;
    }

    // 调用getMySongList时传入userId参数
    const response = await getMySongList(userId);
    console.log('获取我的歌单完整响应:', response);

    // 响应拦截器返回的是完整response对象，需要通过response.data访问数据
    if (response && response.data && (response.data.success || response.data.code === 20)) {
      // 根据测试结果，后端返回的是data.list，而不是data.songList
      mySongLists.value = response.data.data?.list || [];
      console.log('我的歌单数据更新成功，共', mySongLists.value.length, '个歌单');

      // 调试输出每个歌单的信息
      mySongLists.value.forEach(list => {
        console.log(`歌单: ${list.id} - ${list.name}`);
      });
    } else {
      console.warn('获取我的歌单失败，响应数据:', response);
      ElMessage.error(response?.data?.message || '获取我的歌单失败');
    }
  } catch (error) {
    console.error('获取我的歌单错误:', error);
    ElMessage.error('获取我的歌单失败，请稍后重试');
  }
};

// 加载推荐歌单
const loadRecommendSongList = async () => {
  console.log('开始加载推荐歌单...');
  try {
    const response = await getRecommendSongList();
    console.log('推荐歌单数据获取完成，响应数据:', response);
    // 响应拦截器返回的是完整response对象，需要通过response.data访问数据
    if (response && response.data && (response.data.success || response.data.code === 20)) {
      // 兼容两种格式：data.data.songList 或 data.data.list
      recommendSongLists.value = response.data.data?.songList || response.data.data?.list || [];
      console.log('推荐歌单数据更新成功，共', recommendSongLists.value.length, '个歌单');
    } else {
      console.warn('推荐歌单加载失败，响应数据:', response);
      ElMessage.error(response?.data?.message || '加载推荐歌单失败');
    }
  } catch (error) {
    console.error('加载推荐歌单错误:', error);
    ElMessage.error('网络错误，请稍后重试');
  }
};

// 搜索歌单
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }

  console.log('开始搜索歌单，关键词:', searchKeyword.value);
  try {
    activeTab.value = 'searchResult';
    hasSearched.value = true;
    const response = await searchSongList(searchKeyword.value);
    console.log('歌单搜索完成，响应数据:', response);
    // 响应拦截器返回的是完整response对象，需要通过response.data访问数据
    if (response && response.data && (response.data.success || response.data.code === 20)) {
      searchResults.value = response.data.data?.songList || [];
      console.log('搜索结果更新成功，共', searchResults.value.length, '个歌单');
    } else {
      console.warn('歌单搜索失败，响应数据:', response);
      ElMessage.error(response?.data?.message || '搜索失败');
    }
  } catch (error) {
    console.error('搜索歌单错误:', error);
    ElMessage.error('网络错误，请稍后重试');
  }
};

// 显示歌单详情
const showSongListDetail = async (songListId) => {
  try {
    console.log('显示歌单详情，歌单ID:', songListId);
    currentSongListId.value = songListId;
    // 调用接口获取歌单详情
    const response = await selectSongList(songListId);
    console.log('歌单详情获取结果:', response);

    // 响应拦截器返回的是完整response对象，需要通过response.data访问数据
    if (response && response.data && (response.data.success || response.data.code === 20)) {
      // 更新选中的歌单信息
      selectedSongList.value = response.data.data?.listMessage || {};
      // 更新歌曲列表
      songListSongs.value = response.data.data?.musicList || [];
      // 初始化过滤后的歌曲列表
      filteredSongListSongs.value = [...songListSongs.value];
      // 重置搜索关键词
      currentSongListSearchKeyword.value = '';
      // 显示歌单详情弹窗
      songListDetailVisible.value = true;

      // 加载用户收藏列表，以便显示歌曲的收藏状态
      await loadLikedMusic();
    } else {
      console.warn('获取歌单详情失败，响应数据:', response);
      ElMessage.error(response?.data?.message || '获取歌单详情失败');
    }
  } catch (error) {
    console.error('显示歌单详情异常:', error);
    ElMessage.error('获取歌单详情失败，请稍后重试');
  }
};

// 添加歌曲到歌单
const handleAddSongToSongList = async (songListId, songId) => {
  try {
    const response = await addSongToSongList(songId, songListId);
    console.log('添加歌曲到歌单响应:', response);
    // 响应拦截器返回的是完整response对象，需要通过response.data访问数据
    if (response && response.data && (response.data.success || response.data.code === 20)) {
      ElMessage.success('添加成功');
      // 重新加载歌单详情以更新歌曲列表
      if (currentSongList.value && currentSongList.value.id === songListId) {
        await showSongListDetail(songListId);
      }
    } else {
      ElMessage.error(response?.data?.message || '添加失败');
    }
  } catch (error) {
    console.error('添加歌曲到歌单错误:', error);
    ElMessage.error('添加失败，请稍后重试');
  }
};

// 从歌单移除歌曲
const handleRemoveSongFromSongList = async (songListId, songId) => {
  try {
    const response = await removeSongFromSongList(songId, songListId);
    console.log('从歌单移除歌曲响应:', response);
    // 响应拦截器返回的是完整response对象，需要通过response.data访问数据
    if (response && response.data && (response.data.success || response.data.code === 20)) {
      ElMessage.success('移除成功');
      // 重新加载歌单详情以更新歌曲列表
      if (currentSongList.value && currentSongList.value.id === songListId) {
        await showSongListDetail(songListId);
      }
    } else {
      ElMessage.error(response?.data?.message || '移除失败');
    }
  } catch (error) {
    console.error('从歌单移除歌曲错误:', error);
    ElMessage.error('移除失败，请稍后重试');
  }
};

// 标签页切换，根据选中的标签加载对应数据
// 标签页切换，根据选中的标签加载对应数据
// 标签页切换，根据选中的标签加载对应数据
const handleTabChange = (name) => {
  activeTab.value = name;
  if (name === 'mySongList') {
    console.log('切换到我的歌单标签页');
    loadMySongList();
  } else if (name === 'recommendList') {
    console.log('切换到推荐歌单标签页');
    loadRecommendSongList();
  } else if (name === 'likeSongList') {
    console.log('切换到收藏歌单标签页');
    loadLikeSongLists();
  } else if (name === 'searchResult') {
    // 搜索结果不需要重新加载，因为是在搜索时更新的
  }
};

// 图片上传前的验证
const handleBeforeImageUpload = (file) => {
  const isImage = /\.(jpg|jpeg|png|gif|bmp)$/i.test(file.name);
  if (!isImage) {
    ElMessage.error('只支持上传图片格式（jpg、jpeg、png、gif、bmp）！');
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB！');
    return false;
  }
  return true;
};

// 自定义图片上传处理
const customImageUpload = async (options) => {
  const { file } = options;
  try {
    console.log('开始上传图片');
    const result = await uploadImage(file);

    console.log('图片上传结果:', result);
    // 检查图片上传是否成功，增加对直接返回url的情况的处理
    // 检查图片上传是否成功，增加对直接返回url的情况的处理
    // 检查图片上传是否成功，增加对直接返回url的情况的处理
    // 检查图片上传是否成功，增加对直接返回url的情况的处理
    if (result && (result.success || result.code === 20 || result.code === 200 || result.url)) {
      // 从不同可能的返回结构中获取图片URL
      let imageUrl;
      if (result.data && result.data.url) {
        imageUrl = result.data.url;
      } else if (result.url) {
        imageUrl = result.url;
      } else if (typeof result === 'string') {
        imageUrl = result;
      } else {
        throw new Error('返回数据结构不正确');
      }

      // 更新歌单表单中的图片URL
      songListForm.image = imageUrl;
      ElMessage.success('图片上传成功');

      if (options.onSuccess) options.onSuccess();
    } else {
      const errorMsg = result?.message || '图片上传失败';
      ElMessage.error(errorMsg);
      console.error('图片上传失败', result);
      if (options.onError) options.onError();
    }
  } catch (error) {
    console.error('图片上传过程中出错', error);
    ElMessage.error('上传失败，请稍后重试');
    if (options.onError) options.onError(error);
  }
};

// 重置表单
const resetForm = () => {
  songListForm.name = '';
  songListForm.message = '';
  songListForm.tags = '';
  songListForm.image = '';
  editingList.value = null;
};

// 刷新歌单数据
const handleRefresh = async () => {
  try {
    await Promise.all([loadMySongList(), loadRecommendSongList()]);
    ElMessage.success('歌单数据已刷新');
  } catch (error) {
    console.error('刷新歌单数据失败:', error);
    ElMessage.error('刷新失败，请稍后重试');
  }
};

// 重置搜索并刷新列表
const handleReset = () => {
  searchKeyword.value = '';
  handleRefresh();
};

// 打开编辑歌单
const editSongList = (list) => {
  editingList.value = list;
  songListForm.name = list.name;
  songListForm.message = list.message || '';
  songListForm.tags = list.tags || '';
  songListForm.image = list.image || list.titleImg || '';
  showCreateDialog.value = true;
};

// 提交歌单（创建或编辑）
const submitSongList = async () => {
  try {
    // 验证是否上传了图片
    if (!songListForm.image) {
      ElMessage.error('请上传歌单封面图片');
      return;
    }

    const dataBody = {
      name: songListForm.name,
      description: songListForm.message,
      image: songListForm.image,
      tags: songListForm.tags
    };

    let response;
    if (editingList.value) {
      // 添加调试日志，确保editingList.value.id存在且不为空
      console.log('编辑歌单时的editingList对象:', editingList.value);
      console.log('编辑歌单时的ID值:', editingList.value.id, '类型:', typeof editingList.value.id);

      if (!editingList.value.id) {
        console.error('警告: editingList.value.id为空或未定义');
        ElMessage.error('编辑失败: 歌单ID无效');
        return;
      }

      // 编辑歌单：调用updateSongList方法，传入歌单ID
      response = await updateSongList(editingList.value.id, dataBody);
      console.log('编辑歌单响应:', response);
    } else {
      // 创建歌单：保持原有逻辑
      response = await createSongList(dataBody);
      console.log('创建歌单响应:', response);
    }

    // 响应拦截器返回的是完整response对象，需要通过response.data访问数据
    if (response && response.data && (response.data.success || response.data.code === 20 || response.data.code === 200)) {
      ElMessage.success(editingList.value ? '编辑成功' : '创建成功');
      showCreateDialog.value = false;
      resetForm();
      loadMySongList();
    } else {
      ElMessage.error(response?.data?.message || (editingList.value ? '编辑失败' : '创建失败'));
    }
  } catch (error) {
    console.error('提交歌单错误:', error);
    ElMessage.error('网络错误，请稍后重试');
  }
};

// 删除歌单
const deleteSongList = (songListId) => {
  ElMessageBox.confirm('确定要删除这个歌单吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 从auth工具获取真实用户ID
      const { getUserId } = await import('@/utils/auth.js');
      const userId = getUserId();

      const response = await removeSongList(songListId, userId);
      console.log('删除歌单响应:', response);
      // 响应拦截器返回的是完整response对象，需要通过response.data访问数据
      if (response && response.data && (response.data.success || response.data.code === 20)) {
        ElMessage.success('删除成功');
        // 重新加载我的歌单列表
        if (activeTab.value === 'mySongList') {
          loadMySongList();
        }
      } else {
        ElMessage.error(response?.data?.message || '删除失败');
      }
    } catch (error) {
      console.error('删除歌单错误:', error);
      ElMessage.error('删除失败，请稍后重试');
    }
  }).catch(() => {
  });
};
</script>
<style scoped>
/* ========== 主容器与全局重置 ========== */
.container {
  padding: 30px;
  background: linear-gradient(180deg, #f9f9f9 0%, #f0f0f0 100%);
  min-height: 100vh;
}

/* ========== 标题与操作栏区域 ========== */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eaeaea;
}

.header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  position: relative;
  padding-left: 15px;
  margin: 0;
}

.header h2::before {
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

.header-actions .el-button--primary {
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  border: none;
  border-radius: 20px;
  padding: 10px 24px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.header-actions .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(236, 65, 65, 0.3);
}

/* ========== 搜索与功能区 ========== */
.search-section {
  display: flex;
  gap: 12px;
  margin-bottom: 25px;
  padding: 20px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.search-section .el-input__wrapper {
  border-radius: 20px;
  border: 1px solid #e0e0e0;
  box-shadow: none;
  transition: all 0.3s ease;
}

.search-section .el-input__wrapper:hover,
.search-section .el-input__wrapper.is-focus {
  border-color: #ec4141;
  box-shadow: 0 0 0 1px rgba(236, 65, 65, 0.2);
}

.search-section .el-button {
  border-radius: 18px;
  transition: all 0.3s ease;
}

.search-section .el-button--primary {
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  border: none;
}

.search-section .el-button--primary:hover {
  transform: translateY(-1px);
}

/* ========== 标签页导航 ========== */
.el-tabs {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

::v-deep(.el-tabs__header) {
  margin: 0;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  padding: 0 25px;
}

::v-deep(.el-tabs__nav-wrap::after) {
  background-color: transparent;
}

::v-deep(.el-tabs__item) {
  height: 54px;
  line-height: 54px;
  font-size: 16px;
  font-weight: 500;
  color: #666;
  padding: 0 22px;
  transition: all 0.3s ease;
}

::v-deep(.el-tabs__item:hover) {
  color: #333;
}

::v-deep(.el-tabs__item.is-active) {
  color: #ec4141;
  font-weight: 600;
}

::v-deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 2px;
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
}

/* ========== 歌单卡片网格 ========== */
.songlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 25px;
  padding: 25px;
}

.songlist-item {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #f0f0f0;
  position: relative;
}

.songlist-item:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
  border-color: rgba(236, 65, 65, 0.2);
}

/* 歌单封面样式 */
.songlist-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
  position: relative;
  background-color: #f5f5f5;
}

.songlist-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.8s ease;
}

.songlist-item:hover .songlist-cover img {
  transform: scale(1.08);
}

.songlist-cover::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 40%;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.3), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.songlist-item:hover .songlist-cover::after {
  opacity: 1;
}

/* 歌单信息区域 */
.songlist-info {
  padding: 18px;
}

.songlist-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.songlist-desc {
  font-size: 13px;
  color: #999;
  margin: 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.8em;
}

/* 歌单操作按钮 */
.songlist-actions {
  padding: 0 18px 18px;
  display: flex;
  gap: 10px;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.3s ease;
}

.songlist-item:hover .songlist-actions {
  opacity: 1;
  transform: translateY(0);
}

.songlist-actions button {
  flex: 1;
  padding: 3px 6px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #e0e0e0;
  background: #fff;
  color: #666;
}

.songlist-actions button:first-child:hover {
  border-color: #409eff;
  color: #409eff;
  background: rgba(64, 158, 255, 0.05);
}

.songlist-actions button:last-child:hover {
  border-color: #f56c6c;
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.05);
}

.songlist-actions button.is-liked {
  background-color: #f56c6c;
  color: #fff;
  border-color: #f56c6c;
}

.songlist-actions button.is-liked:hover {
  background-color: #f78989;
  border-color: #f78989;
  color: #fff;
}

/* ========== 歌单详情对话框样式 ========== */
/* 对话框通用样式 */
::v-deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

::v-deep(.el-dialog__header) {
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  margin-right: 0;
  padding: 20px 24px;
}

::v-deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.dialog-header .el-button {
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  border: none;
  border-radius: 6px;
  padding: 6px 16px;
  font-size: 13px;
}

/* 歌单详情头部区域 */
.songlist-detail-header {
  display: flex;
  gap: 28px;
  margin-bottom: 30px;
  padding-bottom: 25px;
  border-bottom: 1px solid #f0f0f0;
}

.songlist-cover-large {
  width: 180px;
  height: 180px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  flex-shrink: 0;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.songlist-cover-large:hover .cover-img {
  transform: scale(1.05);
}

.songlist-info-large {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.songlist-name-large {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 12px 0;
  line-height: 1.3;
}

.songlist-desc-large {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 16px 0;
}

.songlist-stats {
  font-size: 14px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 15px;
}

/* 歌曲列表区域 */
.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.song-search-box {
  margin-bottom: 20px;
}

.song-search-box .el-input__wrapper {
  border-radius: 20px;
}

/* 歌曲列表表格样式 */
.songs-list ::v-deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #f0f0f0;
}

.songs-list ::v-deep(.el-table__header) {
  background: #fafafa;
}

.songs-list ::v-deep(.el-table th) {
  background: #fafafa;
  color: #666;
  font-weight: 600;
  height: 50px;
}

.songs-list ::v-deep(.el-table tr) {
  transition: background-color 0.2s ease;
}

.songs-list ::v-deep(.el-table .el-table__row:hover) {
  background-color: rgba(236, 65, 65, 0.04);
}

.songs-list ::v-deep(.el-table .even-row) {
  background-color: #fff;
}

.songs-list ::v-deep(.el-table .odd-row) {
  background-color: #fafafa;
}

/* 歌曲项样式 */
.song-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.song-cover-small {
  width: 46px;
  height: 46px;
  border-radius: 6px;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.song-cover-small:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.song-name {
  font-weight: 500;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s ease;
}

.song-name:hover {
  color: #ec4141;
}

/* 收藏按钮样式 */
::v-deep(.el-table .el-button) {
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 12px;
  transition: all 0.2s ease;
}

::v-deep(.el-table .el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 空状态样式 */
.empty-search-results,
.empty-songs {
  padding: 60px 0;
  text-align: center;
}

.empty-songs p {
  color: #999;
  font-size: 14px;
  margin: 0;
}

/* ========== 创建/编辑歌单对话框 ========== */
::v-deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

.avatar-uploader {
  width: 178px;
  height: 178px;
  border: 2px dashed #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s ease;
}

.avatar-uploader:hover {
  border-color: #ec4141;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #c0c4cc;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.upload-text {
  margin-top: 10px;
  font-size: 14px;
  color: #909399;
  cursor: pointer;
}

/* ========== 歌曲搜索对话框 ========== */
.song-search-container {
  padding: 10px 0;
}

/* ========== 响应式设计 ========== */
@media (max-width: 1200px) {
  .songlist-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 20px;
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 15px;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .search-section {
    flex-direction: column;
    padding: 15px;
  }

  .songlist-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 15px;
    padding: 15px;
  }

  .songlist-cover {
    height: 160px;
  }

  .songlist-detail-header {
    flex-direction: column;
    text-align: center;
  }

  .songlist-cover-large {
    width: 140px;
    height: 140px;
    margin: 0 auto;
  }

  .songlist-name-large {
    font-size: 22px;
  }
}

@media (max-width: 480px) {
  .songlist-grid {
    grid-template-columns: 1fr;
  }

  ::v-deep(.el-tabs__item) {
    padding: 0 12px;
    font-size: 14px;
  }
}
</style>