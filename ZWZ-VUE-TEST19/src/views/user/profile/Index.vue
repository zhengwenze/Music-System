<template>
  <div class="profile-container">
    <div class="profile-header">
      <h2>个人信息</h2>
    </div>
    <el-form ref="userForm" :model="userInfo" label-width="120px">
      <el-form-item label="用户头像">
        <el-upload class="avatar-uploader" :http-request="customAvatarUpload" :before-upload="handleBeforeAvatarUpload"
          :show-file-list="false">
          <img v-if="userInfo.image_url" :src="userInfo.image_url" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="userInfo.username"></el-input>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="userInfo.email"></el-input>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="userInfo.phone"></el-input>
      </el-form-item>
      <el-form-item label="用户角色">
        <el-input v-model="userRoleText" disabled></el-input>
      </el-form-item>
      <el-form-item label="账户状态">
        <el-input v-model="activationText" disabled></el-input>
      </el-form-item>
      <el-form-item label="注册时间">
        <el-input v-model="userInfo.createTime" disabled></el-input>
      </el-form-item>
      <el-form-item label="个人简介">
        <el-input v-model="userInfo.about" type="textarea" :rows="1" maxlength="15" show-word-limit
          placeholder="请输入个人简介"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleUpdate">保存修改</el-button>
        <el-button type="danger" @click="updatePassword">修改密码</el-button>
      </el-form-item>
    </el-form>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px" :close-on-click-modal="false">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordFormRules" label-width="100px"
        class="password-form">
        <el-form-item label="旧密码" prop="oldPassword" class="password-form-item">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" show-password
            class="password-input" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword" class="password-form-item">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password
            class="password-input" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" class="password-form-item">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" show-password
            class="password-input" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePasswordSubmit">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo, setUserImage, updatePassword as updatePasswordAPI } from '@/api/user'
import { getUserId, saveUserInfo } from '@/utils/auth'

export default {
  name: 'Profile',
  setup() {
    // 用户信息
    const userInfo = reactive({
      id: '',
      username: '',
      email: '',
      phone: '',
      image_url: '',
      createTime: '',
      updateTime: '',
      role: 2,
      activation: 0,
      about: ''
    })

    const loading = ref(false)
    const originalUserInfo = ref({})
    const passwordDialogVisible = ref(false)
    const passwordForm = ref({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    const passwordFormRef = ref(null)
    const passwordFormRules = ref({
      oldPassword: [
        { required: true, message: '请输入旧密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度应在6-20位之间', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value === passwordForm.value.oldPassword) {
              callback(new Error('新密码不能与旧密码相同'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ],
      confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== passwordForm.value.newPassword) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    })

    // 计算属性，根据角色编码显示文字描述
    const userRoleText = computed(() => {
      switch (userInfo.role) {
        case 0: return '管理员'
        case 1: return '歌手'
        case 2: return '用户'
        default: return '未知角色'
      }
    })

    // 计算属性，根据激活状态显示文字描述
    const activationText = computed(() => {
      return userInfo.activation === 1 ? '已激活' : '未激活'
    })

    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        loading.value = true
        const userId = getUserId()
        console.log('加载用户资料，userId:', userId)

        if (!userId) {
          console.error('用户ID不存在')
          ElMessage.error('用户未登录')
          return
        }

        const response = await getUserInfo(userId)

        // 正确的响应数据结构验证
        if (response && response.data && (response.data.success || response.data.code === 20) && response.data.data && response.data.data.user) {
          console.log('获取用户信息成功:', response.data.data.user)
          const userData = response.data.data.user

          // 更新用户信息，使用image_url字段
          userInfo.id = userData.id || userId
          userInfo.username = userData.username || ''
          userInfo.email = userData.email || ''
          userInfo.phone = userData.phone || ''
          userInfo.image_url = userData.image_url || ''
          userInfo.createTime = userData.createTime || ''
          userInfo.updateTime = userData.updateTime || ''
          userInfo.role = userData.role || 2
          userInfo.activation = userData.activation || 0
          userInfo.about = userData.about || ''

          // 保存原始数据用于取消操作
          originalUserInfo.value = { ...userInfo }

          // 更新本地存储的用户信息
          saveUserInfo({
            userId: userData.id,
            username: userData.username,
            role: userData.role,
            email: userData.email,
            phone: userData.phone,
            activation: userData.activation,
            image_url: userData.image_url
          })
        } else {
          console.error('获取用户信息数据结构不正确:', response)
          ElMessage.error('获取用户信息失败')
        }
      } catch (error) {
        console.error('获取用户信息异常:', error)
        ElMessage.error('获取用户信息失败，请重试')
      } finally {
        loading.value = false
      }
    }

    // 头像上传前验证
    const handleBeforeAvatarUpload = (file) => {
      const isImage = /\.(jpg|jpeg|png|gif|bmp)$/i.test(file.name)
      if (!isImage) {
        ElMessage.error('只支持上传图片格式（jpg、jpeg、png、gif、bmp）！')
        return false
      }
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        ElMessage.error('图片大小不能超过2MB！')
        return false
      }
      return true
    }

    // 头像上传处理
    const customAvatarUpload = async (options) => {
      const { file } = options
      try {
        const userId = getUserId()
        if (!userId) {
          console.error('未获取到用户ID')
          ElMessage.error('用户未登录或会话已过期')
          if (options.onError) options.onError()
          return
        }

        console.log('开始上传头像')
        // 调用setUserImage函数，它会先上传图片，然后设置用户头像
        const result = await setUserImage(file)

        console.log('setUserImage结果:', result)

        if (result && result.success) {
          // 更新本地用户信息中的头像URL
          userInfo.image_url = result.imageUrl
          ElMessage.success('头像上传成功')

          // 刷新用户信息以确保数据一致性 - 重新从服务器获取最新的用户信息
          console.log('刷新用户信息以确保头像和其他用户数据最新')
          await loadUserInfo()

          if (options.onSuccess) options.onSuccess()
        } else {
          const errorMsg = result?.message || '更新头像失败'
          ElMessage.error(errorMsg)
          console.error('更新头像失败', result)
          if (options.onError) options.onError()
        }
      } catch (error) {
        console.error('头像上传过程中出错', error)
        ElMessage.error('上传失败，请稍后重试')
        if (options.onError) options.onError(error)
      }
    }

    // 保存用户信息修改
    const handleUpdate = async () => {
      try {
        const response = await updateUserInfo({
          id: userInfo.id,
          username: userInfo.username,
          email: userInfo.email,
          phone: userInfo.phone,
          about: userInfo.about
        })

        if (response && response.data && (response.data.success || response.data.code === 20)) {
          ElMessage.success('保存成功')
          originalUserInfo.value = { ...userInfo }

          // 更新本地存储中的用户信息
          saveUserInfo({
            userId: userInfo.id,
            username: userInfo.username,
            email: userInfo.email,
            phone: userInfo.phone,
            image_url: userInfo.image_url,
            about: userInfo.about
          })
        } else {
          ElMessage.error((response && response.data && response.data.message) || '保存失败')
        }
      } catch (error) {
        console.error('更新用户信息失败', error)
        ElMessage.error('更新失败，请稍后重试')
      }
    }

    // 取消修改
    const handleCancel = () => {
      Object.assign(userInfo, originalUserInfo.value)
    }

    // 打开修改密码对话框
    const updatePassword = () => {
      // 重置表单
      passwordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      // 清空验证
      if (passwordFormRef.value) {
        passwordFormRef.value.clearValidate()
      }
      // 显示对话框
      passwordDialogVisible.value = true
    }

    // 提交修改密码表单
    const handlePasswordSubmit = async () => {
      try {
        // 验证表单
        if (!passwordFormRef.value) return

        await passwordFormRef.value.validate()

        // 调用修改密码API
        const response = await updatePasswordAPI(passwordForm.value.oldPassword, passwordForm.value.newPassword)

        // 处理响应
        if (response && response.data && (response.data.success || response.data.code === 20)) {
          ElMessage.success('密码修改成功')
          passwordDialogVisible.value = false
        } else {
          ElMessage.error((response && response.data && response.data.message) || '密码修改失败')
        }
      } catch (error) {
        // 表单验证失败
        console.error('密码修改失败:', error)
      }
    }

    // 组件挂载时加载用户信息
    onMounted(() => {
      loadUserInfo()
    })

    return {
      userInfo,
      loading,
      passwordDialogVisible,
      passwordForm,
      passwordFormRef,
      passwordFormRules,
      userRoleText,
      activationText,
      handleUpdate,
      handleCancel,
      updatePassword,
      handlePasswordSubmit,
      handleBeforeAvatarUpload,
      customAvatarUpload
    }
  }
}
</script>
<style scoped>
/* ========== 全局容器与背景 ========== */
.profile-container {
  padding: 30px;
  background: linear-gradient(180deg, #f9f9f9 0%, #f0f0f0 100%);
  min-height: 100vh;
}

/* ========== 页面标题 ========== */
.profile-header {
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  text-align: left;
  width: 100%;
}

.profile-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0;
  position: relative;
  padding-left: 15px;
}

.profile-header h2::before {
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

/* ========== 表单容器 ========== */
.el-form {
  background-color: #ffffff;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06);
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  transition: all 0.3s ease;
}

.el-form:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
}

/* ========== 头像上传区域 ========== */
.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 10px;
  position: relative;
}

.avatar {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.avatar:hover {
  transform: scale(1.03);
  box-shadow: 0 12px 32px rgba(236, 65, 65, 0.2);
  border-color: rgba(236, 65, 65, 0.3);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #c0c4cc;
  width: 160px;
  height: 160px;
  line-height: 160px;
  text-align: center;
  border: 2px dashed #e0e0e0;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.avatar-uploader:hover .avatar-uploader-icon {
  border-color: #ec4141;
  color: #ec4141;
  background-color: rgba(236, 65, 65, 0.02);
}

/* ========== 表单项样式 ========== */
.el-form-item {
  margin-bottom: 28px;
  position: relative;
}

::v-deep(.el-form-item__label) {
  font-weight: 600;
  color: #333;
  font-size: 14px;
  padding-right: 20px;
  text-align: right;
  line-height: 40px;
}

::v-deep(.el-form-item__content) {
  line-height: 40px;
}

/* ========== 输入框样式 ========== */
::v-deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  box-shadow: none;
  transition: all 0.3s ease;
  background-color: #fafafa;
  padding: 0 12px;
}

::v-deep(.el-input__wrapper:hover) {
  border-color: #b0b0b0;
  background-color: #fff;
}

::v-deep(.el-input__wrapper.is-focus) {
  border-color: #ec4141;
  box-shadow: 0 0 0 2px rgba(236, 65, 65, 0.1);
  background-color: #fff;
}

::v-deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  box-shadow: none;
  transition: all 0.3s ease;
  background-color: #fafafa;
  padding: 12px;
  font-family: inherit;
}

::v-deep(.el-textarea__inner:hover) {
  border-color: #b0b0b0;
  background-color: #fff;
}

::v-deep(.el-textarea__inner:focus) {
  border-color: #ec4141;
  box-shadow: 0 0 0 2px rgba(236, 65, 65, 0.1);
  background-color: #fff;
}

::v-deep(.el-input.is-disabled .el-input__wrapper) {
  background-color: #f5f5f5;
  color: #999;
}

/* ========== 按钮样式 ========== */
.el-form-item:last-child {
  margin-top: 40px;
  margin-bottom: 0;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

::v-deep(.el-button) {
  border-radius: 20px;
  padding: 12px 28px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  margin: 0 8px;
}

::v-deep(.el-button--primary) {
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  border: none;
}

::v-deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(236, 65, 65, 0.3);
}

::v-deep(.el-button--danger) {
  background: linear-gradient(90deg, #f56c6c 0%, #e74c3c 100%);
  border: none;
}

::v-deep(.el-button--danger:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 108, 108, 0.3);
}

::v-deep(.el-button:active) {
  transform: translateY(0);
}

/* ========== 修改密码对话框 ========== */
::v-deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.12);
}

::v-deep(.el-dialog__header) {
  background: linear-gradient(90deg, #ec4141 0%, #c62f2f 100%);
  padding: 20px 24px;
  margin-right: 0;
}

::v-deep(.el-dialog__title) {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

::v-deep(.el-dialog__headerbtn) {
  top: 20px;
}

::v-deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #fff;
}

.password-form {
  padding: 20px 10px 0;
}

.password-form-item {
  margin-bottom: 24px;
}

::v-deep(.password-form-item .el-form-item__label) {
  text-align: right;
  padding-right: 12px;
  color: #666;
  font-weight: 500;
}

::v-deep(.password-input .el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

::v-deep(.password-input .el-input__wrapper:hover) {
  border-color: #b0b0b0;
}

::v-deep(.password-input .el-input__wrapper.is-focus) {
  border-color: #ec4141;
  box-shadow: 0 0 0 2px rgba(236, 65, 65, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 20px 0 10px;
}

::v-deep(.dialog-footer .el-button) {
  min-width: 100px;
}

/* ========== 字数统计 ========== */
::v-deep(.el-input__count) {
  color: #999;
  font-size: 12px;
  background: transparent;
}

/* ========== 响应式设计 ========== */
@media (max-width: 900px) {
  .profile-container {
    padding: 20px;
  }

  .el-form {
    padding: 30px 25px;
    max-width: 100%;
  }

  .profile-header h2 {
    font-size: 24px;
  }
}

@media (max-width: 768px) {
  .profile-container {
    padding: 15px;
  }

  .el-form {
    padding: 25px 20px;
  }

  .avatar {
    width: 140px;
    height: 140px;
  }

  .avatar-uploader-icon {
    width: 140px;
    height: 140px;
    line-height: 140px;
  }

  ::v-deep(.el-form-item__label) {
    text-align: left;
    padding-bottom: 8px;
    line-height: 1.4;
  }

  .el-form-item {
    margin-bottom: 22px;
  }

  ::v-deep(.el-form-item:last-child) {
    flex-direction: column;
    gap: 12px;
  }

  ::v-deep(.el-button) {
    width: 100%;
    margin: 4px 0;
  }
}

@media (max-width: 480px) {
  .el-form {
    padding: 20px 15px;
  }

  .profile-header h2 {
    font-size: 20px;
  }

  .avatar {
    width: 120px;
    height: 120px;
  }

  .avatar-uploader-icon {
    width: 120px;
    height: 120px;
    line-height: 120px;
  }

  ::v-deep(.el-dialog) {
    width: 90% !important;
    max-width: 360px;
  }
}
</style>