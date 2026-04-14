<template>
  <div class="module-page">
    <el-card shadow="never">
      <div slot="header" class="page-title">
        <i class="iconfont icon-r-find"></i>
        社团管理
      </div>
      <el-form :inline="true" :model="qryForm" class="filter-form">
        <el-form-item>
          <el-input v-model="qryForm.name" placeholder="输入社团名称" clearable autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="qryForm.typeId" clearable placeholder="选择社团类型">
            <el-option v-for="item in teamTypes" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getPageLikeInfo">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :md="8">
        <div class="summary-card">
          <div class="summary-label">可见社团</div>
          <div class="summary-value">{{ totalInfo }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :md="8">
        <div class="summary-card">
          <div class="summary-label">当前身份</div>
          <div class="summary-value">{{ roleLabel }}</div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <div slot="header" class="list-header">
        <el-button v-if="userType === 0" type="primary" @click="showAddWin">新增社团</el-button>
      </div>

      <el-table
        v-loading="loading"
        element-loading-text="正在加载社团数据"
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(124, 124, 124, 0.18)"
        :data="pageInfos"
        border
      >
        <el-table-column align="center" type="index" width="60"></el-table-column>
        <el-table-column align="center" prop="name" label="社团名称" min-width="160"></el-table-column>
        <el-table-column align="center" prop="typeName" label="社团类型" min-width="140"></el-table-column>
        <el-table-column align="center" prop="managerName" label="社团管理员" min-width="120"></el-table-column>
        <el-table-column align="center" prop="managerPhone" label="管理员电话" min-width="140"></el-table-column>
        <el-table-column align="center" prop="createTime" label="成立时间" min-width="140"></el-table-column>
        <el-table-column align="center" prop="total" label="社团人数" width="100"></el-table-column>
        <el-table-column align="center" label="社团介绍" width="110">
          <template slot-scope="scope">
            <el-button type="text" @click="showIntro(scope.row)">查看介绍</el-button>
          </template>
        </el-table-column>

        <el-table-column v-if="userType === 0 || userType === 1" align="center" width="180" label="操作" fixed="right">
          <template slot-scope="scope">
            <el-button v-if="userType === 0" type="text" @click="showUpdWin(scope.row)">编辑</el-button>
            <el-button
              v-if="userType === 0 || (userType === 1 && scope.row.manager === currentUserId)"
              type="text"
              class="danger-text"
              @click="delInfo(scope.row)"
            >
              {{ userType === 1 ? "解散社团" : "删除" }}
            </el-button>
          </template>
        </el-table-column>

        <el-table-column v-if="userType === 1 || userType === 2" align="center" label="加入状态" fixed="right" width="180">
          <template slot-scope="scope">
            <el-button
              :type="scope.row.applyButtonType"
              :disabled="scope.row.applyDisabled"
              @click="apply(scope.row.id)"
            >
              {{ scope.row.applyStatusText }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="pageTotal >= 0"
        class="page-bar"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageIndex"
        :page-sizes="[5, 10, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalInfo"
      >
      </el-pagination>
    </el-card>

    <el-dialog :title="dialogMode === 'add' ? '新增社团' : '编辑社团'" width="640px" :visible.sync="dialogVisible" @close="initForm">
      <el-form label-width="105px" :model="teamsForm">
        <el-form-item label="社团名称">
          <el-input v-model="teamsForm.name" placeholder="请输入社团名称" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="社团类型">
          <el-select v-model="teamsForm.typeId" style="width: 100%" placeholder="请选择社团类型">
            <el-option v-for="item in teamTypes" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="社团管理员">
          <el-select
            v-model="teamsForm.manager"
            style="width: 100%"
            filterable
            placeholder="请选择社团管理员"
            @change="syncSelectedManager"
          >
            <el-option
              v-for="item in managers"
              :key="item.id"
              :label="managerLabel(item)"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="selectedManager.id" label="管理员信息">
          <div class="manager-card">
            <div class="manager-card__name">{{ selectedManager.name }}</div>
            <div class="manager-card__meta">
              <span>{{ selectedManager.phone || "暂无电话" }}</span>
              <span>{{ selectedManager.userName || "暂无账号" }}</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="社团介绍">
          <el-input
            v-model="teamsForm.intro"
            type="textarea"
            :rows="6"
            maxlength="1000"
            show-word-limit
            placeholder="请填写社团定位、特色活动、适合人群和加入收获，让学生在申请前就能了解这个社团。"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">{{ dialogMode === 'add' ? '确认新增' : '保存修改' }}</el-button>
      </div>
    </el-dialog>

    <el-dialog title="社团介绍" width="720px" :visible.sync="introVisible">
      <div class="team-detail">
        <div class="team-detail__header">
          <div>
            <div class="team-detail__title">{{ activeTeam.name || "社团详情" }}</div>
            <div class="team-detail__tags">
              <el-tag size="mini">{{ activeTeam.typeName || "未分类" }}</el-tag>
              <el-tag v-if="activeTeam.manager === currentUserId" size="mini" type="warning">我负责</el-tag>
              <el-tag v-else-if="joinedTeamIds.includes(activeTeam.id)" size="mini" type="success">已加入</el-tag>
              <el-tag v-else-if="applyingTeamIds.includes(activeTeam.id)" size="mini" type="info">审核中</el-tag>
            </div>
          </div>
          <div class="team-detail__count">{{ activeTeam.total || 0 }} 人</div>
        </div>

        <div class="team-detail__grid">
          <div class="team-detail__meta">
            <span class="team-detail__label">社团管理员</span>
            <strong>{{ activeTeam.managerName || "暂无" }}</strong>
          </div>
          <div class="team-detail__meta">
            <span class="team-detail__label">联系电话</span>
            <strong>{{ activeTeam.managerPhone || "暂无" }}</strong>
          </div>
          <div class="team-detail__meta">
            <span class="team-detail__label">成立时间</span>
            <strong>{{ activeTeam.createTime || "暂无" }}</strong>
          </div>
          <div class="team-detail__meta">
            <span class="team-detail__label">社团人数</span>
            <strong>{{ activeTeam.total || 0 }} 人</strong>
          </div>
        </div>

        <div class="team-detail__section">
          <div class="team-detail__section-title">社团简介</div>
          <div class="team-detail__section-body">
            {{ activeTeam.intro || "-" }}
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="introVisible = false">关闭</el-button>
        <el-button
          v-if="showIntroApplyButton"
          :type="activeTeam.applyButtonType || 'primary'"
          :disabled="activeTeam.applyDisabled"
          @click="apply(activeTeam.id, true)"
        >
          {{ activeTeam.applyStatusText || "申请加入" }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
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
} from "../../api";

export default {
  data() {
    return {
      teamTypes: [],
      managers: [],
      userType: "",
      currentUserId: "",
      pageInfos: [],
      pageIndex: 1,
      pageSize: 10,
      pageTotal: 0,
      totalInfo: 0,
      loading: true,
      dialogVisible: false,
      introVisible: false,
      dialogMode: "add",
      joinedTeamIds: [],
      applyingTeamIds: [],
      qryForm: {
        name: "",
        typeId: "",
        token: this.$store.state.token,
      },
      teamsForm: {
        id: "",
        name: "",
        total: 1,
        manager: "",
        typeId: "",
        intro: "",
      },
      selectedManager: {},
      activeTeam: {},
    };
  },
  computed: {
    roleLabel() {
      if (this.userType === 0) return "系统管理员";
      if (this.userType === 1) return "社团管理员";
      if (this.userType === 2) return "学生";
      return "未登录";
    },
    showIntroApplyButton() {
      return (this.userType === 1 || this.userType === 2) && !!this.activeTeam.id;
    },
  },
  methods: {
    managerLabel(item) {
      return `${item.name}（${item.userName} / ${item.phone || "暂无电话"}）`;
    },
    decorateTeamRows(rows) {
      if (this.userType !== 1 && this.userType !== 2) {
        return rows || [];
      }

      return (rows || []).map((row) => {
        let applyStatusText = "申请加入";
        let applyDisabled = false;
        let applyButtonType = "primary";

        if (this.userType === 1 && row.manager === this.currentUserId) {
          applyStatusText = "我负责";
          applyDisabled = true;
          applyButtonType = "warning";
        } else if (this.joinedTeamIds.includes(row.id)) {
          applyStatusText = "已加入";
          applyDisabled = true;
          applyButtonType = "success";
        } else if (this.applyingTeamIds.includes(row.id)) {
          applyStatusText = "审核中";
          applyDisabled = true;
          applyButtonType = "info";
        }

        return {
          ...row,
          applyStatusText,
          applyDisabled,
          applyButtonType,
        };
      });
    },
    async loadJoinStatus() {
      if (this.userType !== 1 && this.userType !== 2) {
        this.joinedTeamIds = [];
        this.applyingTeamIds = [];
        return;
      }

      const [memberResp, pendingResp] = await Promise.all([
        getMyMemberTeamIds(this.qryForm.token),
        getMyPendingApplyTeamIds(this.qryForm.token),
      ]);

      this.joinedTeamIds = memberResp.data || [];
      this.applyingTeamIds = pendingResp.data || [];
    },
    getPageInfo(pageIndex, pageSize) {
      getPageTeams(pageIndex, pageSize, this.qryForm.token).then((resp) => {
        this.pageInfos = this.decorateTeamRows(resp.data.data || []);
        this.pageIndex = resp.data.pageIndex;
        this.pageSize = resp.data.pageSize;
        this.pageTotal = resp.data.pageTotal;
        this.totalInfo = resp.data.count;
        this.loading = false;
      });
    },
    getPageLikeInfo() {
      getPageTeams(1, this.pageSize, this.qryForm.token, this.qryForm.name, this.qryForm.typeId).then((resp) => {
        this.pageInfos = this.decorateTeamRows(resp.data.data || []);
        this.pageIndex = resp.data.pageIndex;
        this.pageSize = resp.data.pageSize;
        this.totalInfo = resp.data.count;
        this.pageTotal = resp.data.pageTotal;
        this.loading = false;
      });
    },
    handleSizeChange(pageSize) {
      this.getPageInfo(this.pageIndex, pageSize);
    },
    handleCurrentChange(pageIndex) {
      this.getPageInfo(pageIndex, this.pageSize);
    },
    initForm() {
      this.teamsForm = {
        id: "",
        name: "",
        total: 1,
        manager: "",
        typeId: "",
        intro: "",
      };
      this.selectedManager = {};
      this.dialogMode = "add";
    },
    showIntro(row) {
      this.activeTeam = { ...row };
      this.introVisible = true;
    },
    showAddWin() {
      this.initForm();
      this.dialogVisible = true;
    },
    showUpdWin(row) {
      this.dialogMode = "edit";
      this.teamsForm = {
        id: row.id,
        name: row.name,
        total: row.total,
        manager: row.manager,
        typeId: row.typeId,
        intro: row.intro || "",
      };
      this.syncSelectedManager(row.manager);
      this.dialogVisible = true;
    },
    syncSelectedManager(managerId) {
      this.selectedManager = this.managers.find((item) => item.id === managerId) || {};
    },
    submitForm() {
      if (!this.teamsForm.name || !this.teamsForm.typeId || !this.teamsForm.manager || !this.teamsForm.intro) {
        this.$message.warning("请完整填写社团名称、类型、管理员和社团介绍");
        return;
      }
      const request = this.dialogMode === "add" ? addTeams(this.teamsForm) : updateTeams(this.teamsForm);
      request.then((resp) => {
        this.$message({
          message: resp.msg,
          type: resp.code === 0 ? "success" : "warning",
        });
        if (resp.code === 0) {
          this.getPageInfo(1, this.pageSize);
          this.dialogVisible = false;
          this.initForm();
        }
      });
    },
    delInfo(row) {
      const actionText = this.userType === 1 ? "解散社团" : "删除社团";
      this.$confirm(`${actionText}会同时清理该社团相关的成员、通知、活动和费用记录，是否继续？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        deleteTeams(this.$store.state.token, row.id).then((resp) => {
          this.$message({
            message: resp.msg,
            type: resp.code === 0 ? "success" : "warning",
          });
          if (resp.code === 0) {
            this.getPageInfo(1, this.pageSize);
          }
        });
      });
    },
    apply(id, closeAfterSuccess = false) {
      this.$confirm("确认申请加入该社团吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        addApplyLogs({
          teamId: id,
          status: 0,
          token: this.$store.state.token,
        }).then(async (resp) => {
          if (resp.code === 0) {
            this.$message({
              message: "申请已提交，请耐心等待审核",
              type: "success",
            });
            if (closeAfterSuccess) {
              this.introVisible = false;
            }
            await this.loadJoinStatus();
            this.getPageInfo(1, this.pageSize);
          } else {
            this.$message({
              message: resp.msg,
              type: "warning",
            });
          }
        });
      });
    },
  },
  mounted() {
    Promise.all([getAllTypes(), getManagers()]).then(([typeResp, managerResp]) => {
      this.teamTypes = typeResp.data || [];
      this.managers = managerResp.data || [];
    });
    getLoginUser(this.$store.state.token).then(async (resp) => {
      this.userType = resp.data.type;
      this.currentUserId = resp.data.id;
      await this.loadJoinStatus();
      this.getPageInfo(1, this.pageSize);
    });
  },
};
</script>

<style scoped>
.module-page {
  display: grid;
  gap: 16px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.summary-row {
  margin: 0 !important;
}

.summary-card {
  height: 100%;
  padding: 20px;
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(245, 249, 255, 0.98));
  box-shadow: 0 16px 30px rgba(148, 163, 184, 0.16);
}

.summary-label {
  color: #64748b;
  font-size: 13px;
}

.summary-value {
  margin-top: 8px;
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
}

.summary-desc {
  margin-top: 8px;
  color: #94a3b8;
  line-height: 1.7;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.list-header .el-alert {
  flex: 1;
}

.danger-text {
  color: #ef4444;
}

.manager-card {
  padding: 12px 14px;
  border-radius: 16px;
  background: #f8fbff;
  border: 1px solid #e2e8f0;
}

.manager-card__name {
  color: #0f172a;
  font-size: 15px;
  font-weight: 700;
}

.manager-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 6px;
  color: #64748b;
}

.team-detail {
  display: grid;
  gap: 18px;
}

.team-detail__header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  border-radius: 20px;
  background: linear-gradient(135deg, #eff6ff, #f8fbff);
  border: 1px solid #dbeafe;
}

.team-detail__title {
  color: #0f172a;
  font-size: 24px;
  font-weight: 700;
}

.team-detail__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.team-detail__count {
  min-width: 90px;
  color: #2563eb;
  font-size: 24px;
  font-weight: 700;
  text-align: right;
}

.team-detail__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.team-detail__meta {
  padding: 14px 16px;
  border-radius: 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.team-detail__label {
  display: block;
  margin-bottom: 6px;
  color: #94a3b8;
  font-size: 13px;
}

.team-detail__section {
  padding: 18px 20px;
  border-radius: 20px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
}

.team-detail__section-title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.team-detail__section-body {
  margin-top: 12px;
  color: #475569;
  line-height: 1.9;
  white-space: pre-line;
}

.page-bar {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .list-header {
    flex-direction: column;
    align-items: stretch;
  }

  .team-detail__header,
  .team-detail__grid {
    grid-template-columns: 1fr;
  }

  .team-detail__header {
    flex-direction: column;
  }

  .team-detail__count {
    text-align: left;
  }
}
</style>
