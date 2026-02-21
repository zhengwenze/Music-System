<template>
  <div class="user-management-container">
    <el-card>
      <div class="card-header">
        <h2>歌手管理</h2>
        <el-button type="primary" @click="showAddForm">添加歌手</el-button>
      </div>

      <!-- 搜索区域 -->
      <div class="search-box">
        <el-input v-model="searchKeyword" placeholder="请输入歌手名进行搜索" style="width: 300px; margin-right: 10px;" clearable
          @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
        <el-button @click="fresh">刷新</el-button>
      </div>

      <!-- 用户列表 -->
      <el-table :data="userList" v-loading="loading" style="width: 100%; margin-top: 20px;"
        :default-sort="{ prop: 'id', order: 'ascending' }">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column label="用户头像" width="150">
          <template #default="{ row }">
            <el-image v-if="row.imageUrl" :src="row.imageUrl" :preview-src-list="[row.imageUrl]" fit="cover"
              style="width: 80px; height: 80px; border-radius: 4px;" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="role" label="角色" :formatter="formatRole" width="80" />
        <el-table-column prop="activation" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.activation === 1 ? 'success' : 'danger'">
              {{ row.activation === 1 ? '正常' : '冻结' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showEditForm(row)" style="margin-right: 5px;">
              编辑
            </el-button>
            <el-button size="small" :type="row.activation === 1 ? 'danger' : 'success'" @click="toggleUserStatus(row)"
              style="margin-right: 5px;">
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

    <!-- 添加/编辑用户弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="userFormRef" :model="userForm" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" type="email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <!-- 添加密码字段，仅在添加用户时显示 -->
        <el-form-item v-if="!isEditMode" label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <!-- 添加确认密码字段，仅在添加用户时显示 -->
        <el-form-item v-if="!isEditMode" label="确认密码" prop="confirmPassword">
          <el-input v-model="userForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
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
// import { pageUser, freezeUser, unfreezeUser, deleteUser as deleteUserApi } from '@/api/admin';
import { updateUserInfoByAdmin } from '@/api/admin';
import { register } from '@/api/user';
import { Mod_admin_service } from '@/api/request';

export default {
  name: 'UserManagement',
  setup() {
    // 数据
    const userList = ref([]);
    const loading = ref(false);
    const total = ref(0);
    const currentPage = ref(1);
    const pageSize = ref(5);
    const searchKeyword = ref('');

    // 弹窗相关
    const dialogVisible = ref(false);
    const dialogTitle = ref('');
    const userFormRef = ref(null);
    const isEditMode = ref(false);

    // 用户表单
    const userForm = reactive({
      id: null,
      username: '',
      email: '',
      phone: '',
      password: '',
      confirmPassword: '',
      role: '2', // 默认用户角色
      activation: '1' // 默认正常状态
    });

    // 密码验证函数
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (userForm.confirmPassword !== '') {
          userFormRef.value.validateField('confirmPassword');
        }
        callback();
      }
    };

    // 确认密码验证函数
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== userForm.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };

    // 表单验证规则
    const formRules = reactive({
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
      ],
      password: [
        { required: true, validator: validatePass, trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, validator: validatePass2, trigger: 'blur' }
      ]
    });

    // 格式化角色显示
    const formatRole = (row) => {
      const roleMap = {
        '0': '管理员',
        '1': '歌手',
        '2': '用户'
      };
      return roleMap[row.role] || '未知';
    };

    // 获取用户列表fetchUserList
    const fetchUserList = async () => {
      loading.value = true;
      try {
        // 不准改API路径
        // 获取足够多的数据来确保前端过滤后有足够的用户角色数据进行重新分页
        // 假设我们获取5倍于页面大小的数据，这样应该足够了
        const fetchSize = pageSize.value * 5;
        const url = `/pageUser/${1}/${fetchSize}`; // 总是从第一页开始获取，获取足够多的数据
        const response = await Mod_admin_service.get(url, {
          params: {
            keyword: searchKeyword.value || ''
          }
        });
        // 更新数据，适配返回的数据结构
        if (response.data.code === 20 || response.data.success === true) {
          // 添加详细日志以便调试，查看实际数据结构
          console.log('API响应完整数据:', response);

          // 安全地访问数据，尝试多种可能的路径
          let allUsers = [];
          if (response.data.page && response.data.page.records) {
            allUsers = response.data.page.records;
            console.log('通过 response.data.page.records 获取数据');
          } else if (response.data.data && response.data.data.page && response.data.data.page.records) {
            allUsers = response.data.data.page.records;
            console.log('通过 response.data.data.page.records 获取数据');
          } else if (response.data.records) {
            allUsers = response.data.records;
            console.log('通过 response.data.records 获取数据');
          } else if (Array.isArray(response.data)) {
            allUsers = response.data;
            console.log('直接使用response.data作为数组');
          } else if (response.data.list) {
            allUsers = response.data.list;
            console.log('通过response.data.list获取数据');
          } else if (response.data.data && Array.isArray(response.data.data)) {
            allUsers = response.data.data;
            console.log('通过response.data.data获取数据');
          }

          // 只显示角色为歌手(role为1)的数据，添加容错处理
          const filteredUsers = allUsers.filter(user => user && user.role === 1);
          console.log('过滤后的歌手列表:', filteredUsers);

          // 设置总数为过滤后的总数
          total.value = filteredUsers.length;
          console.log('过滤后的总数:', total.value);

          // 计算当前页的数据范围
          const startIndex = (currentPage.value - 1) * pageSize.value;
          const endIndex = startIndex + pageSize.value;

          // 从过滤后的数据中截取当前页数据
          userList.value = filteredUsers.slice(startIndex, endIndex);
          console.log(`当前页数据: 第${currentPage.value}页，显示${userList.value.length}条，范围[${startIndex},${endIndex})`);

          // 如果当前页没有数据但总数大于0，自动调整到最后一页
          if (userList.value.length === 0 && total.value > 0) {
            const lastPage = Math.ceil(total.value / pageSize.value);
            if (lastPage > 0 && lastPage !== currentPage.value) {
              console.log(`当前页(${currentPage.value})无数据，自动调整到第${lastPage}页`);
              currentPage.value = lastPage;
              // 递归调用获取最后一页数据
              await fetchUserList();
              return; // 防止后续执行
            }
          }
        } else {
          ElMessage.error(response.data.message || '获取歌手列表失败');
        }
      } catch (error) {
        console.error('获取歌手列表失败:', error);
        ElMessage.error('获取歌手列表失败');
      } finally {
        loading.value = false;
      }
    };

    // 搜索用户
    const handleSearch = () => {
      currentPage.value = 1;
      fetchUserList();
    };

    // 重置搜索
    const resetSearch = () => {
      searchKeyword.value = '';
      currentPage.value = 1;
      fetchUserList();
    };

    // 刷新用户列表
    const fresh = () => {
      console.log('手动刷新用户列表...');
      fetchUserList();
    };

    // 切换用户状态（冻结/解冻）
    const toggleUserStatus = async (user) => {
      console.log('====================================================');
      console.log('开始切换用户状态操作：', {
        user: { ...user },
        currentTime: new Date().toISOString()
      });

      try {
        console.log('显示确认对话框：', {
          operation: user.activation === 1 ? '冻结' : '解冻',
          username: user.username,
          userId: user.id
        });

        // 等待用户确认
        const confirmResult = await ElMessageBox.confirm(
          `确定要${user.activation === 1 ? '冻结' : '解冻'}用户 "${user.username}" 吗？`,
          '确认操作',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        );

        console.log('用户确认结果：', confirmResult);

        let response;
        if (user.activation === 1) {
          // 冻结用户
          console.log('准备调用冻结用户API：', { id: user.id });
          // 使用Mod_admin_service直接调用API
          response = await Mod_admin_service.post('/freezeUser', null, {
            params: { id: user.id }
          });
          console.log('冻结用户API调用完成，返回结果：', response);
        } else {
          // 解冻用户
          console.log('准备调用解冻用户API：', { id: user.id });
          // 使用Mod_admin_service直接调用API
          response = await Mod_admin_service.post('/unfreezeUser', null, {
            params: { id: user.id }
          });
          console.log('解冻用户API调用完成，返回结果：', response);
        }

        console.log('处理API响应：', {
          response: { ...response },
          userStatus: user.activation,
          expectedCode: [20, 200]
        });

        if (response.data?.code === 200 || response.data?.success === true || response.data?.code === 20) {
          console.log('操作成功，显示成功消息：', {
            message: response.message || `${user.activation === 1 ? '冻结' : '解冻'}成功`,
            user: { ...user },
            newStatus: user.activation === 1 ? 0 : 1
          });

          ElMessage.success(response.data?.message || `${user.activation === 1 ? '冻结' : '解冻'}成功`);
          // 重新获取用户列表
          console.log('准备刷新用户列表...');
          fetchUserList();
          console.log('刷新用户列表完成');
        } else {
          console.log('操作失败，显示错误消息：', {
            message: response.data?.message || `${user.activation === 1 ? '冻结' : '解冻'}失败`,
            response: { ...response }
          });

          ElMessage.error(response.data?.message || `${user.activation === 1 ? '冻结' : '解冻'}失败`);
        }
      } catch (error) {
        if (error === 'cancel') {
          console.log('用户取消了操作');
        } else {
          console.error('操作失败:', {
            error: error instanceof Error ? error.message : error,
            stack: error instanceof Error ? error.stack : undefined,
            user: { ...user }
          });
          ElMessage.error('操作失败');
        }
      } finally {
        console.log('用户状态切换操作完成:', {
          user: { ...user },
          currentTime: new Date().toISOString()
        });
      }
    };

    // 显示添加歌手表单
    const showAddForm = () => {
      isEditMode.value = false;
      dialogTitle.value = '添加歌手';
      resetUserForm();
      userForm.role = '1'; // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
      dialogVisible.value = true;
    };

    // 显示编辑歌手表单
    const showEditForm = (row) => {
      isEditMode.value = true;
      dialogTitle.value = '编辑歌手';
      Object.assign(userForm, row);
      console.log('编辑表单时设置isEditMode为true，用户ID为:', userForm.id);
      dialogVisible.value = true;
    };

    // 重置用户表单
    const resetUserForm = () => {
      userForm.id = null;
      userForm.username = '';
      userForm.email = '';
      userForm.phone = '';
      userForm.password = '';
      userForm.confirmPassword = '';
      userForm.role = '1'; // 使用字符串类型，与用户管理页面保持一致
      userForm.activation = '1'; // 使用正确的激活状态字段，使用字符串类型
      if (userFormRef.value) {
        userFormRef.value.resetFields();
      }
    };

    // 提交表单
    const submitForm = async () => {
      if (!userFormRef.value) return;
      // 动态设置密码字段的验证规则，只有在添加用户时才需要密码
      if (isEditMode.value) {
        // 编辑模式下移除密码验证
        formRules.password = [];
        formRules.confirmPassword = [];
      } else {
        // 添加模式下恢复密码验证
        formRules.password = [
          { required: true, validator: validatePass, trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ];
        formRules.confirmPassword = [
          { required: true, validator: validatePass2, trigger: 'blur' }
        ];
      }
      await userFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            let response;
            let userData = { ...userForm };
            console.log('提交表单时isEditMode的值:', isEditMode.value);
            console.log('提交的数据:', userData);
            // 双重判断：isEditMode或userForm.id存在都认为是编辑模式
            const isActuallyEditMode = isEditMode.value || userData.id;
            if (isActuallyEditMode) {
              // 编辑用户时移除密码字段
              // 编辑用户时移除密码字段
              delete userData.password;
              delete userData.confirmPassword;
              // 更新用户信息 - 使用管理员专用接口，必须传入目标用户ID
              // 更新用户信息 - 使用管理员专用接口，必须传入目标用户ID
              // 更新用户信息 - 使用管理员专用接口，必须传入目标用户ID
              // 更新用户信息 - 使用管理员专用接口，必须传入目标用户ID
              console.log('调用更新接口');
              response = await updateUserInfoByAdmin(userData.id, userData);
            } else {
              // 添加歌手其实就是注册用户，默认了角色为歌手(role: 1)
              // 构建注册数据对象，移除确认密码字段（后端不需要）
              const registerData = {
                username: userForm.username,
                email: userForm.email,
                phone: userForm.phone,
                password: userForm.password,
                // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
                // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
                // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
                // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
                // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
                // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
                // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
                // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
                // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
                // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
                role: '1' // 设置为歌手角色，与用户管理页面保持一致，使用字符串类型
              };

              console.log('发送注册请求:', registerData);
              // 使用注册接口添加用户，与用户管理页面保持一致
              response = await register(registerData);
            }
            console.log('响应对象:', response);
            if (response.data?.code === 20 || response.data?.success === true) {
              ElMessage.success(response.data?.message || (isEditMode.value ? '更新成功' : '添加成功'));
              dialogVisible.value = false;
              fetchUserList();
            } else {
              ElMessage.error(response.data?.message || (isEditMode.value ? '更新失败' : '添加失败'));
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

    // 删除用户
    const deleteUser = async (user) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除用户 "${user.username}" 吗？此操作不可恢复！`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'error',
          }
        );

        const response = await Mod_admin_service.post('/deleteUser', null, {
          params: { userId: user.id }
        });

        if (response.code === 200 || response.success === true) {
          ElMessage.success(response.message || '删除成功');
          fetchUserList();
        } else {
          ElMessage.error(response.message || '删除失败');
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error);
          ElMessage.error('删除失败');
        }
      }
    };

    // 分页大小改变
    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1;
      fetchUserList();
    };

    // 当前页改变
    const handleCurrentChange = (page) => {
      currentPage.value = page;
      fetchUserList();
    };

    onMounted(() => {
      fetchUserList();
    });

    return {
      userList,
      loading,
      total,
      currentPage,
      pageSize,
      searchKeyword,
      formatRole,
      handleSearch,
      resetSearch,
      fresh,
      toggleUserStatus,
      handleSizeChange,
      handleCurrentChange,
      dialogVisible,
      dialogTitle,
      userForm,
      userFormRef,
      formRules,
      showAddForm,
      showEditForm,
      submitForm,
      deleteUser,
      isEditMode
    };
  },
};
</script>

<style scoped>
/* 网易云音乐风格样式修改 */
.user-management-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 卡片样式 - 白色背景，圆角，阴影 */
.el-card {
  border-radius: 12px !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
  background-color: #ffffff;
  transition: all 0.3s ease;
  padding: 20px !important;
}

.el-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12) !important;
}

/* 卡片标题区域 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
  position: relative;
  padding-left: 12px;
}

.card-header h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background-color: #ec4141;
  border-radius: 2px;
}

/* 添加歌手按钮 */
:deep(.card-header .el-button--primary) {
  background-color: #ec4141 !important;
  border-color: #ec4141 !important;
  border-radius: 20px !important;
  padding: 8px 20px !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
}

:deep(.card-header .el-button--primary:hover) {
  background-color: #d63030 !important;
  border-color: #d63030 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(236, 65, 65, 0.2) !important;
}

/* 搜索区域 */
.search-box {
  margin: 20px 0 30px 0;
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

:deep(.search-box .el-input__wrapper) {
  border-radius: 20px !important;
  border: 1px solid #e0e0e0 !important;
  box-shadow: none !important;
  padding: 0 20px !important;
  height: 40px !important;
  background-color: #fafafa !important;
}

:deep(.search-box .el-input__wrapper:hover) {
  border-color: #c0c0c0 !important;
  background-color: #fff !important;
}

:deep(.search-box .el-input__wrapper.is-focus) {
  border-color: #ec4141 !important;
  box-shadow: 0 0 0 2px rgba(236, 65, 65, 0.1) !important;
  background-color: #fff !important;
}

:deep(.search-box .el-input__inner) {
  color: #333 !important;
  font-size: 14px !important;
}

.search-box .el-button {
  border-radius: 20px !important;
  padding: 8px 20px !important;
}

/* 表格样式 */
.el-table {
  margin: 20px 0;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #f0f0f0 !important;
}

:deep(.el-table__header) {
  background-color: #fafafa !important;
}

:deep(.el-table th) {
  background-color: #fafafa !important;
  color: #333 !important;
  font-weight: 600 !important;
  font-size: 14px !important;
  border-bottom: 1px solid #f0f0f0 !important;
  height: 50px !important;
}

:deep(.el-table tr) {
  background-color: #fff;
  transition: background-color 0.2s ease;
}

:deep(.el-table td) {
  border-bottom: 1px solid #f0f0f0 !important;
  color: #333 !important;
  font-size: 14px !important;
  padding: 12px 0 !important;
  height: 60px !important;
}

:deep(.el-table .cell) {
  color: #333 !important;
  line-height: 1.5 !important;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafafa !important;
}

/* 表格行悬停效果 */
:deep(.el-table tbody tr:hover>td) {
  background-color: #fff5f5 !important;
}

/* 表格图片样式 */
:deep(.el-table .el-image) {
  border-radius: 6px !important;
  transition: transform 0.3s ease;
}

:deep(.el-table .el-image:hover) {
  transform: scale(1.05);
}

/* 标签样式 */
:deep(.el-tag) {
  border-radius: 12px !important;
  border: none !important;
  font-weight: 500 !important;
  font-size: 12px !important;
  padding: 4px 12px !important;
}

:deep(.el-tag--success) {
  background-color: rgba(76, 175, 80, 0.1) !important;
  color: #4caf50 !important;
}

:deep(.el-tag--danger) {
  background-color: rgba(236, 65, 65, 0.1) !important;
  color: #ec4141 !important;
}

/* 按钮样式 */
:deep(.el-button) {
  border-radius: 20px !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
  border: none !important;
  height: 32px !important;
  padding: 0 16px !important;
}

:deep(.el-button--primary) {
  background-color: #ec4141 !important;
  border-color: #ec4141 !important;
  color: #fff !important;
}

:deep(.el-button--primary:hover) {
  background-color: #d63030 !important;
  border-color: #d63030 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(236, 65, 65, 0.2) !important;
}

:deep(.el-button--primary:active) {
  background-color: #c02525 !important;
  transform: translateY(0);
}

:deep(.el-button--success) {
  background-color: #4caf50 !important;
  border-color: #4caf50 !important;
}

:deep(.el-button--success:hover) {
  background-color: #43a047 !important;
  border-color: #43a047 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.2) !important;
}

:deep(.el-button--danger) {
  background-color: #ff6b6b !important;
  border-color: #ff6b6b !important;
}

:deep(.el-button--danger:hover) {
  background-color: #ff5252 !important;
  border-color: #ff5252 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(255, 107, 107, 0.2) !important;
}

:deep(.el-button--default) {
  background-color: #f0f0f0 !important;
  color: #333 !important;
}

:deep(.el-button--default:hover) {
  background-color: #e0e0e0 !important;
  color: #333 !important;
  transform: translateY(-1px);
}

/* 分页样式 */
.el-pagination {
  margin-top: 30px !important;
  justify-content: center !important;
}

:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 6px !important;
  margin: 0 4px !important;
  color: #333 !important;
  background-color: #fff !important;
  border: 1px solid #e0e0e0 !important;
  min-width: 36px !important;
  height: 36px !important;
  line-height: 34px !important;
}

:deep(.el-pagination.is-background .el-pager li:hover) {
  color: #ec4141 !important;
  border-color: #ec4141 !important;
}

:deep(.el-pagination.is-background .el-pager li.active) {
  background-color: #ec4141 !important;
  border-color: #ec4141 !important;
  color: #fff !important;
  font-weight: 600 !important;
}

:deep(.el-pagination button) {
  border-radius: 6px !important;
  border: 1px solid #e0e0e0 !important;
  background-color: #fff !important;
  min-width: 36px !important;
  height: 36px !important;
}

:deep(.el-pagination button:hover) {
  color: #ec4141 !important;
  border-color: #ec4141 !important;
}

:deep(.el-pagination .el-input__wrapper) {
  border-radius: 6px !important;
  height: 36px !important;
}

:deep(.el-pagination__total) {
  color: #666 !important;
  font-size: 14px !important;
}

/* 弹窗样式 */
:deep(.el-dialog) {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15) !important;
  background-color: #fff !important;
}

:deep(.el-dialog__header) {
  padding: 20px 24px !important;
  border-bottom: 1px solid #f0f0f0 !important;
  background-color: #fff !important;
  margin: 0 !important;
}

:deep(.el-dialog__title) {
  color: #333 !important;
  font-weight: 600 !important;
  font-size: 18px !important;
}

:deep(.el-dialog__body) {
  padding: 24px !important;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px !important;
  border-top: 1px solid #f0f0f0 !important;
  background-color: #fafafa !important;
  margin: 0 !important;
}

/* 表单样式 */
:deep(.el-form-item) {
  margin-bottom: 20px !important;
}

:deep(.el-form-item__label) {
  color: #333 !important;
  font-weight: 500 !important;
  font-size: 14px !important;
  height: auto !important;
  line-height: 1.5 !important;
  padding-bottom: 8px !important;
}

:deep(.el-input__wrapper) {
  border-radius: 6px !important;
  border: 1px solid #e0e0e0 !important;
  box-shadow: none !important;
  padding: 0 12px !important;
  background-color: #fff !important;
}

:deep(.el-input__wrapper:hover) {
  border-color: #c0c0c0 !important;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #ec4141 !important;
  box-shadow: 0 0 0 2px rgba(236, 65, 65, 0.1) !important;
}

:deep(.el-input__inner) {
  color: #333 !important;
  font-size: 14px !important;
}

/* 密码输入框样式 */
:deep(.el-input--password .el-input__wrapper) {
  padding-right: 40px !important;
}

:deep(.el-input__suffix) {
  color: #999 !important;
}

:deep(.el-input__suffix .el-icon) {
  cursor: pointer !important;
  transition: color 0.2s ease !important;
}

:deep(.el-input__suffix .el-icon:hover) {
  color: #ec4141 !important;
}

/* 对话框底部按钮 */
.dialog-footer {
  text-align: right;
}

:deep(.dialog-footer .el-button) {
  margin-left: 10px !important;
}

/* 滚动条样式 - 红色滚动条 */
:deep(::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

:deep(::-webkit-scrollbar-track) {
  background-color: #f5f5f5;
  border-radius: 4px;
}

:deep(::-webkit-scrollbar-thumb) {
  background-color: #ec4141;
  border-radius: 4px;
}

:deep(::-webkit-scrollbar-thumb:hover) {
  background-color: #d63030;
}

/* 加载动画样式 */
:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.9) !important;
  border-radius: 8px !important;
}

:deep(.el-loading-spinner .path) {
  stroke: #ec4141 !important;
}

/* 删除确认对话框样式 */
:deep(.el-message-box) {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15) !important;
  background-color: #fff !important;
}

:deep(.el-message-box__header) {
  padding: 20px 24px !important;
  border-bottom: 1px solid #f0f0f0 !important;
  background-color: #fff !important;
}

:deep(.el-message-box__title) {
  color: #333 !important;
  font-weight: 600 !important;
  font-size: 16px !important;
}

:deep(.el-message-box__content) {
  padding: 24px !important;
  color: #666 !important;
  font-size: 14px !important;
}

:deep(.el-message-box__btns) {
  padding: 16px 24px !important;
  border-top: 1px solid #f0f0f0 !important;
  background-color: #fafafa !important;
}

/* 警告按钮样式 */
:deep(.el-message-box__btns .el-button--default) {
  background-color: #f0f0f0 !important;
  color: #333 !important;
}

:deep(.el-message-box__btns .el-button--primary) {
  background-color: #ec4141 !important;
  border-color: #ec4141 !important;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .user-management-container {
    padding: 12px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .search-box {
    flex-direction: column;
    align-items: stretch;
  }
  
  :deep(.search-box .el-input) {
    width: 100% !important;
    margin-right: 0 !important;
    margin-bottom: 10px;
  }
  
  .search-box .el-button {
    width: 100%;
    margin: 5px 0;
  }
  
  :deep(.el-table) {
    font-size: 12px !important;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 6px 0 !important;
  }
}

/* 移动端优化 */
@media (max-width: 576px) {
  :deep(.el-dialog) {
    width: 90% !important;
    margin: 20px auto !important;
  }
  
  :deep(.el-form-item__label) {
    text-align: left !important;
    padding-right: 0 !important;
    width: 100% !important;
    display: block !important;
  }
  
  :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }
}

/* 动画效果增强 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-management-container {
  animation: fadeIn 0.3s ease-out;
}

/* 状态标签动画 */
:deep(.el-tag) {
  transition: all 0.2s ease;
}

:deep(.el-tag:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 操作按钮组样式 */
:deep(.el-table .el-button + .el-button) {
  margin-left: 6px !important;
}

/* 空状态样式 */
:deep(.el-table__empty-block) {
  padding: 40px 0;
  color: #999;
  font-size: 14px;
}

:deep(.el-table__empty-text) {
  color: #999 !important;
  font-size: 14px !important;
}

/* 角色格式化样式 */
.role-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.role-badge.admin {
  background-color: rgba(236, 65, 65, 0.1);
  color: #ec4141;
}

.role-badge.singer {
  background-color: rgba(76, 175, 80, 0.1);
  color: #4caf50;
}

.role-badge.user {
  background-color: rgba(33, 150, 243, 0.1);
  color: #2196f3;
}

/* 操作列样式优化 */
:deep(.el-table .el-button--small) {
  padding: 4px 12px !important;
  height: 28px !important;
  font-size: 12px !important;
}

/* 搜索按钮组样式优化 */
.search-box .el-button--primary {
  background-color: #ec4141 !important;
  border-color: #ec4141 !important;
}

.search-box .el-button--default {
  background-color: #f0f0f0 !important;
  color: #333 !important;
}

/* 添加歌手按钮悬停效果 */
.card-header .el-button--primary:active {
  background-color: #c02525 !important;
  transform: translateY(0);
}

/* 表格图片占位符样式 */
:deep(.el-table .cell span:empty::before) {
  content: '-';
  color: #999;
}
</style>