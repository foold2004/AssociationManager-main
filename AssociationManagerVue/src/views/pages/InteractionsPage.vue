<template>
  <div class="module-page">
    <a-card :bordered="false" class="toolbar-card">
      <div class="toolbar-row">
        <a-space wrap>
          <a-input v-model="query.keyword" placeholder="搜索标题或内容" style="width: 220px" allowClear />
          <a-input v-model="query.teamName" placeholder="输入社团名称" style="width: 220px" allowClear />
          <a-select v-model="postSort" style="width: 160px">
            <a-select-option value="latest">最新发布优先</a-select-option>
            <a-select-option value="oldest">最早发布优先</a-select-option>
          </a-select>
          <a-button type="primary" @click="handleSearch">查询</a-button>
        </a-space>
        <a-button type="primary" icon="plus" @click="openCreate">发布交流</a-button>
      </div>
    </a-card>

    <a-row :gutter="16">
      <a-col :xs="24" :xl="16">
        <a-list
          :data-source="sortedRows"
          :loading="loading"
          :locale="{ emptyText: '暂无交流内容' }"
          item-layout="vertical"
          class="interactive-list"
        >
          <a-list-item slot="renderItem" slot-scope="item" class="interactive-item">
            <div class="post-header">
              <div>
                <div class="post-title">{{ item.title }}</div>
                <div class="post-meta">
                  <span>{{ item.teamName || '公共交流区' }}</span>
                  <span>发布人：{{ item.userName || '-' }}</span>
                  <span>时间：{{ item.createTime || '-' }}</span>
                </div>
              </div>
              <a-space>
                <a-button size="small" @click="openComments(item)">评论</a-button>
                <a-popconfirm
                  v-if="canDeletePost(item)"
                  title="确认删除这条交流内容吗？"
                  @confirm="removeInteraction(item)"
                >
                  <a-button size="small" type="danger">删除</a-button>
                </a-popconfirm>
              </a-space>
            </div>
            <div class="post-content">{{ item.content }}</div>
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
      </a-col>

    </a-row>

    <a-modal
      title="发布交流"
      :visible="createVisible"
      :confirmLoading="submitting"
      okText="发布"
      cancelText="取消"
      @ok="submitPost"
      @cancel="createVisible = false"
    >
      <a-form-model :model="postForm" layout="vertical">
        <a-form-model-item label="交流标题">
          <a-input v-model="postForm.title" placeholder="请输入交流标题" />
        </a-form-model-item>
        <a-form-model-item label="所属社团">
          <a-select v-model="postForm.teamId" allowClear placeholder="选择所属社团">
            <a-select-option v-for="item in teamOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="交流内容">
          <a-textarea v-model="postForm.content" :rows="6" placeholder="请输入交流内容" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>

    <a-drawer
      title="评论区"
      placement="right"
      :width="480"
      :visible="commentVisible"
      @close="commentVisible = false"
    >
      <div v-if="currentPost" class="comment-post">
        <div class="comment-post-title">{{ currentPost.title }}</div>
        <div class="comment-post-meta">{{ currentPost.teamName || '公共交流区' }} · {{ currentPost.createTime || '-' }}</div>
        <div class="comment-post-content">{{ currentPost.content }}</div>
      </div>

      <div class="comment-toolbar">
        <div class="comment-toolbar__left">共 {{ comments.length }} 条评论</div>
        <a-select v-model="commentSort" size="small" style="width: 136px" @change="sortComments">
          <a-select-option value="oldest">最早在前</a-select-option>
          <a-select-option value="latest">最新在前</a-select-option>
        </a-select>
      </div>

      <div class="comment-editor">
        <div v-if="replyTarget" class="reply-banner">
          <span>正在回复 {{ replyTarget.userName || replyTarget.userAccount || '该用户' }}</span>
          <a @click="clearReplyTarget">取消</a>
        </div>
        <a-textarea v-model="commentText" :rows="4" placeholder="说点什么吧..." />
        <div class="comment-submit">
          <a-button type="primary" :loading="commentSubmitting" @click="submitComment">发表评论</a-button>
        </div>
      </div>

      <a-list
        :data-source="comments"
        :loading="commentLoading"
        :locale="{ emptyText: '还没有评论，来发表第一条吧' }"
      >
        <a-list-item slot="renderItem" slot-scope="item">
          <a-comment :author="item.userName || item.userAccount || '匿名用户'" :datetime="item.createTime">
            <template slot="content">
              <div>
                <div v-if="item.parentId" class="reply-quote">
                  回复 {{ item.parentUserName || '上一条评论' }}：{{ item.parentContent || '原评论已不可见' }}
                </div>
                <div class="reply-text">{{ item.content }}</div>
              </div>
            </template>
            <template slot="actions">
              <span @click="setReplyTarget(item)">回复</span>
              <span v-if="canDeleteComment(item)" @click="removeComment(item)">删除</span>
            </template>
          </a-comment>
        </a-list-item>
      </a-list>
    </a-drawer>
  </div>
</template>

<script>
import { message } from 'ant-design-vue';
import {
  addInteraction,
  addInteractionComment,
  deleteInteraction,
  deleteInteractionComment,
  getAllTeamList,
  getInteractionComments,
  getLoginUser,
  getManTeamList,
  getPageInteractions,
} from '@/api';

export default {
  name: 'InteractionsPage',
  data() {
    return {
      userInfo: this.$store.getters.user || null,
      loading: false,
      submitting: false,
      createVisible: false,
      pageIndex: 1,
      pageSize: 10,
      total: 0,
      postSort: 'latest',
      commentSort: 'oldest',
      query: {
        keyword: '',
        teamName: '',
      },
      rows: [],
      teamOptions: [],
      postForm: {
        title: '',
        teamId: undefined,
        content: '',
      },
      currentPost: null,
      commentVisible: false,
      commentLoading: false,
      commentSubmitting: false,
      comments: [],
      commentText: '',
      replyTarget: null,
    };
  },
  computed: {
    userType() {
      return this.userInfo ? this.userInfo.type : null;
    },
    sortedRows() {
      const rows = [...this.rows];
      rows.sort((a, b) => {
        const timeA = new Date(a.createTime || '').getTime() || 0;
        const timeB = new Date(b.createTime || '').getTime() || 0;
        return this.postSort === 'latest' ? timeB - timeA : timeA - timeB;
      });
      return rows;
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
        const resp = await getPageInteractions(
          this.pageIndex,
          this.pageSize,
          this.$store.getters.token,
          this.query.keyword,
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
      this.postForm = {
        title: '',
        teamId: this.userType === 1 && this.teamOptions.length ? this.teamOptions[0].id : undefined,
        content: '',
      };
      this.createVisible = true;
    },
    async submitPost() {
      if (!this.postForm.title || !this.postForm.content) {
        message.warning('请填写完整的标题和内容');
        return;
      }
      this.submitting = true;
      try {
        await addInteraction({
          token: this.$store.getters.token,
          title: this.postForm.title,
          teamId: this.postForm.teamId,
          content: this.postForm.content,
        });
        message.success('交流内容已发布');
        this.createVisible = false;
        await this.loadData();
      } finally {
        this.submitting = false;
      }
    },
    canDeletePost(record) {
      if (this.userType === 0) return true;
      return this.userInfo && record.userId === this.userInfo.id;
    },
    async removeInteraction(record) {
      await deleteInteraction(this.$store.getters.token, record.id);
      message.success('交流内容已删除');
      if (this.currentPost && this.currentPost.id === record.id) {
        this.commentVisible = false;
      }
      await this.loadData();
    },
    async openComments(record) {
      this.currentPost = record;
      this.commentVisible = true;
      this.commentText = '';
      this.commentSort = 'oldest';
      this.replyTarget = null;
      await this.loadComments();
    },
    sortComments() {
      const rows = [...this.comments];
      rows.sort((a, b) => {
        const timeA = new Date(a.createTime || '').getTime() || 0;
        const timeB = new Date(b.createTime || '').getTime() || 0;
        return this.commentSort === 'latest' ? timeB - timeA : timeA - timeB;
      });
      this.comments = rows;
    },
    async loadComments() {
      if (!this.currentPost) return;
      this.commentLoading = true;
      try {
        const resp = await getInteractionComments(this.$store.getters.token, this.currentPost.id);
        this.comments = resp.data || [];
        this.sortComments();
      } finally {
        this.commentLoading = false;
      }
    },
    setReplyTarget(item) {
      this.replyTarget = item;
    },
    clearReplyTarget() {
      this.replyTarget = null;
    },
    async submitComment() {
      if (!this.currentPost) return;
      if (!this.commentText.trim()) {
        message.warning('请输入评论内容');
        return;
      }
      this.commentSubmitting = true;
      try {
        await addInteractionComment({
          token: this.$store.getters.token,
          interactionId: this.currentPost.id,
          parentId: this.replyTarget ? this.replyTarget.id : '',
          content: this.commentText.trim(),
        });
        message.success('评论成功');
        this.commentText = '';
        this.replyTarget = null;
        await this.loadComments();
      } finally {
        this.commentSubmitting = false;
      }
    },
    canDeleteComment(item) {
      if (this.userType === 0) return true;
      return this.userInfo && item.userId === this.userInfo.id;
    },
    async removeComment(item) {
      await deleteInteractionComment(this.$store.getters.token, item.id);
      message.success('评论已删除');
      await this.loadComments();
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
.interactive-list,
.tips-card {
  border-radius: 24px;
  box-shadow: 0 18px 32px rgba(148, 163, 184, 0.14);
  background: #fff;
}

.toolbar-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.interactive-list {
  padding: 18px 22px;
}

.interactive-item {
  padding: 18px 0;
}

.post-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.post-title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.post-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
}

.post-content,
.comment-post-content {
  margin-top: 14px;
  padding: 16px;
  border-radius: 16px;
  background: #f8fbff;
  color: #334155;
  line-height: 1.8;
  white-space: pre-wrap;
}

.tips-card {
  padding: 8px 0;
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

.comment-post-title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.comment-post-meta {
  margin-top: 8px;
  color: #64748b;
}

.comment-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 18px 0 8px;
}

.comment-toolbar__left {
  color: #475569;
  font-size: 13px;
}

.comment-editor {
  margin: 18px 0 24px;
}

.reply-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 10px 12px;
  border-radius: 12px;
  background: #eff6ff;
  color: #1d4ed8;
}

.reply-quote {
  margin-bottom: 8px;
  padding: 10px 12px;
  border-left: 3px solid #93c5fd;
  border-radius: 8px;
  background: #f8fbff;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.reply-text {
  color: #334155;
  line-height: 1.8;
  white-space: pre-wrap;
}

.comment-submit {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
</style>
