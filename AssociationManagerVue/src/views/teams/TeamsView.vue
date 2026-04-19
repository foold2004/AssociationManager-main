<template>
  <div class="page-shell">
    <section class="stats-grid">
      <article class="stat-card">
        <span class="stat-label">社团总数</span>
        <strong class="stat-value">{{ total }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-label">当前角色</span>
        <strong class="stat-value">{{ roleLabel }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-label">已加入社团</span>
        <strong class="stat-value">{{ memberTeamIds.length }}</strong>
      </article>
      <article class="stat-card">
        <span class="stat-label">待审核申请</span>
        <strong class="stat-value">{{ pendingTeamIds.length }}</strong>
      </article>
    </section>

    <section class="page-block">
      <div class="page-toolbar">
        <div class="toolbar-left">
          <a-input
            v-model="queryForm.name"
            allow-clear
            placeholder="输入社团名称"
            style="width: 220px"
            @press-enter="loadTeams"
          />
          <a-select
            v-model="queryForm.typeId"
            allow-clear
            placeholder="选择社团类型"
            style="width: 220px"
          >
            <a-option v-for="item in teamTypes" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-option>
          </a-select>
          <a-button type="primary" @click="loadTeams">查询</a-button>
          <a-button @click="resetQuery">重置</a-button>
        </div>

        <a-button v-if="canManageTeams" type="primary" @click="openCreateModal">
          新建社团
        </a-button>
      </div>
    </section>

    <section class="page-card table-card">
      <a-table
        :bordered="false"
        :data="teams"
        :loading="loading"
        :pagination="false"
        row-key="id"
        :scroll="{ x: 1220 }"
      >
        <template #columns>
          <a-table-column title="封面" :width="110" align="center">
            <template #cell="{ record }">
              <div class="cover-cell">
                <img
                  v-if="record.coverImage"
                  :src="record.coverImage"
                  :alt="record.name"
                  class="cover-cell__image"
                />
                <span v-else class="cover-cell__placeholder">无图</span>
              </div>
            </template>
          </a-table-column>
          <a-table-column title="社团名称" data-index="name" :width="180" />
          <a-table-column title="社团类型" data-index="typeName" :width="150" />
          <a-table-column title="社团管理员" data-index="managerName" :width="140" />
          <a-table-column title="管理员电话" data-index="managerPhone" :width="150" />
          <a-table-column title="成立时间" :width="150">
            <template #cell="{ record }">
              {{ formatDate(record.createTime) }}
            </template>
          </a-table-column>
          <a-table-column title="人数" data-index="total" :width="90" align="center" />
          <a-table-column title="社团介绍" :width="120" align="center">
            <template #cell="{ record }">
              <a-button type="text" @click="showTeamDetail(record)">查看详情</a-button>
            </template>
          </a-table-column>
          <a-table-column title="加入状态" :width="180" align="center">
            <template #cell="{ record }">
              <template v-if="!canApplyTeam">
                <a-tag color="blue">浏览模式</a-tag>
              </template>
              <template v-else-if="isManagerOfTeam(record)">
                <a-tag color="green">我管理的社团</a-tag>
              </template>
              <template v-else-if="isJoinedTeam(record.id)">
                <a-tag color="green">已加入</a-tag>
              </template>
              <template v-else-if="isPendingTeam(record.id)">
                <a-tag color="orange">审核中</a-tag>
              </template>
              <template v-else>
                <a-button type="primary" size="small" @click="applyTeam(record)">
                  申请加入
                </a-button>
              </template>
            </template>
          </a-table-column>
          <a-table-column v-if="canManageTeams" title="操作" :width="180" align="center" fixed="right">
            <template #cell="{ record }">
              <a-space>
                <a-button v-if="canEditTeam(record)" type="text" @click="openEditModal(record)">
                  编辑
                </a-button>
                <a-button v-if="canDeleteTeam(record)" type="text" status="danger" @click="removeTeam(record)">
                  删除
                </a-button>
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
    </section>

    <a-modal
      v-model:visible="teamModalVisible"
      :title="editingTeamId ? '编辑社团' : '新建社团'"
      width="840px"
      @before-ok="submitTeam"
    >
      <a-form ref="teamFormRef" :model="teamForm" layout="vertical">
        <a-grid :cols="2" :col-gap="16">
          <a-grid-item>
            <a-form-item
              field="name"
              label="社团名称"
              :rules="[{ required: true, message: '请输入社团名称' }]"
            >
              <a-input v-model="teamForm.name" placeholder="输入社团名称" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item
              field="typeId"
              label="社团类型"
              :rules="[{ required: true, message: '请选择社团类型' }]"
            >
              <a-select v-model="teamForm.typeId" placeholder="选择社团类型">
                <a-option v-for="item in teamTypes" :key="item.id" :value="item.id">
                  {{ item.name }}
                </a-option>
              </a-select>
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item
              field="manager"
              label="社团管理员"
              :rules="[{ required: true, message: '请选择社团管理员' }]"
            >
              <a-select
                v-model="teamForm.manager"
                :disabled="Number(userType) === 1"
                placeholder="选择社团管理员"
              >
                <a-option v-for="item in managers" :key="item.id" :value="item.id">
                  {{ item.name }}
                </a-option>
              </a-select>
            </a-form-item>
          </a-grid-item>
          <a-grid-item>
            <a-form-item field="total" label="社团人数">
              <a-input-number v-model="teamForm.total" :min="1" :max="9999" style="width: 100%" />
            </a-form-item>
          </a-grid-item>
          <a-grid-item :span="2">
            <a-form-item field="intro" label="社团介绍">
              <a-textarea
                v-model="teamForm.intro"
                :rows="5"
                :max-length="500"
                placeholder="输入社团介绍"
                show-word-limit
              />
            </a-form-item>
          </a-grid-item>
          <a-grid-item :span="2">
            <a-form-item label="介绍配图">
              <div class="image-editor">
                <div class="image-editor__toolbar">
                  <a-button
                    type="outline"
                    :disabled="!editingTeamId || uploadingImages"
                    @click="teamImageInputRef?.click()"
                  >
                    {{ uploadingImages ? '上传中...' : '上传介绍图片' }}
                  </a-button>
                  <span v-if="!editingTeamId" class="image-editor__hint">
                    先创建社团，再编辑上传图片。
                  </span>
                  <span v-else class="image-editor__hint">
                    图片会展示在社团介绍详情里，方便吸引学生了解和加入。
                  </span>
                </div>

                <input
                  ref="teamImageInputRef"
                  type="file"
                  accept="image/*"
                  multiple
                  class="hidden-input"
                  @change="handleTeamImageSelect"
                />

                <div v-if="teamImageList.length" class="image-grid">
                  <div v-for="(image, index) in teamImageList" :key="`${image}-${index}`" class="image-card">
                    <img :src="image" alt="team-image" class="image-card__preview" />
                    <a-button
                      type="text"
                      status="danger"
                      size="mini"
                      class="image-card__remove"
                      @click="removeTeamImage(index)"
                    >
                      删除
                    </a-button>
                  </div>
                </div>

                <div v-else class="image-empty">暂无介绍图片</div>
              </div>
            </a-form-item>
          </a-grid-item>
        </a-grid>
      </a-form>
    </a-modal>

    <a-drawer v-model:visible="detailVisible" title="社团详情" width="760px">
      <div v-if="detailTeam" class="team-detail">
        <div class="team-detail__hero">
          <img
            v-if="detailTeam.coverImage"
            :src="detailTeam.coverImage"
            :alt="detailTeam.name"
            class="team-detail__cover"
          />
          <div v-else class="team-detail__cover team-detail__cover--placeholder">暂无封面</div>
        </div>

        <div class="team-detail__header">
          <div>
            <h3>{{ detailTeam.name }}</h3>
            <div class="team-detail__meta">
              <span>{{ detailTeam.typeName || '-' }}</span>
              <span>管理员：{{ detailTeam.managerName || '-' }}</span>
              <span>人数：{{ detailTeam.total || 0 }}</span>
            </div>
          </div>
        </div>

        <section class="detail-section">
          <h4>社团介绍</h4>
          <div class="detail-content">{{ detailTeam.intro || '暂无介绍' }}</div>
        </section>

        <section v-if="detailTeam.imageList?.length" class="detail-section">
          <h4>介绍图片</h4>
          <div class="detail-gallery">
            <img
              v-for="(image, index) in detailTeam.imageList"
              :key="`${image}-${index}`"
              :src="image"
              :alt="`${detailTeam.name}-${index}`"
              class="detail-gallery__image"
            />
          </div>
        </section>
      </div>
    </a-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import {
  addApplyLogs,
  addTeams,
  deleteTeams,
  getAllTypes,
  getLoginUser,
  getManagers,
  getMyMemberTeamIds,
  getMyPendingApplyTeamIds,
  getPageTeams,
  updateTeams,
  uploadTeamImage,
} from '@/api';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

const userType = computed(() => Number(authStore.userType));
const roleLabel = computed(() => {
  if (userType.value === 0) return '系统管理员';
  if (userType.value === 1) return '社团管理员';
  return '学生';
});
const canManageTeams = computed(() => userType.value === 0 || userType.value === 1);
const canApplyTeam = computed(() => userType.value === 1 || userType.value === 2);

const loading = ref(false);
const uploadingImages = ref(false);
const pageIndex = ref(1);
const pageSize = ref(10);
const total = ref(0);
const teams = ref([]);
const teamTypes = ref([]);
const managers = ref([]);
const pendingTeamIds = ref([]);
const memberTeamIds = ref([]);
const teamImageInputRef = ref(null);

const queryForm = reactive({
  name: '',
  typeId: '',
});

const teamModalVisible = ref(false);
const teamFormRef = ref();
const editingTeamId = ref('');

const detailVisible = ref(false);
const detailTeam = ref(null);

const teamForm = reactive({
  name: '',
  typeId: '',
  manager: '',
  total: 1,
  intro: '',
  images: '',
});

const teamImageList = computed(() => parseTeamImages(teamForm.images));

function parseTeamImages(images) {
  return String(images || '')
    .split(',')
    .map((item) => resolveImageUrl(item))
    .filter(Boolean);
}

function resolveImageUrl(image) {
  const value = String(image || '').trim();
  if (!value) return '';
  if (value.startsWith('/association/uploads/') || value.startsWith('/uploads/') || value.startsWith('http')) {
    return value;
  }
  return '';
}

function normalizeTeamRecord(record) {
  const imageList = parseTeamImages(record?.images);
  return {
    ...record,
    imageList,
    coverImage: imageList[0] || '',
  };
}

function formatDate(value) {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}

function resetTeamForm() {
  editingTeamId.value = '';
  teamForm.name = '';
  teamForm.typeId = '';
  teamForm.manager = userType.value === 1 ? authStore.user?.id || '' : '';
  teamForm.total = 1;
  teamForm.intro = '';
  teamForm.images = '';
  if (teamImageInputRef.value) {
    teamImageInputRef.value.value = '';
  }
  teamFormRef.value?.clearValidate?.();
}

async function loadBaseOptions() {
  const [typesRes, managersRes] = await Promise.all([getAllTypes(), getManagers()]);
  teamTypes.value = typesRes.data || [];
  managers.value = managersRes.data || [];
}

async function loadMembershipState() {
  if (!canApplyTeam.value || !authStore.token) {
    pendingTeamIds.value = [];
    memberTeamIds.value = [];
    return;
  }

  const [pendingRes, memberRes] = await Promise.all([
    getMyPendingApplyTeamIds(authStore.token),
    getMyMemberTeamIds(authStore.token),
  ]);
  pendingTeamIds.value = (pendingRes.data || []).map((item) => String(item));
  memberTeamIds.value = (memberRes.data || []).map((item) => String(item));
}

async function loadTeams() {
  loading.value = true;
  try {
    const response = await getPageTeams(
      pageIndex.value,
      pageSize.value,
      authStore.token,
      queryForm.name || '',
      queryForm.typeId || '',
    );
    const page = response.data || {};
    teams.value = (page.data || []).map(normalizeTeamRecord);
    total.value = Number(page.count || 0);
    pageIndex.value = Number(page.pageIndex || pageIndex.value);
    pageSize.value = Number(page.pageSize || pageSize.value);
  } finally {
    loading.value = false;
  }
}

async function ensureUser() {
  if (authStore.user?.id || !authStore.token) return;
  const response = await getLoginUser(authStore.token);
  authStore.setUser(response.data || null);
}

async function loadPage() {
  await ensureUser();
  await loadBaseOptions();
  await Promise.all([loadMembershipState(), loadTeams()]);
}

async function resetQuery() {
  queryForm.name = '';
  queryForm.typeId = '';
  pageIndex.value = 1;
  await loadTeams();
}

async function changePage(nextPage) {
  pageIndex.value = nextPage;
  await loadTeams();
}

async function changePageSize(size) {
  pageSize.value = size;
  pageIndex.value = 1;
  await loadTeams();
}

function isPendingTeam(teamId) {
  return pendingTeamIds.value.includes(String(teamId));
}

function isJoinedTeam(teamId) {
  return memberTeamIds.value.includes(String(teamId));
}

function isManagerOfTeam(team) {
  return String(team.manager) === String(authStore.user?.id || '');
}

function canEditTeam(team) {
  return userType.value === 0 || isManagerOfTeam(team);
}

function canDeleteTeam(team) {
  return userType.value === 0 || isManagerOfTeam(team);
}

function openCreateModal() {
  resetTeamForm();
  teamModalVisible.value = true;
}

function openEditModal(record) {
  resetTeamForm();
  editingTeamId.value = record.id;
  teamForm.name = record.name || '';
  teamForm.typeId = record.typeId || '';
  teamForm.manager = record.manager || '';
  teamForm.total = Number(record.total || 1);
  teamForm.intro = record.intro || '';
  teamForm.images = record.images || '';
  teamModalVisible.value = true;
}

function showTeamDetail(record) {
  detailTeam.value = normalizeTeamRecord(record);
  detailVisible.value = true;
}

async function handleTeamImageSelect(event) {
  const files = Array.from(event.target.files || []);
  if (!files.length || !editingTeamId.value) {
    return;
  }

  uploadingImages.value = true;
  try {
    const uploadedImages = [];
    for (const file of files) {
      const response = await uploadTeamImage(authStore.token, editingTeamId.value, file);
      if (response?.data) {
        uploadedImages.push(response.data);
      }
    }

    if (uploadedImages.length) {
      teamForm.images = [...teamImageList.value, ...uploadedImages].join(',');
      Message.success(`已上传 ${uploadedImages.length} 张介绍图片`);
    }
  } finally {
    uploadingImages.value = false;
    if (teamImageInputRef.value) {
      teamImageInputRef.value.value = '';
    }
  }
}

function removeTeamImage(index) {
  const nextImages = [...teamImageList.value];
  nextImages.splice(index, 1);
  teamForm.images = nextImages.join(',');
}

async function submitTeam() {
  const errors = await teamFormRef.value?.validate();
  if (errors) return false;

  const basePayload = {
    name: teamForm.name,
    typeId: teamForm.typeId,
    manager: userType.value === 1 ? authStore.user?.id || '' : teamForm.manager,
    total: Number(teamForm.total || 1),
    intro: teamForm.intro,
    images: teamForm.images,
  };

  if (!editingTeamId.value) {
    await addTeams(basePayload);
    Message.success('社团已创建，可继续编辑上传介绍图片');
  } else {
    await updateTeams({
      ...basePayload,
      id: editingTeamId.value,
    });
    Message.success('社团已更新');
  }

  teamModalVisible.value = false;
  await loadPage();
  return true;
}

function removeTeam(record) {
  Modal.confirm({
    title: '确认删除该社团？',
    content: `${record.name} 删除后将无法恢复`,
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      await deleteTeams({ id: record.id });
      Message.success('社团已删除');
      await loadPage();
    },
  });
}

async function applyTeam(team) {
  await addApplyLogs({
    token: authStore.token,
    teamId: team.id,
    userId: authStore.user?.id,
  });
  Message.success(`已提交加入 ${team.name} 的申请`);
  await loadMembershipState();
}

onMounted(loadPage);
</script>

<style scoped>
.table-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}

.cover-cell {
  width: 72px;
  height: 54px;
  margin: 0 auto;
  border-radius: 12px;
  overflow: hidden;
  background: var(--surface-muted);
  border: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-cell__image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-cell__placeholder {
  color: var(--text-3);
  font-size: 12px;
}

.hidden-input {
  display: none;
}

.image-editor {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.image-editor__toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.image-editor__hint {
  color: var(--text-3);
  font-size: 13px;
}

.image-empty {
  padding: 18px;
  border: 1px dashed var(--border-color);
  border-radius: var(--radius-md);
  color: var(--text-3);
  text-align: center;
  background: var(--surface-muted);
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(132px, 1fr));
  gap: 12px;
}

.image-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid var(--border-color);
  background: #fff;
}

.image-card__preview {
  display: block;
  width: 100%;
  height: 110px;
  object-fit: cover;
}

.image-card__remove {
  position: absolute;
  right: 8px;
  top: 8px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 999px;
}

.team-detail__hero {
  margin-bottom: 18px;
}

.team-detail__cover {
  width: 100%;
  height: 240px;
  border-radius: 20px;
  object-fit: cover;
  background: var(--surface-muted);
  border: 1px solid var(--border-color);
}

.team-detail__cover--placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-3);
}

.team-detail__header h3 {
  margin: 0;
  font-size: 24px;
  color: var(--text-1);
}

.team-detail__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 10px;
  color: var(--text-2);
  font-size: 13px;
}

.detail-section {
  margin-top: 22px;
}

.detail-section h4 {
  margin: 0 0 12px;
  font-size: 16px;
  color: var(--text-1);
}

.detail-content {
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--surface-muted);
  white-space: pre-wrap;
  line-height: 1.8;
}

.detail-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
}

.detail-gallery__image {
  width: 100%;
  height: 120px;
  border-radius: 16px;
  object-fit: cover;
  border: 1px solid var(--border-color);
  background: var(--surface-muted);
}
</style>
