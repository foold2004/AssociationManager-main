<template>
  <div class="notice-page">
    <a-card :bordered="false" class="toolbar-card">
      <div class="toolbar-row">
        <a-space wrap>
          <a-input
            v-model="query.title"
            placeholder="输入通知标题"
            style="width: 220px"
            allowClear
          />
          <a-select
            v-model="query.teamFilter"
            placeholder="选择通知范围"
            style="width: 220px"
            allowClear
          >
            <a-select-option value="">全部通知</a-select-option>
            <a-select-option value="system">系统通知</a-select-option>
            <a-select-option v-for="item in teamOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
          <a-button type="primary" @click="handleSearch">查询</a-button>
        </a-space>
        <a-button v-if="userType !== 2" type="primary" icon="plus" @click="openCreate">
          发布通知
        </a-button>
      </div>
    </a-card>

    <a-card :bordered="false" class="list-card">
      <a-table
        :columns="columns"
        :data-source="tableRows"
        :loading="loading"
        :pagination="false"
        rowKey="id"
        :locale="{ emptyText: '暂无通知内容' }"
        :scroll="{ x: 1200 }"
      >
        <template slot="titleCell" slot-scope="text, record">
          <div class="title-cell">
            <a-tag v-if="record && Number(record.isTop) === 1" color="red">置顶</a-tag>
            <span>{{ (record && record.title) || text || '-' }}</span>
          </div>
        </template>

        <template slot="isTopCell" slot-scope="text">
          <a-tag :color="Number(text) === 1 ? 'red' : 'default'">
            {{ Number(text) === 1 ? '已置顶' : '普通' }}
          </a-tag>
        </template>

        <template slot="teamCell" slot-scope="text">
          <a-tag :color="text ? 'green' : 'gold'">
            {{ text || '系统通知' }}
          </a-tag>
        </template>

        <template slot="detailCell" slot-scope="text">
          <div class="detail-cell">{{ text || '-' }}</div>
        </template>

        <template slot="actionCell" slot-scope="text, record">
          <a-space>
            <a-button
              v-if="userType === 0 && record"
              size="small"
              type="primary"
              ghost
              @click="toggleTop(record)"
            >
              {{ Number(record.isTop) === 1 ? '取消置顶' : '设为置顶' }}
            </a-button>
            <a-button
              v-if="record && canEdit(record)"
              size="small"
              @click="openEdit(record)"
            >
              编辑
            </a-button>
            <a-popconfirm
              v-if="record && canDelete(record)"
              title="确认删除这条通知吗？"
              @confirm="removeNotice(record)"
            >
              <a-button size="small" type="danger">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>

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

    <a-modal
      :title="editMode ? '编辑通知' : '发布通知'"
      :visible="visible"
      :confirmLoading="submitting"
      :okText="editMode ? '保存修改' : '发布'"
      cancelText="取消"
      @ok="submitNotice"
      @cancel="visible = false"
    >
      <a-form-model :model="form" layout="vertical">
        <a-form-model-item label="通知标题">
          <a-input v-model="form.title" placeholder="请输入通知标题" />
        </a-form-model-item>

        <a-form-model-item v-if="userType === 0" label="发布范围">
          <a-radio-group v-model="publishMode">
            <a-radio-button value="system">系统通知</a-radio-button>
            <a-radio-button value="team">指定社团</a-radio-button>
          </a-radio-group>
        </a-form-model-item>

        <a-form-model-item
          v-if="userType === 1 || (userType === 0 && publishMode === 'team')"
          label="所属社团"
        >
          <a-select v-model="form.teamId" allowClear placeholder="请选择所属社团">
            <a-select-option v-for="item in teamOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>

        <a-form-model-item v-if="userType === 0" label="通知设置">
          <a-checkbox v-model="form.isTopChecked">发布为置顶消息</a-checkbox>
        </a-form-model-item>

        <a-form-model-item label="通知内容">
          <a-textarea v-model="form.detail" :rows="6" placeholder="请输入通知内容" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import { message } from "ant-design-vue";
import {
  addNotices,
  deleteNotices,
  getAllTeamList,
  getLoginUser,
  getManTeamList,
  getPageNotices,
  toggleNoticeTop,
  updateNotices,
} from "@/api";

export default {
  name: "NoticesPage",
  data() {
    return {
      userInfo: this.$store.getters.user || null,
      loading: false,
      submitting: false,
      visible: false,
      editMode: false,
      publishMode: "system",
      pageIndex: 1,
      pageSize: 10,
      total: 0,
      rows: [],
      teamOptions: [],
      query: {
        title: "",
        teamFilter: "",
      },
      form: {
        id: undefined,
        title: "",
        detail: "",
        teamId: undefined,
        isTopChecked: false,
      },
    };
  },
  computed: {
    userType() {
      return this.userInfo ? this.userInfo.type : null;
    },
    tableRows() {
      return Array.isArray(this.rows) ? this.rows : [];
    },
    columns() {
      return [
        {
          title: "通知标题",
          dataIndex: "title",
          key: "title",
          scopedSlots: { customRender: "titleCell" },
        },
        {
          title: "置顶状态",
          dataIndex: "isTop",
          key: "isTop",
          width: 110,
          scopedSlots: { customRender: "isTopCell" },
        },
        {
          title: "发布范围",
          dataIndex: "teamName",
          key: "teamName",
          width: 140,
          scopedSlots: { customRender: "teamCell" },
        },
        {
          title: "发布时间",
          dataIndex: "createTime",
          key: "createTime",
          width: 130,
        },
        {
          title: "通知内容",
          dataIndex: "detail",
          key: "detail",
          scopedSlots: { customRender: "detailCell" },
        },
        {
          title: "操作",
          key: "actions",
          width: 260,
          scopedSlots: { customRender: "actionCell" },
        },
      ];
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
        this.$store.commit("setUser", resp.data);
      }
      await Promise.all([this.loadTeams(), this.loadData()]);
    },
    async loadTeams() {
      const resp = this.userType === 1 ? await getManTeamList(this.userInfo.id) : await getAllTeamList();
      this.teamOptions = (resp && resp.data) || [];
    },
    async loadData() {
      this.loading = true;
      try {
        const teamFilter = this.query.teamFilter || "";
        const resp = await getPageNotices(
          this.pageIndex,
          this.pageSize,
          this.$store.getters.token,
          this.query.title,
          "",
          teamFilter && teamFilter !== "system" ? teamFilter : "",
          teamFilter === "system" ? 1 : 0
        );
        const page = (resp && resp.data) || {};
        this.rows = Array.isArray(page.data) ? page.data : [];
        this.total = Number(page.count || 0);
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
      this.editMode = false;
      this.publishMode = "system";
      this.form = {
        id: undefined,
        title: "",
        detail: "",
        teamId: this.userType === 1 && this.teamOptions.length ? this.teamOptions[0].id : undefined,
        isTopChecked: false,
      };
      this.visible = true;
    },
    openEdit(record) {
      this.editMode = true;
      this.publishMode = record && record.teamId ? "team" : "system";
      this.form = {
        id: record.id,
        title: record.title,
        detail: record.detail,
        teamId: record.teamId || undefined,
        isTopChecked: Number(record.isTop) === 1,
      };
      this.visible = true;
    },
    async submitNotice() {
      if (!this.form.title || !this.form.detail) {
        message.warning("请先填写完整的通知标题和内容");
        return;
      }
      if (this.userType === 1 && !this.form.teamId) {
        message.warning("社团管理员发布通知时必须选择所属社团");
        return;
      }
      if (this.userType === 0 && this.publishMode === "team" && !this.form.teamId) {
        message.warning("请先选择要发布到的社团");
        return;
      }

      this.submitting = true;
      try {
        const payload = {
          token: this.$store.getters.token,
          id: this.form.id,
          title: this.form.title,
          detail: this.form.detail,
          teamId: this.userType === 0 ? (this.publishMode === "team" ? this.form.teamId : null) : this.form.teamId,
          isTop: this.userType === 0 && this.form.isTopChecked ? 1 : 0,
        };
        if (this.editMode) {
          await updateNotices(payload);
          message.success("通知已更新");
        } else {
          await addNotices(payload);
          message.success("通知已发布");
        }
        this.visible = false;
        await this.loadData();
      } finally {
        this.submitting = false;
      }
    },
    canEdit(record) {
      if (this.userType === 0) return true;
      return this.userType === 1 && !!record.teamId;
    },
    canDelete(record) {
      if (this.userType === 0) return true;
      return this.userType === 1 && !!record.teamId;
    },
    async removeNotice(record) {
      await deleteNotices(this.$store.getters.token, record.id);
      message.success("通知已删除");
      await this.loadData();
    },
    async toggleTop(record) {
      await toggleNoticeTop(this.$store.getters.token, record.id, Number(record.isTop) === 1 ? 0 : 1);
      message.success(Number(record.isTop) === 1 ? "已取消置顶" : "已设为置顶");
      await this.loadData();
    },
  },
};
</script>

<style scoped>
.notice-page {
  display: grid;
  gap: 16px;
}

.toolbar-card,
.list-card {
  border-radius: 24px;
  box-shadow: 0 18px 32px rgba(148, 163, 184, 0.14);
}

.toolbar-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.top-hint {
  margin-bottom: 16px;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-cell {
  white-space: pre-wrap;
  line-height: 1.8;
  color: #334155;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
