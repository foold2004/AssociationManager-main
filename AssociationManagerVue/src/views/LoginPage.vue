<template>
  <div class="login-page">
    <div class="login-page__backdrop" :style="loginBackdropStyle"></div>
    <div class="login-page__content">
      <section class="hero-card">
        <h1>社团管理系统</h1>

        <div class="account-panel">
          <div class="account-panel__head">
            <div class="account-panel__title">实时演示账号</div>
            <a-button size="small" @click="loadDemoAccounts" :loading="demoLoading">刷新账号</a-button>
          </div>

          <div class="hero-gallery">
            <div class="hero-gallery__main" :style="{ backgroundImage: `url(${communityImage})` }"></div>
            <div class="hero-gallery__side">
              <div class="hero-gallery__tile" :style="{ backgroundImage: `url(${campusCourtImage})` }"></div>
              <div class="hero-gallery__tile" :style="{ backgroundImage: `url(${bulletinImage})` }"></div>
            </div>
          </div>

          <div v-if="demoAccounts.length" class="account-list">
            <button
              v-for="account in demoAccounts"
              :key="account.id || account.userName"
              type="button"
              class="account-chip"
              @click="fillAccount(account)"
            >
              <span class="account-chip__role">{{ account.roleLabel }}</span>
              <span class="account-chip__name">{{ account.name }}</span>
            </button>
          </div>
          <a-empty v-else class="account-empty" description="" />
        </div>
      </section>

      <section class="login-card">
        <div class="login-card__header">
          <h2>账号登录</h2>
        </div>

        <a-form-model
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          layout="vertical"
          class="login-form"
        >
          <a-form-model-item label="账号" prop="userName">
            <a-input
              v-model="loginForm.userName"
              size="large"
              placeholder="请输入账号"
              allow-clear
            />
          </a-form-model-item>

          <a-form-model-item label="密码" prop="passWord">
            <a-input-password
              v-model="loginForm.passWord"
              size="large"
              placeholder="请输入密码"
            />
          </a-form-model-item>

          <a-button
            type="primary"
            size="large"
            block
            class="login-submit"
            :loading="loginLoading"
            @click="submitLogin"
          >
            登录系统
          </a-button>
        </a-form-model>

        <div class="login-card__footer">
          <span>还没有学生账号？</span>
          <a-button type="link" @click="registerVisible = true">立即注册</a-button>
        </div>
      </section>
    </div>

    <a-modal
      title="学生注册"
      :visible="registerVisible"
      :confirm-loading="registerLoading"
      ok-text="注册并登录"
      cancel-text="取消"
      width="720px"
      @ok="submitRegister"
      @cancel="handleRegisterCancel"
    >
      <a-form-model
        ref="registerForm"
        :model="registerForm"
        :rules="registerRules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item label="登录账号" prop="userName">
              <a-input v-model="registerForm.userName" placeholder="如：student_linxy" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="登录密码" prop="passWord">
              <a-input-password v-model="registerForm.passWord" placeholder="请输入登录密码" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item label="姓名" prop="name">
              <a-input v-model="registerForm.name" placeholder="请输入真实姓名" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="性别" prop="gender">
              <a-radio-group v-model="registerForm.gender">
                <a-radio value="男">男</a-radio>
                <a-radio value="女">女</a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item label="年龄" prop="age">
              <a-input-number
                v-model="registerForm.age"
                :min="16"
                :max="30"
                style="width: 100%"
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="联系电话" prop="phone">
              <a-input v-model="registerForm.phone" placeholder="请输入 11 位手机号" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-form-model-item label="联系地址" prop="address">
          <a-textarea
            v-model="registerForm.address"
            :rows="3"
            placeholder="请输入常住校区或联系地址"
          />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import { message } from "ant-design-vue";
import { addUsers, getDemoAccounts, getLoginUser, login } from "@/api";
import campusBackground from "@/image/zihao-chen-PjRdGQzAGC8-unsplash.jpg";
import communityImage from "@/image/pexels-votso-sothu-53802751-32275471.jpg";
import campusCourtImage from "@/image/declan-sun-f3jWwGAXtSA-unsplash.jpg";
import bulletinImage from "@/image/zoshua-colah-ufRqsiLaKII-unsplash.jpg";

const createRegisterForm = () => ({
  userName: "",
  passWord: "",
  name: "",
  gender: "女",
  age: 20,
  phone: "",
  address: "",
  type: 2,
  status: 1,
});

export default {
  name: "LoginPage",
  data() {
    return {
      loginLoading: false,
      registerLoading: false,
      registerVisible: false,
      demoLoading: false,
      campusBackground,
      communityImage,
      campusCourtImage,
      bulletinImage,
      loginForm: {
        userName: "",
        passWord: "",
      },
      registerForm: createRegisterForm(),
      demoAccounts: [],
      loginRules: {
        userName: [{ required: true, message: "请输入登录账号", trigger: "blur" }],
        passWord: [{ required: true, message: "请输入登录密码", trigger: "blur" }],
      },
      registerRules: {
        userName: [{ required: true, message: "请输入登录账号", trigger: "blur" }],
        passWord: [{ required: true, message: "请输入登录密码", trigger: "blur" }],
        name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
        gender: [{ required: true, message: "请选择性别", trigger: "change" }],
        age: [{ required: true, message: "请输入年龄", trigger: "change" }],
        phone: [
          { required: true, message: "请输入联系电话", trigger: "blur" },
          { pattern: /^1\d{10}$/, message: "请输入正确的 11 位手机号", trigger: "blur" },
        ],
        address: [{ required: true, message: "请输入联系地址", trigger: "blur" }],
      },
    };
  },
  computed: {
    loginBackdropStyle() {
      return {
        backgroundImage: `linear-gradient(rgba(15, 23, 42, 0.26), rgba(15, 23, 42, 0.26)), url(${this.campusBackground})`,
      };
    },
  },
  async created() {
    await this.loadDemoAccounts();
  },
  methods: {
    async loadDemoAccounts() {
      this.demoLoading = true;
      try {
        const resp = await getDemoAccounts();
        this.demoAccounts = resp.data || [];
      } finally {
        this.demoLoading = false;
      }
    },
    fillAccount(account) {
      this.loginForm.userName = account.userName || "";
      this.loginForm.passWord = "";
    },
    async finishLogin(token) {
      this.$store.commit("setToken", token);
      const resp = await getLoginUser(token);
      this.$store.commit("setUser", resp.data);
      this.$router.push("/app");
    },
    submitLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (!valid) {
          return;
        }
        this.loginLoading = true;
        try {
          const resp = await login(this.loginForm);
          await this.finishLogin(resp.data);
          message.success("登录成功");
        } finally {
          this.loginLoading = false;
        }
      });
    },
    handleRegisterCancel() {
      this.registerVisible = false;
      this.registerForm = createRegisterForm();
      this.$nextTick(() => {
        if (this.$refs.registerForm) {
          this.$refs.registerForm.clearValidate();
        }
      });
    },
    submitRegister() {
      this.$refs.registerForm.validate(async (valid) => {
        if (!valid) {
          return;
        }
        this.registerLoading = true;
        try {
          await addUsers(this.registerForm);
          const loginPayload = {
            userName: this.registerForm.userName,
            passWord: this.registerForm.passWord,
          };
          this.registerVisible = false;
          await this.finishLogin((await login(loginPayload)).data);
          await this.loadDemoAccounts();
          message.success("注册成功，已自动登录");
        } finally {
          this.registerLoading = false;
        }
      });
    },
  },
};
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(56, 189, 248, 0.2), transparent 32%),
    radial-gradient(circle at bottom right, rgba(251, 191, 36, 0.18), transparent 30%),
    linear-gradient(135deg, #0f172a 0%, #111827 55%, #1e293b 100%);
}

.login-page__backdrop {
  position: absolute;
  inset: 0;
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  opacity: 0.18;
}

.login-page__content {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.2fr 0.9fr;
  gap: 32px;
  align-items: center;
  padding: 28px 56px;
}

.hero-card,
.login-card {
  backdrop-filter: blur(14px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 32px;
  box-shadow: 0 28px 80px rgba(15, 23, 42, 0.35);
}

.hero-card {
  padding: 30px 32px;
  color: #f8fafc;
  background: rgba(15, 23, 42, 0.68);
}

.hero-card__tag {
  display: inline-flex;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(56, 189, 248, 0.15);
  color: #7dd3fc;
  font-size: 14px;
  letter-spacing: 0.08em;
}

.hero-card h1 {
  margin: 16px 0 12px;
  color: #ffffff;
  font-size: 46px;
  line-height: 1.1;
  font-weight: 700;
}

.hero-card__desc {
  max-width: 680px;
  margin: 0 0 18px;
  color: rgba(226, 232, 240, 0.9);
  font-size: 15px;
  line-height: 1.75;
}

.account-panel {
  padding: 18px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.account-panel__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.account-panel__title {
  color: #f8fafc;
  font-size: 18px;
  font-weight: 600;
}

.account-panel__alert {
  margin-bottom: 14px;
}

.hero-gallery {
  display: grid;
  grid-template-columns: 1.35fr 0.82fr;
  gap: 12px;
  margin-bottom: 14px;
}

.hero-gallery__main,
.hero-gallery__tile {
  border-radius: 18px;
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: inset 0 -40px 80px rgba(15, 23, 42, 0.16);
}

.hero-gallery__main {
  min-height: 160px;
}

.hero-gallery__side {
  display: grid;
  gap: 12px;
}

.hero-gallery__tile {
  min-height: 74px;
}

.account-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.account-chip {
  width: 100%;
  padding: 14px;
  text-align: left;
  border: 1px solid rgba(125, 211, 252, 0.22);
  border-radius: 20px;
  background: rgba(15, 23, 42, 0.42);
  color: #f8fafc;
  cursor: pointer;
  transition: transform 0.2s ease, border-color 0.2s ease, background 0.2s ease;
}

.account-chip:hover {
  transform: translateY(-2px);
  border-color: rgba(56, 189, 248, 0.6);
  background: rgba(30, 41, 59, 0.92);
}

.account-chip__role,
.account-chip__name,
.account-chip__action {
  display: block;
}

.account-chip__role {
  margin-bottom: 8px;
  color: #7dd3fc;
  font-size: 13px;
}

.account-chip__name {
  margin-bottom: 4px;
  font-size: 16px;
  font-weight: 600;
}

.account-chip__action {
  margin-top: 10px;
  color: #fbbf24;
  font-size: 12px;
}

.account-empty {
  margin: 20px 0;
}

.account-panel__tip {
  margin: 14px 0 0;
  color: rgba(226, 232, 240, 0.74);
  font-size: 13px;
}

.login-card {
  padding: 28px 28px 22px;
  background: rgba(255, 255, 255, 0.94);
  align-self: center;
  margin: auto 0;
}

.login-card__header h2 {
  margin: 0 0 8px;
  color: #0f172a;
  font-size: 30px;
  font-weight: 700;
}

.login-card__header p {
  margin: 0 0 20px;
  color: #64748b;
  font-size: 15px;
}

.login-form :deep(.ant-form-item-label > label) {
  color: #334155;
  font-weight: 600;
}

.login-form :deep(.ant-input),
.login-form :deep(.ant-input-password) {
  border-radius: 14px;
}

.login-submit {
  height: 48px;
  margin-top: 8px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, #2563eb, #0ea5e9);
  box-shadow: 0 14px 28px rgba(37, 99, 235, 0.24);
}

.login-card__footer {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 18px;
  color: #64748b;
  font-size: 14px;
}

@media (max-width: 1120px) {
  .login-page__content {
    grid-template-columns: 1fr;
    padding: 20px 16px;
  }

  .hero-card h1 {
    font-size: 42px;
  }
}

@media (max-width: 720px) {
  .hero-card,
  .login-card {
    padding: 24px;
    border-radius: 24px;
  }

  .hero-gallery {
    grid-template-columns: 1fr;
  }

  .hero-gallery__side {
    grid-template-columns: 1fr 1fr;
  }

  .account-list {
    grid-template-columns: 1fr;
  }

  .hero-card h1 {
    font-size: 34px;
  }

  .account-panel__head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
