<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-card__label">制度总数</span>
        <strong class="stat-card__value">{{ total }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">系统制度</span>
        <strong class="stat-card__value">{{ systemCount }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">社团制度</span>
        <strong class="stat-card__value">{{ teamCount }}</strong>
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
              placeholder="输入制度标题"
              style="width: 220px"
              @press-enter="search"
            />
            <a-input
              v-model="queryForm.teamName"
              allow-clear
              placeholder="输入社团名称"
              style="width: 220px"
              @press-enter="search"
            />
            <a-button type="primary" @click="search">查询</a-button>
            <a-button @click="resetQuery">重置</a-button>
          </div>

          <div class="filter-row__actions">
            <a-button v-if="canManage" type="primary" @click="openCreate">
              发布制度
            </a-button>
          </div>
        </div>
      </div>
    </section>

    <section class="page-card">
      <div class="page-card__body">
        <a-list :data="rows" :loading="loading" :bordered="false">
          <template #item="{ item }">
            <a-list-item>
              <div class="rule-item">
                <div class="rule-head">
                  <div>
                    <div class="rule-title">{{ item.title }}</div>
                    <div class="rule-meta">
                      <a-tag :color="item.teamId ? 'arcoblue' : 'gold'">
                        {{ item.teamName || '系统制度' }}
                      </a-tag>
                      <span>发布人：{{ item.userName || '-' }}</span>
                      <span>更新时间：{{ formatDate(item.updateTime || item.createTime) }}</span>
                    </div>
                  </div>

                  <a-space v-if="canEdit(item)">
                    <a-button type="text" @click="openEdit(item)">编辑</a-button>
                    <a-button type="text" status="danger" @click="removeRule(item)">删除</a-button>
                  </a-space>
                </div>

                <div class="rule-content">{{ item.content || '-' }}</div>
              </div>
            </a-list-item>
          </template>
        </a-list>

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
      :title="editingId ? '编辑制度' : '发布制度'"
      width="720px"
      @before-ok="submitRule"
    >
      <a-form ref="formRef" :model="form" layout="vertical">
        <a-form-item
          field="title"
          label="制度标题"
          :rules="[{ required: true, message: '请输入制度标题' }]"
        >
          <a-input v-model="form.title" placeholder="输入制度标题" />
        </a-form-item>
        <a-form-item
          field="teamId"
          label="所属社团"
          :rules="userType === 1 ? [{ required: true, message: '请选择所属社团' }] : []"
        >
          <a-select v-model="form.teamId" allow-clear placeholder="选择所属社团">
            <a-option v-for="item in teamOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-option>
          </a-select>
        </a-form-item>
        <a-form-item
          field="content"
          label="制度内容"
          :rules="[{ required: true, message: '请输入制度内容' }]"
        >
          <a-textarea
            v-model="form.content"
            :rows="8"
            :max-length="1000"
            placeholder="输入制度内容"
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
import { addRule, deleteRule, getAllTeamList, getManTeamList, getPageRules, updateRule } from '@/api';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const userType = computed(() => authStore.userType);
const canManage = computed(() => userType.value === 0 || userType.value === 1);
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
const formRef = ref();

const queryForm = reactive({
  title: '',
  teamName: '',
});

const form = reactive({
  title: '',
  teamId: '',
  content: '',
});

const systemCount = computed(() => rows.value.filter((item) => !item.teamId).length);
const teamCount = computed(() => rows.value.filter((item) => !!item.teamId).length);

const formatDate = (value) => {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
};

const loadTeamOptions = async () => {
  const response = userType.value === 1
    ? await getManTeamList(authStore.user?.id)
    : await getAllTeamList();
  teamOptions.value = response.data || [];
};

const loadRules = async () => {
  loading.value = true;
  try {
    const response = await getPageRules(
      pageIndex.value,
      pageSize.value,
      authStore.token,
      queryForm.title,
      queryForm.teamName,
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

const resetForm = () => {
  editingId.value = '';
  form.title = '';
  form.teamId = userType.value === 1 ? teamOptions.value[0]?.id || '' : '';
  form.content = '';
  formRef.value?.clearValidate?.();
};

const search = async () => {
  pageIndex.value = 1;
  await loadRules();
};

const resetQuery = async () => {
  queryForm.title = '';
  queryForm.teamName = '';
  pageIndex.value = 1;
  await loadRules();
};

const changePage = async (nextPage) => {
  pageIndex.value = nextPage;
  await loadRules();
};

const changePageSize = async (size) => {
  pageSize.value = size;
  pageIndex.value = 1;
  await loadRules();
};

const openCreate = () => {
  resetForm();
  modalVisible.value = true;
};

const openEdit = (record) => {
  editingId.value = record.id;
  form.title = record.title || '';
  form.teamId = record.teamId || '';
  form.content = record.content || '';
  modalVisible.value = true;
};

const canEdit = (record) => {
  if (!canManage.value) return false;
  if (userType.value === 0) return true;
  return record.userId === authStore.user?.id;
};

const submitRule = async () => {
  const errors = await formRef.value?.validate();
  if (errors) {
    return false;
  }

  const payload = {
    id: editingId.value || undefined,
    title: form.title,
    teamId: form.teamId || null,
    content: form.content,
  };

  if (editingId.value) {
    await updateRule(payload);
    Message.success('制度已更新');
  } else {
    await addRule(payload);
    Message.success('制度已发布');
  }

  modalVisible.value = false;
  await loadRules();
  return true;
};

const removeRule = (record) => {
  Modal.confirm({
    title: '确认删除这条制度？',
    content: record.title,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await deleteRule({ id: record.id });
      Message.success('制度已删除');
      await loadRules();
    },
  });
};

onMounted(async () => {
  await loadTeamOptions();
  await loadRules();
});
</script>

<style scoped>
.page-card__body {
  padding: 20px;
}

.rule-item {
  width: 100%;
}

.rule-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.rule-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-1);
}

.rule-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-top: 8px;
  color: var(--text-2);
  font-size: 13px;
}

.rule-content {
  margin-top: 16px;
  padding: 16px 18px;
  background: var(--surface-muted);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  color: var(--text-1);
  line-height: 1.8;
  white-space: pre-wrap;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}

@media (max-width: 768px) {
  .rule-head {
    flex-direction: column;
  }
}
</style>
