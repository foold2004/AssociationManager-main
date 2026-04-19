<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-card__label">类型总数</span>
        <strong class="stat-card__value">{{ total }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">当前页数量</span>
        <strong class="stat-card__value">{{ rows.length }}</strong>
      </article>
    </section>

    <section class="page-block">
      <div class="page-toolbar">
        <div class="filter-row">
          <div class="filter-row__main">
            <a-input
              v-model="queryForm.name"
              allow-clear
              placeholder="输入社团类型名称"
              style="width: 240px"
              @press-enter="search"
            />
            <a-button type="primary" @click="search">查询</a-button>
            <a-button @click="resetQuery">重置</a-button>
          </div>

          <div class="filter-row__actions">
            <a-button type="primary" @click="openCreate">新增类型</a-button>
          </div>
        </div>
      </div>
    </section>

    <section class="page-card table-card">
      <div class="page-card__body">
        <a-table :data="rows" :loading="loading" :pagination="false" row-key="id" :bordered="false">
          <template #columns>
            <a-table-column title="类型名称" data-index="name" />
            <a-table-column title="创建时间" :width="200">
              <template #cell="{ record }">
                {{ formatDate(record.createTime) }}
              </template>
            </a-table-column>
            <a-table-column title="操作" :width="160" align="center">
              <template #cell="{ record }">
                <a-space>
                  <a-button type="text" @click="openEdit(record)">编辑</a-button>
                  <a-button type="text" status="danger" @click="removeType(record)">删除</a-button>
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
      :title="editingId ? '编辑社团类型' : '新增社团类型'"
      width="560px"
      @before-ok="submitType"
    >
      <a-form ref="formRef" :model="form" layout="vertical">
        <a-form-item
          field="name"
          label="类型名称"
          :rules="[{ required: true, message: '请输入类型名称' }]"
        >
          <a-input v-model="form.name" placeholder="输入社团类型名称" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import { addTeamTypes, delTeamTypes, getPageTeamTypes, updTeamTypes } from '@/api';

const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const pageIndex = ref(1);
const pageSize = ref(10);
const modalVisible = ref(false);
const editingId = ref('');
const formRef = ref();

const queryForm = reactive({
  name: '',
});

const form = reactive({
  id: '',
  name: '',
});

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '-');

const loadTypes = async () => {
  loading.value = true;
  try {
    const response = await getPageTeamTypes(pageIndex.value, pageSize.value, queryForm.name);
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
  await loadTypes();
};

const resetQuery = async () => {
  queryForm.name = '';
  pageIndex.value = 1;
  await loadTypes();
};

const changePage = async (nextPage) => {
  pageIndex.value = nextPage;
  await loadTypes();
};

const changePageSize = async (size) => {
  pageSize.value = size;
  pageIndex.value = 1;
  await loadTypes();
};

const resetForm = () => {
  editingId.value = '';
  form.id = '';
  form.name = '';
  formRef.value?.clearValidate?.();
};

const openCreate = () => {
  resetForm();
  modalVisible.value = true;
};

const openEdit = (record) => {
  editingId.value = record.id;
  form.id = record.id;
  form.name = record.name || '';
  modalVisible.value = true;
};

const submitType = async () => {
  const errors = await formRef.value?.validate();
  if (errors) return false;

  if (editingId.value) {
    await updTeamTypes({ id: editingId.value, name: form.name });
    Message.success('社团类型已更新');
  } else {
    await addTeamTypes({ name: form.name });
    Message.success('社团类型已新增');
  }

  modalVisible.value = false;
  await loadTypes();
  return true;
};

const removeType = (record) => {
  Modal.confirm({
    title: '确认删除该类型？',
    content: record.name,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await delTeamTypes({ id: record.id });
      Message.success('社团类型已删除');
      await loadTypes();
    },
  });
};

onMounted(loadTypes);
</script>

<style scoped>
.table-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}
</style>
