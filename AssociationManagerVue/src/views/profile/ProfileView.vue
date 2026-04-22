<template>
  <div class="page-block">
    <div class="page-toolbar">
      <h2 class="page-section-title">个人中心</h2>
    </div>

    <a-grid :cols="24" :col-gap="16" :row-gap="16">
      <a-grid-item :span="8">
        <div class="page-card">
          <div class="page-card__body profile-card">
            <div class="profile-card__avatar">
              <img v-if="avatarPreview || avatarUrl" :src="avatarPreview || avatarUrl" alt="avatar" />
              <span v-else>{{ avatarText }}</span>
            </div>
            <div class="profile-card__name">{{ form.name || form.userName || '未命名用户' }}</div>
            <div class="profile-card__meta">{{ roleLabel }}</div>
            <div class="profile-card__actions">
              <input ref="avatarInputRef" type="file" accept="image/*" hidden @change="handleAvatarChange" />
              <a-button @click="avatarInputRef?.click()">选择头像</a-button>
              <a-button type="primary" :loading="avatarSaving" @click="saveAvatar" :disabled="!selectedAvatarFile">
                保存头像
              </a-button>
            </div>
          </div>
        </div>
      </a-grid-item>

      <a-grid-item :span="16">
        <div class="page-card">
          <div class="page-card__body">
            <h3 class="page-section-title">基础资料</h3>
            <a-form ref="profileFormRef" :model="form" layout="vertical">
              <a-grid :cols="2" :col-gap="16">
                <a-grid-item>
                  <a-form-item field="userName" label="登录账号">
                    <a-input v-model="form.userName" disabled />
                  </a-form-item>
                </a-grid-item>
                <a-grid-item>
                  <a-form-item field="name" label="姓名" :rules="[{ required: true, message: '请输入姓名' }]">
                    <a-input v-model="form.name" />
                  </a-form-item>
                </a-grid-item>
                <a-grid-item>
                  <a-form-item field="gender" label="性别">
                    <a-radio-group v-model="form.gender" type="button">
                      <a-radio value="男">男</a-radio>
                      <a-radio value="女">女</a-radio>
                    </a-radio-group>
                  </a-form-item>
                </a-grid-item>
                <a-grid-item>
                  <a-form-item field="age" label="年龄">
                    <a-input-number v-model="form.age" :min="0" style="width: 100%" />
                  </a-form-item>
                </a-grid-item>
              </a-grid>

              <a-grid :cols="2" :col-gap="16">
                <a-grid-item>
                  <a-form-item field="phone" label="联系电话">
                    <a-input v-model="form.phone" />
                  </a-form-item>
                </a-grid-item>
                <a-grid-item>
                  <a-form-item field="address" label="联系地址">
                    <a-input v-model="form.address" />
                  </a-form-item>
                </a-grid-item>
              </a-grid>

              <div class="dialog-footer">
                <a-button type="primary" :loading="savingProfile" @click="saveProfile">保存资料</a-button>
              </div>
            </a-form>
          </div>
        </div>

        <div class="page-card password-card">
          <div class="page-card__body">
            <h3 class="page-section-title">修改密码</h3>
            <a-form ref="passwordFormRef" :model="passwordForm" layout="vertical">
              <a-grid :cols="3" :col-gap="16">
                <a-grid-item>
                  <a-form-item field="oldPwd" label="原始密码" :rules="[{ required: true, message: '请输入原始密码' }]">
                    <a-input-password v-model="passwordForm.oldPwd" />
                  </a-form-item>
                </a-grid-item>
                <a-grid-item>
                  <a-form-item field="newPwd" label="新密码" :rules="[{ required: true, message: '请输入新密码' }]">
                    <a-input-password v-model="passwordForm.newPwd" />
                  </a-form-item>
                </a-grid-item>
                <a-grid-item>
                  <a-form-item field="rePwd" label="确认密码" :rules="[{ required: true, message: '请再次输入密码' }]">
                    <a-input-password v-model="passwordForm.rePwd" />
                  </a-form-item>
                </a-grid-item>
              </a-grid>

              <div class="dialog-footer">
                <a-button type="primary" :loading="savingPassword" @click="savePassword">更新密码</a-button>
              </div>
            </a-form>
          </div>
        </div>
      </a-grid-item>
    </a-grid>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue';
import { Message } from '@arco-design/web-vue';
import { storeToRefs } from 'pinia';
import { useAuthStore } from '@/stores/auth';
import { checkUserPwd, updateLoginUserInfo, updateLoginUserPwd, uploadAvatar } from '@/api';
import { resolveAvatarUrl } from '@/utils/avatar';

const authStore = useAuthStore();
const { token, user, userType } = storeToRefs(authStore);

const profileFormRef = ref();
const passwordFormRef = ref();
const avatarInputRef = ref();

const selectedAvatarFile = ref(null);
const avatarPreview = ref('');
const savingProfile = ref(false);
const savingPassword = ref(false);
const avatarSaving = ref(false);

const form = reactive({
  id: user.value?.id || '',
  userName: user.value?.userName || '',
  name: user.value?.name || '',
  gender: user.value?.gender || '女',
  age: user.value?.age || 20,
  phone: user.value?.phone || '',
  address: user.value?.address || '',
  avatar: user.value?.avatar || '',
});

const passwordForm = reactive({
  oldPwd: '',
  newPwd: '',
  rePwd: '',
});

const roleLabel = computed(() => {
  if (Number(userType.value) === 0) return '系统管理员';
  if (Number(userType.value) === 1) return '社团管理员';
  if (Number(userType.value) === 2) return '学生';
  return '未登录';
});

const avatarUrl = computed(() => resolveAvatarUrl(form.avatar));

const avatarText = computed(() => (form.name || form.userName || '用').slice(0, 1));

function syncForm(nextUser) {
  Object.assign(form, {
    id: nextUser?.id || '',
    userName: nextUser?.userName || '',
    name: nextUser?.name || '',
    gender: nextUser?.gender || '女',
    age: nextUser?.age || 20,
    phone: nextUser?.phone || '',
    address: nextUser?.address || '',
    avatar: nextUser?.avatar || '',
  });
}

watch(
  user,
  (nextUser) => {
    if (nextUser) {
      syncForm(nextUser);
    }
  },
  { immediate: true },
);

function handleAvatarChange(event) {
  const file = event.target.files?.[0];
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    Message.warning('请选择图片文件');
    return;
  }
  if (avatarPreview.value?.startsWith('blob:')) {
    URL.revokeObjectURL(avatarPreview.value);
  }
  selectedAvatarFile.value = file;
  avatarPreview.value = URL.createObjectURL(file);
}

async function saveAvatar() {
  if (!selectedAvatarFile.value) {
    return;
  }
  avatarSaving.value = true;
  try {
    const resp = await uploadAvatar(token.value, selectedAvatarFile.value);
    authStore.setUser(resp.data);
    syncForm(resp.data);
    selectedAvatarFile.value = null;
    if (avatarPreview.value?.startsWith('blob:')) {
      URL.revokeObjectURL(avatarPreview.value);
    }
    avatarPreview.value = '';
    if (avatarInputRef.value) {
      avatarInputRef.value.value = '';
    }
    Message.success('头像已更新');
  } finally {
    avatarSaving.value = false;
  }
}

async function saveProfile() {
  const valid = await profileFormRef.value?.validate();
  if (valid) return;
  savingProfile.value = true;
  try {
    const resp = await updateLoginUserInfo({
      token: token.value,
      userName: form.userName,
      name: form.name,
      gender: form.gender,
      age: form.age,
      phone: form.phone,
      address: form.address,
    });
    authStore.setUser(resp.data);
    syncForm(resp.data);
    Message.success('资料已更新');
  } finally {
    savingProfile.value = false;
  }
}

async function savePassword() {
  const valid = await passwordFormRef.value?.validate();
  if (valid) return;
  if (passwordForm.newPwd !== passwordForm.rePwd) {
    Message.warning('两次输入的新密码不一致');
    return;
  }

  savingPassword.value = true;
  try {
    await checkUserPwd(token.value, passwordForm.oldPwd);
    await updateLoginUserPwd(token.value, passwordForm.newPwd, passwordForm.oldPwd);
    passwordForm.oldPwd = '';
    passwordForm.newPwd = '';
    passwordForm.rePwd = '';
    Message.success('密码已更新');
  } finally {
    savingPassword.value = false;
  }
}
</script>

<style scoped>
.profile-card {
  display: grid;
  justify-items: center;
  gap: 10px;
}

.profile-card__avatar {
  width: 112px;
  height: 112px;
  border-radius: 28px;
  overflow: hidden;
  background: linear-gradient(135deg, #f59e0b, #fb7185);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: 700;
}

.profile-card__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-card__name {
  margin-top: 6px;
  font-size: 20px;
  font-weight: 700;
}

.profile-card__meta {
  color: var(--text-2);
}

.profile-card__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.password-card {
  margin-top: 16px;
}
</style>
