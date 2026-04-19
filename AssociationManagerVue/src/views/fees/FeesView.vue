<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-card__label">收入总额</span>
        <strong class="stat-card__value">¥{{ summaryIncome }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">支出总额</span>
        <strong class="stat-card__value">¥{{ summaryExpense }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">当前余额</span>
        <strong class="stat-card__value">¥{{ summaryBalance }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">当前团队</span>
        <strong class="stat-card__value">{{ currentTeamName }}</strong>
      </article>
    </section>

    <section class="page-block">
      <div class="page-toolbar">
        <div class="filter-row">
          <div class="filter-row__main">
            <a-select
              v-model="queryForm.teamId"
              allow-clear
              placeholder="选择社团"
              style="width: 220px"
              @change="handleTeamChange"
            >
              <a-option v-for="item in teams" :key="item.id" :value="item.id">
                {{ item.name }}
              </a-option>
            </a-select>
            <a-input
              v-model="queryForm.userName"
              allow-clear
              placeholder="输入成员姓名"
              style="width: 220px"
              @press-enter="loadAll"
            />
            <a-button type="primary" @click="loadAll">查询</a-button>
            <a-button @click="resetQuery">重置</a-button>
          </div>

          <div class="filter-row__actions" v-if="canManageFees">
            <a-button type="primary" status="success" @click="openIncomeModal">新增收款</a-button>
            <a-button type="primary" status="warning" @click="openExpenseModal">记录支出</a-button>
          </div>
        </div>
      </div>
    </section>

    <section class="page-card chart-card">
      <div class="card-head">
        <h3>费用概览</h3>
      </div>
      <div class="chart-bars">
        <div class="chart-row">
          <span>收入</span>
          <div class="chart-track">
            <div class="chart-fill income" :style="{ width: incomeBarWidth }" />
          </div>
          <strong>¥{{ summaryIncome }}</strong>
        </div>
        <div class="chart-row">
          <span>支出</span>
          <div class="chart-track">
            <div class="chart-fill expense" :style="{ width: expenseBarWidth }" />
          </div>
          <strong>¥{{ summaryExpense }}</strong>
        </div>
        <div class="chart-row">
          <span>余额</span>
          <div class="chart-track">
            <div class="chart-fill balance" :style="{ width: balanceBarWidth }" />
          </div>
          <strong>¥{{ summaryBalance }}</strong>
        </div>
      </div>
    </section>

    <section class="page-card table-card">
      <div class="card-head">
        <h3>缴费记录</h3>
      </div>
      <div class="table-wrap">
        <a-table :data="payLogs" :loading="loadingIncome" :pagination="false" row-key="id" :bordered="false">
          <template #columns>
            <a-table-column title="社团名称" data-index="teamName" />
            <a-table-column title="成员姓名" data-index="userName" />
            <a-table-column title="手机号" data-index="userPhone" />
            <a-table-column title="缴费金额">
              <template #cell="{ record }">¥{{ formatMoney(record.total) }}</template>
            </a-table-column>
            <a-table-column title="缴费时间">
              <template #cell="{ record }">{{ formatDate(record.createTime) }}</template>
            </a-table-column>
            <a-table-column v-if="canManageFees" title="操作" :width="180" align="center">
              <template #cell="{ record }">
                <a-space>
                  <a-button type="text" @click="editIncome(record)">编辑</a-button>
                  <a-button type="text" status="danger" @click="removeIncome(record)">删除</a-button>
                </a-space>
              </template>
            </a-table-column>
          </template>
        </a-table>
      </div>
    </section>

    <section class="page-card table-card">
      <div class="card-head">
        <h3>支出明细</h3>
      </div>
      <div class="table-wrap">
        <a-table :data="expenseLogs" :loading="loadingExpense" :pagination="false" row-key="id" :bordered="false">
          <template #columns>
            <a-table-column title="社团名称" data-index="teamName" />
            <a-table-column title="支出项目" data-index="title" />
            <a-table-column title="支出金额">
              <template #cell="{ record }">¥{{ formatMoney(record.total) }}</template>
            </a-table-column>
            <a-table-column title="用途说明" data-index="detail" />
            <a-table-column title="登记时间">
              <template #cell="{ record }">{{ formatDate(record.createTime) }}</template>
            </a-table-column>
            <a-table-column v-if="canManageFees" title="操作" :width="180" align="center">
              <template #cell="{ record }">
                <a-space>
                  <a-button type="text" @click="editExpense(record)">编辑</a-button>
                  <a-button type="text" status="danger" @click="removeExpense(record)">删除</a-button>
                </a-space>
              </template>
            </a-table-column>
          </template>
        </a-table>
      </div>
    </section>

    <a-modal
      v-model:visible="incomeModalVisible"
      :title="editingIncomeId ? '编辑收款记录' : '新增收款记录'"
      width="640px"
      @before-ok="submitIncome"
    >
      <a-form ref="incomeFormRef" :model="incomeForm" layout="vertical">
        <a-grid :cols="2" :col-gap="16">
          <a-grid-item>
            <a-form-item field="teamId" label="所属社团" :rules="[{ required: true, message: '请选择社团' }]">
              <a-select v-model="incomeForm.teamId" placeholder="选择社团">
                <a-option v-for="item in teams" :key="item.id" :value="item.id">
                  {{ item.name }}
                </a-option>
              </a-select>
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="userId" label="缴费成员" :rules="[{ required: true, message: '请选择成员' }]">
              <a-select v-model="incomeForm.userId" placeholder="选择成员">
                <a-option v-for="item in memberOptions" :key="item.id" :value="item.id">
                  {{ item.name }}
                </a-option>
              </a-select>
            </a-form-item>
          </a-grid-item>
          <a-grid-item :span="2">
            <a-form-item field="total" label="缴费金额" :rules="[{ required: true, message: '请输入金额' }]">
              <a-input-number v-model="incomeForm.total" :min="0.01" :precision="2" style="width: 100%" />
            </a-form-item>
          </a-grid-item>
        </a-grid>
      </a-form>
    </a-modal>

    <a-modal
      v-model:visible="expenseModalVisible"
      :title="editingExpenseId ? '编辑支出记录' : '新增支出记录'"
      width="640px"
      @before-ok="submitExpense"
    >
      <a-form ref="expenseFormRef" :model="expenseForm" layout="vertical">
        <a-form-item field="teamId" label="所属社团" :rules="[{ required: true, message: '请选择社团' }]">
          <a-select v-model="expenseForm.teamId" placeholder="选择社团">
            <a-option v-for="item in teams" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-option>
          </a-select>
        </a-form-item>
        <a-form-item field="title" label="支出项目" :rules="[{ required: true, message: '请输入支出项目' }]">
          <a-input
            v-model="expenseForm.title"
            :max-length="50"
            placeholder="例如：活动物料、场地费、宣传打印"
            allow-clear
          />
        </a-form-item>
        <a-form-item field="total" label="支出金额" :rules="[{ required: true, message: '请输入支出金额' }]">
          <a-input-number v-model="expenseForm.total" :min="0.01" :precision="2" style="width: 100%" />
        </a-form-item>
        <a-form-item field="detail" label="用途说明" :rules="[{ required: true, message: '请输入用途说明' }]">
          <a-textarea
            v-model="expenseForm.detail"
            :max-length="200"
            placeholder="输入支出用途说明"
            show-word-limit
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import {
  addPayExpenses,
  addPayLogs,
  deletePayExpenses,
  deletePayLogs,
  getAllTeamList,
  getLoginUser,
  getManTeamList,
  getMemberOptions,
  getPagePayExpenses,
  getPagePayLogs,
  getPaySummary,
  updatePayExpenses,
  updatePayLogs,
} from '@/api';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const userType = computed(() => Number(authStore.userType));
const canManageFees = computed(() => userType.value === 0 || userType.value === 1);

const teams = ref([]);
const memberOptions = ref([]);
const payLogs = ref([]);
const expenseLogs = ref([]);

const summaryIncome = ref('0.00');
const summaryExpense = ref('0.00');
const summaryBalance = ref('0.00');

const loadingIncome = ref(false);
const loadingExpense = ref(false);

const incomeModalVisible = ref(false);
const expenseModalVisible = ref(false);
const editingIncomeId = ref('');
const editingExpenseId = ref('');
const incomeFormRef = ref();
const expenseFormRef = ref();

const queryForm = reactive({
  teamId: '',
  userName: '',
});

const incomeForm = reactive({
  id: '',
  teamId: '',
  userId: '',
  total: 10,
});

const expenseForm = reactive({
  id: '',
  teamId: '',
  title: '',
  total: 10,
  detail: '',
});

const numericIncome = computed(() => Number(summaryIncome.value || 0));
const numericExpense = computed(() => Number(summaryExpense.value || 0));
const numericBalance = computed(() => Number(summaryBalance.value || 0));
const maxMetric = computed(() => Math.max(numericIncome.value, numericExpense.value, numericBalance.value, 1));

const makeBarWidth = (value) => `${Math.max((value / maxMetric.value) * 100, value > 0 ? 8 : 0)}%`;
const incomeBarWidth = computed(() => makeBarWidth(numericIncome.value));
const expenseBarWidth = computed(() => makeBarWidth(numericExpense.value));
const balanceBarWidth = computed(() => makeBarWidth(numericBalance.value));

const currentTeamName = computed(() => {
  if (!queryForm.teamId) return '全部社团';
  return teams.value.find((item) => String(item.id) === String(queryForm.teamId))?.name || '未选择';
});

const formatMoney = (value) => Number(value || 0).toFixed(2);
const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '-');

const ensureUser = async () => {
  if (authStore.user?.id || !authStore.token) return;
  const response = await getLoginUser(authStore.token);
  authStore.setUser(response.data || null);
};

const loadTeams = async () => {
  const response = userType.value === 1 ? await getManTeamList(authStore.user?.id) : await getAllTeamList();
  teams.value = Array.isArray(response.data) ? response.data : [];
};

const loadMemberOptions = async () => {
  const teamId = incomeForm.teamId || queryForm.teamId || teams.value[0]?.id;
  if (!teamId) {
    memberOptions.value = [];
    return;
  }
  const response = await getMemberOptions(teamId);
  memberOptions.value = response.data || [];
};

const loadSummary = async () => {
  const response = await getPaySummary({
    teamId: queryForm.teamId || null,
  });
  summaryIncome.value = formatMoney(response.data?.income);
  summaryExpense.value = formatMoney(response.data?.expense);
  summaryBalance.value = formatMoney(response.data?.balance);
};

const loadPayLogs = async () => {
  loadingIncome.value = true;
  try {
    const selectedTeamName = teams.value.find((item) => String(item.id) === String(queryForm.teamId))?.name || null;
    const response = await getPagePayLogs({
      pageIndex: 1,
      pageSize: 100,
      teamName: selectedTeamName,
      userName: queryForm.userName || null,
    });
    const page = response.data || {};
    payLogs.value = page.data || [];
  } finally {
    loadingIncome.value = false;
  }
};

const loadExpenseLogs = async () => {
  loadingExpense.value = true;
  try {
    const response = await getPagePayExpenses({
      pageIndex: 1,
      pageSize: 100,
      teamId: queryForm.teamId || null,
      title: null,
    });
    const page = response.data || {};
    expenseLogs.value = page.data || [];
  } finally {
    loadingExpense.value = false;
  }
};

const loadAll = async () => {
  await Promise.all([loadSummary(), loadPayLogs(), loadExpenseLogs()]);
};

const boot = async () => {
  await ensureUser();
  await loadTeams();
  if (!queryForm.teamId && userType.value === 1 && teams.value.length === 1) {
    queryForm.teamId = teams.value[0].id;
  }
  await loadMemberOptions();
  await loadAll();
};

const handleTeamChange = async () => {
  if (!expenseForm.teamId && queryForm.teamId) {
    expenseForm.teamId = queryForm.teamId;
  }
  await loadMemberOptions();
  await loadAll();
};

const resetQuery = async () => {
  queryForm.teamId = userType.value === 1 && teams.value.length === 1 ? teams.value[0].id : '';
  queryForm.userName = '';
  await loadMemberOptions();
  await loadAll();
};

const resetIncomeForm = () => {
  editingIncomeId.value = '';
  incomeForm.id = '';
  incomeForm.teamId = queryForm.teamId || teams.value[0]?.id || '';
  incomeForm.userId = '';
  incomeForm.total = 10;
  incomeFormRef.value?.clearValidate?.();
};

const resetExpenseForm = () => {
  editingExpenseId.value = '';
  expenseForm.id = '';
  expenseForm.teamId = queryForm.teamId || teams.value[0]?.id || '';
  expenseForm.title = '';
  expenseForm.total = 10;
  expenseForm.detail = '';
  expenseFormRef.value?.clearValidate?.();
};

const openIncomeModal = async () => {
  resetIncomeForm();
  await loadMemberOptions();
  incomeModalVisible.value = true;
};

const openExpenseModal = () => {
  resetExpenseForm();
  expenseModalVisible.value = true;
};

const submitIncome = async () => {
  const errors = await incomeFormRef.value?.validate();
  if (errors) return false;

  const payload = {
    id: editingIncomeId.value || undefined,
    teamId: incomeForm.teamId,
    userId: incomeForm.userId,
    total: incomeForm.total,
  };

  if (editingIncomeId.value) {
    await updatePayLogs(payload);
    Message.success('收款记录已更新');
  } else {
    await addPayLogs(payload);
    Message.success('收款记录已新增');
  }

  incomeModalVisible.value = false;
  await loadAll();
  return true;
};

const submitExpense = async () => {
  const errors = await expenseFormRef.value?.validate();
  if (errors) return false;

  const payload = {
    id: editingExpenseId.value || undefined,
    teamId: expenseForm.teamId,
    title: expenseForm.title,
    total: expenseForm.total,
    detail: expenseForm.detail,
  };

  if (editingExpenseId.value) {
    await updatePayExpenses(payload);
    Message.success('支出记录已更新');
  } else {
    await addPayExpenses(payload);
    Message.success('支出记录已新增');
  }

  expenseModalVisible.value = false;
  await loadAll();
  return true;
};

const editIncome = async (record) => {
  editingIncomeId.value = record.id;
  incomeForm.id = record.id;
  incomeForm.teamId = record.teamId;
  incomeForm.userId = record.userId;
  incomeForm.total = Number(record.total || 0);
  await loadMemberOptions();
  incomeModalVisible.value = true;
};

const editExpense = (record) => {
  editingExpenseId.value = record.id;
  expenseForm.id = record.id;
  expenseForm.teamId = record.teamId;
  expenseForm.title = record.title || '';
  expenseForm.total = Number(record.total || 0);
  expenseForm.detail = record.detail || '';
  expenseModalVisible.value = true;
};

const removeIncome = (record) => {
  Modal.confirm({
    title: '确认删除该收款记录？',
    content: `${record.userName} / ¥${formatMoney(record.total)}`,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await deletePayLogs({ id: record.id });
      Message.success('收款记录已删除');
      await loadAll();
    },
  });
};

const removeExpense = (record) => {
  Modal.confirm({
    title: '确认删除该支出记录？',
    content: `${record.teamName} / ¥${formatMoney(record.total)}`,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await deletePayExpenses({ id: record.id });
      Message.success('支出记录已删除');
      await loadAll();
    },
  });
};

watch(
  () => incomeForm.teamId,
  async (value) => {
    if (value) {
      await loadMemberOptions();
    }
  },
);

onMounted(boot);
</script>

<style scoped>
.chart-card,
.table-card {
  overflow: hidden;
}

.card-head {
  padding: 20px 20px 0;
}

.card-head h3 {
  margin: 0;
  color: var(--text-1);
  font-size: 16px;
}

.chart-bars {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
}

.chart-row {
  display: grid;
  grid-template-columns: 52px minmax(120px, 1fr) auto;
  align-items: center;
  gap: 12px;
  color: var(--text-2);
  font-size: 14px;
}

.chart-track {
  height: 10px;
  overflow: hidden;
  background: #eef2f7;
  border-radius: 999px;
}

.chart-fill {
  height: 100%;
  border-radius: 999px;
}

.chart-fill.income {
  background: linear-gradient(90deg, #2563eb, #60a5fa);
}

.chart-fill.expense {
  background: linear-gradient(90deg, #f59e0b, #fbbf24);
}

.chart-fill.balance {
  background: linear-gradient(90deg, #10b981, #34d399);
}

.table-wrap {
  padding: 0 20px 20px;
}
</style>
