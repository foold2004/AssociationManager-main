<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-card__label">活动总数</span>
        <strong class="stat-card__value">{{ total }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">开放报名</span>
        <strong class="stat-card__value">{{ openCount }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">已通过报名</span>
        <strong class="stat-card__value">{{ approvedTotal }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">当前角色</span>
        <strong class="stat-card__value">{{ roleLabel }}</strong>
      </article>
    </section>

    <section class="page-block">
      <div class="page-toolbar">
        <div class="filter-row">
          <div class="filter-row__main">
            <a-input
              v-model="queryForm.teamName"
              allow-clear
              placeholder="输入社团名称"
              style="width: 220px"
              @press-enter="search"
            />
            <a-input
              v-model="queryForm.activeName"
              allow-clear
              placeholder="输入活动名称"
              style="width: 220px"
              @press-enter="search"
            />
            <a-button type="primary" @click="search">查询</a-button>
            <a-button @click="resetQuery">重置</a-button>
          </div>

          <div class="filter-row__actions">
            <a-button v-if="canCreateActivity" type="primary" @click="openCreate">新增活动</a-button>
            <a-button v-if="canReviewLogs" @click="router.push('/app/active-logs')">报名审批</a-button>
          </div>
        </div>
        <div v-if="activityScopeHint" class="scope-hint">{{ activityScopeHint }}</div>
      </div>
    </section>

    <section class="page-card table-card">
      <div class="page-card__body">
        <a-table :data="rows" :loading="loading" :pagination="false" row-key="id" :bordered="false">
          <template #columns>
            <a-table-column title="活动名称" data-index="name" :width="220" />
            <a-table-column title="所属社团" data-index="teamName" :width="160" />
            <a-table-column title="活动时间" :width="180">
              <template #cell="{ record }">
                {{ formatDate(record.activeTime) }}
              </template>
            </a-table-column>
            <a-table-column title="报名截止" :width="180">
              <template #cell="{ record }">
                {{ formatDate(record.enrollEndTime) }}
              </template>
            </a-table-column>
            <a-table-column title="报名人数" :width="120" align="center">
              <template #cell="{ record }">
                {{ Number(record.total || 0) }} / {{ Number(record.maxTotal || 0) }}
              </template>
            </a-table-column>
            <a-table-column title="活动状态" :width="120" align="center">
              <template #cell="{ record }">
                <a-tag :color="activityState(record).color">{{ activityState(record).text }}</a-tag>
              </template>
            </a-table-column>
            <a-table-column v-if="canSignUp" title="我的报名" :width="120" align="center">
              <template #cell="{ record }">
                <a-tag :color="signupState(record).color">{{ signupState(record).text }}</a-tag>
              </template>
            </a-table-column>
            <a-table-column title="详情" :width="90" align="center">
              <template #cell="{ record }">
                <a-button type="text" @click="showDetail(record)">查看</a-button>
              </template>
            </a-table-column>
            <a-table-column title="参与人员" :width="110" align="center">
              <template #cell="{ record }">
                <a-button type="text" @click="showParticipants(record)">查看</a-button>
              </template>
            </a-table-column>
            <a-table-column title="操作" :width="240" align="center" fixed="right">
              <template #cell="{ record }">
                <a-space wrap>
                  <a-button
                    v-if="canSignUp"
                    type="primary"
                    size="mini"
                    :disabled="signupButton(record).disabled"
                    @click="signUp(record)"
                  >
                    {{ signupButton(record).text }}
                  </a-button>
                  <a-button v-if="canManageActivity(record)" type="text" size="mini" @click="openEdit(record)">
                    编辑
                  </a-button>
                  <a-button
                    v-if="canManageActivity(record)"
                    type="text"
                    size="mini"
                    status="danger"
                    @click="removeActivity(record)"
                  >
                    删除
                  </a-button>
                </a-space>
              </template>
            </a-table-column>
          </template>
        </a-table>

        <div class="table-footer">
          <a-pagination
            :current="pageIndex"
            :page-size="pageSize"
            :total="total"
            show-total
            show-jumper
            show-page-size
            @change="changePage"
            @page-size-change="changePageSize"
          />
        </div>
      </div>
    </section>

    <a-modal
      v-model:visible="modalVisible"
      :title="editingId ? '编辑活动' : '新增活动'"
      width="760px"
      @before-ok="submitActivity"
    >
      <a-form ref="formRef" :model="form" layout="vertical">
        <a-grid :cols="2" :col-gap="16">
          <a-grid-item>
            <a-form-item field="name" label="活动名称" :rules="[{ required: true, message: '请输入活动名称' }]">
              <a-input v-model="form.name" placeholder="输入活动名称" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="teamId" label="所属社团" :rules="[{ required: true, message: '请选择所属社团' }]">
              <a-select v-model="form.teamId" placeholder="选择所属社团">
                <a-option v-for="item in teamOptions" :key="item.id" :value="item.id">{{ item.name }}</a-option>
              </a-select>
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item
              field="activeTime"
              label="活动时间"
              :rules="[{ required: true, message: '请选择活动时间' }]"
            >
              <a-date-picker
                v-model="form.activeTime"
                show-time
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item
              field="enrollEndTime"
              label="报名截止"
              :rules="[{ required: true, message: '请选择报名截止时间' }]"
            >
              <a-date-picker
                v-model="form.enrollEndTime"
                show-time
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="maxTotal" label="人数上限" :rules="[{ required: true, message: '请输入人数上限' }]">
              <a-input-number v-model="form.maxTotal" :min="1" :max="999" style="width: 100%" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="comm" label="活动概述">
              <a-input v-model="form.comm" placeholder="输入活动概述" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item :span="2">
            <a-form-item field="ask" label="活动要求">
              <a-textarea v-model="form.ask" :rows="4" placeholder="输入活动要求" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item :span="2">
            <a-form-item field="detail" label="活动详情">
              <a-textarea v-model="form.detail" :rows="6" placeholder="输入活动详情" />
            </a-form-item>
          </a-grid-item>
        </a-grid>
      </a-form>
    </a-modal>

    <a-drawer v-model:visible="detailVisible" title="活动详情" width="640px">
      <a-descriptions :column="1" bordered size="large">
        <a-descriptions-item label="活动名称">{{ currentRecord?.name || '-' }}</a-descriptions-item>
        <a-descriptions-item label="所属社团">{{ currentRecord?.teamName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="活动时间">{{ formatDate(currentRecord?.activeTime) }}</a-descriptions-item>
        <a-descriptions-item label="报名截止">{{ formatDate(currentRecord?.enrollEndTime) }}</a-descriptions-item>
        <a-descriptions-item label="报名状态">{{ signupState(currentRecord).text }}</a-descriptions-item>
        <a-descriptions-item label="活动概述">{{ currentRecord?.comm || '-' }}</a-descriptions-item>
        <a-descriptions-item label="活动要求">{{ currentRecord?.ask || '-' }}</a-descriptions-item>
        <a-descriptions-item label="活动详情">{{ currentRecord?.detail || '-' }}</a-descriptions-item>
      </a-descriptions>
    </a-drawer>

    <a-drawer v-model:visible="participantVisible" title="已通过参与人员" width="640px">
      <a-table :data="participantRows" :loading="participantLoading" :pagination="false" :bordered="false">
        <template #columns>
          <a-table-column title="姓名" data-index="userName" />
          <a-table-column title="手机号" data-index="userPhone" />
          <a-table-column title="报名时间">
            <template #cell="{ record }">
              {{ formatDate(record.createTime) }}
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import { useRouter } from 'vue-router';
import {
  addActiveLogs,
  addActivities,
  deleteActivities,
  getActiveLogs,
  getAllTeamList,
  getLoginUser,
  getManTeamList,
  getMyActiveStatuses,
  getPageActivities,
  updateActivities,
} from '@/api';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const pageIndex = ref(1);
const pageSize = ref(10);
const teamOptions = ref([]);
const myStatusMap = ref({});
const modalVisible = ref(false);
const editingId = ref('');
const formRef = ref();
const detailVisible = ref(false);
const participantVisible = ref(false);
const participantLoading = ref(false);
const participantRows = ref([]);
const currentRecord = ref(null);

const queryForm = reactive({
  teamName: '',
  activeName: '',
});

const createEmptyForm = () => ({
  id: '',
  name: '',
  comm: '',
  detail: '',
  ask: '',
  total: 0,
  maxTotal: 30,
  activeTime: '',
  enrollEndTime: '',
  teamId: '',
});

const form = reactive(createEmptyForm());

const userType = computed(() => Number(authStore.userType));
const canCreateActivity = computed(() => userType.value === 1);
const canReviewLogs = computed(() => userType.value === 0 || userType.value === 1);
const canSignUp = computed(() => userType.value === 1 || userType.value === 2);
const activityScopeHint = computed(() => {
  if (userType.value === 1) {
    return '可查看自己负责或已加入社团的活动；只能审批自己负责社团的报名。';
  }
  if (userType.value === 2) {
    return '可查看自己已加入社团的活动，并提交报名申请。';
  }
  return '';
});
const roleLabel = computed(() => {
  if (userType.value === 0) return '系统管理员';
  if (userType.value === 1) return '社团管理员';
  return '学生';
});
const openCount = computed(() => rows.value.filter((item) => activityState(item).text === '报名中').length);
const approvedTotal = computed(() => rows.value.reduce((sum, item) => sum + Number(item.total || 0), 0));

function resetForm() {
  Object.assign(form, createEmptyForm());
}

function formatDate(value) {
  return value ? String(value).replace('T', ' ').slice(0, 19) : '-';
}

async function ensureUser() {
  if (authStore.user?.id || !authStore.token) return;
  const response = await getLoginUser(authStore.token);
  authStore.setUser(response.data || null);
}

async function loadTeamOptions() {
  if (userType.value === 1) {
    const response = await getManTeamList(authStore.user?.id);
    teamOptions.value = response.data || [];
    return;
  }
  const response = await getAllTeamList();
  teamOptions.value = response.data || [];
}

async function loadMyStatuses() {
  if (!canSignUp.value) {
    myStatusMap.value = {};
    return;
  }
  try {
    const response = await getMyActiveStatuses({ token: authStore.token });
    const raw = response?.data;

    if (Array.isArray(raw)) {
      myStatusMap.value = raw.reduce((result, item) => {
        result[String(item.activeId)] = Number(item.status);
        return result;
      }, {});
      return;
    }

    if (raw && typeof raw === 'object') {
      myStatusMap.value = Object.entries(raw).reduce((result, [activeId, status]) => {
        result[String(activeId)] = Number(status);
        return result;
      }, {});
      return;
    }

    myStatusMap.value = {};
  } catch (error) {
    myStatusMap.value = {};
  }
}

async function loadActivities() {
  loading.value = true;
  try {
    const response = await getPageActivities({
      pageIndex: pageIndex.value,
      pageSize: pageSize.value,
      token: authStore.token,
      teamName: queryForm.teamName,
      activeName: queryForm.activeName,
    });
    rows.value = response.data?.data || [];
    total.value = Number(response.data?.count || 0);
  } finally {
    loading.value = false;
  }
}

function activityState(record) {
  const endTime = record?.enrollEndTime ? new Date(record.enrollEndTime).getTime() : 0;
  const now = Date.now();
  const totalCount = Number(record?.total || 0);
  const maxCount = Number(record?.maxTotal || 0);
  if (endTime && endTime < now) return { text: '已截止', color: 'gray' };
  if (maxCount > 0 && totalCount >= maxCount) return { text: '已满员', color: 'orange' };
  return { text: '报名中', color: 'green' };
}

function signupState(record) {
  const status = Number(myStatusMap.value[String(record?.id)]);
  if (status === 1) return { text: '已通过', color: 'green' };
  if (status === 2) return { text: '已驳回', color: 'red' };
  if (status === 0 && Number.isFinite(status)) return { text: '审核中', color: 'orange' };
  return { text: '未报名', color: 'gray' };
}

function signupButton(record) {
  const state = activityState(record);
  const status = myStatusMap.value[String(record?.id)];
  if (status === 1) return { text: '已通过', disabled: true };
  if (status === 2) return { text: '已驳回', disabled: true };
  if (status === 0 && Number.isFinite(Number(status))) return { text: '审核中', disabled: true };
  if (state.text === '已截止') return { text: '已截止', disabled: true };
  if (state.text === '已满员') return { text: '已满员', disabled: true };
  return { text: '报名参加', disabled: false };
}

function canManageActivity(record) {
  if (userType.value === 0) return true;
  return userType.value === 1 && String(record?.managerId || '') === String(authStore.user?.id || '');
}

function search() {
  pageIndex.value = 1;
  loadActivities();
}

function resetQuery() {
  queryForm.teamName = '';
  queryForm.activeName = '';
  pageIndex.value = 1;
  loadActivities();
}

function changePage(value) {
  pageIndex.value = value;
  loadActivities();
}

function changePageSize(value) {
  pageSize.value = value;
  pageIndex.value = 1;
  loadActivities();
}

function openCreate() {
  editingId.value = '';
  resetForm();
  form.teamId = teamOptions.value[0]?.id || '';
  formRef.value?.clearValidate?.();
  modalVisible.value = true;
}

function openEdit(record) {
  editingId.value = record.id;
  Object.assign(form, {
    id: record.id,
    name: record.name || '',
    comm: record.comm || '',
    detail: record.detail || '',
    ask: record.ask || '',
    total: Number(record.total || 0),
    maxTotal: Number(record.maxTotal || 0),
    activeTime: formatDate(record.activeTime),
    enrollEndTime: formatDate(record.enrollEndTime),
    teamId: record.teamId || '',
  });
  formRef.value?.clearValidate?.();
  modalVisible.value = true;
}

async function submitActivity() {
  const errors = await formRef.value?.validate();
  if (errors) return false;

  const payload = {
    ...form,
    token: authStore.token,
    total: Number(form.total || 0),
    maxTotal: Number(form.maxTotal || 0),
  };

  if (editingId.value) {
    await updateActivities(payload);
    Message.success('活动已更新');
  } else {
    await addActivities(payload);
    Message.success('活动已新增');
  }

  modalVisible.value = false;
  await loadActivities();
  return true;
}

function removeActivity(record) {
  Modal.confirm({
    title: '确认删除该活动？',
    content: record.name,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await deleteActivities({ token: authStore.token, id: record.id });
      Message.success('活动已删除');
      await loadActivities();
    },
  });
}

async function signUp(record) {
  const action = signupButton(record);
  if (action.disabled) {
    return;
  }
  await addActiveLogs({ token: authStore.token, activeId: record.id });
  Message.success('报名请求已提交');
  await Promise.all([loadMyStatuses(), loadActivities()]);
}

function showDetail(record) {
  currentRecord.value = record;
  detailVisible.value = true;
}

async function showParticipants(record) {
  currentRecord.value = record;
  participantVisible.value = true;
  participantLoading.value = true;
  try {
    const response = await getActiveLogs(record.id);
    participantRows.value = response.data || [];
  } finally {
    participantLoading.value = false;
  }
}

async function boot() {
  await ensureUser();
  const tasks = [loadTeamOptions(), loadActivities()];
  if (canSignUp.value) {
    tasks.push(loadMyStatuses());
  }
  await Promise.allSettled(tasks);
}

onMounted(boot);
</script>

<style scoped>
.scope-hint {
  margin-top: 14px;
  color: #4f6788;
  font-size: 14px;
  line-height: 1.6;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}
</style>
