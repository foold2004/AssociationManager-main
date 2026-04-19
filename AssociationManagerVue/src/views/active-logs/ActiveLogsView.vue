<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-card__label">报名总数</span>
        <strong class="stat-card__value">{{ total }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">待审核</span>
        <strong class="stat-card__value">{{ pendingCount }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">已通过</span>
        <strong class="stat-card__value">{{ approvedCount }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">已驳回</span>
        <strong class="stat-card__value">{{ rejectedCount }}</strong>
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
            <a-input
              v-if="canReview"
              v-model="queryForm.userName"
              allow-clear
              placeholder="输入报名人姓名"
              style="width: 220px"
              @press-enter="search"
            />
            <a-select v-model="queryForm.status" allow-clear placeholder="筛选状态" style="width: 160px">
              <a-option v-for="item in statusOptions" :key="item.value" :value="item.value">
                {{ item.label }}
              </a-option>
            </a-select>
            <a-button type="primary" @click="search">查询</a-button>
            <a-button @click="resetQuery">重置</a-button>
          </div>
        </div>
      </div>
    </section>

    <section class="page-card table-card">
      <div class="page-card__body">
        <a-table
          :data="rows"
          :loading="loading"
          :pagination="false"
          row-key="id"
          :bordered="false"
          :scroll="{ x: canReview ? 1480 : 1180 }"
        >
          <template #columns>
            <a-table-column title="活动名称" data-index="activeName" :width="180" />
            <a-table-column title="所属社团" data-index="teamName" :width="160" />
            <a-table-column v-if="canReview" title="报名人" data-index="userName" :width="120" />
            <a-table-column v-if="canReview" title="手机号" data-index="userPhone" :width="150" />
            <a-table-column title="报名时间" :width="180">
              <template #cell="{ record }">
                {{ formatDate(record.createTime) }}
              </template>
            </a-table-column>
            <a-table-column title="申请状态" :width="120" align="center">
              <template #cell="{ record }">
                <a-tag :color="statusColor(record.status)">
                  {{ statusLabel(record.status) }}
                </a-tag>
              </template>
            </a-table-column>
            <a-table-column title="审核时间" :width="180">
              <template #cell="{ record }">
                {{ formatDate(record.reviewTime) }}
              </template>
            </a-table-column>
            <a-table-column title="审核备注" :width="260">
              <template #cell="{ record }">
                <div class="review-remark-cell">{{ record.reviewRemark || '-' }}</div>
              </template>
            </a-table-column>
            <a-table-column title="操作" :width="240" align="center">
              <template #cell="{ record }">
                <a-space wrap>
                  <template v-if="canReview">
                    <a-button
                      v-if="Number(record.status) !== 1"
                      type="primary"
                      size="mini"
                      @click="openReview(record, 1)"
                    >
                      通过
                    </a-button>
                    <a-button
                      v-if="Number(record.status) !== 2"
                      type="outline"
                      status="warning"
                      size="mini"
                      @click="openReview(record, 2)"
                    >
                      驳回
                    </a-button>
                    <a-button type="text" size="mini" status="danger" @click="removeLog(record)">
                      删除
                    </a-button>
                  </template>
                  <template v-else>
                    <a-button type="text" size="mini" status="danger" @click="removeLog(record)">
                      取消报名
                    </a-button>
                  </template>
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
      v-model:visible="reviewVisible"
      title="审核报名"
      width="560px"
      @before-ok="submitReview"
    >
      <a-form ref="reviewFormRef" :model="reviewForm" layout="vertical">
        <a-form-item label="活动名称">
          <a-input :model-value="currentRecord?.activeName || '-'" readonly />
        </a-form-item>
        <a-form-item label="报名人">
          <a-input :model-value="currentRecord?.userName || '-'" readonly />
        </a-form-item>
        <a-form-item label="审核结果">
          <a-tag :color="statusColor(reviewForm.status)">
            {{ statusLabel(reviewForm.status) }}
          </a-tag>
        </a-form-item>
        <a-form-item field="reviewRemark" label="审核备注">
          <a-textarea v-model="reviewForm.reviewRemark" :rows="4" placeholder="输入审核备注" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import { deleteActiveLogs, getPageActiveLogs, updateActiveLogs } from '@/api';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const pageIndex = ref(1);
const pageSize = ref(10);
const reviewVisible = ref(false);
const reviewFormRef = ref();
const currentRecord = ref(null);

const queryForm = reactive({
  teamName: '',
  activeName: '',
  userName: '',
  status: undefined,
});

const reviewForm = reactive({
  id: '',
  status: 1,
  reviewRemark: '',
});

const userType = computed(() => Number(authStore.userType));
const canReview = computed(() => userType.value === 0 || userType.value === 1);
const pendingCount = computed(() => rows.value.filter((item) => Number(item.status) === 0).length);
const approvedCount = computed(() => rows.value.filter((item) => Number(item.status) === 1).length);
const rejectedCount = computed(() => rows.value.filter((item) => Number(item.status) === 2).length);

const statusOptions = [
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 },
  { label: '已驳回', value: 2 },
];

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '-');

const statusLabel = (status) => {
  if (Number(status) === 1) return '已通过';
  if (Number(status) === 2) return '已驳回';
  return '待审核';
};

const statusColor = (status) => {
  if (Number(status) === 1) return 'green';
  if (Number(status) === 2) return 'red';
  return 'orange';
};

const loadLogs = async () => {
  loading.value = true;
  try {
    const response = await getPageActiveLogs(
      pageIndex.value,
      pageSize.value,
      authStore.token,
      queryForm.teamName,
      queryForm.activeName,
      queryForm.userName,
      queryForm.status,
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
  await loadLogs();
};

const resetQuery = async () => {
  queryForm.teamName = '';
  queryForm.activeName = '';
  queryForm.userName = '';
  queryForm.status = undefined;
  pageIndex.value = 1;
  await loadLogs();
};

const changePage = async (nextPage) => {
  pageIndex.value = nextPage;
  await loadLogs();
};

const changePageSize = async (size) => {
  pageSize.value = size;
  pageIndex.value = 1;
  await loadLogs();
};

const openReview = (record, status) => {
  currentRecord.value = record;
  reviewForm.id = record.id;
  reviewForm.status = status;
  reviewForm.reviewRemark = status === 1 ? '审核通过' : '';
  reviewVisible.value = true;
};

const submitReview = async () => {
  await updateActiveLogs({
    id: reviewForm.id,
    status: reviewForm.status,
    reviewRemark: reviewForm.reviewRemark,
  });
  Message.success('报名状态已更新');
  reviewVisible.value = false;
  await loadLogs();
  return true;
};

const removeLog = (record) => {
  Modal.confirm({
    title: canReview.value ? '确认删除该报名记录？' : '确认取消该报名？',
    content: `${record.activeName || ''}`,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await deleteActiveLogs({ id: record.id });
      Message.success(canReview.value ? '报名记录已删除' : '报名已取消');
      await loadLogs();
    },
  });
};

onMounted(loadLogs);
</script>

<style scoped>
.table-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}

.review-remark-cell {
  min-width: 220px;
  white-space: normal;
  word-break: break-word;
  line-height: 1.7;
}
</style>
