<template>
  <div class="profile-page">
    <el-row :gutter="18">
      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="profile-summary">
          <div class="profile-summary__avatar">
            <img v-if="avatarUrl" :src="avatarUrl" alt="avatar" />
            <span v-else>{{ avatarText }}</span>
          </div>
          <div class="profile-summary__name">{{ displayName }}</div>
          <div class="profile-summary__role">{{ roleLabel }}</div>
          <div class="profile-summary__actions">
            <el-button type="primary" plain @click="$emit('open-avatar')">更换头像</el-button>
            <el-button @click="loadProfile">刷新资料</el-button>
          </div>

          <div class="profile-summary__grid">
            <div class="summary-item">
              <span class="summary-item__label">登录账号</span>
              <span class="summary-item__value">{{ profileForm.userName || '-' }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-item__label">账号状态</span>
              <span class="summary-item__value">{{ profileForm.status === 1 ? '启用' : '停用' }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-item__label">账号身份</span>
              <span class="summary-item__value">{{ roleLabel }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-item__label">创建时间</span>
              <span class="summary-item__value">{{ profileForm.createTime || '-' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="16">
        <el-card shadow="never" class="profile-card">
          <div slot="header" class="card-title">个人资料</div>
          <el-form label-width="92px" class="profile-form">
            <el-row :gutter="16">
              <el-col :xs="24" :md="12">
                <el-form-item label="姓名">
                  <el-input v-model="profileForm.name" placeholder="请输入姓名" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="性别">
                  <el-radio-group v-model="profileForm.gender">
                    <el-radio label="男">男</el-radio>
                    <el-radio label="女">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :xs="24" :md="12">
                <el-form-item label="年龄">
                  <el-input-number v-model="profileForm.age" :min="16" :max="60" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="手机号">
                  <el-input v-model="profileForm.phone" placeholder="请输入 11 位手机号" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="联系地址">
              <el-input v-model="profileForm.address" type="textarea" :rows="3" placeholder="请输入联系地址" />
            </el-form-item>

            <div class="form-actions">
              <el-button @click="resetProfile">重置</el-button>
              <el-button type="primary" :loading="profileSaving" @click="submitProfile">保存资料</el-button>
            </div>
          </el-form>
        </el-card>

        <el-card shadow="never" class="profile-card">
          <div slot="header" class="card-title">账号安全</div>
          <div class="security-tip">
            修改密码后，下次登录需要使用新密码。为了保护隐私，登录页不会再展示任何账号密码明文。
          </div>
          <el-form label-width="108px" class="profile-form">
            <el-row :gutter="16">
              <el-col :xs="24" :md="12">
                <el-form-item label="当前密码">
                  <el-input
                    v-model="passwordForm.oldPwd"
                    type="password"
                    show-password
                    placeholder="请输入当前密码"
                    autocomplete="new-password"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="新密码">
                  <el-input
                    v-model="passwordForm.newPwd"
                    type="password"
                    show-password
                    placeholder="至少 6 位"
                    autocomplete="new-password"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="确认新密码">
              <el-input
                v-model="passwordForm.confirmPwd"
                type="password"
                show-password
                placeholder="请再次输入新密码"
                autocomplete="new-password"
              />
            </el-form-item>

            <div class="form-actions">
              <el-button @click="resetPasswordForm">清空</el-button>
              <el-button type="primary" :loading="passwordSaving" @click="submitPassword">修改密码</el-button>
            </div>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getLoginUser, updateLoginUserInfo, updateLoginUserPwd } from "@/api";

const createPasswordForm = () => ({
  oldPwd: "",
  newPwd: "",
  confirmPwd: "",
});

export default {
  name: "ProfilePage",
  props: {
    userInfo: {
      type: Object,
      default: null,
    },
    avatarUrl: {
      type: String,
      default: "",
    },
    roleLabel: {
      type: String,
      default: "未登录",
    },
  },
  data() {
    return {
      profileSaving: false,
      passwordSaving: false,
      profileForm: {
        id: "",
        userName: "",
        name: "",
        gender: "男",
        age: 20,
        phone: "",
        address: "",
        status: 1,
        createTime: "",
      },
      passwordForm: createPasswordForm(),
    };
  },
  computed: {
    displayName() {
      return this.profileForm.name || this.profileForm.userName || "未命名用户";
    },
    avatarText() {
      return (this.displayName || "社").slice(0, 1);
    },
  },
  watch: {
    userInfo: {
      immediate: true,
      handler(value) {
        this.syncProfileForm(value);
      },
    },
  },
  methods: {
    syncProfileForm(user) {
      const next = user || {};
      this.profileForm = {
        id: next.id || "",
        userName: next.userName || "",
        name: next.name || "",
        gender: next.gender || "男",
        age: next.age || 20,
        phone: next.phone || "",
        address: next.address || "",
        status: next.status === 0 ? 0 : 1,
        createTime: next.createTime || "",
      };
    },
    async loadProfile() {
      const resp = await getLoginUser(this.$store.getters.token);
      this.syncProfileForm(resp.data);
      this.$emit("user-updated", resp.data);
      this.$message.success("个人资料已刷新");
    },
    resetProfile() {
      this.syncProfileForm(this.userInfo);
    },
    async submitProfile() {
      if (!this.profileForm.name.trim()) {
        this.$message.warning("请输入姓名");
        return;
      }
      if (!/^1\d{10}$/.test(this.profileForm.phone)) {
        this.$message.warning("请输入正确的 11 位手机号");
        return;
      }
      if (!this.profileForm.address.trim()) {
        this.$message.warning("请输入联系地址");
        return;
      }

      this.profileSaving = true;
      try {
        const resp = await updateLoginUserInfo({
          token: this.$store.getters.token,
          name: this.profileForm.name,
          gender: this.profileForm.gender,
          age: this.profileForm.age,
          phone: this.profileForm.phone,
          address: this.profileForm.address,
        });
        this.syncProfileForm(resp.data);
        this.$emit("user-updated", resp.data);
        this.$message.success("个人资料已保存");
      } finally {
        this.profileSaving = false;
      }
    },
    resetPasswordForm() {
      this.passwordForm = createPasswordForm();
    },
    async submitPassword() {
      if (!this.passwordForm.oldPwd) {
        this.$message.warning("请输入当前密码");
        return;
      }
      if (!this.passwordForm.newPwd || this.passwordForm.newPwd.length < 6) {
        this.$message.warning("新密码长度不能少于 6 位");
        return;
      }
      if (this.passwordForm.newPwd !== this.passwordForm.confirmPwd) {
        this.$message.warning("两次输入的新密码不一致");
        return;
      }

      this.passwordSaving = true;
      try {
        const resp = await updateLoginUserPwd(
          this.$store.getters.token,
          this.passwordForm.newPwd,
          this.passwordForm.oldPwd
        );
        this.$message({
          message: resp.msg || "密码修改成功",
          type: resp.code === 0 ? "success" : "warning",
        });
        if (resp.code === 0) {
          this.resetPasswordForm();
        }
      } finally {
        this.passwordSaving = false;
      }
    },
  },
};
</script>

<style scoped>
.profile-page {
  display: grid;
  gap: 18px;
}

.profile-summary,
.profile-card {
  border-radius: 24px;
  overflow: hidden;
}

.profile-summary {
  min-height: 100%;
  text-align: center;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(245, 249, 255, 0.98));
}

.profile-summary__avatar {
  width: 112px;
  height: 112px;
  margin: 0 auto 18px;
  border-radius: 32px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2563eb, #f97316);
  color: #fff;
  font-size: 42px;
  font-weight: 700;
  box-shadow: 0 16px 32px rgba(37, 99, 235, 0.24);
}

.profile-summary__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-summary__name {
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
}

.profile-summary__role {
  margin-top: 6px;
  color: #64748b;
  font-size: 14px;
}

.profile-summary__actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin: 18px 0 24px;
}

.profile-summary__grid {
  display: grid;
  gap: 12px;
  text-align: left;
}

.summary-item {
  padding: 14px 16px;
  border-radius: 16px;
  background: #f8fbff;
}

.summary-item__label {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.summary-item__value {
  display: block;
  margin-top: 6px;
  color: #0f172a;
  font-weight: 600;
  word-break: break-all;
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.profile-card {
  margin-bottom: 18px;
}

.profile-form {
  margin-top: 6px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.security-tip {
  margin-bottom: 18px;
  padding: 12px 14px;
  border-radius: 14px;
  background: #eff6ff;
  color: #475569;
  line-height: 1.7;
}

@media (max-width: 768px) {
  .profile-summary__actions,
  .form-actions {
    flex-direction: column;
  }
}
</style>
