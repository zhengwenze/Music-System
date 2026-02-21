<template>
  <div class="register-container">
    <div class="background">
      <div class="shape"></div>
      <div class="shape"></div>
    </div>
    <div class="register-box">
      <div class="register-title">
        <h3>Music Dreamer 悦享音乐</h3>
      </div>

      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="register-form">
        <label for="username">用户名</label>
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" class="glass-input" />
        </el-form-item>

        <!-- 移除姓名字段 -->

        <label for="email">邮箱</label>
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" class="glass-input" />
        </el-form-item>

        <label for="phone">手机号</label>
        <el-form-item prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" class="glass-input" />
        </el-form-item>

        <label for="password">密码</label>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password
            class="glass-input" />
        </el-form-item>

        <label for="confirmPassword">确认密码</label>
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password
            class="glass-input" />
        </el-form-item>

        <label for="role">角色</label>
        <el-form-item prop="role">
          <el-select v-model="registerForm.role" placeholder="请选择角色" class="glass-input" style="width: 100%">
            <el-option label="用户" value="user" />
            <el-option label="歌手" value="singer" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="register-button" :loading="loading" @click="handleRegister">
            注册
          </el-button>
        </el-form-item>

        <el-form-item>
          <el-row :gutter="20" class="register-row">
            <el-col :span="24" class="text-center">
              <span>已有账号? </span>
              <el-link type="primary" @click="goToLogin" class="login-link">立即登录</el-link>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <p>© 2025 Music Dreamer 悦享音乐 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { register } from '@/api/user';

const router = useRouter();
const registerFormRef = ref(null);
const loading = ref(false);

// 注册表单
const registerForm = reactive({
  username: '',
  // 移除name字段
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  role: 'user'
});

// 密码验证函数
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'));
  } else {
    if (registerForm.confirmPassword !== '') {
      registerFormRef.value.validateField('confirmPassword');
    }
    callback();
  }
};

// 确认密码验证函数
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  // 移除name验证规则
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { validator: validatePass, trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validatePass2, trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
};

// 处理注册
const handleRegister = async () => {
  try {
    await registerFormRef.value.validate();

    loading.value = true;

    try {
      // 构建注册数据对象，移除确认密码字段（后端不需要）
      // 添加角色映射：user -> 2, singer -> 1, admin -> 0
      const roleMap = {
        'user': '2',
        'singer': '1', 
        'admin': '0'
      };
      
      const registerData = {
        username: registerForm.username,
        // 移除name字段
        email: registerForm.email,
        phone: registerForm.phone,
        password: registerForm.password,
        role: roleMap[registerForm.role] || '2' // 默认用户角色
      };

      console.log('发送注册请求:', registerData);
      // 使用API发送注册请求
      const result = await register(registerData);
      console.log('注册响应数据:', result);
      console.log('注册响应data:', result.data);

      // 修复：从响应对象的data属性中获取message
      ElMessage.success(result.data?.message || '注册成功，请登录');
      router.push('/login');

    } catch (error) {
      // 请求失败
      console.error('注册请求失败:', error);
      ElMessage.error(error.message || '注册失败，请检查网络连接或联系管理员');
    } finally {
      loading.value = false;
    }

  } catch (error) {
    console.error('表单验证失败:', error);
    loading.value = false;
  }
};

// 跳转到登录页
const goToLogin = () => {
  router.push('/login');
};
</script>

<style scoped>
.register-container {
  height: 100vh;
  width: 100%;
  background-color: #ffffff;
  position: relative;
  font-family: 'Poppins', sans-serif;
  overflow: hidden;
}

.background {
  width: 430px;
  height: 520px;
  position: absolute;
  transform: translate(-50%, -50%);
  left: 50%;
  top: 50%;
  z-index: 0;
  perspective: 800px;
}

.background .shape {
  height: 200px;
  width: 200px;
  position: absolute;
  border-radius: 50%;
  transform-origin: center;
  opacity: 0.8;
}

.shape:first-child {
  background: linear-gradient(#1845ad, #23a2f6);
  left: -80px;
  top: -80px;
  animation: orbit1 15s linear infinite;
}

.shape:last-child {
  background: linear-gradient(to right, #ff512f, #f09819);
  right: -30px;
  bottom: -80px;
  animation: orbit2 15s linear infinite;
}

@keyframes orbit1 {
  0% {
    transform: rotate(0) translateX(150px) rotate(0);
  }

  100% {
    transform: rotate(360deg) translateX(150px) rotate(-360deg);
  }
}

@keyframes orbit2 {
  0% {
    transform: rotate(180deg) translateX(150px) rotate(-180deg);
  }

  100% {
    transform: rotate(-180deg) translateX(150px) rotate(180deg);
  }
}

.register-box {
  height: 560px;
  width: 450px;
  background-color: rgba(255, 255, 255, 0.8);
  position: absolute;
  transform: translate(-50%, -50%);
  top: 50%;
  left: 50%;
  border-radius: 10px;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  padding: 25px 35px;
  z-index: 1;
  display: flex;
  flex-direction: column;
}

.register-title {
  text-align: center;
  margin-bottom: 10px;
  flex-shrink: 0;
}

.register-title h3 {
  font-size: 22px;
  font-weight: 500;
  line-height: 32px;
  color: #333333;
  margin: 0;
}

.register-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  padding-right: 5px;
}

/* 自定义滚动条样式 */
.register-form::-webkit-scrollbar {
  width: 4px;
}

.register-form::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.register-form::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.register-form::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}

label {
  display: block;
  margin-top: 15px;
  margin-bottom: 3px;
  font-size: 16px;
  font-weight: 500;
  color: #333333;
}

.el-form-item {
  margin-bottom: 12px;
}

.glass-input :deep(input),
.glass-input :deep(.el-select-v2__combobox-input) {
  display: block;
  height: 45px;
  width: 100%;
  background-color: rgba(255, 255, 255, 0.8) !important;
  border-radius: 3px;
  padding: 0 10px;
  font-size: 14px;
  font-weight: 300;
  color: #333333 !important;
  border: 1px solid #000000 !important;
}

.glass-input :deep(input::placeholder) {
  color: rgba(0, 0, 0, 0.5);
}

.glass-input :deep(.el-input__wrapper),
.glass-input :deep(.el-select__wrapper) {
  background-color: transparent !important;
  box-shadow: none !important;
}

.glass-input :deep(.el-input__prefix) {
  color: #e5e5e5 !important;
}

/* 下拉菜单样式 */
.el-select :deep(.el-select-dropdown__item) {
  color: #606266;
}

.register-button {
  margin-top: 10px;
  width: 100%;
  background-color: #333333 !important;
  color: #ffffff !important;
  padding: 7px 0;
  font-size: 15px;
  font-weight: 600;
  border-radius: 5px;
  cursor: pointer;
  border: none !important;
}

.register-button:hover {
  background-color: #555555 !important;
}

.register-row {
    margin-top: 15px;
    text-align: center;
    width: 100%;
  }

  .text-center {
    text-align: center;
    color: #666666;
    font-size: 14px;
    width: 100%;
    line-height: 1;
  }

  .text-center span {
    color: #666666;
    font-size: 14px;
    display: inline-block;
    vertical-align: middle;
    line-height: 1;
    line-height: 1;
  }

  .login-link {
    color: #23a2f6 !important;
    font-weight: normal;
    font-size: 14px;
    text-decoration: none;
    display: inline-block;
    vertical-align: middle;
    line-height: 1;
    margin: 0;
    padding: 0;
  }

  .login-link:hover {
    text-decoration: underline;
    text-decoration-color: #23a2f6;
  }

  :deep(.el-link) {
    font-size: 14px;
    color: #23a2f6 !important;
    display: inline-block;
    vertical-align: middle;
    line-height: 1;
    margin: 0;
    padding: 0;
    text-decoration: none;
  }

  :deep(.el-link:hover) {
    text-decoration: underline;
    text-decoration-color: #23a2f6;
  }

.register-footer {
  text-align: center;
  color: rgba(0, 0, 0, 0.5);
  font-size: 10px;
  margin-top: 8px;
  flex-shrink: 0;
}

:deep(.el-form-item__error) {
  color: #f56c6c;
}
</style>