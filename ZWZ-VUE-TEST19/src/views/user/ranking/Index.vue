<template>
  <div class="container">
    <h2>歌曲排行</h2>
    <p>该页面正在开发中...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

onMounted(() => {
  // 可以在这里添加初始化逻辑
  console.log('歌曲排行页面加载完成');
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