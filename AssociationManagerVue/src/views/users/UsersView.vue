<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-card__label">用户总数</span>
        <strong class="stat-card__value">{{ total }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">系统管理员</span>
        <strong class="stat-card__value">{{ systemCount }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">社团管理员</span>
        <strong class="stat-card__value">{{ managerCount }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">学生</span>
        <strong class="stat-card__value">{{ studentCount }}</strong>
      </article>
    </section>

    <section class="page-block">
      <div class="page-toolbar">
        <div class="filter-row">
          <div class="filter-row__main">
            <a-input
              v-model="queryForm.userName"
              allow-clear
              placeholder="输入登录账号"
              style="width: 220px"
              @press-enter="search"
            />
            <a-input
              v-model="queryForm.name"
              allow-clear
              placeholder="输入姓名"
              style="width: 220px"
              @press-enter="search"
            />
            <a-input
              v-model="queryForm.phone"
              allow-clear
              placeholder="输入手机号"
              style="width: 220px"
              @press-enter="search"
            />
            <a-select v-model="queryForm.type" allow-clear placeholder="筛选身份" style="width: 180px">
              <a-option v-for="item in roleOptions" :key="item.value" :value="item.value">
                {{ item.label }}
              </a-option>
            </a-select>
            <a-button type="primary" @click="search">查询</a-button>
            <a-button @click="resetQuery">重置</a-button>
          </div>

          <div class="filter-row__actions">
            <a-button type="primary" @click="openCreate">新增用户</a-button>
          </div>
        </div>
      </div>
    </section>

    <section class="page-card table-card">
      <div class="page-card__body">
        <a-table :data="rows" :loading="loading" :pagination="false" row-key="id" :bordered="false">
          <template #columns>
            <a-table-column title="登录账号" data-index="userName" :width="160" />
            <a-table-column title="姓名" data-index="name" :width="120" />
            <a-table-column title="性别" data-index="gender" :width="90" align="center" />
            <a-table-column title="年龄" data-index="age" :width="90" align="center" />
            <a-table-column title="手机号" data-index="phone" :width="150" />
            <a-table-column title="地址">
              <template #cell="{ record }">
                {{ record.address || '-' }}
              </template>
            </a-table-column>
            <a-table-column title="状态" :width="100" align="center">
              <template #cell="{ record }">
                <a-tag :color="record.status === 1 ? 'green' : 'gray'">
                  {{ record.status === 1 ? '启用' : '停用' }}
                </a-tag>
              </template>
            </a-table-column>
            <a-table-column title="身份" :width="120" align="center">
              <template #cell="{ record }">
                <a-tag :color="roleColor(record.type)">
                  {{ roleLabel(record.type) }}
                </a-tag>
              </template>
            </a-table-column>
            <a-table-column title="创建时间" :width="180">
              <template #cell="{ record }">
                {{ formatDate(record.createTime) }}
              </template>
            </a-table-column>
            <a-table-column title="操作" :width="160" align="center" fixed="right">
              <template #cell="{ record }">
                <a-space>
                  <a-button type="text" @click="openEdit(record)">编辑</a-button>
                  <a-button type="text" status="danger" @click="removeUser(record)">删除</a-button>
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
      :title="editingId ? '编辑用户' : '新增用户'"
      width="720px"
      @before-ok="submitUser"
    >
      <a-form ref="formRef" :model="form" layout="vertical">
        <a-grid :cols="2" :col-gap="16">
          <a-grid-item>
            <a-form-item
              field="type"
              label="身份"
              :rules="[{ required: true, message: '请选择身份' }]"
            >
              <a-select v-model="form.type" placeholder="选择身份">
                <a-option v-for="item in roleOptions" :key="item.value" :value="item.value">
                  {{ item.label }}
                </a-option>
              </a-select>
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item
              field="status"
              label="状态"
              :rules="[{ required: true, message: '请选择状态' }]"
            >
              <a-select v-model="form.status" placeholder="选择状态">
                <a-option :value="1">启用</a-option>
                <a-option :value="0">停用</a-option>
              </a-select>
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item
              field="userName"
              label="登录账号"
              :rules="[{ required: true, message: '请输入登录账号' }]"
            >
              <a-input v-model="form.userName" placeholder="输入登录账号" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item
              field="passWord"
              label="登录密码"
              :rules="[{ required: true, message: '请输入登录密码' }]"
            >
              <a-input-password v-model="form.passWord" placeholder="输入登录密码" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item
              field="name"
              label="姓名"
              :rules="[{ required: true, message: '请输入姓名' }]"
            >
              <a-input v-model="form.name" placeholder="输入姓名" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item
              field="gender"
              label="性别"
              :rules="[{ required: true, message: '请选择性别' }]"
            >
              <a-radio-group v-model="form.gender">
                <a-radio value="男">男</a-radio>
                <a-radio value="女">女</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="age" label="年龄">
              <a-input-number v-model="form.age" :min="16" :max="80" style="width: 100%" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="phone" label="手机号">
              <a-input v-model="form.phone" placeholder="输入手机号" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item :span="2">
            <a-form-item field="address" label="地址">
              <a-textarea v-model="form.address" :rows="3" placeholder="输入地址" />
            </a-form-item>
          </a-grid-item>
        </a-grid>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import { addUsers, deleteUsers, getPageUsers, updateUsers } from '@/api';

const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const pageIndex = ref(1);
const pageSize = ref(10);
const modalVisible = ref(false);
const editingId = ref('');
const formRef = ref();

const roleOptions = [
  { label: '系统管理员', value: 0 },
  { label: '社团管理员', value: 1 },
  { label: '学生', value: 2 },
];

const queryForm = reactive({
  userName: '',
  name: '',
  phone: '',
  type: undefined,
});

const form = reactive({
  id: '',
  userName: '',
  passWord: '',
  type: 2,
  name: '',
  gender: '男',
  age: 20,
  phone: '',
  address: '',
  status: 1,
});

const systemCount = computed(() => rows.value.filter((item) => Number(item.type) === 0).length);
const managerCount = computed(() => rows.value.filter((item) => Number(item.type) === 1).length);
const studentCount = computed(() => rows.value.filter((item) => Number(item.type) === 2).length);

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '-');

const roleLabel = (type) => roleOptions.find((item) => item.value === Number(type))?.label || '未知';

const roleColor = (type) => {
  if (Number(type) === 0) return 'red';
  if (Number(type) === 1) return 'orange';
  return 'arcoblue';
};

const loadUsers = async () => {
  loading.value = true;
  try {
    const response = await getPageUsers(
      pageIndex.value,
      pageSize.value,
      queryForm.userName,
      queryForm.name,
      queryForm.phone,
      queryForm.type,
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
  await loadUsers();
};

const resetQuery = async () => {
  queryForm.userName = '';
  queryForm.name = '';
  queryForm.phone = '';
  queryForm.type = undefined;
  pageIndex.value = 1;
  await loadUsers();
};

const changePage = async (nextPage) => {
  pageIndex.value = nextPage;
  await loadUsers();
};

const changePageSize = async (size) => {
  pageSize.value = size;
  pageIndex.value = 1;
  await loadUsers();
};

const resetForm = () => {
  editingId.value = '';
  form.id = '';
  form.userName = '';
  form.passWord = '';
  form.type = 2;
  form.name = '';
  form.gender = '男';
  form.age = 20;
  form.phone = '';
  form.address = '';
  form.status = 1;
  formRef.value?.clearValidate?.();
};

const openCreate = () => {
  resetForm();
  modalVisible.value = true;
};

const openEdit = (record) => {
  editingId.value = record.id;
  form.id = record.id;
  form.userName = record.userName || '';
  form.passWord = record.passWord || '';
  form.type = Number(record.type);
  form.name = record.name || '';
  form.gender = record.gender || '男';
  form.age = Number(record.age || 20);
  form.phone = record.phone || '';
  form.address = record.address || '';
  form.status = Number(record.status ?? 1);
  modalVisible.value = true;
};

const submitUser = async () => {
  const errors = await formRef.value?.validate();
  if (errors) return false;

  const payload = {
    id: editingId.value || undefined,
    userName: form.userName,
    passWord: form.passWord,
    type: form.type,
    name: form.name,
    gender: form.gender,
    age: form.age,
    phone: form.phone,
    address: form.address,
    status: form.status,
  };

  if (editingId.value) {
    await updateUsers(payload);
    Message.success('用户已更新');
  } else {
    await addUsers(payload);
    Message.success('用户已新增');
  }

  modalVisible.value = false;
  await loadUsers();
  return true;
};

const removeUser = (record) => {
  Modal.confirm({
    title: '确认删除该用户？',
    content: `${record.name || record.userName}`,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await deleteUsers({ id: record.id });
      Message.success('用户已删除');
      await loadUsers();
    },
  });
};

onMounted(loadUsers);
</script>

<style scoped>
.table-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}
</style>
