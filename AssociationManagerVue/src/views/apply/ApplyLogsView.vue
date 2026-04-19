<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-label">申请总数</span>
        <strong class="stat-value">{{ total }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-label">待审批</span>
        <strong class="stat-value">{{ pendingCount }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-label">已通过</span>
        <strong class="stat-value">{{ approvedCount }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-label">已驳回</span>
        <strong class="stat-value">{{ rejectedCount }}</strong>
      </article>
    </section>

    <section class="page-block">
      <div class="page-toolbar">
        <div class="toolbar-left">
          <a-input
            v-model="queryForm.teamName"
            allow-clear
            placeholder="输入社团名称"
            style="width: 220px"
            @press-enter="loadApplyLogs"
          />
          <a-input
            v-model="queryForm.userName"
            allow-clear
            placeholder="输入申请人姓名"
            style="width: 220px"
            @press-enter="loadApplyLogs"
          />
          <a-button type="primary" @click="search">查询</a-button>
          <a-button @click="resetQuery">重置</a-button>
        </div>
      </div>
    </section>

    <section class="page-card table-card">
      <a-table
        :bordered="false"
        :data="applyLogs"
        :loading="loading"
        :pagination="false"
        row-key="id"
      >
        <template #columns>
          <a-table-column title="社团名称" data-index="teamName" />
          <a-table-column title="申请人姓名" data-index="userName" />
          <a-table-column title="申请人性别" data-index="userGender" :width="120" align="center" />
          <a-table-column title="申请人电话" data-index="userPhone" />
          <a-table-column title="申请时间">
            <template #cell="{ record }">
              {{ formatDate(record.createTime) }}
            </template>
          </a-table-column>
          <a-table-column title="状态" :width="220" align="center">
            <template #cell="{ record }">
              <template v-if="Number(record.status) === 0">
                <a-space>
                  <a-button type="primary" size="small" @click="updateStatus(record, 1)">
                    通过
                  </a-button>
                  <a-button status="danger" size="small" @click="updateStatus(record, 2)">
                    驳回
                  </a-button>
                </a-space>
              </template>
              <template v-else>
                <a-tag :color="statusColor(record.status)">
                  {{ statusLabel(record.status) }}
                </a-tag>
              </template>
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
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import { storeToRefs } from 'pinia';
import { getPageApplyLogs, updApplyLogs } from '@/api';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const { token } = storeToRefs(authStore);

const loading = ref(false);
const applyLogs = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const total = ref(0);

const queryForm = reactive({
  teamName: '',
  userName: '',
});

const pendingCount = computed(() => applyLogs.value.filter((item) => Number(item.status) === 0).length);
const approvedCount = computed(() => applyLogs.value.filter((item) => Number(item.status) === 1).length);
const rejectedCount = computed(() => applyLogs.value.filter((item) => Number(item.status) === 2).length);

const extractPage = (response) => response?.data || {};

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '-');

const statusLabel = (status) => {
  if (Number(status) === 1) return '已通过';
  if (Number(status) === 2) return '已驳回';
  return '审核中';
};

const statusColor = (status) => {
  if (Number(status) === 1) return 'green';
  if (Number(status) === 2) return 'red';
  return 'orange';
};

const loadApplyLogs = async () => {
  loading.value = true;
  try {
    const response = await getPageApplyLogs(
      pageIndex.value,
      pageSize.value,
      token.value,
      queryForm.teamName,
      queryForm.userName,
    );
    const page = extractPage(response);
    applyLogs.value = page.data || [];
    total.value = Number(page.count || 0);
    pageIndex.value = Number(page.pageIndex || 1);
    pageSize.value = Number(page.pageSize || 10);
  } finally {
    loading.value = false;
  }
};

const search = async () => {
  pageIndex.value = 1;
  await loadApplyLogs();
};

const resetQuery = async () => {
  queryForm.teamName = '';
  queryForm.userName = '';
  pageIndex.value = 1;
  await loadApplyLogs();
};

const changePage = async (nextPage) => {
  pageIndex.value = nextPage;
  await loadApplyLogs();
};

const changePageSize = async (size) => {
  pageSize.value = size;
  pageIndex.value = 1;
  await loadApplyLogs();
};

const updateStatus = (record, status) => {
  const actionText = status === 1 ? '通过' : '驳回';
  Modal.confirm({
    title: `确认${actionText}这条入团申请？`,
    content: `${record.teamName} / ${record.userName}`,
    okButtonProps: status === 1 ? {} : { status: 'danger' },
    onOk: async () => {
      await updApplyLogs({
        ...record,
        token: token.value,
        status,
      });
      Message.success(`已${actionText}`);
      await loadApplyLogs();
    },
  });
};

onMounted(loadApplyLogs);
</script>

<style scoped>
.table-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}
</style>
