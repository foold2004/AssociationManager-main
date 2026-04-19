<template>
  <a-layout class="shell">
    <aside
      class="shell-sidebar"
      :class="{
        'is-expanded': appStore.navExpanded,
        'is-overlay-open': appStore.mobileNavVisible && isMobile,
      }"
      @mouseenter="!isMobile && appStore.expandNav()"
      @mouseleave="!isMobile && !appStore.navPinned && appStore.collapseNav()"
    >
      <div class="sidebar-head">
        <div v-if="userAvatarUrl" class="brand-mark brand-mark--image">
          <img :src="userAvatarUrl" alt="avatar" />
        </div>
        <div v-else class="brand-mark">{{ userInitial }}</div>

        <transition name="fade-slide">
          <div v-if="appStore.navExpanded" class="brand-copy">
            <strong>Association</strong>
            <span>{{ roleLabel }}</span>
          </div>
        </transition>
      </div>

      <button class="nav-toggle" type="button" @click="toggleNavPin">
        <icon-menu />
      </button>

      <nav class="nav-list">
        <button
          v-for="item in menuModules"
          :key="item.key"
          class="nav-item"
          :class="{ active: item.key === currentModuleKey }"
          type="button"
          @click="goModule(item.key)"
        >
          <component :is="item.icon" class="nav-icon" />
          <transition name="fade-slide">
            <span v-if="appStore.navExpanded" class="nav-label">{{ item.label }}</span>
          </transition>
        </button>
      </nav>
    </aside>

    <div
      v-if="isMobile && appStore.mobileNavVisible"
      class="shell-mask"
      @click="appStore.hideMobileNav()"
    />

    <a-layout class="shell-main">
      <header class="shell-topbar">
        <div class="topbar-left">
          <button class="topbar-menu" type="button" @click="toggleResponsiveNav">
            <icon-menu />
          </button>
          <div>
            <div class="topbar-title">{{ currentModule.label }}</div>
            <div class="topbar-subtitle">{{ currentDateLabel }}</div>
          </div>
        </div>

        <div class="topbar-actions">
          <a-button type="text" @click="refreshPage">
            <template #icon><icon-refresh /></template>
            刷新
          </a-button>
          <a-button type="text" status="danger" @click="handleLogout">退出登录</a-button>
          <a-dropdown>
            <a-button type="outline">
              {{ userName }}
              <template #icon><icon-user /></template>
            </a-button>
            <template #content>
              <a-doption @click="goModule('profile')">个人中心</a-doption>
            </template>
          </a-dropdown>
        </div>
      </header>

      <main class="shell-content">
        <div v-if="booting" class="loading-wrap">
          <a-spin size="large" />
        </div>
        <component
          :is="currentView"
          v-else
          :title="currentModule.title"
          :subtitle="currentModule.subtitle"
        />
      </main>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import { IconMenu, IconRefresh, IconUser } from '@arco-design/web-vue/es/icon';
import { useAuthStore } from '@/stores/auth';
import { useAppStore } from '@/stores/app';
import { findModule, getModulesByRole } from '@/config/modules';
import { getLoginUser, logout } from '@/api';
import { resolveAvatarUrl } from '@/utils/avatar';
import OverviewView from '@/views/dashboard/OverviewView.vue';
import ProfileView from '@/views/profile/ProfileView.vue';
import NoticesView from '@/views/notices/NoticesView.vue';
import ApplyLogsView from '@/views/apply/ApplyLogsView.vue';
import TeamsView from '@/views/teams/TeamsView.vue';
import FeesView from '@/views/fees/FeesView.vue';
import RulesView from '@/views/rules/RulesView.vue';
import UsersView from '@/views/users/UsersView.vue';
import TeamTypesView from '@/views/team-types/TeamTypesView.vue';
import MembersView from '@/views/members/MembersView.vue';
import ActivitiesView from '@/views/activities/ActivitiesView.vue';
import ActiveLogsView from '@/views/active-logs/ActiveLogsView.vue';
import InteractionsView from '@/views/interactions/InteractionsView.vue';
import PlaceholderView from '@/views/shared/PlaceholderView.vue';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const appStore = useAppStore();

const booting = ref(true);
const isMobile = ref(false);

const moduleComponentMap = {
  overview: OverviewView,
  profile: ProfileView,
  users: UsersView,
  'team-types': TeamTypesView,
  teams: TeamsView,
  members: MembersView,
  activities: ActivitiesView,
  'active-logs': ActiveLogsView,
  notices: NoticesView,
  'apply-logs': ApplyLogsView,
  fees: FeesView,
  rules: RulesView,
  interactions: InteractionsView,
};

const updateViewport = () => {
  isMobile.value = window.innerWidth <= 960;
  if (isMobile.value) {
    appStore.collapseNav();
  }
};

const ensureUser = async () => {
  if (authStore.user?.id) return;
  try {
    const response = await getLoginUser(authStore.token);
    authStore.setUser(response.data || null);
  } catch (error) {
    authStore.clearSession();
    router.replace('/login');
  }
};

onMounted(async () => {
  updateViewport();
  window.addEventListener('resize', updateViewport);
  await ensureUser();
  booting.value = false;
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateViewport);
});

const userType = computed(() => Number(authStore.userType));
const userName = computed(() => authStore.user?.name || authStore.user?.userName || '当前用户');
const userInitial = computed(() => String(userName.value || '用').slice(0, 1));
const userAvatarUrl = computed(() => resolveAvatarUrl(authStore.user?.avatar));

const roleLabel = computed(() => {
  if (userType.value === 0) return '系统管理员';
  if (userType.value === 1) return '社团管理员';
  return '学生';
});

const menuModules = computed(() => getModulesByRole(userType.value));
const currentModuleKey = computed(() => String(route.params.module || 'overview'));
const currentModule = computed(() => {
  return menuModules.value.find((item) => item.key === currentModuleKey.value) || findModule('overview');
});
const currentView = computed(() => moduleComponentMap[currentModuleKey.value] || PlaceholderView);

const currentDateLabel = computed(() => {
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long',
  }).format(new Date());
});

watch(
  () => [currentModuleKey.value, userType.value, booting.value],
  () => {
    if (booting.value) return;
    const allowed = menuModules.value.some((item) => item.key === currentModuleKey.value);
    if (!allowed) {
      router.replace('/app/overview');
    }
  },
  { immediate: true },
);

const goModule = (moduleKey) => {
  if (moduleKey === currentModuleKey.value) {
    if (isMobile.value) appStore.hideMobileNav();
    return;
  }

  router.push(`/app/${moduleKey}`);
  if (isMobile.value) {
    appStore.hideMobileNav();
  }
};

const toggleResponsiveNav = () => {
  if (isMobile.value) {
    appStore.toggleMobileNav();
    return;
  }
  if (appStore.navExpanded) {
    appStore.collapseNav();
  } else {
    appStore.expandNav();
  }
};

const toggleNavPin = () => {
  if (isMobile.value) {
    appStore.toggleMobileNav();
    return;
  }
  appStore.togglePinnedNav();
};

const refreshPage = () => {
  window.location.reload();
};

const handleLogout = async () => {
  try {
    if (authStore.token) {
      await logout(authStore.token);
    }
  } catch (error) {
    // Ignore remote logout failures and clear session locally.
  } finally {
    authStore.clearSession();
    appStore.hideMobileNav();
    Message.success('已退出登录');
    router.replace('/login');
  }
};
</script>

<style scoped>
.shell {
  min-height: 100vh;
  background: var(--page-bg);
}

.shell-sidebar {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 30;
  display: flex;
  flex-direction: column;
  width: 80px;
  height: 100vh;
  background: linear-gradient(180deg, #17213a 0%, #101828 100%);
  color: #f8fafc;
  box-shadow: 12px 0 32px rgba(15, 23, 42, 0.18);
  transition: width 0.24s ease, transform 0.24s ease;
}

.shell-sidebar.is-expanded {
  width: 236px;
}

.sidebar-head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 22px 18px 14px;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  font-size: 18px;
  font-weight: 700;
  box-shadow: 0 10px 22px rgba(59, 130, 246, 0.35);
}

.brand-mark--image {
  overflow: hidden;
  padding: 0;
  background: rgba(255, 255, 255, 0.08);
}

.brand-mark--image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.brand-copy {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.brand-copy strong {
  font-size: 15px;
  font-weight: 700;
}

.brand-copy span {
  color: rgba(226, 232, 240, 0.76);
  font-size: 12px;
}

.nav-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  margin: 0 20px 18px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  color: inherit;
  cursor: pointer;
}

.nav-list {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 6px;
  padding: 0 12px 16px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 48px;
  padding: 0 16px;
  border: none;
  border-radius: 14px;
  background: transparent;
  color: rgba(226, 232, 240, 0.88);
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, transform 0.2s ease;
}

.nav-item:hover,
.nav-item.active {
  background: rgba(59, 130, 246, 0.2);
  color: #ffffff;
}

.nav-item.active {
  transform: translateX(2px);
  box-shadow: inset 0 0 0 1px rgba(96, 165, 250, 0.35);
}

.nav-icon {
  flex-shrink: 0;
  font-size: 20px;
}

.nav-label {
  font-size: 14px;
  white-space: nowrap;
}

.shell-main {
  min-height: 100vh;
  margin-left: 80px;
}

.shell-topbar {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 24px;
  backdrop-filter: blur(18px);
  background: rgba(246, 247, 251, 0.88);
  border-bottom: 1px solid rgba(230, 232, 239, 0.95);
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.topbar-menu {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  background: #fff;
  color: var(--text-1);
  cursor: pointer;
}

.topbar-title {
  color: var(--text-1);
  font-size: 18px;
  font-weight: 700;
}

.topbar-subtitle {
  margin-top: 2px;
  color: var(--text-3);
  font-size: 12px;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.shell-content {
  padding: 24px;
}

.loading-wrap {
  display: grid;
  place-items: center;
  min-height: calc(100vh - 140px);
}

.shell-mask {
  position: fixed;
  inset: 0;
  z-index: 24;
  background: rgba(15, 23, 42, 0.28);
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.16s ease, transform 0.16s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-4px);
}

@media (max-width: 960px) {
  .shell-sidebar {
    transform: translateX(-100%);
  }

  .shell-sidebar.is-overlay-open {
    transform: translateX(0);
    width: 236px;
  }

  .shell-main {
    margin-left: 0;
  }

  .shell-topbar {
    padding: 16px;
  }

  .shell-content {
    padding: 16px;
  }
}
</style>





