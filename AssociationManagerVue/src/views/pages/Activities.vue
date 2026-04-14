<template>
  <div class="module-page">
    <el-card shadow="never">
      <div slot="header" class="page-title">
        <i class="iconfont icon-r-find"></i>
        社团活动
      </div>
      <el-form :inline="true" :model="qryForm">
        <el-form-item>
          <el-input v-model="qryForm.teamName" placeholder="输入社团名称" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="qryForm.activeName" placeholder="输入活动名称" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getPageLikeInfo">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div slot="header" class="header-row">
        <div class="header-left">
          <el-button v-if="userType === 1" type="primary" @click="showAddWin">新增活动</el-button>
          <el-button
            v-if="userType === 0 || userType === 1"
            plain
            @click="$emit && $emit('switch-module', 'activeLogs')"
          >
            报名审批
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        element-loading-text="正在加载活动数据"
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(124, 124, 124, 0.18)"
        :data="pageInfos"
        border
      >
        <el-table-column align="center" type="index" width="60"></el-table-column>
        <el-table-column align="center" prop="name" label="活动名称" min-width="180"></el-table-column>
        <el-table-column align="center" prop="teamName" label="发布社团" min-width="130"></el-table-column>
        <el-table-column align="center" prop="activeTime" label="活动时间" min-width="160"></el-table-column>
        <el-table-column align="center" prop="enrollEndTime" label="报名截止" min-width="160"></el-table-column>
        <el-table-column align="center" label="报名情况" min-width="140">
          <template slot-scope="scope">
            {{ scope.row.total || 0 }} / {{ scope.row.maxTotal || 0 }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="活动状态" min-width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.activityStateType" effect="plain">{{ scope.row.activityStateText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="userType === 1 || userType === 2" align="center" label="我的报名" min-width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.signupStateType" effect="plain">{{ scope.row.signupStateText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="参与人员" width="170">
          <template slot-scope="scope">
            <el-popover title="已通过成员" trigger="click" @show="getActivePeople(scope.row.id)" :width="560" placement="left">
              <el-table :data="activeLogs" border>
                <el-table-column align="center" type="index" width="60"></el-table-column>
                <el-table-column align="center" prop="userName" label="参与人员"></el-table-column>
                <el-table-column align="center" prop="userPhone" label="联系电话"></el-table-column>
                <el-table-column align="center" prop="createTime" label="加入时间"></el-table-column>
              </el-table>
              <el-button slot="reference" type="primary" plain>参与人员</el-button>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column v-if="userType === 0 || userType === 1" align="center" label="操作" fixed="right" width="150">
          <template slot-scope="scope">
            <el-button
              v-if="canDeleteActivity(scope.row)"
              type="danger"
              size="mini"
              @click="delInfo(scope.row.id)"
            >
              删除
            </el-button>
            <span v-else class="table-muted"></span>
          </template>
        </el-table-column>
        <el-table-column v-if="userType === 1 || userType === 2" align="center" label="查看详情" fixed="right" width="210">
          <template slot-scope="scope">
            <el-popover trigger="click" :width="720" placement="left">
              <div class="detail-actions">
                <el-button
                  :type="scope.row.signupButtonType"
                  :disabled="scope.row.signupDisabled"
                  @click="active(scope.row.id)"
                >
                  {{ scope.row.signupButtonText }}
                </el-button>
              </div>
              <el-descriptions :column="1" size="small" border>
                <el-descriptions-item>
                  <template slot="label">活动名称</template>
                  {{ scope.row.name }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">发布社团</template>
                  {{ scope.row.teamName }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">活动时间</template>
                  {{ scope.row.activeTime }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">报名截止</template>
                  {{ scope.row.enrollEndTime }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">活动状态</template>
                  {{ scope.row.activityStateText }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">我的报名</template>
                  {{ scope.row.signupStateText }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">报名上限</template>
                  {{ scope.row.maxTotal || 0 }} 人
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">已通过人数</template>
                  {{ scope.row.total || 0 }} 人
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">活动概述</template>
                  {{ scope.row.comm || "-" }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">活动要求</template>
                  {{ scope.row.ask || "-" }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">活动详情</template>
                  {{ scope.row.detail || "-" }}
                </el-descriptions-item>
              </el-descriptions>
              <el-button slot="reference" type="primary">查看详情</el-button>
            </el-popover>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="pageTotal >= 0"
        style="margin-top: 15px"
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

    <el-dialog title="新增活动" :fullscreen="true" :visible.sync="showAddFlag">
      <el-form label-width="110px" :model="activitiesForm">
        <el-form-item label="活动名称">
          <el-input v-model="activitiesForm.name" placeholder="请输入活动名称" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker
            style="width: 100%"
            value-format="yyyy-MM-dd HH:mm:ss"
            v-model="activitiesForm.activeTime"
            type="datetime"
            placeholder="选择活动时间"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="报名截止时间">
          <el-date-picker
            style="width: 100%"
            value-format="yyyy-MM-dd HH:mm:ss"
            v-model="activitiesForm.enrollEndTime"
            type="datetime"
            placeholder="选择报名截止时间"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="报名人数上限">
          <el-input-number v-model="activitiesForm.maxTotal" :min="1" :max="500" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="发布社团">
          <el-select style="width: 100%" v-model="activitiesForm.teamId" placeholder="请选择社团">
            <el-option v-for="item in teams" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="活动概述">
          <el-input type="textarea" :rows="3" v-model="activitiesForm.comm" placeholder="请输入活动概述"></el-input>
        </el-form-item>
        <el-form-item label="活动要求">
          <el-input type="textarea" :rows="4" v-model="activitiesForm.ask" placeholder="请输入活动要求"></el-input>
        </el-form-item>
        <el-form-item label="活动详情">
          <el-input type="textarea" :rows="6" v-model="activitiesForm.detail" placeholder="请输入活动详情"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddFlag = false">取消</el-button>
        <el-button type="primary" @click="addInfo">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addActiveLogs,
  addActivities,
  deleteActivities,
  getActiveLogs,
  getLoginUser,
  getManTeamList,
  getMyActiveStatuses,
  getPageActivities,
} from "../../api";

export default {
  data() {
    return {
      teams: [],
      userType: "",
      currentUserId: "",
      activeLogs: [],
      activeStatusMap: {},
      pageInfos: [],
      pageIndex: 1,
      pageSize: 10,
      pageTotal: 0,
      totalInfo: 0,
      loading: true,
      showAddFlag: false,
      qryForm: {
        token: this.$store.state.token,
        teamName: "",
        activeName: "",
      },
      activitiesForm: {
        id: "",
        name: "",
        comm: "",
        detail: "",
        ask: "",
        total: 1,
        maxTotal: 30,
        activeTime: "",
        enrollEndTime: "",
        teamId: "",
      },
    };
  },
  methods: {
    getActivityState(row) {
      const now = new Date().getTime();
      const deadline = new Date(row.enrollEndTime || "").getTime();
      const total = Number(row.total || 0);
      const maxTotal = Number(row.maxTotal || 0);
      if (deadline && deadline < now) {
        return { text: "报名截止", type: "info" };
      }
      if (maxTotal > 0 && total >= maxTotal) {
        return { text: "人数已满", type: "warning" };
      }
      return { text: "报名开放", type: "success" };
    },
    getSignupState(status, row) {
      const isManagedByCurrentUser = this.userType === 1 && row && row.managerId === this.currentUserId;
      if (status === 1) {
        return { text: isManagedByCurrentUser ? "已参加" : "已通过", type: "success" };
      }
      if (status === 2) return { text: "已驳回", type: "danger" };
      if (status === 0) {
        return { text: isManagedByCurrentUser ? "待审核" : "待对方审核", type: "warning" };
      }
      return { text: "未报名", type: "info" };
    },
    buildSignupButton(row, activityState) {
      const signupStatus = this.activeStatusMap[row.id];
      const isManagedByCurrentUser = this.userType === 1 && row.managerId === this.currentUserId;
      if (signupStatus === 1) {
        return { text: isManagedByCurrentUser ? "已参加" : "已通过", type: "success", disabled: true };
      }
      if (signupStatus === 0) {
        return { text: isManagedByCurrentUser ? "审核中" : "待对方审核", type: "warning", disabled: true };
      }
      if (activityState.text === "报名截止") {
        return { text: "报名已截止", type: "info", disabled: true };
      }
      if (activityState.text === "人数已满") {
        return { text: "人数已满", type: "warning", disabled: true };
      }
      if (signupStatus === 2) {
        return { text: "重新报名", type: "primary", disabled: false };
      }
      if (this.userType === 1) {
        return { text: isManagedByCurrentUser ? "加入活动" : "申请参加", type: "primary", disabled: false };
      }
      return { text: "我要报名", type: "primary", disabled: false };
    },
    decorateActivityRows(rows) {
      return (rows || []).map((row) => {
        const activityState = this.getActivityState(row);
        const signupState = this.getSignupState(this.activeStatusMap[row.id], row);
        const signupButton = this.buildSignupButton(row, activityState);
        return {
          ...row,
          activityStateText: activityState.text,
          activityStateType: activityState.type,
          signupStateText: signupState.text,
          signupStateType: signupState.type,
          signupButtonText: signupButton.text,
          signupButtonType: signupButton.type,
          signupDisabled: this.userType === 1 || this.userType === 2 ? signupButton.disabled : true,
        };
      });
    },
    async loadMyActivityStatus() {
      if (this.userType !== 1 && this.userType !== 2) {
        this.activeStatusMap = {};
        return;
      }
      const resp = await getMyActiveStatuses(this.qryForm.token);
      this.activeStatusMap = resp.data || {};
    },
    getActivePeople(activeId) {
      getActiveLogs(activeId).then((resp) => {
        this.activeLogs = resp.data || [];
      });
    },
    getPageInfo(pageIndex, pageSize) {
      this.loading = true;
      getPageActivities(pageIndex, pageSize, this.qryForm.token, this.qryForm.teamName, this.qryForm.activeName).then((resp) => {
        this.pageInfos = this.decorateActivityRows((resp.data && resp.data.data) || []);
        this.pageIndex = resp.data.pageIndex;
        this.pageSize = resp.data.pageSize;
        this.pageTotal = resp.data.pageTotal;
        this.totalInfo = resp.data.count;
        this.loading = false;
      });
    },
    getPageLikeInfo() {
      this.getPageInfo(1, this.pageSize);
    },
    handleSizeChange(pageSize) {
      this.getPageInfo(this.pageIndex, pageSize);
    },
    handleCurrentChange(pageIndex) {
      this.getPageInfo(pageIndex, this.pageSize);
    },
    canDeleteActivity(row) {
      if (this.userType === 0) return true;
      return this.userType === 1 && row.managerId === this.currentUserId;
    },
    initForm() {
      this.activitiesForm = {
        id: "",
        name: "",
        comm: "",
        detail: "",
        ask: "",
        total: 1,
        maxTotal: 30,
        activeTime: "",
        enrollEndTime: "",
        teamId: this.teams.length ? this.teams[0].id : "",
      };
    },
    showAddWin() {
      this.initForm();
      this.showAddFlag = true;
    },
    active(id) {
      addActiveLogs({
        token: this.$store.state.token,
        activeId: id,
      }).then(async (resp) => {
        this.$message({
          message: resp.msg,
          type: resp.code === 0 ? "success" : "warning",
        });
        if (resp.code === 0) {
          await this.loadMyActivityStatus();
          this.getPageInfo(this.pageIndex, this.pageSize);
        }
      });
    },
    addInfo() {
      addActivities({
        ...this.activitiesForm,
        token: this.$store.state.token,
      }).then((resp) => {
        this.$message({
          message: resp.msg,
          type: resp.code === 0 ? "success" : "warning",
        });
        if (resp.code === 0) {
          this.getPageInfo(1, this.pageSize);
          this.showAddFlag = false;
        }
      });
    },
    delInfo(id) {
      this.$confirm("删除活动会同时删除该活动的报名记录，是否继续？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        deleteActivities(this.$store.state.token, id).then((resp) => {
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
  },
  mounted() {
    getLoginUser(this.$store.state.token).then(async (resp) => {
      this.userType = resp.data.type;
      this.currentUserId = resp.data.id;
      if (resp.data.type === 1) {
        const teamResp = await getManTeamList(resp.data.id);
        this.teams = teamResp.data || [];
      }
      await this.loadMyActivityStatus();
      this.getPageInfo(1, this.pageSize);
    });
  },
};
</script>

<style scoped>
.module-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.page-title {
  font-size: 26px;
  font-weight: 700;
  color: #203152;
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-tip {
  color: #7b879e;
  font-size: 14px;
}

.detail-actions {
  margin-bottom: 15px;
}

.table-muted {
  color: #a0a9ba;
}
</style>
