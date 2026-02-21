<template>
  <div class="music-management-container">
    <el-card>
      <div class="card-header">
        <h2>音乐管理</h2>
      </div>

      <!-- 搜索区域 -->
      <div class="search-box">
        <el-input v-model="searchKeyword" placeholder="请输入音乐名称进行搜索" style="width: 300px; margin-right: 10px;" clearable
          @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
        <el-button @click="fresh">刷新</el-button>
      </div>

      <!-- 音乐列表 -->
      <el-table :data="musicList" v-loading="loading" style="margin-top: 20px;"
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
        <el-table-column prop="timeLength" label="时长" width="80">
          <template #default="{ row }">
            {{ formatDuration(row.timeLength) }}
          </template>
        </el-table-column>
        <el-table-column prop="listenNumb" label="播放量" width="80" />
        <el-table-column prop="activation" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.activation === 1 ? 'success' : 'danger'">
              {{ row.activation === 1 ? '正常' : '冻结' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showEditForm(row)" style="margin-right: 5px;">
              编辑
            </el-button>
            <el-button size="small" :type="row.activation === 1 ? 'danger' : 'success'" @click="toggleMusicStatus(row)">
              {{ row.activation === 1 ? '冻结' : '解冻' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 20, 50, 100]"
        :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" style="margin-top: 20px; justify-content: center;" />
    </el-card>

    <!-- 添加/编辑音乐弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="musicFormRef" :model="musicForm" :rules="formRules" label-width="80px">
        <el-form-item label="音乐ID" prop="musicId">
          <el-input v-model="musicForm.musicId" placeholder="音乐ID" readonly :disabled="true" />
        </el-form-item>
        <el-form-item label="音乐名称" prop="musicName">
          <el-input v-model="musicForm.musicName" placeholder="请输入音乐名称" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="musicForm.tags" placeholder="请输入标签（用逗号分隔）" />
        </el-form-item>
        <el-form-item label="选择音乐文件">
          <div style="display: flex; gap: 10px; align-items: center;">
            <el-upload :action="''" :before-upload="beforeMusicUpload" :http-request="customMusicUpload"
              :before-remove="beforeRemove" :auto-upload="false" :file-list="musicFileList"
              accept=".mp3,.wav,.flac,.m4a" @change="handleMusicChange" :limit="1" :on-exceed="handleExceed">
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
        <el-form-item label="音乐URL" prop="musicUrl">
          <el-input v-model="musicForm.musicUrl" placeholder=" " readonly disabled />
        </el-form-item>
        <el-form-item label="选择封面图片">
          <div style="display: flex; gap: 10px; align-items: center;">
            <el-upload :action="''" :before-upload="beforeImageUpload" :http-request="customImageUpload"
              :before-remove="beforeRemove" :auto-upload="false" :file-list="imageFileList"
              accept=".jpg,.jpeg,.png,.gif" @change="handleImageChange" :limit="1" :on-exceed="handleExceed">
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
            <el-image :src="musicForm.imageUrl" :preview-src-list="[musicForm.imageUrl]" fit="cover"
              style="width: 200px; height: 200px; border-radius: 4px;" />
          </div>
          <div v-else class="image-placeholder">
            暂无封面图片
          </div>
        </el-form-item>
        <el-form-item label="图片URL" prop="imageUrl">
          <el-input v-model="musicForm.imageUrl" placeholder=" " readonly disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import { getMusicList, freezeMusic, unfreezeMusic, addMusic, editMusic } from '@/api/admin';
import { uploadMusic, uploadImage } from '@/api/upload';
import { getUserId } from '@/utils/auth';

export default {
  name: 'MusicManagement',
  setup() {
    // 数据
    const musicList = ref([]);
    const loading = ref(false);
    const total = ref(0);
    const currentPage = ref(1);
    const pageSize = ref(5);
    const searchKeyword = ref('');

    // 弹窗相关
    const dialogVisible = ref(false);
    const dialogTitle = ref('');
    const musicFormRef = ref(null);
    const isEditMode = ref(false);

    // 音乐表单
    const musicForm = reactive({
      musicId: null,
      musicName: '',
      musicUrl: '',
      imageUrl: '',
      timeLength: null,
      listenNumb: 0,
      fromSinger: null,
      tags: '',
      activation: 0
    });

    // 上传文件列表
    const musicFileList = ref([]);
    const imageFileList = ref([]);
    // 上传结果
    const uploadResult = ref(null);
    const submitResult = ref(null);

    // 表单验证规则
    const formRules = reactive({
      musicId: [
        {
          validator: (rule, value, callback) => {
            if (isEditMode.value && (!value || value === null)) {
              callback(new Error('音乐ID不能为空'));
            } else {
              callback();
            }
          }, trigger: 'blur'
        }
      ],
      musicName: [
        { required: true, message: '请输入音乐名称', trigger: 'blur' },
        { min: 1, max: 50, message: '音乐名称长度在 1 到 50 个字符', trigger: 'blur' }
      ],
      musicUrl: [
        { required: true, message: '请上传音乐文件', trigger: 'blur' }
      ],
      imageUrl: [
        { required: true, message: '请上传封面图片', trigger: 'blur' }
      ]
    });

    // 格式化时长
    const formatDuration = (seconds) => {
      if (!seconds || isNaN(seconds)) return '00:00';
      const minutes = Math.floor(seconds / 60);
      const secs = Math.floor(seconds % 60);
      return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    };

    // 获取音乐列表
    const fetchMusicList = async () => {
      loading.value = true;

      // 添加调试信息
      console.log(`调用getMusicList API: 页码=${currentPage.value}, 每页大小=${pageSize.value}, 搜索词=${searchKeyword.value}`);

      try {
        // 使用封装好的getMusicList函数调用API
        const response = await getMusicList({
          page: currentPage.value,
          size: pageSize.value,
          name: searchKeyword.value
        });
        console.log('API响应:', response);

        // 更新数据，确保正确访问数据路径
        if (response.data?.code === 20 || response.data?.success === true) {
          // 修复数据访问路径，使用正确的层级结构
          musicList.value = response.data.data?.page?.records || [];
          total.value = response.data.data?.page?.total || 0;
          console.log(`成功获取音乐列表: 共 ${total.value} 条，当前页显示 ${musicList.value.length} 条`);
        } else {
          console.error('API返回错误:', response);
          ElMessage.error(response.data?.message || '获取音乐列表失败');
        }
      } catch (error) {
        console.error('获取音乐列表失败:', error);
        ElMessage.error('获取音乐列表失败');
      } finally {
        loading.value = false;
      }
    };

    // 搜索音乐
    const handleSearch = () => {
      currentPage.value = 1;
      fetchMusicList();
    };

    // 重置搜索
    const resetSearch = () => {
      searchKeyword.value = '';
      currentPage.value = 1;
      fetchMusicList();
    };

    // 刷新音乐列表
    const fresh = () => {
      console.log('手动刷新音乐列表...');
      fetchMusicList();
    };

    // 冻结/解冻音乐
    const toggleMusicStatus = async (music) => {
      console.log('====================================================');
      console.log('开始切换音乐状态操作：', {
        music: { ...music },
        currentTime: new Date().toISOString()
      });

      try {
        console.log('显示确认对话框：', {
          operation: music.activation === 1 ? '冻结' : '解冻',
          musicName: music.musicName,
          musicId: music.musicId
        });

        // 等待用户确认
        const confirmResult = await ElMessageBox.confirm(
          `确定要${music.activation === 1 ? '冻结' : '解冻'}音乐 "${music.musicName}" 吗？`,
          '确认操作',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        );

        console.log('用户确认结果：', confirmResult);

        let response;
        if (music.activation === 1) {
          // 冻结音乐
          // 使用封装好的freezeMusic函数
          response = await freezeMusic(music.musicId);
        } else {
          // 解冻音乐
          // 使用封装好的unfreezeMusic函数
          response = await unfreezeMusic(music.musicId);
        }

        console.log('处理API响应：', {
          response: { ...response },
          musicStatus: music.activation,
          expectedCode: [20, 200]
        });

        // 修复响应数据判断，正确访问response.data中的状态码
        if (response.data?.code === 20 || response.data?.success === true || response.code === 20) {
          console.log('操作成功，显示成功消息：', {
            message: response.data?.message || `${music.activation === 1 ? '冻结' : '解冻'}成功`,
            music: { ...music },
            newStatus: music.activation === 1 ? 0 : 1
          });

          ElMessage.success(response.data?.message || `${music.activation === 1 ? '冻结' : '解冻'}成功`);
          // 重新获取音乐列表
          console.log('准备刷新音乐列表...');
          fetchMusicList();
          console.log('刷新音乐列表完成');
        } else {
          console.log('操作失败，显示错误消息：', {
            message: response.data?.message || `${music.activation === 1 ? '冻结' : '解冻'}失败`,
            response: { ...response }
          });

          ElMessage.error(response.data?.message || `${music.activation === 1 ? '冻结' : '解冻'}失败`);
        }
      } catch (error) {
        if (error === 'cancel') {
          console.log('用户取消了操作');
        } else {
          console.error('操作失败:', {
            error: error instanceof Error ? error.message : error,
            stack: error instanceof Error ? error.stack : undefined,
            music: { ...music }
          });
          ElMessage.error('操作失败');
        }
      } finally {
        console.log('音乐状态切换操作完成:', {
          music: { ...music },
          currentTime: new Date().toISOString()
        });
      }
    };

    // 显示添加音乐表单
    const showAddForm = () => {
      isEditMode.value = false;
      dialogTitle.value = '添加音乐';
      resetMusicForm();
      dialogVisible.value = true;
    };

    // 显示编辑音乐表单
    const showEditForm = (row) => {
      isEditMode.value = true;
      dialogTitle.value = '编辑音乐';
      // 复制音乐数据到表单
      Object.assign(musicForm, row);
      dialogVisible.value = true;
    };

    // 重置音乐表单
    const resetMusicForm = () => {
      musicForm.musicId = null;
      musicForm.musicName = '';
      musicForm.musicUrl = '';
      musicForm.imageUrl = '';
      musicForm.timeLength = null;
      musicForm.fromSinger = null;
      musicForm.tags = '';
      musicForm.listenNumb = 0;
      musicForm.activation = 0;
      if (musicFormRef.value) {
        musicFormRef.value.resetFields();
      }
      // 清空文件列表
      musicFileList.value = [];
      imageFileList.value = [];
      // 清空上传结果
      uploadResult.value = null;
      submitResult.value = null;
    };

    // 处理文件超出数量限制
    const handleExceed = (files, fileList) => {
      ElMessage.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
    };

    // 移除文件前的操作
    const beforeRemove = (file, uploadFiles) => {
      return ElMessageBox.confirm(
        `确定移除 ${file.name}？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).catch(() => false);
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

    // 自定义上传函数（使用真实API）
    const customMusicUpload = async (options) => {
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
        } else if (file.raw && file.raw instanceof File) {
          actualFile = file.raw;
        } else {
          throw new Error('无效的文件对象：不是File实例且没有有效的raw属性');
        }

        // 创建进度监听器
        const progressHandler = (progressEvent) => {
          if (progressEvent.total) {
            const percentComplete = Math.round((progressEvent.loaded * 100) / progressEvent.total);
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
        const result = await uploadMusic(actualFile, progressHandler);

        // 严格验证API响应数据
        if (!result) {
          throw new Error('API返回空结果');
        }

        // 获取响应数据
        const responseData = result || {};

        // 修改：同时支持后端直接返回{url, timelength}格式和标准格式
        let success = false;
        let musicUrl = '';
        let timelength = 0;
        // 情况1: 检查是否直接返回了url和timelength（简化格式）
        if (responseData.url) {
          success = true;
          musicUrl = responseData.url;
          timelength = responseData.timelength || 0;
        }
        // 情况2: 检查是否是标准响应格式（{code, message, success, data}）
        else if ((responseData.success === true || responseData.code === 20) && responseData.data && responseData.data.url) {
          success = true;
          musicUrl = responseData.data.url;
          timelength = responseData.data.timelength || 0;
        }

        if (success) {
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

          // 更新表单中的音乐URL
          musicForm.musicUrl = musicUrl;
          // 如果返回了时长，自动填充
          if (timelength > 0) {
            musicForm.timeLength = timelength;
          }

          // 更新file对象状态和URL
          if (file) {
            file.status = 'success';
            file.url = musicUrl;
            file.percentage = 100;
          }

          // 触发成功回调
          if (onSuccess) {
            onSuccess({ status: 'success', data: musicUrl });
          }

          return Promise.resolve({ status: 'success', data: musicUrl });
        } else {
          // 上传失败的情况
          const errorMessage = responseData.message || '上传失败，API返回错误';
          throw new Error(errorMessage);
        }

      } catch (error) {
        // 设置明确的失败状态
        const errorMessage = error.message || '音乐文件上传失败';
        uploadResult.value = {
          success: false,
          message: errorMessage
        };

        // 确保清空URL，避免使用旧数据
        musicForm.musicUrl = '';

        // 更新file对象状态为失败
        if (options && options.file) {
          options.file.status = 'error';
          options.file.url = '';
          options.file.percentage = 0;
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
    };

    // 文件状态变化时的处理函数
    const handleMusicChange = (file, fileList) => {
      // 更新文件列表
      musicFileList.value = fileList;

      // 检查文件状态
      if (file.status === 'success') {
        // 确保file.url存在且不为空
        if (file.url && file.url.trim()) {
          musicForm.musicUrl = file.url;
        } else {
          musicForm.musicUrl = '';
        }
      } else if (file.status === 'error') {
        // 清空URL，避免使用可能的无效数据
        musicForm.musicUrl = '';
      } else if (file.status === 'removed') {
        // 文件被移除时清空URL
        musicForm.musicUrl = '';
      }
    };

    // 处理音乐文件上传
    const handleMusicUpload = async () => {
      // 从文件列表中获取文件
      const file = musicFileList.value[0];

      // 验证文件是否存在
      if (!file) {
        ElMessage.error('请选择音乐文件后再上传');
        return Promise.reject(new Error('未选择音乐文件'));
      }

      // 清空旧的上传结果和URL
      uploadResult.value = { success: false, message: '' };
      musicForm.musicUrl = '';

      try {
        // 调用自定义上传函数
        const result = await customMusicUpload({
          file,
          onProgress: (progress) => {
            // 进度更新会通过customMusicUpload内部处理
          },
          onSuccess: (res) => {
            console.log('音乐上传成功回调被触发:', res);
          },
          onError: (err) => {
            console.error('音乐上传错误回调被触发:', err);
          }
        });

        return result;
      } catch (error) {
        ElMessage.error(error.message || '音乐文件上传失败');
        return Promise.reject(error);
      }
    };

    // 提交表单
    const submitForm = async () => {
      if (!musicFormRef.value) return;

      await musicFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            let response;
            let musicData = { ...musicForm };

            if (isEditMode.value) {
              // 调用编辑音乐API
              console.log('调用editMusic API:', musicData);
              response = await editMusic(musicData);
            } else {
              // 调用添加音乐API
              console.log('调用addMusic API:', musicData);
              response = await addMusic(musicData);
            }

            console.log('API响应:', response);
            // 正确处理API响应格式
            if (response.data) {
              // 优先使用response.data中的数据
              const responseData = response.data;
              if (responseData.code === 20 || responseData.success === true) {
                ElMessage.success(responseData.message || (isEditMode.value ? '更新成功' : '添加成功'));
                dialogVisible.value = false;
                fetchMusicList();
              } else {
                ElMessage.error(responseData.message || (isEditMode.value ? '更新失败' : '添加失败'));
              }
            } else {
              // 兼容旧的响应格式
              if (response.code === 20 || response.success === true) {
                ElMessage.success(isEditMode.value ? '更新成功' : '添加成功');
                dialogVisible.value = false;
                fetchMusicList();
              } else {
                ElMessage.error(response.message || (isEditMode.value ? '更新失败' : '添加失败'));
              }
            }
          } catch (error) {
            console.error('操作失败:', error);
            ElMessage.error('操作失败');
          }
        } else {
          return false;
        }
      });
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

    // 自定义图片上传函数
    const customImageUpload = async (options) => {
      try {
        // 确保options参数有效
        if (!options || typeof options !== 'object') {
          throw new Error('无效的选项参数');
        }

        const { file, onProgress, onSuccess, onError } = options;

        // 确保file是有效的File对象
        let actualFile = file;
        if (file instanceof File) {
          actualFile = file;
        } else if (file.raw && file.raw instanceof File) {
          actualFile = file.raw;
        } else {
          throw new Error('无效的文件对象');
        }

        // 创建进度监听器
        const progressHandler = (progressEvent) => {
          if (progressEvent.total) {
            const percentComplete = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            if (onProgress) {
              onProgress({ percent: percentComplete });
            }
            if (file) {
              file.percentage = percentComplete;
            }
          }
        };

        // 调用真实的uploadImage API
        const result = await uploadImage(actualFile);

        // 验证API响应数据
        if (!result) {
          throw new Error('API返回空结果');
        }

        let imageUrl = '';

        // 处理不同格式的响应
        if (typeof result === 'string') {
          // 纯URL字符串
          imageUrl = result;
        } else if (result.url) {
          // 包含url字段的对象
          imageUrl = result.url;
        } else if (result.data && result.data.url) {
          // 包含data.url的对象
          imageUrl = result.data.url;
        } else if (result.data) {
          // 直接使用data字段
          imageUrl = result.data;
        } else {
          throw new Error('无法从响应中提取有效的图片URL');
        }

        // 验证提取的URL
        if (!imageUrl || typeof imageUrl !== 'string') {
          throw new Error('无法从响应中提取有效的图片URL');
        }

        // 更新表单数据
        musicForm.imageUrl = imageUrl;

        // 更新上传结果
        uploadResult.value = {
          success: true,
          message: '图片上传成功',
          url: imageUrl
        };

        // 更新file对象状态
        if (file) {
          file.status = 'success';
          file.url = imageUrl;
          file.percentage = 100;
        }

        // 调用成功回调
        if (onSuccess) {
          onSuccess({ status: 'success', data: imageUrl });
        }

        // 显示成功提示
        ElMessage.success('图片上传成功');

        return { success: true, url: imageUrl };

      } catch (error) {
        // 设置明确的失败状态
        const errorMessage = error.message || '图片文件上传失败';
        uploadResult.value = {
          success: false,
          message: errorMessage
        };

        // 确保清空URL，避免使用旧数据
        musicForm.imageUrl = '';

        // 更新file对象状态为失败
        if (options && options.file) {
          options.file.status = 'error';
          options.file.url = '';
          options.file.percentage = 0;
        }

        // 触发错误回调
        if (options && options.onError) {
          options.onError(error);
        }

        // 显示错误消息
        ElMessage.error(errorMessage);

        // 返回rejected Promise
        return Promise.reject(error);
      }
    };

    // 图片文件状态变化时的处理函数
    const handleImageChange = (file, fileList) => {
      // 更新图片文件列表
      imageFileList.value = fileList;

      // 检查文件状态
      if (file.status === 'success') {
        // 确保file.url存在且不为空
        if (file.url && file.url.trim()) {
          musicForm.imageUrl = file.url;
        } else {
          musicForm.imageUrl = '';
        }
      } else if (file.status === 'error') {
        // 清空URL，避免使用可能的无效数据
        musicForm.imageUrl = '';
      } else if (file.status === 'removed') {
        // 文件被移除时清空URL
        musicForm.imageUrl = '';
      }
    };

    // 处理图片文件上传
    const handleImageUpload = async () => {
      // 从文件列表中获取文件
      const file = imageFileList.value[0];

      // 验证文件是否存在
      if (!file) {
        ElMessage.error('请选择封面图片后再上传');
        return Promise.reject(new Error('未选择图片文件'));
      }

      // 清空旧的上传结果和URL
      uploadResult.value = { success: false, message: '' };
      musicForm.imageUrl = '';

      try {
        // 调用自定义上传函数
        const result = await customImageUpload({
          file,
          onProgress: (progress) => {
            // 进度更新会通过customImageUpload内部处理
          },
          onSuccess: (res) => {
            console.log('图片上传成功回调被触发:', res);
          },
          onError: (err) => {
            console.error('图片上传错误回调被触发:', err);
          }
        });

        return result;
      } catch (error) {
        ElMessage.error(error.message || '图片文件上传失败');
        return Promise.reject(error);
      }
    };

    // 分页大小改变
    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1;
      fetchMusicList();
    };

    // 当前页改变
    const handleCurrentChange = (page) => {
      currentPage.value = page;
      fetchMusicList();
    };

    onMounted(() => {
      fetchMusicList();
    });

    return {
      musicList,
      loading,
      total,
      currentPage,
      pageSize,
      searchKeyword,
      handleSearch,
      resetSearch,
      fresh,
      // deleteMusic,  // 移除删除功能
      toggleMusicStatus, // 保留冻结/解冻功能
      handleSizeChange,
      handleCurrentChange,
      dialogVisible,
      dialogTitle,
      musicForm,
      musicFormRef,
      formRules,
      showAddForm,
      showEditForm,
      submitForm,
      isEditMode,
      formatDuration,
      // 上传相关函数
      beforeMusicUpload,
      customMusicUpload,
      handleMusicChange,
      handleMusicUpload,
      beforeImageUpload,
      customImageUpload,
      handleImageChange,
      handleImageUpload,
      // 上传相关状态
      musicFileList,
      imageFileList,
      uploadResult
    };
  },
};
</script>

<style scoped>
.music-management-container {
  padding: 40px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-box {
  margin: 20px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.el-table {
  margin: 20px auto;
  max-width: 1400px;
  width: fit-content;
}

.dialog-footer {
  text-align: right;
}
</style>