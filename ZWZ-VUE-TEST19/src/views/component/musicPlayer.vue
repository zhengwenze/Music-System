<template>
  <div class="simple-aplayer">
    <audio ref="audioRef" :src="audioUrl" @play="isPlay = true" @pause="isPlay = false" @timeupdate="handleTimeUpdate"
      @ended="handleEnded" style="display: none;"></audio>
    <div class="player-controls">
      <!-- 播放/暂停按钮 -->
      <button class="control-btn" @click="togglePlay">
        {{ isPlay ? '暂停' : '播放' }}
      </button>
      <!-- 进度条 -->
      <div class="progress-container">
        <div class="progress-bar" @click="seek">
          <div class="progress-filled" :style="{ width: progress + '%' }"></div>
        </div>
      </div>
      <!-- 时间显示 -->
      <div class="time-display">
        {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
      </div>
      <!-- 音量控制 -->
      <div class="volume-container">
        <button class="control-btn" @click="toggleMute">
          {{ isMuted ? '🔇' : '🔊' }}
        </button>
        <div class="volume-bar" @click="setVolume">
          <div class="volume-filled" :style="{ width: volume + '%' }"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
// Props 需提供audioUrl即可播放，autoplay为完全可选属性
const props = defineProps({
  audioUrl: {
    type: String,
    required: true, // 唯一必填参数
    default: ''
  },
  autoplay: {
    type: Boolean,
    default: false, // 可选：是否自动播放，不提供时默认为false
    required: false
  }
});

// Refs
const audioRef = ref(null);
// State
const isPlay = ref(false);
const currentTime = ref(0);
const duration = ref(0);
const progress = ref(0);
const volume = ref(100);
const isMuted = ref(false);
// Methods
const togglePlay = () => {
  if (isPlay.value) {
    audioRef.value.pause();
  } else {
    audioRef.value.play();
  }
};

const handleTimeUpdate = () => {
  currentTime.value = audioRef.value.currentTime;
  duration.value = audioRef.value.duration || 0;
  progress.value = (currentTime.value / duration.value) * 100 || 0;
};

const handleEnded = () => {
  isPlay.value = false;
  currentTime.value = 0;
  progress.value = 0;
};

const seek = (e) => {
  const progressBar = e.currentTarget;
  const rect = progressBar.getBoundingClientRect();
  const percent = (e.clientX - rect.left) / rect.width;
  const newTime = percent * duration.value;

  audioRef.value.currentTime = newTime;
  currentTime.value = newTime;
  progress.value = percent * 100;

  if (!isPlay.value) {
    audioRef.value.play();
  }
};

const toggleMute = () => {
  isMuted.value = !isMuted.value;
  audioRef.value.muted = isMuted.value;
};

const setVolume = (e) => {
  const volumeBar = e.currentTarget;
  const rect = volumeBar.getBoundingClientRect();
  const percent = Math.max(0, Math.min(100, ((e.clientX - rect.left) / rect.width) * 100));

  volume.value = percent;
  audioRef.value.volume = percent / 100;
  isMuted.value = percent === 0;
  audioRef.value.muted = isMuted.value;
};

const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00';

  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

// Watchers
watch(() => props.audioUrl, (newUrl) => {
  if (newUrl) {
    currentTime.value = 0;
    duration.value = 0;
    progress.value = 0;
    isPlay.value = false;
  }
});

// Lifecycle
onMounted(() => {
  if (props.autoplay && props.audioUrl) {
    audioRef.value.play().catch(error => {
      console.log('Auto play failed:', error);
    });
  }
});

// Expose methods for external control
defineExpose({
  play: () => audioRef.value?.play(),
  pause: () => audioRef.value?.pause(),
  seekTo: (time) => {
    if (audioRef.value) {
      audioRef.value.currentTime = time;
    }
  },
  setVolumeLevel: (level) => {
    if (audioRef.value) {
      volume.value = Math.max(0, Math.min(100, level));
      audioRef.value.volume = volume.value / 100;
      isMuted.value = volume.value === 0;
      audioRef.value.muted = isMuted.value;
    }
  }
});
</script>

<style scoped>
.simple-aplayer {
  width: 100%;
  margin: 0 auto;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 0px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.player-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.control-btn {
  background-color: #ec4141;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.control-btn:hover {
  background-color: #c62f2f;
}

.progress-container {
  flex: 1;
}

.progress-bar,
.volume-bar {
  width: 100%;
  height: 8px;
  background-color: #ddd;
  border-radius: 4px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.progress-filled,
.volume-filled {
  height: 100%;
  background-color: #ec4141;
  border-radius: 4px;
  transition: width 0.1s linear;
}

.time-display {
  font-size: 14px;
  color: #666;
  min-width: 80px;
  text-align: center;
}

.volume-container {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 120px;
}

.volume-bar {
  width: 80px;
}
</style>