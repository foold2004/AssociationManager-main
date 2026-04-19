<template>
  <div class="login-shell">
    <div class="login-shell__backdrop" :style="backdropStyle"></div>
    <div class="login-shell__content">
      <section class="login-brand">
        <div class="login-brand__badge">社团管理系统</div>
        <h1>统一到一个更清晰的管理界面</h1>
        <p>
          新版前端将登录、工作台、社团管理与费用管理优先迁入 Vue 3 + Arco 体系，
          让信息密度、布局边界和操作效率真正稳定下来。
        </p>

        <div class="login-demo">
          <div class="login-demo__header">
            <div>
              <div class="login-demo__title">演示账号</div>
              <div class="login-demo__hint">点击即可快速填充登录账号</div>
            </div>
            <a-button size="small" @click="loadDemoAccounts" :loading="demoLoading">刷新账号</a-button>
          </div>

          <div class="login-demo__list">
            <button
              v-for="account in demoAccounts"
              :key="account.id || account.userName"
              type="button"
              class="login-demo__item"
              @click="fillAccount(account)"
            >
              <span class="login-demo__role">{{ account.roleLabel }}</span>
              <strong>{{ account.name }}</strong>
              <span>{{ account.userName }}</span>
            </button>
          </div>
        </div>
      </section>

      <section class="login-card">
        <div class="login-card__header">
          <h2>账号登录</h2>
          <p>请输入账号和密码进入系统。</p>
        </div>

        <a-form ref="loginFormRef" :model="loginForm" layout="vertical" @submit.prevent="submitLogin">
          <a-form-item field="userName" label="账号" :rules="[{ required: true, message: '请输入登录账号' }]">
            <a-input v-model="loginForm.userName" placeholder="请输入登录账号" allow-clear />
          </a-form-item>

          <a-form-item field="passWord" label="密码" :rules="[{ required: true, message: '请输入登录密码' }]">
            <a-input-password v-model="loginForm.passWord" placeholder="请输入登录密码" />
          </a-form-item>

          <a-button type="primary" long size="large" :loading="loginLoading" @click="submitLogin">
            登录系统
          </a-button>
        </a-form>

        <div class="login-card__footer">
          <span>还没有学生账号？</span>
          <a-button type="text" @click="registerVisible = true">立即注册</a-button>
        </div>
      </section>
    </div>

    <a-modal
      v-model:visible="registerVisible"
      title="学生注册"
      width="720px"
      :confirm-loading="registerLoading"
      @ok="submitRegister"
      @cancel="handleRegisterCancel"
    >
      <a-form ref="registerFormRef" :model="registerForm" layout="vertical">
        <a-grid :cols="2" :col-gap="16">
          <a-grid-item>
            <a-form-item field="userName" label="登录账号" :rules="[{ required: true, message: '请输入登录账号' }]">
              <a-input v-model="registerForm.userName" placeholder="例如：student_linxy" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="passWord" label="登录密码" :rules="[{ required: true, message: '请输入登录密码' }]">
              <a-input-password v-model="registerForm.passWord" placeholder="请输入登录密码" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="name" label="姓名" :rules="[{ required: true, message: '请输入姓名' }]">
              <a-input v-model="registerForm.name" placeholder="请输入真实姓名" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="gender" label="性别" :rules="[{ required: true, message: '请选择性别' }]">
              <a-radio-group v-model="registerForm.gender" type="button">
                <a-radio value="男">男</a-radio>
                <a-radio value="女">女</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="age" label="年龄" :rules="[{ required: true, message: '请输入年龄' }]">
              <a-input-number v-model="registerForm.age" :min="16" :max="30" style="width: 100%" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item
              field="phone"
              label="联系电话"
              :rules="[
                { required: true, message: '请输入联系电话' },
                { match: /^1\\d{10}$/, message: '请输入正确的 11 位手机号' },
              ]"
            >
              <a-input v-model="registerForm.phone" placeholder="请输入 11 位手机号" />
            </a-form-item>
          </a-grid-item>
        </a-grid>

        <a-form-item field="address" label="联系地址" :rules="[{ required: true, message: '请输入联系地址' }]">
          <a-textarea v-model="registerForm.address" :auto-size="{ minRows: 3, maxRows: 5 }" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { Message } from '@arco-design/web-vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { addUsers, getDemoAccounts, getLoginUser, login } from '@/api';
import campusBackground from '@/image/zihao-chen-PjRdGQzAGC8-unsplash.jpg';

const router = useRouter();
const authStore = useAuthStore();

const loginFormRef = ref();
const registerFormRef = ref();

const loginLoading = ref(false);
const registerLoading = ref(false);
const registerVisible = ref(false);
const demoLoading = ref(false);
const demoAccounts = ref([]);

const loginForm = reactive({
  userName: '',
  passWord: '',
});

const createRegisterForm = () => ({
  userName: '',
  passWord: '',
  name: '',
  gender: '女',
  age: 20,
  phone: '',
  address: '',
  type: 2,
  status: 1,
});

const registerForm = reactive(createRegisterForm());

const backdropStyle = computed(() => ({
  backgroundImage: `linear-gradient(rgba(15, 23, 42, 0.38), rgba(15, 23, 42, 0.38)), url(${campusBackground})`,
}));

async function loadDemoAccounts() {
  demoLoading.value = true;
  try {
    const resp = await getDemoAccounts();
    demoAccounts.value = resp.data || [];
  } finally {
    demoLoading.value = false;
  }
}

function fillAccount(account) {
  loginForm.userName = account.userName || '';
  loginForm.passWord = '';
}

async function finishLogin(token) {
  authStore.setToken(token);
  const resp = await getLoginUser(token);
  authStore.setUser(resp.data);
  await router.replace('/app/overview');
}

async function submitLogin() {
  const valid = await loginFormRef.value?.validate();
  if (valid) return;

  loginLoading.value = true;
  try {
    const resp = await login(loginForm);
    await finishLogin(resp.data);
    Message.success('登录成功');
  } finally {
    loginLoading.value = false;
  }
}

function handleRegisterCancel() {
  registerVisible.value = false;
  Object.assign(registerForm, createRegisterForm());
  registerFormRef.value?.clearValidate?.();
}

async function submitRegister() {
  const valid = await registerFormRef.value?.validate();
  if (valid) return;

  registerLoading.value = true;
  try {
    await addUsers(registerForm);
    const loginResp = await login({
      userName: registerForm.userName,
      passWord: registerForm.passWord,
    });
    await finishLogin(loginResp.data);
    await loadDemoAccounts();
    Message.success('注册成功，已自动登录');
    registerVisible.value = false;
  } finally {
    registerLoading.value = false;
  }
}

loadDemoAccounts();
</script>

<style scoped>
.login-shell {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(43, 77, 140, 0.18), transparent 28%),
    radial-gradient(circle at bottom right, rgba(59, 130, 246, 0.12), transparent 30%),
    #f4f7fb;
}

.login-shell__backdrop {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  opacity: 0.14;
}

.login-shell__content {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 24px;
  align-items: center;
  max-width: 1360px;
  margin: 0 auto;
  padding: 40px 32px;
}

.login-brand,
.login-card {
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(10px);
}

.login-brand {
  padding: 36px;
}

.login-brand__badge {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(43, 77, 140, 0.1);
  color: var(--primary-color);
  font-size: 12px;
  font-weight: 700;
}

.login-brand h1 {
  margin: 18px 0 12px;
  font-size: 40px;
  line-height: 1.15;
}

.login-brand p {
  max-width: 680px;
  color: var(--text-2);
  line-height: 1.8;
}

.login-demo {
  margin-top: 28px;
  padding: 24px;
  background: #f8fafc;
  border: 1px solid var(--border-color);
  border-radius: 20px;
}

.login-demo__header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.login-demo__title {
  font-size: 16px;
  font-weight: 700;
}

.login-demo__hint {
  margin-top: 4px;
  color: var(--text-2);
  font-size: 12px;
}

.login-demo__list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.login-demo__item {
  padding: 16px;
  text-align: left;
  border: 1px solid var(--border-color);
  border-radius: 16px;
  background: #fff;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.login-demo__item:hover {
  transform: translateY(-2px);
  border-color: rgba(43, 77, 140, 0.3);
  box-shadow: var(--shadow-sm);
}

.login-demo__item strong,
.login-demo__item span {
  display: block;
}

.login-demo__item strong {
  margin: 6px 0 4px;
  color: var(--text-1);
}

.login-demo__item span {
  color: var(--text-2);
}

.login-demo__role {
  color: var(--primary-color) !important;
  font-size: 12px;
  font-weight: 700;
}

.login-card {
  padding: 32px;
}

.login-card__header h2 {
  margin: 0;
  font-size: 28px;
}

.login-card__header p {
  margin: 8px 0 24px;
  color: var(--text-2);
}

.login-card__footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
  color: var(--text-2);
}

@media (max-width: 960px) {
  .login-shell__content {
    grid-template-columns: 1fr;
    padding: 24px 16px;
  }

  .login-demo__list {
    grid-template-columns: 1fr;
  }
}
</style>
