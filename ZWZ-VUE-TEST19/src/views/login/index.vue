<template>
  <div class="login-container">
    <div class="background">
      <div class="shape"></div>
      <div class="shape"></div>
    </div>
    <div class="login-box">
      <div class="login-title">
        <h3>Music Dreamer 悦享音乐</h3>
      </div>

      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form" @keyup.enter="handleLogin">
        <label for="username">用户名</label>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" class="glass-input" />
        </el-form-item>

        <label for="password">密码</label>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password
            class="glass-input" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>

        <el-form-item>
          <el-row :gutter="20" class="register-row">
            <el-col :span="24" class="text-center">
              <span>还没有账号? </span>
              <el-link type="primary" @click="goToRegister" class="register-link">立即注册</el-link>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>© 2025 Music Dreamer 悦享音乐 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { login } from '@/api/user';
import { saveUserInfo, debugAuthInfo } from '@/utils/auth';

const router = useRouter();
const loginFormRef = ref(null);
const loading = ref(false);

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
});

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
};

// 处理登录
const handleLogin = async () => {
  try {
    await loginFormRef.value.validate();

    loading.value = true;

    try {
      // 使用API登录
      console.log('发送登录请求:', {
        username: loginForm.username,
        password: loginForm.password
      });

      const response = await login({
        username: loginForm.username,
        password: loginForm.password
      });

      console.log('=== 登录响应完整数据 ===');
      console.log('完整响应:', JSON.stringify(response, null, 2));
      console.log('response.data:', response.data);
      console.log('response.data.data:', response.data?.data);
      console.log('response.data.data.user:', response.data?.data?.user);

      const result = response.data || response;

      console.log('=== 处理后的登录结果 ===');
      console.log('result:', result);
      console.log('result.code:', result.code);
      console.log('result.success:', result.success);
      console.log('result.data:', result.data);

      // 获取响应数据（注意：新的响应格式中code为20，success为true）
      if ((result.code === 20 || result.success === true) && result.data) {
        // 重要：根据后端接口文档，用户信息嵌套在data.user对象中
        const userData = result.data.user || {};

        console.log('=== 登录成功，用户数据详情 ===');
        console.log('完整用户数据:', JSON.stringify(userData, null, 2));
        console.log('用户ID:', userData.id);
        console.log('用户名:', userData.username);
        console.log('邮箱:', userData.email);
        console.log('角色:', userData.role);
        console.log('简介:', userData.about);

        // 登陆时，检查用户是否被冻结
        // 登陆时，检查用户是否被冻结
        // 登陆时，检查用户是否被冻结
        if (userData.activation === 0) {
          console.log('用户已被冻结:', userData.username);
          ElMessage.error('该账户已被冻结！');
          return;
        }

        // 保存用户id和其他信息，而不是token
        saveUserInfo({
          userId: userData.id,
          username: userData.username,
          role: userData.role !== undefined ? userData.role.toString() : '2', // 保存数字角色编码，默认为普通用户角色
          name: userData.username,
          email: userData.email || '',
          phone: userData.phone || ''
        });

        // 调试日志
        console.log('=== 保存的用户信息 ===');
        console.log('保存的用户ID:', userData.id);
        console.log('保存的用户名:', userData.username);
        console.log('保存的角色:', userData.role !== undefined ? userData.role.toString() : '2');
        console.log('保存的邮箱:', userData.email || '');
        console.log('保存的电话:', userData.phone || '');

        // 检查认证信息是否正确保存
        const authStatus = debugAuthInfo();
        if (!authStatus.isComplete) {
          console.error('认证信息保存不完整，尝试重新保存');
          // 强制保存所有字段，包括可能为空的值
          saveUserInfo({
            userId: userData.id,
            username: userData.username,
            role: userData.role !== undefined ? userData.role.toString() : '2', // 数字角色编码，默认为普通用户角色
            name: userData.username,
            email: userData.email || '',
            phone: userData.phone || ''
          });
          // 再次检查
          debugAuthInfo();
        }

        ElMessage.success(result.message || '登录成功');

        // 延迟跳转，确保信息已保存
        setTimeout(() => {
          // 跳转到对应角色的首页，使用Vue Router
          let redirectPath = '/login'; // 默认路径

          // 确保角色是数字类型进行比较
          const roleNumber = userData.role !== undefined ? parseInt(userData.role) : 2; // 默认为普通用户角色

          if (roleNumber === 0) {
            redirectPath = '/admin/profile';
          } else if (roleNumber === 1) {
            redirectPath = '/singer/profile';
          } else if (roleNumber === 2) {
            redirectPath = '/user/profile';
          }

          console.log('根据角色重定向:', userData.role, '->', redirectPath);
          router.push(redirectPath);
        }, 500);
      } else {
        console.error('登录响应格式异常或登录失败');
        ElMessage.error(result.message || '登录失败，请检查用户名和密码');
      }

    } catch (error) {
      // 请求失败
      console.error('登录请求失败:', error);
      // 捕获TypeError和其他可能的错误
      if (error instanceof TypeError) {
        ElMessage.error('登录处理出错，请稍后重试');
      } else {
        ElMessage.error(error.message || '登录失败，请检查网络连接或联系管理员');
      }
    } finally {
      loading.value = false;
    }

  } catch (error) {
    console.error('表单验证失败:', error);
    // 提供更具体的表单验证错误提示
    if (error && error.password && error.password.length > 0) {
      ElMessage.error('密码验证失败：' + error.password[0]);
    } else if (error && error.username && error.username.length > 0) {
      ElMessage.error('用户名验证失败：' + error.username[0]);
    } else {
      ElMessage.error('表单验证失败，请检查输入');
    }
    loading.value = false;
  }
};

// 跳转到注册页
const goToRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
.login-container {
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
}

.shape:first-child {
  background: linear-gradient(#1845ad, #23a2f6);
  left: -80px;
  top: -80px;
  animation: orbit1 15s linear infinite;
  opacity: 0.8;
}

.shape:last-child {
  background: linear-gradient(to right, #ff512f, #f09819);
  right: -30px;
  bottom: -80px;
  animation: orbit2 15s linear infinite;
  opacity: 0.8;
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

.login-box {
  height: 420px;
  width: 400px;
  background-color: rgba(255, 255, 255, 0.8);
  position: absolute;
  transform: translate(-50%, -50%);
  top: 50%;
  left: 50%;
  border-radius: 10px;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 2px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 0 40px rgba(0, 0, 0, 0.1);
  padding: 30px 35px;
  z-index: 1;
}

.login-form {
  width: 100%;
  margin: 0 auto;
}

.login-title {
  text-align: center;
  margin-bottom: 15px;
}

.login-title h3 {
  font-size: 26px;
  font-weight: 500;
  line-height: 38px;
  color: #333333;
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

.glass-input :deep(input) {
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

.glass-input :deep(.el-input__wrapper) {
  background-color: transparent !important;
  box-shadow: none !important;
}

.glass-input :deep(.el-input__prefix) {
  color: #e5e5e5 !important;
}

.login-button {
  margin-top: 20px;
  width: 100%;
  background-color: #ffffff !important;
  color: #080710 !important;
  padding: 10px 0;
  font-size: 18px;
  font-weight: 600;
  border-radius: 5px;
  cursor: pointer;
  border: none !important;
}

.login-button:hover {
  background-color: #f0f0f0 !important;
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
}

.register-link {
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

.register-link:hover {
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

.login-footer {
  text-align: center;
  color: #000000;
  font-size: 12px;
  position: absolute;
  bottom: 15px;
  left: 0;
  right: 0;
}

:deep(.el-form-item__error) {
  color: #f56c6c;
}
</style>