<template>
  <div class="module-page">
    <a-card :bordered="false" class="toolbar-card">
      <div class="toolbar-row">
        <a-space wrap>
          <a-input v-model="query.title" placeholder="输入制度标题" style="width: 220px" allowClear />
          <a-input v-model="query.teamName" placeholder="输入社团名称" style="width: 220px" allowClear />
          <a-button type="primary" @click="handleSearch">查询</a-button>
        </a-space>
        <a-button v-if="canManage" type="primary" icon="plus" @click="openCreate">发布制度</a-button>
      </div>
    </a-card>

    <a-row :gutter="16" class="board-row">
      <a-col :xs="24" :xl="16">
        <a-card :bordered="false" class="list-card">
          <a-list
            :loading="loading"
            :data-source="rows"
            :locale="{ emptyText: '暂无规则制度' }"
            item-layout="vertical"
          >
            <a-list-item slot="renderItem" slot-scope="item">
              <div class="rule-head">
                <div>
                  <div class="rule-title">{{ item.title }}</div>
                  <div class="rule-meta">
                    <span>{{ item.teamName || '系统规则' }}</span>
                    <span>发布人：{{ item.userName || '-' }}</span>
                    <span>更新时间：{{ item.updateTime || item.createTime || '-' }}</span>
                  </div>
                </div>
                <a-space v-if="canEdit(item)">
                  <a-button size="small" @click="openEdit(item)">编辑</a-button>
                  <a-popconfirm title="确认删除这条制度吗？" @confirm="removeRule(item)">
                    <a-button size="small" type="danger">删除</a-button>
                  </a-popconfirm>
                </a-space>
              </div>
              <div class="rule-content">{{ item.content }}</div>
            </a-list-item>
          </a-list>

          <div class="pager">
            <a-pagination
              :current="pageIndex"
              :pageSize="pageSize"
              :total="total"
              :showSizeChanger="true"
              :pageSizeOptions="['5', '10', '20', '50']"
              showQuickJumper
              @change="handlePageChange"
              @showSizeChange="handleSizeChange"
            />
          </div>
        </a-card>
      </a-col>

    </a-row>

    <a-modal
      :title="form.id ? '编辑制度' : '发布制度'"
      :visible="visible"
      :confirmLoading="submitting"
      okText="保存"
      cancelText="取消"
      @ok="submitForm"
      @cancel="visible = false"
    >
      <a-form-model :model="form" layout="vertical">
        <a-form-model-item label="制度标题">
          <a-input v-model="form.title" placeholder="请输入制度标题" />
        </a-form-model-item>
        <a-form-model-item label="所属社团">
          <a-select v-model="form.teamId" allowClear placeholder="选择所属社团">
            <a-select-option v-for="item in teamOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="制度内容">
          <a-textarea v-model="form.content" :rows="6" placeholder="请输入制度内容" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import { message } from 'ant-design-vue';
import {
  addRule,
  deleteRule,
  getAllTeamList,
  getLoginUser,
  getManTeamList,
  getPageRules,
  updateRule,
} from '@/api';

export default {
  name: 'RulesPage',
  data() {
    return {
      userInfo: this.$store.getters.user || null,
      loading: false,
      submitting: false,
      visible: false,
      pageIndex: 1,
      pageSize: 10,
      total: 0,
      query: {
        title: '',
        teamName: '',
      },
      rows: [],
      teamOptions: [],
      form: {
        id: '',
        title: '',
        teamId: undefined,
        content: '',
      },
    };
  },
  computed: {
    userType() {
      return this.userInfo ? this.userInfo.type : null;
    },
    canManage() {
      return this.userType === 0 || this.userType === 1;
    },
  },
  async created() {
    await this.initPage();
  },
  methods: {
    async initPage() {
      if (!this.userInfo || !this.userInfo.id) {
        const resp = await getLoginUser(this.$store.getters.token);
        this.userInfo = resp.data;
        this.$store.commit('setUser', resp.data);
      }
      await Promise.all([this.loadTeams(), this.loadData()]);
    },
    async loadTeams() {
      const resp = this.userType === 1 ? await getManTeamList(this.userInfo.id) : await getAllTeamList();
      this.teamOptions = resp.data || [];
    },
    async loadData() {
      this.loading = true;
      try {
        const resp = await getPageRules(
          this.pageIndex,
          this.pageSize,
          this.$store.getters.token,
          this.query.title,
          this.query.teamName
        );
        const page = resp.data || {};
        this.rows = page.data || [];
        this.total = page.count || 0;
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.pageIndex = 1;
      this.loadData();
    },
    handlePageChange(page, pageSize) {
      this.pageIndex = page;
      this.pageSize = pageSize;
      this.loadData();
    },
    handleSizeChange(current, size) {
      this.pageIndex = 1;
      this.pageSize = size;
      this.loadData();
    },
    openCreate() {
      this.form = {
        id: '',
        title: '',
        teamId: this.userType === 1 && this.teamOptions.length ? this.teamOptions[0].id : undefined,
        content: '',
      };
      this.visible = true;
    },
    openEdit(record) {
      this.form = {
        id: record.id,
        title: record.title,
        teamId: record.teamId || undefined,
        content: record.content,
      };
      this.visible = true;
    },
    canEdit(record) {
      if (!this.canManage) return false;
      if (this.userType === 0) return true;
      return record.userId === this.userInfo.id;
    },
    async submitForm() {
      if (!this.form.title || !this.form.content) {
        message.warning('请先填写完整的制度标题和内容');
        return;
      }
      this.submitting = true;
      try {
        const params = { ...this.form, token: this.$store.getters.token };
        if (this.form.id) {
          await updateRule(params);
          message.success('制度已更新');
        } else {
          await addRule(params);
          message.success('制度已发布');
        }
        this.visible = false;
        await this.loadData();
      } finally {
        this.submitting = false;
      }
    },
    async removeRule(record) {
      await deleteRule(this.$store.getters.token, record.id);
      message.success('制度已删除');
      await this.loadData();
    },
  },
};
</script>

<style scoped>
.module-page {
  display: grid;
  gap: 16px;
}

.toolbar-card,
.list-card,
.tips-card {
  border-radius: 24px;
  box-shadow: 0 18px 32px rgba(148, 163, 184, 0.14);
}

.toolbar-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.board-row {
  margin-top: 0;
}

.rule-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.rule-title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.rule-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
}

.rule-content {
  margin-top: 14px;
  padding: 16px;
  border-radius: 16px;
  background: #f8fbff;
  color: #334155;
  line-height: 1.8;
  white-space: pre-wrap;
}

.tips-title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 14px;
}

.tips-item {
  padding: 14px 0;
  color: #475569;
  line-height: 1.8;
  border-bottom: 1px solid #edf2f7;
}

.tips-item:last-child {
  border-bottom: 0;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
