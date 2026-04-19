<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-card__label">成员总数</span>
        <strong class="stat-card__value">{{ total }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">当前页人数</span>
        <strong class="stat-card__value">{{ rows.length }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">涉及社团</span>
        <strong class="stat-card__value">{{ teamCount }}</strong>
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
              v-model="queryForm.userName"
              allow-clear
              placeholder="输入成员姓名"
              style="width: 220px"
              @press-enter="search"
            />
            <a-button type="primary" @click="search">查询</a-button>
            <a-button @click="resetQuery">重置</a-button>
          </div>
        </div>
      </div>
    </section>

    <section class="page-card table-card">
      <div class="page-card__body">
        <a-table :data="rows" :loading="loading" :pagination="false" row-key="id" :bordered="false">
          <template #columns>
            <a-table-column title="成员ID" data-index="userId" :width="140" />
            <a-table-column title="成员姓名" data-index="userName" :width="120" />
            <a-table-column title="性别" data-index="userGender" :width="90" align="center" />
            <a-table-column title="年龄" data-index="userAge" :width="90" align="center" />
            <a-table-column title="手机号" data-index="userPhone" :width="150" />
            <a-table-column title="所属社团" data-index="teamName" :width="180" />
            <a-table-column title="加入时间" :width="180">
              <template #cell="{ record }">
                {{ formatDate(record.createTime) }}
              </template>
            </a-table-column>
            <a-table-column title="操作" :width="120" align="center">
              <template #cell="{ record }">
                <a-button type="text" status="danger" @click="removeMember(record)">移除</a-button>
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
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import { delMembers, getPageMembers } from '@/api';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const pageIndex = ref(1);
const pageSize = ref(10);

const queryForm = reactive({
  teamName: '',
  userName: '',
});

const teamCount = computed(() => new Set(rows.value.map((item) => item.teamName).filter(Boolean)).size);

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '-');

const loadMembers = async () => {
  loading.value = true;
  try {
    const response = await getPageMembers(
      pageIndex.value,
      pageSize.value,
      authStore.token,
      queryForm.teamName,
      queryForm.userName,
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
  await loadMembers();
};

const resetQuery = async () => {
  queryForm.teamName = '';
  queryForm.userName = '';
  pageIndex.value = 1;
  await loadMembers();
};

const changePage = async (nextPage) => {
  pageIndex.value = nextPage;
  await loadMembers();
};

const changePageSize = async (size) => {
  pageSize.value = size;
  pageIndex.value = 1;
  await loadMembers();
};

const removeMember = (record) => {
  Modal.confirm({
    title: '确认移除该成员？',
    content: `${record.userName} / ${record.teamName}`,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await delMembers({ id: record.id });
      Message.success('成员已移除');
      await loadMembers();
    },
  });
};

onMounted(loadMembers);
</script>

<style scoped>
.table-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}
</style>
