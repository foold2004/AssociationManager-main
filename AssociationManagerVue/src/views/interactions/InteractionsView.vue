<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-card__label">交流总数</span>
        <strong class="stat-card__value">{{ total }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">当前页数</span>
        <strong class="stat-card__value">{{ rows.length }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-card__label">当前评论</span>
        <strong class="stat-card__value">{{ comments.length }}</strong>
      </article>
    </section>

    <section class="page-block">
      <div class="page-toolbar">
        <div class="filter-row">
          <div class="filter-row__main">
            <a-input
              v-model="queryForm.keyword"
              allow-clear
              placeholder="输入标题或内容"
              style="width: 220px"
              @press-enter="search"
            />
            <a-select
              v-model="queryForm.teamId"
              allow-clear
              placeholder="选择社团"
              style="width: 220px"
            >
              <a-option v-for="item in teamOptions" :key="item.id" :value="item.id">
                {{ item.name }}
              </a-option>
            </a-select>
            <a-button type="primary" @click="search">查询</a-button>
            <a-button @click="resetQuery">重置</a-button>
          </div>

          <div class="filter-row__actions">
            <a-button type="primary" @click="openCreate">发布交流</a-button>
          </div>
        </div>
      </div>
    </section>

    <section class="page-card">
      <div class="page-card__body">
        <a-list :data="rows" :loading="loading" :bordered="false">
          <template #item="{ item }">
            <a-list-item>
              <div class="post-item">
                <div class="post-head">
                  <div class="post-head__main">
                    <div class="post-title">{{ item.title }}</div>
                    <div class="post-meta">
                      <span>{{ item.teamName || '公共交流' }}</span>
                      <span>发布人：{{ item.userName || '-' }}</span>
                      <span>{{ formatDate(item.createTime) }}</span>
                    </div>
                  </div>

                  <a-space>
                    <a-button type="text" @click="openComments(item)">评论</a-button>
                    <a-button
                      v-if="canDeletePost(item)"
                      type="text"
                      status="danger"
                      @click="removePost(item)"
                    >
                      删除
                    </a-button>
                  </a-space>
                </div>

                <div class="post-content">{{ item.content || '-' }}</div>
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
      title="发布交流"
      width="720px"
      @before-ok="submitPost"
    >
      <a-form ref="formRef" :model="form" layout="vertical">
        <a-form-item
          field="title"
          label="交流标题"
          :rules="[{ required: true, message: '请输入交流标题' }]"
        >
          <a-input v-model="form.title" placeholder="输入交流标题" />
        </a-form-item>
        <a-form-item field="teamId" label="所属社团">
          <a-select v-model="form.teamId" allow-clear placeholder="选择所属社团">
            <a-option v-for="item in teamOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-option>
          </a-select>
        </a-form-item>
        <a-form-item
          field="content"
          label="交流内容"
          :rules="[{ required: true, message: '请输入交流内容' }]"
        >
          <a-textarea v-model="form.content" :rows="8" placeholder="输入交流内容" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-drawer v-model:visible="drawerVisible" title="评论区" width="560px">
      <div v-if="currentPost" class="drawer-post">
        <div class="drawer-post__title">{{ currentPost.title }}</div>
        <div class="drawer-post__meta">
          <span>{{ currentPost.teamName || '公共交流' }}</span>
          <span>{{ formatDate(currentPost.createTime) }}</span>
        </div>
        <div class="drawer-post__content">{{ currentPost.content || '-' }}</div>
      </div>

      <div class="comment-editor">
        <div v-if="replyTarget" class="reply-banner">
          <span>正在回复 {{ replyTarget.userName || replyTarget.userAccount || '该用户' }}</span>
          <a-link @click="clearReply">取消</a-link>
        </div>
        <a-textarea v-model="commentForm.content" :rows="4" placeholder="输入评论内容" />
        <div class="comment-editor__action">
          <a-button type="primary" :loading="commentSubmitting" @click="submitComment">
            发表评论
          </a-button>
        </div>
      </div>

      <a-list :data="comments" :loading="commentLoading" :bordered="false">
        <template #item="{ item }">
          <a-list-item>
            <div class="comment-item">
              <div class="comment-item__head">
                <strong>{{ item.userName || item.userAccount || '-' }}</strong>
                <span>{{ formatDate(item.createTime) }}</span>
              </div>
              <div v-if="item.parentId" class="comment-item__quote">
                回复 {{ item.parentUserName || '上级评论' }}：{{ item.parentContent || '原评论已不可见' }}
              </div>
              <div class="comment-item__content">{{ item.content }}</div>
              <div class="comment-item__action">
                <a-button type="text" size="mini" @click="setReply(item)">回复</a-button>
                <a-button
                  v-if="canDeleteComment(item)"
                  type="text"
                  size="mini"
                  status="danger"
                  @click="removeComment(item)"
                >
                  删除
                </a-button>
              </div>
            </div>
          </a-list-item>
        </template>
      </a-list>
    </a-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
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
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const pageIndex = ref(1);
const pageSize = ref(10);
const teamOptions = ref([]);
const modalVisible = ref(false);
const formRef = ref();
const drawerVisible = ref(false);
const currentPost = ref(null);
const commentLoading = ref(false);
const commentSubmitting = ref(false);
const comments = ref([]);
const replyTarget = ref(null);

const queryForm = reactive({
  keyword: '',
  teamId: '',
});

const form = reactive({
  title: '',
  teamId: '',
  content: '',
});

const commentForm = reactive({
  content: '',
});

const userType = computed(() => Number(authStore.userType));
const teamNameMap = computed(() => {
  const map = new Map();
  (teamOptions.value || []).forEach((item) => {
    map.set(String(item.id), item.name);
  });
  return map;
});

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '-');

const ensureUser = async () => {
  if (authStore.user?.id || !authStore.token) return;
  const response = await getLoginUser(authStore.token);
  authStore.setUser(response.data || null);
};

const loadTeams = async () => {
  const response =
    userType.value === 1 ? await getManTeamList(authStore.user?.id) : await getAllTeamList();
  teamOptions.value = response.data || [];
};

const loadPosts = async () => {
  loading.value = true;
  try {
    const response = await getPageInteractions(
      pageIndex.value,
      pageSize.value,
      authStore.token,
      queryForm.keyword,
      queryForm.teamId ? teamNameMap.value.get(String(queryForm.teamId)) || '' : '',
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
  await loadPosts();
};

const resetQuery = async () => {
  queryForm.keyword = '';
  queryForm.teamId = '';
  pageIndex.value = 1;
  await loadPosts();
};

const changePage = async (nextPage) => {
  pageIndex.value = nextPage;
  await loadPosts();
};

const changePageSize = async (size) => {
  pageSize.value = size;
  pageIndex.value = 1;
  await loadPosts();
};

const resetForm = () => {
  form.title = '';
  form.teamId = userType.value === 1 ? teamOptions.value[0]?.id || '' : '';
  form.content = '';
  formRef.value?.clearValidate?.();
};

const openCreate = () => {
  resetForm();
  modalVisible.value = true;
};

const submitPost = async () => {
  const errors = await formRef.value?.validate();
  if (errors) return false;

  await addInteraction({
    title: form.title,
    teamId: form.teamId || '',
    content: form.content,
  });
  Message.success('交流内容已发布');
  modalVisible.value = false;
  await loadPosts();
  return true;
};

const canDeletePost = (record) =>
  userType.value === 0 || String(record.userId) === String(authStore.user?.id || '');

const removePost = (record) => {
  Modal.confirm({
    title: '确认删除这条交流？',
    content: record.title,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await deleteInteraction({ id: record.id });
      Message.success('交流内容已删除');
      if (currentPost.value?.id === record.id) {
        drawerVisible.value = false;
      }
      await loadPosts();
    },
  });
};

const loadComments = async () => {
  if (!currentPost.value) return;
  commentLoading.value = true;
  try {
    const response = await getInteractionComments(authStore.token, currentPost.value.id);
    comments.value = response.data || [];
  } finally {
    commentLoading.value = false;
  }
};

const openComments = async (record) => {
  currentPost.value = record;
  replyTarget.value = null;
  commentForm.content = '';
  drawerVisible.value = true;
  await loadComments();
};

const setReply = (item) => {
  replyTarget.value = item;
};

const clearReply = () => {
  replyTarget.value = null;
};

const submitComment = async () => {
  if (!currentPost.value) return;
  if (!commentForm.content.trim()) {
    Message.warning('请输入评论内容');
    return;
  }

  commentSubmitting.value = true;
  try {
    await addInteractionComment({
      interactionId: currentPost.value.id,
      parentId: replyTarget.value?.id || '',
      content: commentForm.content.trim(),
    });
    Message.success('评论已发布');
    commentForm.content = '';
    replyTarget.value = null;
    await loadComments();
  } finally {
    commentSubmitting.value = false;
  }
};

const canDeleteComment = (record) =>
  userType.value === 0 || String(record.userId) === String(authStore.user?.id || '');

const removeComment = (record) => {
  Modal.confirm({
    title: '确认删除这条评论？',
    content: record.content,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await deleteInteractionComment({ id: record.id });
      Message.success('评论已删除');
      await loadComments();
    },
  });
};

onMounted(async () => {
  await ensureUser();
  await loadTeams();
  await loadPosts();
});
</script>

<style scoped>
.page-card__body {
  padding: 20px;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}

.post-item {
  width: 100%;
}

.post-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.post-head__main {
  flex: 1;
  min-width: 0;
}

.post-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-1);
}

.post-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
  color: var(--text-2);
  font-size: 13px;
}

.post-content,
.drawer-post__content {
  margin-top: 14px;
  padding: 16px;
  background: var(--surface-muted);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  line-height: 1.8;
  white-space: pre-wrap;
}

.drawer-post__title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-1);
}

.drawer-post__meta {
  display: flex;
  gap: 12px;
  margin-top: 8px;
  color: var(--text-2);
  font-size: 13px;
}

.comment-editor {
  margin: 20px 0 24px;
}

.reply-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 10px 12px;
  background: #eef6ff;
  border-radius: var(--radius-md);
}

.comment-editor__action {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.comment-item {
  width: 100%;
}

.comment-item__head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: var(--text-2);
  font-size: 13px;
}

.comment-item__quote {
  margin-top: 10px;
  padding: 10px 12px;
  background: var(--surface-muted);
  border-left: 3px solid #9dbcf9;
  border-radius: var(--radius-sm);
  color: var(--text-2);
  font-size: 13px;
}

.comment-item__content {
  margin-top: 10px;
  line-height: 1.8;
  white-space: pre-wrap;
}

.comment-item__action {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .post-head {
    flex-direction: column;
  }
}
</style>
