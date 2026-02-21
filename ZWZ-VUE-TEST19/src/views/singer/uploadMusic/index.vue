<template>
  <div class="upload-music-container">
    <h2>发布音乐</h2>
    <el-form :model="musicForm" :rules="rules" ref="musicFormRef" label-width="120px" class="music-form">
      <el-form-item label="选择音乐文件">
        <div style="display: flex; gap: 10px; align-items: center;">
          <el-upload :action="''" :before-upload="beforeMusicUpload" :http-request="customMusicUpload"
            :before-remove="beforeRemove" :auto-upload="false" :file-list="musicFileList" accept=".mp3,.wav,.flac,.m4a"
            @change="handleMusicChange" :limit="1" :on-exceed="handleExceed" list-type="text">
            <el-button type="primary">选择文件</el-button>
          </el-upload>
          <el-button type="primary" @click="handleMusicUpload">
            上传
          </el-button>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 mp3/wav/flac/m4a 格式的音频文件，且不超过 10MB，最多上传1个文件
          </div>
        </template>
      </el-form-item>

      <el-form-item label="音乐名称" prop="musicName">
        <el-input v-model="musicForm.musicName" placeholder="请输入音乐名称" />
      </el-form-item>

      <el-form-item label="歌手用户名" prop="singerUsername">
        <el-input v-model="musicForm.singerUsername" placeholder=" " disabled />
      </el-form-item>

      <el-form-item label="图片URL">
        <div style="display: flex; gap: 10px; align-items: center;">
          <el-upload :action="''" :before-upload="beforeImageUpload" :http-request="customImageUpload"
            :before-remove="beforeRemove" :auto-upload="false" :file-list="imageFileList" accept=".jpg,.jpeg,.png,.gif"
            @change="handleImageChange" :limit="1" :on-exceed="handleExceed" list-type="text">
            <el-button type="primary">选择封面</el-button>
          </el-upload>
          <el-button type="primary" @click="handleImageUpload">
            上传
          </el-button>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 jpg/jpeg/png/gif 格式的图片文件，且不超过 5MB，最多上传1个文件
          </div>
        </template>
      </el-form-item>
      <el-form-item label="封面预览">
        <div v-if="musicForm.imageUrl" class="image-preview">
          <img :src="musicForm.imageUrl" alt="音乐封面" style="max-width: 200px; max-height: 200px;" />
        </div>
        <div v-else class="image-placeholder">
          暂无封面图片
        </div>
      </el-form-item>
      <el-form-item label="音乐URL">
        <el-input v-model="musicForm.musicUrl" placeholder=" " readonly disabled />
      </el-form-item>
      <el-form-item label="图片URL">
        <el-input v-model="musicForm.imageUrl" placeholder=" " readonly disabled />
      </el-form-item>

      <!-- 操作按钮组 -->
      <el-form-item>
        <div class="button-group">
          <el-button type="success" @click="submitMusicInfo">
            提交音乐信息
          </el-button>
        </div>
      </el-form-item>
    </el-form>

    <!-- 上传结果信息 -->
    <div v-if="uploadResult" class="upload-result">
      <el-alert v-if="uploadResult.success" :title="uploadResult.message" type="success" show-icon />
      <div v-if="uploadResult.success && uploadResult.data">
        <el-descriptions border>
          <el-descriptions-item label="URL">{{ uploadResult.data.url }}</el-descriptions-item>
          <el-descriptions-item v-if="uploadResult.data.timelength" label="音乐时长">{{
            formatDuration(uploadResult.data.timelength) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
    <!-- 提交结果信息 -->
    <div v-if="submitResult" class="submit-result">
      <el-alert :title="submitResult.message" :type="submitResult.success ? 'success' : 'error'" show-icon />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { addMusic } from '@/api/singer';
import { uploadMusic } from '@/api/upload';
import { getUserInfo } from '@/api/user';
import { getUserId } from '@/utils/auth';

// 导入上传图片API
import { uploadImage } from '@/api/upload';

// 表单数据
const musicForm = reactive({
  musicName: '',
  singerUsername: '',
  musicUrl: '',
  imageUrl: ''
});

// 组件挂载时
onMounted(async () => {
  console.log('上传音乐组件已挂载');
  // 获取当前用户信息并自动填充歌手用户名
  await loadSingerUsername();
});

// 加载歌手用户名
const loadSingerUsername = async () => {
  try {
    const userId = getUserId();
    if (!userId) {
      console.error('用户ID不存在');
      ElMessage.error('用户未登录');
      return;
    }

    const response = await getUserInfo(userId);
    // 正确的响应数据结构验证
    if (response && response.data && (response.data.success || response.data.code === 20) && response.data.data && response.data.data.user) {
      const userData = response.data.data.user;
      musicForm.singerUsername = userData.username || '';
      console.log('自动填充歌手用户名:', musicForm.singerUsername);
    } else {
      console.error('获取用户信息数据结构不正确:', response);
      // 用户名获取失败不应该阻止用户操作，只记录错误
    }
  } catch (error) {
    console.error('获取用户信息异常:', error);
    // 用户名获取失败不应该阻止用户操作，只记录错误
  }
};

// 表单验证规则
const rules = {
  musicName: [
    { required: true, message: '请输入音乐名称', trigger: 'blur' },
    { min: 1, max: 50, message: '音乐名称长度应在1-50个字符之间', trigger: 'blur' }
  ],
  singerUsername: [
    { required: true, message: '请输入歌手用户名', trigger: 'blur' },
    { min: 1, max: 50, message: '歌手用户名长度应在1-50个字符之间', trigger: 'blur' }
  ]
};

// 表单引用
const musicFormRef = ref(null);

// 音乐文件列表
const musicFileList = ref([]);

// 图片文件列表
const imageFileList = ref([]);

// 上传结果
const uploadResult = ref(null);

// 提交结果
const submitResult = ref(null);

// 当前用户ID（实际应用中应从登录状态获取）
const currentUserId = ref(1); // 这里假设用户ID为1，实际应从登录状态获取

// 处理文件超出数量限制
const handleExceed = (files, fileList) => {
  ElMessage.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
};

// 移除文件前的操作
const beforeRemove = (file, uploadFiles) => {
  console.log('=== beforeRemove 触发 ===');
  console.log('要移除的文件:', file.name);

  try {
    // 使用ElMessageBox.confirm而不是ElMessage.confirm
    return ElMessageBox.confirm(
      `确定移除 ${file.name}？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).catch(() => false);
  } catch (error) {
    console.error('移除确认对话框错误:', error);
    return false;
  }
};

// 文件上传前的验证
const beforeMusicUpload = (file) => {
  const isAudio = ['audio/mpeg', 'audio/wav', 'audio/flac', 'audio/mp4'].includes(file.type);
  if (!isAudio) {
    ElMessage.error('上传文件必须是音频格式!');
    return false;
  }

  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    ElMessage.error('上传文件大小不能超过 10MB!');
    return false;
  }

  return true;
};

// 图片上传前的验证
const beforeImageUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type);
  if (!isImage) {
    ElMessage.error('上传文件必须是图片格式!');
    return false;
  }

  const isLt5M = file.size / 1024 / 1024 < 5;
  if (!isLt5M) {
    ElMessage.error('上传文件大小不能超过 5MB!');
    return false;
  }

  return true;
};

// 自定义上传函数（使用真实API）
const customMusicUpload = async (options) => {
  console.log('=== customMusicUpload 开始 ===');
  console.log('options 参数:', options);
  try {
    // 确保options参数有效
    if (!options || typeof options !== 'object') {
      throw new Error('无效的选项参数');
    }

    const { file, onProgress, onSuccess, onError } = options;

    // 确保file是有效的File对象
    let actualFile = file;
    // 先检查file是否存在
    if (!file) {
      throw new Error('无效的文件对象：文件参数为空');
    }
    // 然后检查是File实例还是有raw属性
    if (file instanceof File) {
      actualFile = file;
      console.log('直接使用File实例作为上传文件');
    } else if (file.raw && file.raw instanceof File) {
      console.log('使用file.raw作为上传文件');
      actualFile = file.raw;
    } else {
      console.error('无效的文件对象:', file);
      throw new Error('无效的文件对象：不是File实例且没有有效的raw属性');
    }

    console.log('上传的文件信息:', { name: actualFile.name, size: actualFile.size, type: actualFile.type });

    // 创建进度监听器
    const progressHandler = (progressEvent) => {
      if (progressEvent.total) {
        const percentComplete = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        console.log(`上传进度: ${percentComplete}%`);
        // 触发进度回调
        if (onProgress) {
          onProgress({
            percent: percentComplete
          });
        }
        // 更新file对象的进度
        if (file) {
          file.percentage = percentComplete;
        }
      }
    };

    // 调用真实的uploadMusic API，传入进度回调
    console.log('开始调用uploadMusic API...');
    const result = await uploadMusic(actualFile, progressHandler);

    console.log('=== API调用完成 ===');
    console.log('API返回的原始结果:', result);

    // 严格验证API响应数据
    if (!result) {
      throw new Error('API返回空结果');
    }

    // 获取响应数据
    const responseData = result || {};
    console.log('API返回的完整响应:', responseData);

    // 修改：同时支持后端直接返回{url, timelength}格式和标准格式
    let success = false;
    let musicUrl = '';
    let timelength = 0;
    // 情况1: 检查是否直接返回了url和timelength（简化格式）
    if (responseData.url) {
      success = true;
      musicUrl = responseData.url;
      timelength = responseData.timelength || 0;
      console.log('✓ 识别到简化响应格式，直接包含URL和timelength');
    }
    // 情况2: 检查是否是标准响应格式（{code, message, success, data}）
    else if ((responseData.success === true || responseData.code === 20) && responseData.data && responseData.data.url) {
      success = true;
      musicUrl = responseData.data.url;
      timelength = responseData.data.timelength || 0;
      console.log('✓ 识别到标准响应格式');
    }

    if (success) {
      console.log('✓ 上传成功判断条件满足');

      // 严格验证URL是否有效
      if (!musicUrl || musicUrl.trim() === '') {
        throw new Error('API返回的数据中没有有效的URL');
      }

      // 准备成功响应数据
      const successData = {
        success: true,
        message: responseData.message || '音乐文件上传成功',
        data: {
          url: musicUrl,
          timelength: timelength
        }
      };

      // 更新上传结果
      uploadResult.value = successData;
      console.log('✓ 上传结果已更新:', successData);

      // 更新表单中的音乐URL
      musicForm.musicUrl = musicUrl;
      console.log('✓ 表单URL已更新:', musicForm.musicUrl);

      // 更新file对象状态和URL
      if (file) {
        file.status = 'success';
        file.url = musicUrl;
        file.percentage = 100;
        console.log('✓ 文件对象已更新状态和URL');
      }

      // 触发成功回调
      if (onSuccess) {
        onSuccess({ status: 'success', data: musicUrl });
      }

      return Promise.resolve({ status: 'success', data: musicUrl });
    } else {
      // 上传失败的情况
      const errorMessage = responseData.message || '上传失败，API返回错误';
      console.error('❌ API返回失败状态:', { success: responseData.success, code: responseData.code, message: errorMessage });
      throw new Error(errorMessage);
    }

  } catch (error) {
    console.error('❌ 文件上传失败:', error);

    // 设置明确的失败状态
    const errorMessage = error.message || '音乐文件上传失败';
    uploadResult.value = {
      success: false,
      message: errorMessage
    };

    // 确保清空URL，避免使用旧数据
    musicForm.musicUrl = '';
    console.log('❌ 已清空表单URL');

    // 更新file对象状态为失败
    if (options && options.file) {
      options.file.status = 'error';
      options.file.url = '';
      options.file.percentage = 0;
      console.log('❌ 文件对象已更新为失败状态');
    }

    // 触发错误回调
    if (options && options.onError) {
      options.onError(error);
    }

    // 显示错误消息
    ElMessage.error(errorMessage);

    // 返回rejected Promise，确保Promise链正确处理错误
    return Promise.reject(error);
  }

  console.log('=== customMusicUpload 结束 ===');
};

// 自定义图片上传函数
const customImageUpload = async (options) => {
  console.log('=== customImageUpload 开始 ===');

  try {
    // 解构参数并进行类型检查
    if (!options || typeof options !== 'object') {
      console.error('❌ 无效的参数：options必须是一个对象');
      ElMessage.error('上传配置错误');
      options?.onError?.({ message: '上传配置错误' });
      return Promise.reject(new Error('上传配置错误'));
    }

    let { file, onProgress, onSuccess, onError } = options;

    // 验证file参数
    if (!file) {
      console.error('❌ 无效的参数：缺少file对象');
      ElMessage.error('请选择要上传的文件');
      onError?.({ message: '请选择要上传的文件' });
      return Promise.reject(new Error('请选择要上传的文件'));
    }

    // 兼容不同类型的file对象
    const rawFile = file.raw || file;
    if (!(rawFile instanceof File)) {
      console.error('❌ 无效的文件类型：必须是File实例');
      ElMessage.error('无效的文件类型');
      onError?.({ message: '无效的文件类型' });
      return Promise.reject(new Error('无效的文件类型'));
    }

    console.log('📁 待上传的原始文件:', rawFile);

    // 创建进度监听器
    const progressHandler = (progressEvent) => {
      if (!progressEvent) return;

      let percent = 0;
      const total = progressEvent.total;
      const loaded = progressEvent.loaded;

      if (total > 0) {
        percent = Math.round((loaded / total) * 100);
      }

      console.log('📊 上传进度:', percent + '%');

      // 调用进度回调
      if (typeof onProgress === 'function') {
        onProgress({ percent });
      }

      // 尝试更新file对象的进度（如果file对象存在）
      if (file && typeof file === 'object') {
        file.percentage = percent;
      }
    };

    console.log('🚀 开始调用uploadImage API...');
    // 调用uploadImage API上传图片，不传递进度回调参数以符合原始接口定义
    const result = await uploadImage(rawFile);

    console.log('📥 上传API返回结果:', result);

    // 验证API响应
    if (!result) {
      throw new Error('上传API返回空结果');
    }

    let imageUrl = '';

    // 处理不同格式的响应
    if (typeof result === 'string') {
      // 纯URL字符串
      imageUrl = result;
      console.log('🔗 从字符串响应中提取URL:', imageUrl);
    } else if (result.url) {
      // 包含url字段的对象
      imageUrl = result.url;
      console.log('🔗 从url字段中提取URL:', imageUrl);
    } else if (result.data && result.data.url) {
      // 包含data.url的对象
      imageUrl = result.data.url;
      console.log('🔗 从data.url中提取URL:', imageUrl);
    } else if (result.data) {
      // 直接使用data字段
      imageUrl = result.data;
      console.log('🔗 从data字段中提取URL:', imageUrl);
    } else {
      // 默认使用整个结果
      imageUrl = String(result);
      console.log('🔗 使用整个响应作为URL:', imageUrl);
    }

    // 验证提取的URL
    if (!imageUrl || typeof imageUrl !== 'string') {
      throw new Error('无法从响应中提取有效的图片URL');
    }

    console.log('✅ 成功提取图片URL:', imageUrl);

    // 更新表单数据
    musicForm.imageUrl = imageUrl;
    console.log('📝 更新后的表单图片URL:', musicForm.imageUrl);

    // 更新上传结果
    uploadResult.value = {
      success: true,
      message: '图片上传成功',
      url: imageUrl
    };
    console.log('📊 更新后的上传结果:', uploadResult.value);

    // 调用成功回调
    if (typeof onSuccess === 'function') {
      onSuccess({ url: imageUrl, ...result });
    }

    // 显示成功提示
    ElMessage.success('图片上传成功');

    return { success: true, url: imageUrl };

  } catch (error) {
    console.error('❌ customImageUpload 异常:', error);

    // 构建错误信息
    const errorMessage = error.message || '图片上传失败';

    // 更新上传结果
    uploadResult.value = {
      success: false,
      message: errorMessage
    };
    console.log('📊 更新后的上传结果:', uploadResult.value);

    // 调用错误回调
    if (options?.onError && typeof options.onError === 'function') {
      options.onError({ message: errorMessage });
    }

    // 显示错误提示
    ElMessage.error(errorMessage);

    return Promise.reject(error);
  } finally {
    console.log('=== customImageUpload 结束 ===');
  }
};

// 文件状态变化时的处理函数
const handleMusicChange = (file, fileList) => {
  console.log('=== handleMusicChange 触发 ===');
  console.log('当前文件状态:', file.status);
  console.log('文件列表变化:', fileList);

  // 更新文件列表
  musicFileList.value = fileList;

  // 检查文件状态
  if (file.status === 'success') {
    console.log('✅ 文件上传成功，检查URL');
    // 确保file.url存在且不为空
    if (file.url && file.url.trim()) {
      console.log('✅ 使用文件的URL更新表单:', file.url);
      musicForm.musicUrl = file.url;
    } else {
      console.warn('⚠️ 文件状态为成功但URL为空');
      // 不使用模拟数据，只依赖实际上传结果
      musicForm.musicUrl = '';
    }
  } else if (file.status === 'error') {
    console.error('❌ 文件上传失败');
    // 清空URL，避免使用可能的无效数据
    musicForm.musicUrl = '';
  } else if (file.status === 'removed') {
    console.log('🗑️ 文件被移除');
    // 文件被移除时清空URL
    musicForm.musicUrl = '';
  } else if (file.status === 'uploading') {
    console.log('⏳ 文件上传中... 进度:', file.percentage || 0 + '%');
  }

  console.log('📝 当前表单音乐URL:', musicForm.musicUrl);
};

// 图片文件状态变化时的处理函数
const handleImageChange = (file, fileList) => {
  console.log('=== handleImageChange 触发 ===');
  console.log('当前图片状态:', file.status);
  console.log('图片列表变化:', fileList);

  // 更新图片文件列表
  imageFileList.value = fileList;

  // 检查文件状态
  if (file.status === 'success') {
    console.log('✅ 图片上传成功，检查URL');
    if (file.url && file.url.trim()) {
      console.log('✅ 使用图片的URL更新表单:', file.url);
      musicForm.imageUrl = file.url;
    } else {
      console.warn('⚠️ 图片状态为成功但URL为空');
      musicForm.imageUrl = '';
    }
  } else if (file.status === 'error') {
    console.error('❌ 图片上传失败');
    musicForm.imageUrl = '';
  } else if (file.status === 'removed') {
    console.log('🗑️ 图片被移除');
    musicForm.imageUrl = '';
  } else if (file.status === 'uploading') {
    console.log('⏳ 图片上传中... 进度:', file.percentage || 0 + '%');
  }

  console.log('📝 当前表单图片URL:', musicForm.imageUrl);
};

// 处理音乐文件上传
const handleMusicUpload = async () => {
  console.log('=== handleMusicUpload 开始 ===');

  // 从文件列表中获取文件
  const file = musicFileList.value[0];

  console.log('从文件列表获取的文件:', file);

  // 验证文件是否存在
  if (!file) {
    console.error('❌ 未选择音乐文件');
    ElMessage.error('请选择音乐文件后再上传');
    return Promise.reject(new Error('未选择音乐文件'));
  }

  // 清空旧的上传结果和URL
  console.log('🧹 清空旧的上传结果和URL');
  uploadResult.value = { success: false, message: '' };
  musicForm.musicUrl = '';

  try {
    // 调用自定义上传函数
    console.log('🚀 开始调用customMusicUpload函数...');
    const result = await customMusicUpload({
      file,
      onProgress: (progress) => {
        console.log('📊 上传进度更新:', progress.percent + '%');
        // 进度更新会通过customMusicUpload内部处理
      },
      onSuccess: (res) => {
        console.log('🎉 上传成功回调被触发:', res);
      },
      onError: (err) => {
        console.error('💥 上传错误回调被触发:', err);
      }
    });

    console.log('✅ handleMusicUpload 完成');
    return result;
  } catch (error) {
    console.error('❌ handleMusicUpload 异常:', error);
    ElMessage.error(error.message || '音乐文件上传失败');
    return Promise.reject(error);
  }
};

// 处理图片文件上传
const handleImageUpload = async () => {
  console.log('=== handleImageUpload 开始 ===');

  // 从文件列表中获取文件
  const file = imageFileList.value[0];

  console.log('从图片列表获取的文件:', file);

  // 验证文件是否存在
  if (!file) {
    console.error('❌ 未选择图片文件');
    ElMessage.error('请选择封面图片后再上传');
    return Promise.reject(new Error('未选择图片文件'));
  }

  // 清空旧的上传结果和URL
  console.log('🧹 清空旧的上传结果和URL');
  uploadResult.value = { success: false, message: '' };
  musicForm.imageUrl = '';

  try {
    // 调用自定义上传函数
    console.log('🚀 开始调用customImageUpload函数...');
    const result = await customImageUpload({
      file,
      onProgress: (progress) => {
        console.log('📊 图片上传进度更新:', progress.percent + '%');
      },
      onSuccess: (res) => {
        console.log('🎉 图片上传成功回调被触发:', res);
      },
      onError: (err) => {
        console.error('💥 图片上传错误回调被触发:', err);
      }
    });

    console.log('✅ handleImageUpload 完成');
    return result;
  } catch (error) {
    console.error('❌ handleImageUpload 异常:', error);
    ElMessage.error(error.message || '图片文件上传失败');
    return Promise.reject(error);
  }
};

// // 提交音乐信息,后端示例响应为:
// {
//     "code": 20,
//     "message": "音乐上传成功",
//     "success": true,
//     "data": {
//         "url": "http://localhost:8089/file/music/dc2b8ac3-618c-4061-a90c-81abed93d283.mp3",
//         "timelength": 214
//     }
// }
const submitMusicInfo = async () => {
  console.log('=== submitMusicInfo 开始 ===');
  console.log('提交前的表单数据:', { ...musicForm });

  try {
    // 表单验证
    await musicFormRef.value.validate();
    console.log('✅ 表单验证通过');

    // 检查音乐URL是否已上传
    if (!musicForm.musicUrl || musicForm.musicUrl.trim() === '') {
      console.error('❌ 音乐文件未上传或URL为空');
      ElMessage.error('请先上传音乐文件');
      return;
    }

    // 直接使用musicForm数据，不再尝试解析不存在的category和tags字段
    const submitData = { ...musicForm };
    console.log('📤 准备提交的数据:', submitData);

    // 调用添加音乐的API
    const userId = getUserId();
    const result = await addMusic(submitData, userId);
    console.log('📥 添加音乐API返回结果:', result);

    // 验证API响应
    if (!result || !result.data) {
      throw new Error('API返回空结果');
    }

    // 获取响应数据
    const resData = result.data;
    console.log('解析后的响应数据:', resData);

    // 根据后端响应格式判断是否成功
    if (resData.success === true || resData.code === 20) {
      // 显示成功提示
      ElMessage.success(resData.message || '音乐信息添加成功');

      // 重置表单
      console.log('✅ 表单字段已重置');

      // 确保清空音乐URL和图片URL
      musicForm.musicUrl = '';
      musicForm.imageUrl = '';
      console.log('✅ 音乐URL和图片URL已清空:', { musicUrl: musicForm.musicUrl, imageUrl: musicForm.imageUrl });

      // 清空文件列表
      musicFileList.value = [];
      imageFileList.value = [];
      console.log('✅ 文件列表和图片列表已清空');

      // 清空上传结果
      uploadResult.value = { success: false, message: '' };
      submitResult.value = null;
      console.log('✅ 上传结果和提交结果已清空');

      console.log('🔄 重置表单');
      resetForm();

      // 延迟3秒后刷新页面
      setTimeout(() => {
        console.log('🔄 刷新页面');
        router.push(router.currentRoute.value.fullPath);
      }, 3000);
    } else {
      // 处理API返回的错误
      throw new Error(resData.message || '提交失败');
    }

  } catch (error) {
    console.error('❌ submitMusicInfo 异常:', error);
    // 区分不同类型的错误并提供更详细的错误信息
    if (error.response) {
      // 服务器返回了错误响应
      const errorMsg = error.response.data?.message || error.response.data?.error || '服务器处理失败';
      ElMessage.error(errorMsg);
    } else if (error.request) {
      // 请求已发出但没有收到响应
      ElMessage.error('网络错误，请检查您的连接');
    } else {
      // 其他错误
      ElMessage.error(error.message || '提交失败，请重试');
    }
  }
};

// 重置表单
const resetForm = () => {
  console.log('=== resetForm 开始 ===');

  if (musicFormRef.value) {
    musicFormRef.value.resetFields();
    console.log('✅ 表单字段已重置');
  }

  // 确保清空音乐URL和图片URL
  musicForm.musicUrl = '';
  musicForm.imageUrl = '';
  console.log('✅ 音乐URL和图片URL已清空:', { musicUrl: musicForm.musicUrl, imageUrl: musicForm.imageUrl });

  // 清空文件列表
  musicFileList.value = [];
  imageFileList.value = [];
  console.log('✅ 文件列表和图片列表已清空');

  // 清空上传结果
  uploadResult.value = { success: false, message: '' };
  submitResult.value = null;
  console.log('✅ 上传结果和提交结果已清空');

  console.log('=== resetForm 完成 ===');
};

// 选择分类和标签后的处理函数
const handleSelectChange = (key, values) => {
  console.log(`=== handleSelectChange 触发 ===`);
  console.log(`选择类型: ${key}, 选择值:`, values);

  if (key === 'category') {
    musicForm.category = JSON.stringify(values);
    console.log('📝 分类已更新:', musicForm.category);
  } else if (key === 'tags') {
    musicForm.tags = JSON.stringify(values);
    console.log('📝 标签已更新:', musicForm.tags);
  }
};

// 格式化音乐时长
const formatDuration = (seconds) => {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
};
</script>



<style scoped>
/* ========== 主容器与标题 ========== */
.upload-music-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06);
}

.upload-music-container h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
  position: relative;
  padding-bottom: 15px;
}

.upload-music-container h2::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  border-radius: 2px;
}

/* ========== 表单样式 ========== */
.music-form {
  padding: 25px;
  background-color: #ffffff;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.el-form-item {
  margin-bottom: 28px;
}

::v-deep(.el-form-item__label) {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

::v-deep(.el-input__wrapper),
::v-deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  box-shadow: none;
  transition: all 0.3s ease;
  background-color: #fafafa;
  padding: 0 12px;
}

::v-deep(.el-input__wrapper:hover),
::v-deep(.el-textarea__inner:hover) {
  border-color: #b0b0b0;
  background-color: #fff;
}

::v-deep(.el-input__wrapper.is-focus),
::v-deep(.el-textarea__inner:focus) {
  border-color: #ec4141;
  box-shadow: 0 0 0 2px rgba(236, 65, 65, 0.1);
  background-color: #fff;
}

::v-deep(.el-input.is-disabled .el-input__wrapper) {
  background-color: #f5f5f5;
  color: #999;
}

/* ========== 上传组件样式 ========== */
::v-deep(.el-upload) {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

::v-deep(.el-upload-list--text) {
  width: 100%;
  margin-top: 10px;
}

::v-deep(.el-upload-list__item) {
  transition: all 0.3s ease;
}

::v-deep(.el-upload-list__item:hover) {
  background-color: rgba(236, 65, 65, 0.05);
}

::v-deep(.el-upload-list__item-name) {
  color: #333;
}

::v-deep(.el-upload-list__item-status-label) {
  color: #ec4141;
}

/* 上传按钮样式 */
.el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-button--primary {
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  border: none;
  padding: 10px 24px;
}

.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(236, 65, 65, 0.3);
}

.el-button--success {
  background: linear-gradient(90deg, #67c23a 0%, #5da934 100%);
  border: none;
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 600;
}

.el-button--success:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(103, 194, 58, 0.3);
}

/* 文件选择区域样式 */
.el-upload__tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  line-height: 1.4;
}

/* 上传文件列表中的按钮组 */
[style*="display: flex; gap: 10px; align-items: center;"] {
  margin-top: 8px;
}

/* ========== 图片预览区域 ========== */
.image-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 200px;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  border: 2px dashed #e0e0e0;
  transition: all 0.3s ease;
}

.image-preview:hover {
  border-color: #ec4141;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 200px;
  height: 200px;
  border-radius: 12px;
  border: 2px dashed #e0e0e0;
  color: #999;
  font-size: 14px;
  background-color: #fafafa;
  transition: all 0.3s ease;
}

.image-placeholder:hover {
  border-color: #ec4141;
  background-color: rgba(236, 65, 65, 0.02);
}

/* ========== 按钮组样式 ========== */
.button-group {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* ========== 上传结果和提交结果样式 ========== */
.upload-result,
.submit-result {
  margin-top: 30px;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

::v-deep(.el-alert) {
  border-radius: 8px;
  border: none;
}

::v-deep(.el-alert--success) {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

::v-deep(.el-alert--error) {
  background-color: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

::v-deep(.el-descriptions) {
  margin-top: 15px;
}

::v-deep(.el-descriptions__body) {
  background-color: #fafafa;
}

::v-deep(.el-descriptions__label) {
  color: #666;
  font-weight: 600;
}

::v-deep(.el-descriptions__content) {
  color: #333;
}

/* ========== 响应式设计 ========== */
@media (max-width: 768px) {
  .upload-music-container {
    padding: 20px 15px;
    margin: 10px;
  }

  .upload-music-container h2 {
    font-size: 24px;
  }

  .music-form {
    padding: 20px;
  }

  .image-preview,
  .image-placeholder {
    width: 150px;
    height: 150px;
  }

  .button-group {
    flex-direction: column;
    gap: 15px;
  }

  .el-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .upload-music-container h2 {
    font-size: 20px;
  }

  .music-form {
    padding: 15px;
  }

  .image-preview,
  .image-placeholder {
    width: 120px;
    height: 120px;
  }

  [style*="display: flex; gap: 10px; align-items: center;"] {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>