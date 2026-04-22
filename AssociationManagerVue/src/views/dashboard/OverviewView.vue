<template>
  <div class="page-shell">
    <section class="page-block hero-bar">
      <div>
        <h2 class="page-section-title">欢迎回来，{{ displayName }}</h2>
      </div>
    </section>

    <section class="stats-grid">
      <article v-for="item in metrics" :key="item.label" class="stat-card">
        <span class="stat-label">{{ item.label }}</span>
        <strong class="stat-value">{{ item.value }}</strong>
      </article>
    </section>

    <a-grid :cols="24" :col-gap="16" :row-gap="16">
      <a-grid-item :span="8">
        <section class="page-card panel-card">
          <div class="section-head">
            <h3>待处理事项</h3>
          </div>
          <div class="task-list">
            <div v-for="task in todoList" :key="task.index" class="task-item">
              <span class="task-index">{{ task.index }}</span>
              <div class="task-content">
                <div class="task-title">{{ task.title }}</div>
                <div class="task-meta">{{ task.hint }}</div>
              </div>
            </div>
          </div>
        </section>
      </a-grid-item>

      <a-grid-item :span="16">
        <section class="page-card panel-card">
          <div class="section-head">
            <h3>最新通知</h3>
          </div>
          <a-empty v-if="notices.length === 0" description="暂无通知" />
          <div v-else class="feed-list">
            <article v-for="item in notices.slice(0, 6)" :key="item.id" class="feed-item">
              <div class="feed-title">{{ item.title }}</div>
              <div class="feed-meta">{{ item.teamName || '系统通知' }} · {{ formatDate(item.createTime) }}</div>
            </article>
          </div>
        </section>
      </a-grid-item>

      <a-grid-item :span="24">
        <section class="page-card panel-card">
          <div class="section-head">
            <h3>近期活动</h3>
          </div>
          <a-empty v-if="activities.length === 0" description="暂无活动" />
          <div v-else class="activity-grid">
            <article v-for="item in activities.slice(0, 4)" :key="item.id" class="activity-card">
              <div class="activity-title">{{ item.name }}</div>
              <div class="activity-meta">{{ item.teamName || '未指定社团' }}</div>
              <div class="activity-meta">{{ formatDate(item.activeTime) }}</div>
              <div class="activity-desc">{{ item.comm || '暂无简介' }}</div>
            </article>
          </div>
        </section>
      </a-grid-item>
    </a-grid>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { storeToRefs } from 'pinia';
import { useAuthStore } from '@/stores/auth';
import { getPageActivities, getPageNotices, getPageTeams } from '@/api';

const authStore = useAuthStore();
const { token, user, userType } = storeToRefs(authStore);

const loading = ref(false);
const teams = ref([]);
const notices = ref([]);
const activities = ref([]);
const teamCount = ref(0);
const noticeCount = ref(0);
const activityCount = ref(0);

const roleLabel = computed(() => {
  if (Number(userType.value) === 0) return '系统管理员';
  if (Number(userType.value) === 1) return '社团管理员';
  if (Number(userType.value) === 2) return '学生';
  return '未登录';
});

const displayName = computed(() => user.value?.name || user.value?.userName || '用户');

const metrics = computed(() => [
  { label: '当前身份', value: roleLabel.value },
  { label: '社团数量', value: teamCount.value },
  { label: '通知条数', value: noticeCount.value },
  { label: '活动条数', value: activityCount.value },
]);

const todoList = computed(() => {
  if (Number(userType.value) === 0) {
    return [
      { index: '01', title: '检查社团数据', hint: '核对基础信息与类型配置' },
      { index: '02', title: '查看费用记录', hint: '关注收入、支出与余额变化' },
      { index: '03', title: '处理系统通知', hint: '确认最新公告是否需要更新' },
    ];
  }
  if (Number(userType.value) === 1) {
    return [
      { index: '01', title: '维护社团信息', hint: '更新简介、成员与社团资料' },
      { index: '02', title: '查看费用变化', hint: '核对团费收入和支出明细' },
      { index: '03', title: '关注活动安排', hint: '查看近期活动和通知' },
    ];
  }
  return [
    { index: '01', title: '查看可加入社团', hint: '在社团管理中提交加入申请' },
    { index: '02', title: '查看个人缴费', hint: '在费用管理中核对缴费记录' },
    { index: '03', title: '关注近期活动', hint: '及时查看活动和通知信息' },
  ];
});

const extractPage = (response) => response?.data || {};

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '-');

async function loadOverview() {
  loading.value = true;
  try {
    const [teamsResp, noticesResp, activitiesResp] = await Promise.all([
      getPageTeams(1, 8, token.value, '', ''),
      getPageNotices(1, 8, token.value, '', ''),
      getPageActivities(1, 8, token.value, '', ''),
    ]);
    const teamsPage = extractPage(teamsResp);
    const noticesPage = extractPage(noticesResp);
    const activitiesPage = extractPage(activitiesResp);

    teams.value = teamsPage.data || [];
    notices.value = noticesPage.data || [];
    activities.value = activitiesPage.data || [];

    teamCount.value = Number(teamsPage.count || teams.value.length || 0);
    noticeCount.value = Number(noticesPage.count || notices.value.length || 0);
    activityCount.value = Number(activitiesPage.count || activities.value.length || 0);
  } finally {
    loading.value = false;
  }
}

onMounted(loadOverview);
</script>

<style scoped>
.hero-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.panel-card {
  padding: 20px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-head h3 {
  margin: 0;
  color: var(--text-primary);
  font-size: 16px;
}

.task-list,
.feed-list {
  display: grid;
  gap: 12px;
}

.task-item,
.feed-item,
.activity-card {
  padding: 14px 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--surface-muted);
}

.task-item {
  display: flex;
  gap: 12px;
}

.task-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: rgba(59, 130, 246, 0.1);
  color: var(--primary-color);
  font-size: 13px;
  font-weight: 700;
}

.task-content,
.feed-item,
.activity-card {
  min-width: 0;
}

.task-title,
.feed-title,
.activity-title {
  color: var(--text-primary);
  font-weight: 700;
}

.task-meta,
.feed-meta,
.activity-meta,
.activity-desc {
  margin-top: 6px;
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.6;
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

@media (max-width: 1024px) {
  .activity-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .hero-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .activity-grid {
    grid-template-columns: 1fr;
  }
}
</style>
