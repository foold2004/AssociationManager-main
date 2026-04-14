<template>
  <a-layout class="workspace-shell" :class="{ 'workspace-shell--collapsed': navCollapsed }">
    <a-layout-sider
      :collapsed="navCollapsed"
      :trigger="null"
      collapsible
      class="workspace-sider"
      @mouseenter.native="siderHover = true"
      @mouseleave.native="siderHover = false"
    >
      <div class="brand-panel">
        <button class="brand-avatar-button" @click="openAvatarModal">
          <div v-if="avatarUrl" class="brand-avatar brand-avatar--image">
            <img :src="avatarUrl" :class="avatarImageClass" alt="avatar" />
          </div>
          <div v-else class="brand-avatar brand-avatar--fallback">{{ avatarText }}</div>
        </button>
        <transition name="fade-slide">
          <div v-if="!navCollapsed" class="brand-copy">
            <div class="brand-title">社团管理系统</div>
            <div class="brand-subtitle">{{ roleLabel }}</div>
          </div>
        </transition>
      </div>

      <div class="pin-switch">
        <a-tooltip :title="navPinned ? '固定展开' : '悬停展开'">
          <a-button shape="circle" size="small" class="pin-button" @click="navPinned = !navPinned">
            <a-icon :type="navPinned ? 'pushpin' : 'menu'" />
          </a-button>
        </a-tooltip>
      </div>

      <a-menu
        theme="dark"
        mode="inline"
        :selectedKeys="[activeModule]"
        class="workspace-menu"
        @click="onMenuClick"
      >
        <a-menu-item v-for="item in menuList" :key="item.key">
          <a-icon :type="item.icon" />
          <span>{{ item.title }}</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>

    <a-layout>
      <a-layout-header class="workspace-header">
        <div class="header-main">
          <div>
            <div class="header-title">{{ currentTitle }}</div>
          </div>
          <div class="header-actions">
            <button class="header-avatar" @click="openAvatarModal">
              <img v-if="avatarUrl" :src="avatarUrl" :class="avatarImageClass" alt="avatar" />
              <span v-else>{{ avatarText }}</span>
            </button>
            <a-tag color="blue">{{ displayName }}</a-tag>
            <a-tag color="gold">{{ roleLabel }}</a-tag>
            <a-button @click="switchModule('profile')">个人中心</a-button>
            <a-button @click="refreshCurrent">刷新</a-button>
            <a-button type="primary" ghost @click="logoutNow">退出登录</a-button>
          </div>
        </div>
      </a-layout-header>

      <a-layout-content class="workspace-content">
        <template v-if="activeModule === 'overview'">
          <section class="hero-panel">
            <div class="hero-copy">
              <p class="hero-kicker">ASSOCIATION CONTROL DESK</p>
              <h1>{{ greeting }}</h1>
            </div>
            <div class="hero-visual">
              <div class="hero-poster"></div>
              <div class="visual-card visual-card-top">
                <div class="visual-label">可访问模块</div>
                <div class="visual-value">{{ menuList.length }}</div>
              </div>
              <div class="visual-card visual-card-bottom">
                <div class="visual-label">概览通知</div>
                <div class="visual-value">{{ rowsMap.notices.length }}</div>
              </div>
            </div>
          </section>

          <a-row :gutter="16" class="metrics-row">
            <a-col :xs="24" :sm="12" :xl="6" v-for="item in summaryCards" :key="item.title">
              <div class="metric-card">
                <div class="metric-icon" :class="item.tone">
                  <a-icon :type="item.icon" />
                </div>
                <div class="metric-copy">
                  <div class="metric-title">{{ item.title }}</div>
                  <div class="metric-value">{{ item.value }}</div>
                </div>
              </div>
            </a-col>
          </a-row>

          <a-row :gutter="16">
            <a-col :xs="24" :xl="9">
              <a-card :bordered="false" class="content-card">
                <div slot="title">待处理事项</div>
                <div class="todo-list">
                  <div v-for="item in todoList" :key="item.index" class="todo-item">
                    <div class="todo-index">{{ item.index }}</div>
                    <div>
                      <div class="todo-title">{{ item.title }}</div>
                    </div>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :xs="24" :xl="15">
              <a-card :bordered="false" class="content-card notice-card">
                <div slot="title">最新通知</div>
                <div class="notice-banner">
                  <div class="notice-banner__title">校园公告与社团动态</div>
                </div>
                <a-empty v-if="rowsMap.notices.length === 0" description="暂无通知" />
                <div v-else class="feed-list">
                  <div v-for="item in rowsMap.notices.slice(0, 6)" :key="item.id" class="feed-item">
                    <div class="feed-dot"></div>
                    <div class="feed-body">
                      <div class="feed-title">{{ item.title }}</div>
                      <div class="feed-meta">{{ item.teamName || '系统通知' }} · {{ item.createTime || '-' }}</div>
                      <div class="feed-desc">{{ item.detail || '暂无详情' }}</div>
                    </div>
                  </div>
                </div>
              </a-card>
            </a-col>
          </a-row>

          <a-row :gutter="16" class="overview-lower">
            <a-col :xs="24" :xl="24">
              <a-card :bordered="false" class="content-card">
                <div slot="title">最近活动</div>
                <a-empty v-if="rowsMap.activities.length === 0" description="暂无活动" />
                <div v-else class="activity-board">
                  <div v-for="item in rowsMap.activities.slice(0, 4)" :key="item.id" class="activity-tile">
                    <div class="activity-title">{{ item.name }}</div>
                    <div class="activity-meta">{{ item.teamName || '未指定社团' }}</div>
                    <div class="activity-time">{{ item.activeTime || '时间待定' }}</div>
                    <div class="activity-desc">{{ item.comm || '暂无简介' }}</div>
                  </div>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </template>

        <ProfilePage
          v-else-if="activeModule === 'profile'"
          :key="componentKey"
          :user-info="userInfo"
          :avatar-url="avatarUrl"
          :role-label="roleLabel"
          @open-avatar="openAvatarModal"
          @user-updated="handleUserUpdated"
        />

        <component v-else-if="activeComponent" :is="activeComponent" :key="componentKey" @switch-module="switchModule" />
      </a-layout-content>
    </a-layout>

    <a-modal
      title="上传头像"
      :visible="avatarVisible"
      :confirm-loading="avatarSaving"
      ok-text="保存头像"
      cancel-text="取消"
      @ok="saveAvatar"
      @cancel="closeAvatarModal"
    >
      <div class="avatar-upload-panel">
        <div class="avatar-upload-preview">
          <img
            v-if="avatarPreview || avatarUrl"
            :src="avatarPreview || avatarUrl"
            :class="avatarPreviewImageClass"
            alt="avatar preview"
          />
          <span v-else>{{ avatarText }}</span>
        </div>
        <div class="avatar-upload-copy">
          <div class="avatar-upload-title">自定义头像</div>
          <input
            ref="avatarInput"
            type="file"
            accept="image/png,image/jpeg,image/webp"
            class="avatar-file-input"
            @change="onAvatarFileChange"
          />
          <div class="avatar-library">
            <div class="avatar-library__title">推荐头像</div>
            <div class="avatar-library__grid">
              <button
                v-for="item in defaultAvatarList"
                :key="item.key"
                class="avatar-library__item"
                :class="{ 'avatar-library__item--active': selectedAvatarKey === item.key && !selectedAvatarFile }"
                @click="selectDefaultAvatar(item.key)"
              >
                <img :src="item.url" :alt="item.key" />
              </button>
            </div>
          </div>
          <div class="avatar-upload-actions">
            <a-button type="primary" @click="chooseAvatarFile">选择图片</a-button>
            <a-button v-if="selectedAvatarFile || selectedAvatarKey" @click="clearSelectedAvatar">清空选择</a-button>
          </div>
          <div class="avatar-file-name">{{ avatarSelectionText }}</div>
        </div>
      </div>
    </a-modal>
  </a-layout>
</template>

<script>
import UsersPage from '@/views/pages/Users.vue';
import TeamTypesPage from '@/views/pages/TeamTypes.vue';
import TeamsPage from '@/views/pages/Teams.vue';
import MembersPage from '@/views/pages/Members.vue';
import ActivitiesPage from '@/views/pages/Activities.vue';
import ActiveLogsPage from '@/views/pages/ActiveLogs.vue';
import NoticesPage from '@/views/pages/Notices.vue';
import ApplyLogsPage from '@/views/pages/ApplyLogs.vue';
import PayLogsPage from '@/views/pages/PayLogs.vue';
import RulesPage from '@/views/pages/RulesPage.vue';
import InteractionsPage from '@/views/pages/InteractionsPage.vue';
import ProfilePage from '@/views/pages/ProfilePage.vue';
import { exit, getLoginUser, getPageActivities, getPageNotices, getPageTeams, updateLoginUserInfo, uploadAvatar } from '@/api';

const DEFAULT_AVATARS = {
  avatar_a: require('@/image/touxiang/Multiavatar-1712b989a2c4543c67.png'),
  avatar_b: require('@/image/touxiang/Multiavatar-1aa830d7457b675f20.png'),
  avatar_c: require('@/image/touxiang/Multiavatar-1df51d5d2ebabc4fe8.png'),
  avatar_d: require('@/image/touxiang/Multiavatar-46e9cfc0049f9ce99a.png'),
  avatar_e: require('@/image/touxiang/Multiavatar-4df0524f491f7e81a2.png'),
  avatar_f: require('@/image/touxiang/Multiavatar-4f89fbdc56a7ee050d.png'),
  avatar_g: require('@/image/touxiang/Multiavatar-97a8745543a0f094eb.png'),
  avatar_h: require('@/image/touxiang/Multiavatar-Bugzilla.png'),
  avatar_i: require('@/image/touxiang/Multiavatar-fbfae26517628e3237.png'),
  avatar_j: require('@/image/touxiang/Multiavatar-Vincent Plant.png'),
};

const MODULES_RAW = [
  { key: 'overview', title: '首页概览', icon: 'dashboard', desc: '', roles: [0, 1, 2] },
  { key: 'profile', title: '个人中心', icon: 'idcard', desc: '', roles: [0, 1, 2] },
  { key: 'users', title: '用户管理', icon: 'team', desc: '', roles: [0] },
  { key: 'teamTypes', title: '社团类型', icon: 'tags', desc: '', roles: [0] },
  { key: 'teams', title: '社团管理', icon: 'bank', desc: '', roles: [0, 1, 2] },
  { key: 'members', title: '社团成员', icon: 'usergroup-add', desc: '', roles: [0, 1] },
  { key: 'activities', title: '社团活动', icon: 'calendar', desc: '', roles: [0, 1, 2] },
  { key: 'activeLogs', title: '活动报名', icon: 'solution', desc: '', roles: [0, 1, 2] },
  { key: 'notices', title: '通知公告', icon: 'notification', desc: '', roles: [0, 1, 2] },
  { key: 'applyLogs', title: '入团申请', icon: 'audit', desc: '', roles: [0, 1] },
  { key: 'payLogs', title: '费用管理', icon: 'wallet', desc: '', roles: [0, 1, 2] },
  { key: 'rules', title: '规则制度', icon: 'book', desc: '', roles: [0, 1, 2] },
  { key: 'interactions', title: '交流互动', icon: 'message', desc: '', roles: [0, 1, 2] },
];

const MODULES = MODULES_RAW.map((item) => ({
  ...item,
  desc: '',
}));

export default {
  name: 'DashboardPage',
  components: {
    UsersPage,
    TeamTypesPage,
    TeamsPage,
    MembersPage,
    ActivitiesPage,
    ActiveLogsPage,
    NoticesPage,
    ApplyLogsPage,
    PayLogsPage,
    RulesPage,
    InteractionsPage,
    ProfilePage,
  },
  data() {
    return {
      siderHover: false,
      navPinned: false,
      activeModule: 'overview',
      componentKey: 0,
      avatarVisible: false,
      avatarSaving: false,
      selectedAvatarFile: null,
      selectedAvatarKey: '',
      avatarPreview: '',
      userInfo: this.$store.getters.user || null,
      rowsMap: {
        teams: [],
        notices: [],
        activities: [],
      },
    };
  },
  computed: {
    navCollapsed() {
      return !this.navPinned && !this.siderHover;
    },
    userType() {
      return this.userInfo ? this.userInfo.type : null;
    },
    roleLabel() {
      if (this.userType === 0) return '系统管理员';
      if (this.userType === 1) return '社团管理员';
      if (this.userType === 2) return '学生';
      return '未登录';
    },
    displayName() {
      if (!this.userInfo) return '未登录';
      return this.userInfo.name || this.userInfo.userName || '未命名用户';
    },
    greeting() {
      return `欢迎回来，${this.displayName}`;
    },
    avatarText() {
      return (this.displayName || '社').slice(0, 1);
    },
    avatarUrl() {
      const avatar = this.userInfo && this.userInfo.avatar ? String(this.userInfo.avatar) : '';
      if (!avatar) return '';
      if (DEFAULT_AVATARS[avatar]) {
        return DEFAULT_AVATARS[avatar];
      }
      if (avatar.startsWith('/association/uploads/') || avatar.startsWith('/uploads/') || avatar.startsWith('http')) {
        return avatar;
      }
      return '';
    },
    isPresetAvatar() {
      const avatar = this.userInfo && this.userInfo.avatar ? String(this.userInfo.avatar) : '';
      return !!DEFAULT_AVATARS[avatar];
    },
    avatarImageClass() {
      return this.isPresetAvatar ? 'avatar-image avatar-image--preset' : 'avatar-image avatar-image--upload';
    },
    avatarPreviewImageClass() {
      if (this.selectedAvatarFile) {
        return 'avatar-image avatar-image--upload';
      }
      if (this.selectedAvatarKey) {
        return 'avatar-image avatar-image--preset';
      }
      return this.avatarImageClass;
    },
    menuList() {
      return MODULES.filter((item) => item.roles.includes(this.userType));
    },
    defaultAvatarList() {
      return Object.keys(DEFAULT_AVATARS).map((key) => ({
        key,
        url: DEFAULT_AVATARS[key],
      }));
    },
    avatarSelectionText() {
      if (this.selectedAvatarFile) {
        return this.selectedAvatarFile.name;
      }
      if (this.selectedAvatarKey) {
        return '已选择系统预设头像';
      }
      return '未选择文件';
    },
    activeComponent() {
      const map = {
        users: UsersPage,
        teamTypes: TeamTypesPage,
        teams: TeamsPage,
        members: MembersPage,
        activities: ActivitiesPage,
        activeLogs: ActiveLogsPage,
        notices: NoticesPage,
        applyLogs: ApplyLogsPage,
        payLogs: PayLogsPage,
        rules: RulesPage,
        interactions: InteractionsPage,
      };
      return map[this.activeModule] || null;
    },
    currentTitle() {
      const found = MODULES.find((item) => item.key === this.activeModule);
      return found ? found.title : '首页概览';
    },
    currentDescription() {
      const found = MODULES.find((item) => item.key === this.activeModule);
      return found ? found.desc : '';
    },
    summaryCards() {
      return [
        { title: '当前角色', value: this.roleLabel, desc: '', icon: 'safety-certificate', tone: 'tone-blue' },
        { title: '可见模块', value: this.menuList.length, desc: '', icon: 'appstore', tone: 'tone-orange' },
        { title: '社团数量', value: this.rowsMap.teams.length, desc: '', icon: 'bank', tone: 'tone-green' },
        { title: '通知条数', value: this.rowsMap.notices.length, desc: '', icon: 'notification', tone: 'tone-violet' },
      ];
    },
    todoList() {
      const common = [
        { index: '01', title: '查看最新通知', desc: '' },
        { index: '02', title: '检查最近活动', desc: '' },
      ];
      if (this.userType === 0) {
        return common.concat([
          { index: '03', title: '维护平台基础数据', desc: '' },
          { index: '04', title: '检查制度与互动内容', desc: '' },
        ]);
      }
      if (this.userType === 1) {
        return common.concat([
          { index: '03', title: '审批入团申请', desc: '' },
          { index: '04', title: '发布活动与公告', desc: '' },
        ]);
      }
      return common.concat([
        { index: '03', title: '查看申请状态', desc: '' },
        { index: '04', title: '参与交流互动', desc: '' },
      ]);
    },
    roleSummary() {
      return '';
    },
    roleHighlights() {
      return [];
    },
  },
  async created() {
    await this.initPage();
  },
  methods: {
    async initPage() {
      if (!this.userInfo || !this.userInfo.id) {
        const resp = await getLoginUser(this.$store.getters.token);
        this.userInfo = resp.data;
        this.$store.commit('setUser', resp.data);
      }
      await this.loadOverview();
    },
    async loadOverview() {
      const token = this.$store.getters.token;
      const [teamsResp, noticesResp, activitiesResp] = await Promise.all([
        getPageTeams(1, 8, token, '', ''),
        getPageNotices(1, 8, token, '', ''),
        getPageActivities(1, 8, token, '', ''),
      ]);
      this.rowsMap.teams = (teamsResp.data && teamsResp.data.data) || [];
      this.rowsMap.notices = (noticesResp.data && noticesResp.data.data) || [];
      this.rowsMap.activities = (activitiesResp.data && activitiesResp.data.data) || [];
    },
    onMenuClick({ key }) {
      this.activeModule = key;
    },
    switchModule(key) {
      if (key) {
        this.activeModule = key;
      }
    },
    refreshCurrent() {
      if (this.activeModule === 'overview') {
        return this.loadOverview();
      }
      this.componentKey += 1;
      return Promise.resolve();
    },
    openAvatarModal() {
      const avatar = this.userInfo && this.userInfo.avatar ? String(this.userInfo.avatar) : '';
      this.selectedAvatarKey = DEFAULT_AVATARS[avatar] ? avatar : '';
      this.avatarVisible = true;
    },
    selectDefaultAvatar(key) {
      this.selectedAvatarKey = key;
      if (this.avatarPreview) {
        URL.revokeObjectURL(this.avatarPreview);
      }
      this.avatarPreview = DEFAULT_AVATARS[key];
      this.selectedAvatarFile = null;
      if (this.$refs.avatarInput) {
        this.$refs.avatarInput.value = '';
      }
    },
    chooseAvatarFile() {
      if (this.$refs.avatarInput) {
        this.$refs.avatarInput.click();
      }
    },
    onAvatarFileChange(event) {
      const file = event.target.files && event.target.files[0];
      if (!file) return;
      if (!file.type.startsWith('image/')) {
        this.$message.warning('请选择图片文件');
        return;
      }
      if (file.size > 2 * 1024 * 1024) {
        this.$message.warning('头像图片不能超过 2MB');
        return;
      }
      if (this.avatarPreview) {
        URL.revokeObjectURL(this.avatarPreview);
      }
      this.selectedAvatarKey = '';
      this.selectedAvatarFile = file;
      this.avatarPreview = URL.createObjectURL(file);
    },
    clearSelectedAvatar() {
      this.selectedAvatarFile = null;
      this.selectedAvatarKey = '';
      if (this.avatarPreview) {
        if (this.avatarPreview.startsWith('blob:')) {
          URL.revokeObjectURL(this.avatarPreview);
        }
      }
      this.avatarPreview = '';
      if (this.$refs.avatarInput) {
        this.$refs.avatarInput.value = '';
      }
    },
    closeAvatarModal() {
      this.avatarVisible = false;
      this.clearSelectedAvatar();
    },
    async saveAvatar() {
      this.avatarSaving = true;
      try {
        if (this.selectedAvatarFile) {
          const resp = await uploadAvatar(this.$store.getters.token, this.selectedAvatarFile);
          this.handleUserUpdated(resp.data);
        } else if (this.selectedAvatarKey) {
          const nextUser = { ...this.userInfo, avatar: this.selectedAvatarKey };
          await this.$apiFallbackSave(nextUser);
        } else {
          this.$message.warning('请先选择头像图片或预设头像');
          return;
        }
        this.$message.success('头像更新成功');
        this.closeAvatarModal();
      } finally {
        this.avatarSaving = false;
      }
    },
    async $apiFallbackSave(nextUser) {
      const resp = await updateLoginUserInfo({
        token: this.$store.getters.token,
        avatar: nextUser.avatar,
      });
      this.handleUserUpdated(resp.data);
    },
    handleUserUpdated(user) {
      this.userInfo = user;
      this.$store.commit('setUser', user);
    },
    async logoutNow() {
      try {
        if (this.$store.getters.token) {
          await exit(this.$store.getters.token);
        }
      } finally {
        this.$store.commit('clearSession');
        this.$router.replace('/login');
      }
    },
  },
  beforeDestroy() {
    if (this.avatarPreview) {
      URL.revokeObjectURL(this.avatarPreview);
    }
  },
};
</script>

<style scoped>
.workspace-shell {
  min-height: 100vh;
  position: relative;
  padding-left: 0;
  transition: padding-left 0.25s ease;
  background: #f6f7fb;
}

.workspace-shell--collapsed {
  padding-left: 72px;
}

.workspace-sider {
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 1200;
  background: #0f172a;
  box-shadow: 18px 0 26px rgba(15, 23, 42, 0.18);
}

.brand-panel {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 22px 18px 10px;
}

.brand-avatar-button {
  padding: 0;
  border: 0;
  background: transparent;
  cursor: pointer;
}

.brand-avatar {
  width: 42px;
  height: 42px;
  border-radius: 15px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.28);
}

.brand-avatar--fallback {
  background: linear-gradient(135deg, #f59e0b, #fb7185);
  color: #fff;
  font-size: 19px;
  font-weight: 700;
}

.brand-avatar--image img,
.header-avatar img,
.avatar-upload-preview img {
  width: 100%;
  height: 100%;
}

.avatar-image {
  display: block;
}

.avatar-image--preset {
  object-fit: contain;
  object-position: center;
  background: linear-gradient(135deg, #f59e0b, #fb7185);
  padding: 2px;
}

.avatar-image--upload {
  object-fit: cover;
  object-position: center;
}

.brand-title {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}

.brand-subtitle {
  color: rgba(255, 255, 255, 0.66);
  font-size: 12px;
}

.pin-switch {
  padding: 0 18px 16px;
}

.pin-button {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.12);
  color: #fff;
}

.workspace-menu {
  background: transparent;
  border-right: 0;
}

.workspace-header {
  height: auto;
  min-height: 76px;
  padding: 16px 24px;
  background: #ffffff;
  border-bottom: 1px solid #e6e8ef;
}

.header-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.header-title {
  color: #0f172a;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
}

.header-subtitle {
  color: #64748b;
  font-size: 13px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-avatar {
  width: 52px;
  height: 52px;
  border-radius: 18px;
  overflow: hidden;
  border: 0;
  background: linear-gradient(135deg, #f59e0b, #fb7185);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 22px rgba(37, 99, 235, 0.22);
}

.workspace-content {
  padding: 24px;
}

.hero-panel {
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: 1.4fr 0.8fr;
  gap: 24px;
  padding: 30px;
  margin-bottom: 18px;
  border-radius: 32px;
  background: #ffffff;
  border: 1px solid #e6e8ef;
  box-shadow: none;
}

.hero-copy {
  position: relative;
  z-index: 1;
}

.hero-kicker {
  margin: 0 0 10px;
  color: #64748b;
  font-size: 12px;
  letter-spacing: 2px;
}

.hero-panel h1 {
  margin: 0 0 14px;
  color: #0f172a;
  font-size: 36px;
}

.hero-text {
  max-width: 780px;
  margin: 0;
  color: rgba(255, 255, 255, 0.82);
  line-height: 1.8;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.hero-tag {
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  font-size: 12px;
}

.hero-visual {
  position: relative;
  min-height: 200px;
}

.hero-poster {
  display: none;
}

.visual-card {
  position: absolute;
  right: 0;
  width: 220px;
  padding: 18px;
  border-radius: 24px;
  background: #ffffff;
  border: 1px solid #e6e8ef;
  color: #0f172a;
  box-shadow: 0 8px 16px rgba(15, 23, 42, 0.08);
}

.visual-card-top {
  top: 10px;
}

.visual-card-bottom {
  bottom: 10px;
  right: 50px;
}

.visual-label {
  color: #6b7280;
  font-size: 12px;
}

.visual-value {
  margin: 10px 0 6px;
  font-size: 32px;
  font-weight: 700;
}

.visual-desc {
  color: #6b7280;
}

.metrics-row {
  margin-bottom: 16px;
}

.metric-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  border-radius: 24px;
  background: #ffffff;
  border: 1px solid #e6e8ef;
  box-shadow: none;
}

.metric-icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.tone-blue {
  background: rgba(47, 74, 124, 0.12);
  color: #2f4a7c;
}

.tone-orange {
  background: rgba(91, 107, 136, 0.12);
  color: #5b6b88;
}

.tone-green {
  background: rgba(63, 125, 95, 0.12);
  color: #3f7d5f;
}

.tone-violet {
  background: rgba(91, 107, 136, 0.12);
  color: #5b6b88;
}

.metric-title {
  color: #64748b;
  font-size: 13px;
}

.metric-value {
  margin-top: 6px;
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
}

.metric-desc {
  margin-top: 6px;
  color: #94a3b8;
  font-size: 12px;
}

.content-card {
  border-radius: 18px;
  border: 1px solid #e6e8ef;
  box-shadow: none;
  overflow: hidden;
}

.notice-card /deep/ .ant-card-body {
  padding-top: 12px;
}

.notice-banner {
  padding: 18px 20px;
  margin-bottom: 14px;
  border-radius: 18px;
  background: #f6f7fb;
  border: 1px solid #e6e8ef;
}

.notice-banner__title {
  color: #0f172a;
  font-size: 17px;
  font-weight: 700;
}

.notice-banner__desc {
  margin-top: 6px;
  max-width: 420px;
  color: #475569;
  line-height: 1.7;
}

.overview-lower {
  margin-top: 16px;
}

.accent-card {
  background: #ffffff;
}

.todo-list,
.feed-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.todo-item,
.feed-item {
  display: flex;
  gap: 14px;
  padding: 14px;
  border-radius: 18px;
  background: #f6f7fb;
}

.todo-index {
  width: 38px;
  height: 38px;
  border-radius: 14px;
  background: #dbeafe;
  color: #1d4ed8;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.todo-title,
.feed-title,
.activity-title,
.role-intro-title {
  color: #0f172a;
  font-weight: 700;
}

.todo-desc,
.feed-desc,
.feed-meta,
.role-intro-text,
.activity-meta,
.activity-time,
.activity-desc {
  color: #64748b;
}

.feed-dot {
  width: 10px;
  height: 10px;
  margin-top: 8px;
  border-radius: 999px;
  background: #2f4a7c;
}

.role-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 14px;
}

.role-badge {
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  color: #1d4ed8;
}

.activity-board {
  display: grid;
  gap: 14px;
}

.activity-tile {
  padding: 16px 18px;
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid #e6e8ef;
}

.avatar-upload-panel {
  display: flex;
  gap: 18px;
  align-items: center;
}

.avatar-upload-preview {
  width: 96px;
  height: 96px;
  border-radius: 28px;
  overflow: hidden;
  background: linear-gradient(135deg, #f59e0b, #fb7185);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34px;
  font-weight: 700;
}

.avatar-upload-copy {
  flex: 1;
}

.avatar-upload-title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.avatar-upload-desc {
  margin-top: 6px;
  color: #64748b;
}

.avatar-file-input {
  display: none;
}

.avatar-upload-actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.avatar-library {
  margin-top: 18px;
}

.avatar-library__title {
  color: #0f172a;
  font-size: 13px;
  font-weight: 600;
}

.avatar-library__grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  margin-top: 10px;
}

.avatar-library__item {
  padding: 4px;
  border: 1px solid #dbe4f0;
  border-radius: 16px;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.avatar-library__item img {
  display: block;
  width: 100%;
  border-radius: 12px;
}

.avatar-library__item:hover,
.avatar-library__item--active {
  border-color: #2563eb;
  box-shadow: 0 12px 20px rgba(37, 99, 235, 0.16);
  transform: translateY(-1px);
}

.avatar-file-name {
  margin-top: 12px;
  color: #475569;
}

@media (max-width: 1200px) {
  .hero-panel {
    grid-template-columns: 1fr;
  }

  .hero-visual {
    min-height: 240px;
  }
}

@media (max-width: 768px) {
  .workspace-content {
    padding: 16px;
  }

  .header-main {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    flex-wrap: wrap;
  }

  .hero-panel h1 {
    font-size: 28px;
  }

  .avatar-upload-panel {
    flex-direction: column;
    align-items: flex-start;
  }

  .avatar-library__grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
