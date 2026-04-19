<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-card__label">公告总数</span>
        <strong class="stat-card__value">{{ total }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">置顶公告</span>
        <strong class="stat-card__value">{{ topCount }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">系统公告</span>
        <strong class="stat-card__value">{{ systemCount }}</strong>
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
              v-model="queryForm.title"
              allow-clear
              placeholder="输入公告标题"
              style="width: 220px"
              @press-enter="search"
            />
            <a-select
              v-model="queryForm.teamFilter"
              allow-clear
              placeholder="选择公告范围"
              style="width: 220px"
            >
              <a-option value="">全部公告</a-option>
              <a-option value="__system__">系统公告</a-option>
              <a-option v-for="item in teamOptions" :key="item.id" :value="item.id">
                {{ item.name }}
              </a-option>
            </a-select>
            <a-button type="primary" @click="search">查询</a-button>
            <a-button @click="resetQuery">重置</a-button>
          </div>

          <div class="filter-row__actions">
            <a-button v-if="canManageNotices" type="primary" @click="openCreate">
              发布公告
            </a-button>
          </div>
        </div>
      </div>
    </section>

    <section class="page-card table-card">
      <div class="page-card__body">
        <a-table
          :bordered="false"
          :data="rows"
          :loading="loading"
          :pagination="false"
          row-key="id"
        >
          <template #columns>
            <a-table-column title="公告标题" :width="260">
              <template #cell="{ record }">
                <div class="title-cell">
                  <a-tag v-if="Number(record.isTop) === 1" color="red">置顶</a-tag>
                  <span class="title-text">{{ record.title || '-' }}</span>
                </div>
              </template>
            </a-table-column>
            <a-table-column title="发布范围" :width="140">
              <template #cell="{ record }">
                <a-tag :color="record.teamId ? 'arcoblue' : 'gold'">
                  {{ record.teamName || '系统公告' }}
                </a-tag>
              </template>
            </a-table-column>
            <a-table-column title="发布时间" :width="180">
              <template #cell="{ record }">
                {{ formatDate(record.createTime) }}
              </template>
            </a-table-column>
            <a-table-column title="公告内容">
              <template #cell="{ record }">
                <div class="detail-cell">{{ record.detail || '-' }}</div>
              </template>
            </a-table-column>
            <a-table-column title="操作" :width="220" align="center">
              <template #cell="{ record }">
                <a-space wrap>
                  <a-button
                    v-if="canTopNotices"
                    type="text"
                    @click="toggleTop(record)"
                  >
                    {{ Number(record.isTop) === 1 ? '取消置顶' : '设为置顶' }}
                  </a-button>
                  <a-button
                    v-if="canEdit(record)"
                    type="text"
                    @click="openEdit(record)"
                  >
                    编辑
                  </a-button>
                  <a-button
                    v-if="canDelete(record)"
                    type="text"
                    status="danger"
                    @click="removeNotice(record)"
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
      :title="editingId ? '编辑公告' : '发布公告'"
      width="680px"
      @before-ok="submitNotice"
    >
      <a-form ref="formRef" :model="form" layout="vertical">
        <a-form-item
          field="title"
          label="公告标题"
          :rules="[{ required: true, message: '请输入公告标题' }]"
        >
          <a-input v-model="form.title" placeholder="输入公告标题" />
        </a-form-item>

        <a-form-item v-if="userType === 0" field="publishMode" label="发布范围">
          <a-radio-group v-model="publishMode" type="button">
            <a-radio value="system">系统公告</a-radio>
            <a-radio value="team">社团公告</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item
          v-if="userType === 1 || (userType === 0 && publishMode === 'team')"
          field="teamId"
          label="所属社团"
          :rules="[{ required: true, message: '请选择所属社团' }]"
        >
          <a-select v-model="form.teamId" placeholder="选择所属社团">
            <a-option v-for="item in teamOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-option>
          </a-select>
        </a-form-item>

        <a-form-item v-if="userType === 0" field="isTopChecked" label="公告设置">
          <a-checkbox v-model="form.isTopChecked">发布为置顶公告</a-checkbox>
        </a-form-item>

        <a-form-item
          field="detail"
          label="公告内容"
          :rules="[{ required: true, message: '请输入公告内容' }]"
        >
          <a-textarea
            v-model="form.detail"
            :max-length="500"
            :rows="6"
            placeholder="输入公告内容"
            show-word-limit
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import {
  addNotices,
  deleteNotices,
  getAllTeamList,
  getManTeamList,
  getPageNotices,
  toggleNoticeTop,
  updateNotices,
} from '@/api';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const userType = computed(() => authStore.userType);
const canManageNotices = computed(() => userType.value === 0 || userType.value === 1);
const canTopNotices = computed(() => userType.value === 0);
const roleLabel = computed(() => {
  if (userType.value === 0) return '系统管理员';
  if (userType.value === 1) return '社团管理员';
  return '学生';
});

const loading = ref(false);
const rows = ref([]);
const teamOptions = ref([]);
const total = ref(0);
const pageIndex = ref(1);
const pageSize = ref(10);
const modalVisible = ref(false);
const editingId = ref('');
const publishMode = ref('system');
const formRef = ref();

const queryForm = reactive({
  title: '',
  teamFilter: '',
});

const form = reactive({
  title: '',
  detail: '',
  teamId: '',
  isTopChecked: false,
});

const topCount = computed(() => rows.value.filter((item) => Number(item.isTop) === 1).length);
const systemCount = computed(() => rows.value.filter((item) => !item.teamId).length);

const formatDate = (value) => {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
};

const resetForm = () => {
  editingId.value = '';
  publishMode.value = userType.value === 1 ? 'team' : 'system';
  form.title = '';
  form.detail = '';
  form.teamId = userType.value === 1 ? teamOptions.value[0]?.id || '' : '';
  form.isTopChecked = false;
  formRef.value?.clearValidate?.();
};

const loadTeams = async () => {
  const response = userType.value === 1
    ? await getManTeamList(authStore.user?.id)
    : await getAllTeamList();
  teamOptions.value = response.data || [];
};

const loadNotices = async () => {
  loading.value = true;
  try {
    const isSystem = queryForm.teamFilter === '__system__';
    const response = await getPageNotices(
      pageIndex.value,
      pageSize.value,
      authStore.token,
      queryForm.title,
      '',
      isSystem ? '' : queryForm.teamFilter,
      isSystem ? 1 : 0,
    );
    const page = response.data || {};
    rows.value = page.data || [];
    total.value = Number(page.count || 0);
    pageIndex.value = Number(page.pageIndex || pageIndex.value);
    pageSize.value = Number(page.pageSize || pageSize.value);
  } finally {
    loading.value = false;
  }
};

const search = async () => {
  pageIndex.value = 1;
  await loadNotices();
};

const resetQuery = async () => {
  queryForm.title = '';
  queryForm.teamFilter = '';
  pageIndex.value = 1;
  await loadNotices();
};

const changePage = async (nextPage) => {
  pageIndex.value = nextPage;
  await loadNotices();
};

const changePageSize = async (size) => {
  pageSize.value = size;
  pageIndex.value = 1;
  await loadNotices();
};

const openCreate = () => {
  resetForm();
  modalVisible.value = true;
};

const openEdit = (record) => {
  editingId.value = record.id;
  publishMode.value = record.teamId ? 'team' : 'system';
  form.title = record.title || '';
  form.detail = record.detail || '';
  form.teamId = record.teamId || '';
  form.isTopChecked = Number(record.isTop) === 1;
  modalVisible.value = true;
};

const canEdit = (record) => {
  if (userType.value === 0) return true;
  return userType.value === 1 && !!record.teamId;
};

const canDelete = (record) => {
  if (userType.value === 0) return true;
  return userType.value === 1 && !!record.teamId;
};

const submitNotice = async () => {
  const errors = await formRef.value?.validate();
  if (errors) {
    return false;
  }

  const payload = {
    id: editingId.value || undefined,
    title: form.title,
    detail: form.detail,
    teamId: userType.value === 0
      ? (publishMode.value === 'team' ? form.teamId : null)
      : form.teamId,
    isTop: userType.value === 0 && form.isTopChecked ? 1 : 0,
  };

  if (editingId.value) {
    await updateNotices(payload);
    Message.success('公告已更新');
  } else {
    await addNotices(payload);
    Message.success('公告已发布');
  }

  modalVisible.value = false;
  await loadNotices();
  return true;
};

const removeNotice = (record) => {
  Modal.confirm({
    title: '确认删除这条公告？',
    content: record.title,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await deleteNotices({ id: record.id });
      Message.success('公告已删除');
      await loadNotices();
    },
  });
};

const toggleTop = async (record) => {
  const nextIsTop = Number(record.isTop) === 1 ? 0 : 1;
  await toggleNoticeTop({ id: record.id, isTop: nextIsTop });
  Message.success(nextIsTop === 1 ? '已设为置顶' : '已取消置顶');
  await loadNotices();
};

onMounted(async () => {
  await loadTeams();
  await loadNotices();
});
</script>

<style scoped>
.page-card__body {
  padding: 20px;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  font-weight: 600;
  color: var(--text-1);
}

.detail-cell {
  display: -webkit-box;
  overflow: hidden;
  color: var(--text-2);
  line-height: 1.7;
  white-space: pre-wrap;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}
</style>
