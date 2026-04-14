<template>
  <div class="module-page">
    <el-card shadow="never">
      <div slot="header" class="page-title">
        <i class="iconfont icon-r-find"></i>
        费用管理
      </div>
      <el-form :inline="true" :model="qryForm" class="filter-form">
        <el-form-item>
          <el-select v-model="qryForm.teamId" clearable filterable placeholder="选择社团">
            <el-option v-for="item in teams" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="qryForm.userName" placeholder="输入成员姓名" clearable autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getPageLikeInfo">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- legacy summary (placeholder; replaced below) -->
    <!--
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :md="8">
        <div class="summary-card">
          <div class="summary-label">当前身份</div>
          <div class="summary-value">{{ roleLabel }}</div>
          <div class="summary-desc">按身份自动限制费用数据的可见范围和操作权限</div>
        </div>
      </el-col>
      <el-col :xs="24" :md="8">
        <div class="summary-card">
          <div class="summary-label">当前页记录</div>
          <div class="summary-value">{{ pageInfos.length }}</div>
          <div class="summary-desc">展示本次筛选结果中的缴费记录数量</div>
        </div>
      </el-col>
      <el-col :xs="24" :md="8">
        <div class="summary-card">
          <div class="summary-label">当前页金额</div>
          <div class="summary-value">￥{{ currentPageAmount }}</div>
          <div class="summary-desc">方便快速核对社团近期收缴情况</div>
        </div>
      </el-col>
    </el-row>
    -->

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :md="6">
        <div class="summary-card">
          <div class="summary-label">当前身份</div>
          <div class="summary-value">{{ roleLabel }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :md="6">
        <div class="summary-card">
          <div class="summary-label">累计收入</div>
          <div class="summary-value">¥{{ summaryIncome }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :md="6">
        <div class="summary-card">
          <div class="summary-label">累计支出</div>
          <div class="summary-value">¥{{ summaryExpense }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :md="6">
        <div class="summary-card">
          <div class="summary-label">当前余额</div>
          <div class="summary-value">¥{{ summaryBalance }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :md="12">
        <div class="summary-card">
          <div class="summary-label">本次筛选缴费金额</div>
          <div class="summary-value">¥{{ currentPageAmount }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :md="12">
        <div class="summary-card">
          <div class="summary-label">本次筛选消费金额</div>
          <div class="summary-value">¥{{ currentExpenseAmount }}</div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never" class="insight-card">
      <div class="insight-head">收支概览</div>
      <div class="insight-body">
        <div class="ratio-bar">
          <div class="ratio-income" :style="{ width: incomeShare + '%' }"></div>
          <div class="ratio-expense" :style="{ width: expenseShare + '%' }"></div>
        </div>
        <div class="ratio-legend">
          <span>收入 ¥{{ summaryIncome }}</span>
          <span>支出 ¥{{ summaryExpense }}</span>
          <span>余额 ¥{{ summaryBalance }}</span>
        </div>
      </div>
    </el-card>

    <el-card shadow="never">
      <div slot="header" class="list-header">
        <el-button v-if="canManage" type="primary" @click="showAddWin">新增缴费记录</el-button>
      </div>

      <el-table
        v-loading="loading"
        element-loading-text="正在加载缴费记录"
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(124, 124, 124, 0.18)"
        :data="pageInfos"
        border
      >
        <el-table-column align="center" type="index" width="60"></el-table-column>
        <el-table-column align="center" prop="teamName" label="所属社团" min-width="150"></el-table-column>
        <el-table-column align="center" prop="userName" label="成员姓名" min-width="130"></el-table-column>
        <el-table-column align="center" prop="userGender" label="性别" width="90"></el-table-column>
        <el-table-column align="center" prop="userPhone" label="联系电话" min-width="140"></el-table-column>
        <el-table-column align="center" prop="createTime" label="缴费时间" min-width="160"></el-table-column>
        <el-table-column align="center" label="缴费金额" min-width="120">
          <template slot-scope="scope">
            <span class="money-text">￥{{ formatMoney(scope.row.total) }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="canManage" align="center" label="操作" fixed="right" width="180">
          <template slot-scope="scope">
            <el-button type="text" @click="showUpdWin(scope.row)">编辑</el-button>
            <el-button type="text" class="danger-text" @click="delInfo(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && pageInfos.length === 0" description="当前筛选条件下暂无缴费记录"></el-empty>

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

    <el-card shadow="never">
      <div slot="header" class="list-header">
        <div class="list-title">
          <div class="list-title__main">消费明细</div>
        </div>
        <el-button v-if="canManage" type="primary" @click="showExpenseAddWin">新增消费明细</el-button>
      </div>

      <el-form :inline="true" :model="expenseQryForm" class="filter-form">
        <el-form-item>
          <el-select v-model="expenseQryForm.teamId" clearable filterable placeholder="选择社团">
            <el-option v-for="item in teams" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="expenseQryForm.title" placeholder="输入消费标题" clearable autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getExpensePageLikeInfo">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="expenseLoading"
        element-loading-text="正在加载消费明细"
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(124, 124, 124, 0.18)"
        :data="expensePageInfos"
        border
      >
        <el-table-column align="center" type="index" width="60"></el-table-column>
        <el-table-column align="center" prop="teamName" label="所属社团" min-width="150"></el-table-column>
        <el-table-column align="center" prop="title" label="消费标题" min-width="160"></el-table-column>
        <el-table-column align="center" prop="detail" label="用途说明" min-width="220"></el-table-column>
        <el-table-column align="center" prop="handlerName" label="记录人" min-width="120"></el-table-column>
        <el-table-column align="center" prop="createTime" label="消费时间" min-width="160"></el-table-column>
        <el-table-column align="center" label="消费金额" min-width="120">
          <template slot-scope="scope">
            <span class="money-text expense-text">¥{{ formatMoney(scope.row.total) }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="canManage" align="center" label="操作" fixed="right" width="180">
          <template slot-scope="scope">
            <el-button type="text" @click="showExpenseUpdWin(scope.row)">编辑</el-button>
            <el-button type="text" class="danger-text" @click="delExpense(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!expenseLoading && expensePageInfos.length === 0" description="当前暂无消费明细"></el-empty>

      <el-pagination
        v-if="expensePageTotal >= 0"
        class="page-bar"
        @size-change="handleExpenseSizeChange"
        @current-change="handleExpenseCurrentChange"
        :current-page="expensePageIndex"
        :page-sizes="[5, 10, 20, 50]"
        :page-size="expensePageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="expenseTotalInfo"
      >
      </el-pagination>
    </el-card>

    <el-dialog :title="dialogTitle" width="620px" :visible.sync="dialogVisible" @close="resetForm">
      <el-form label-width="105px" :model="payLogsForm">
        <el-form-item label="费用金额">
          <el-input-number
            v-model="payLogsForm.total"
            :min="0.01"
            :precision="2"
            :step="10"
            controls-position="right"
            style="width: 100%"
            placeholder="请输入缴费金额"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="所属社团">
          <el-select
            v-model="payLogsForm.teamId"
            filterable
            style="width: 100%"
            placeholder="请选择收费所属社团"
            @change="handleTeamChange"
          >
            <el-option v-for="item in teams" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="缴费成员">
          <el-select
            v-model="payLogsForm.userId"
            filterable
            style="width: 100%"
            :loading="memberLoading"
            placeholder="请选择缴费成员"
            no-data-text="请先选择有成员的社团"
          >
            <el-option
              v-for="item in memberOptions"
              :key="item.userId"
              :label="memberLabel(item)"
              :value="item.userId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="payLogsForm.userId" label="成员信息">
          <div class="member-card">
            <div class="member-card__name">{{ currentSelectedMember.userName || "-" }}</div>
            <div class="member-card__meta">
              <span>{{ currentSelectedMember.userPhone || "暂无电话" }}</span>
              <span>{{ currentSelectedMember.accountName || "暂无账号" }}</span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">{{ dialogMode === 'add' ? '确认新增' : '保存修改' }}</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="expenseDialogTitle" width="640px" :visible.sync="expenseDialogVisible" @close="resetExpenseForm">
      <el-form label-width="105px" :model="expenseForm">
        <el-form-item label="消费标题">
          <el-input v-model="expenseForm.title" placeholder="请输入消费标题"></el-input>
        </el-form-item>
        <el-form-item label="消费金额">
          <el-input-number
            v-model="expenseForm.total"
            :min="0.01"
            :precision="2"
            :step="10"
            controls-position="right"
            style="width: 100%"
            placeholder="请输入消费金额"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="所属社团">
          <el-select
            v-model="expenseForm.teamId"
            filterable
            style="width: 100%"
            placeholder="请选择所属社团"
          >
            <el-option v-for="item in teams" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用途说明">
          <el-input
            v-model="expenseForm.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入消费用途或备注"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="expenseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitExpenseForm">{{ expenseDialogMode === 'add' ? '确认新增' : '保存修改' }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addPayLogs,
  addPayExpenses,
  deletePayLogs,
  deletePayExpenses,
  getAllTeamList,
  getLoginUser,
  getManTeamList,
  getMemberOptions,
  getPagePayLogs,
  getPagePayExpenses,
  getPaySummary,
  updatePayLogs,
  updatePayExpenses,
} from "../../api";

export default {
  data() {
    return {
      teams: [],
      memberOptions: [],
      userType: "",
      pageInfos: [],
      pageIndex: 1,
      pageSize: 10,
      pageTotal: 0,
      totalInfo: 0,
      loading: true,
      summaryIncome: "0.00",
      summaryExpense: "0.00",
      summaryBalance: "0.00",
      expensePageInfos: [],
      expensePageIndex: 1,
      expensePageSize: 10,
      expensePageTotal: 0,
      expenseTotalInfo: 0,
      expenseLoading: true,
      memberLoading: false,
      dialogVisible: false,
      dialogMode: "add",
      expenseDialogVisible: false,
      expenseDialogMode: "add",
      qryForm: {
        token: this.$store.state.token,
        teamId: "",
        userName: "",
      },
      expenseQryForm: {
        token: this.$store.state.token,
        teamId: "",
        title: "",
      },
      payLogsForm: {
        id: "",
        createTime: "",
        total: null,
        teamId: "",
        userId: "",
      },
      expenseForm: {
        id: "",
        createTime: "",
        total: null,
        title: "",
        detail: "",
        teamId: "",
      },
    };
  },
  computed: {
    canManage() {
      return this.userType === 0 || this.userType === 1;
    },
    roleLabel() {
      if (this.userType === 0) return "系统管理员";
      if (this.userType === 1) return "社团管理员";
      if (this.userType === 2) return "学生";
      return "未登录";
    },
    currentPageAmount() {
      const total = this.pageInfos.reduce((sum, item) => sum + Number(item.total || 0), 0);
      return total.toFixed(2);
    },
    currentExpenseAmount() {
      const total = this.expensePageInfos.reduce((sum, item) => sum + Number(item.total || 0), 0);
      return total.toFixed(2);
    },
    incomeShare() {
      const income = Number(this.summaryIncome || 0);
      const expense = Number(this.summaryExpense || 0);
      const total = income + expense;
      if (total <= 0) return 50;
      return Math.min(100, Math.round((income / total) * 100));
    },
    expenseShare() {
      const income = Number(this.summaryIncome || 0);
      const expense = Number(this.summaryExpense || 0);
      const total = income + expense;
      if (total <= 0) return 50;
      return 100 - this.incomeShare;
    },
    dialogTitle() {
      return this.dialogMode === "add" ? "新增缴费记录" : "编辑缴费记录";
    },
    expenseDialogTitle() {
      return this.expenseDialogMode === "add" ? "新增消费明细" : "编辑消费明细";
    },
    currentSelectedMember() {
      return this.memberOptions.find((item) => item.userId === this.payLogsForm.userId) || {};
    },
  },
  methods: {
    formatMoney(value) {
      return Number(value || 0).toFixed(2);
    },
    memberLabel(item) {
      return `${item.userName}（${item.userPhone || "暂无电话"}）`;
    },
    getQueryTeamName() {
      if (!this.qryForm.teamId) return "";
      const found = this.teams.find((item) => item.id === this.qryForm.teamId);
      return found ? found.name : "";
    },
    async loadMemberOptions(teamId) {
      if (!teamId || !this.canManage) {
        this.memberOptions = [];
        return;
      }
      this.memberLoading = true;
      try {
        const resp = await getMemberOptions(this.$store.state.token, teamId);
        if (resp.code === 0) {
          this.memberOptions = resp.data || [];
        } else {
          this.memberOptions = [];
          this.$message.warning(resp.msg);
        }
      } finally {
        this.memberLoading = false;
      }
    },
    getPageInfo(pageIndex, pageSize) {
      getPagePayLogs(pageIndex, pageSize, this.qryForm.token, this.getQueryTeamName(), this.qryForm.userName).then((resp) => {
        this.pageInfos = resp.data.data || [];
        this.pageIndex = resp.data.pageIndex;
        this.pageSize = resp.data.pageSize;
        this.pageTotal = resp.data.pageTotal;
        this.totalInfo = resp.data.count;
        this.loading = false;
      });
      this.getSummary();
      this.getExpensePageInfo(this.expensePageIndex, this.expensePageSize);
    },
    getPageLikeInfo() {
      getPagePayLogs(1, this.pageSize, this.qryForm.token, this.getQueryTeamName(), this.qryForm.userName).then((resp) => {
        this.pageInfos = resp.data.data || [];
        this.pageIndex = resp.data.pageIndex;
        this.pageSize = resp.data.pageSize;
        this.totalInfo = resp.data.count;
        this.pageTotal = resp.data.pageTotal;
        this.loading = false;
      });
      this.expenseQryForm.teamId = this.qryForm.teamId;
      this.getSummary();
      this.getExpensePageLikeInfo();
    },
    handleSizeChange(pageSize) {
      this.getPageInfo(this.pageIndex, pageSize);
    },
    handleCurrentChange(pageIndex) {
      this.getPageInfo(pageIndex, this.pageSize);
    },
    resetForm() {
      this.payLogsForm = {
        id: "",
        createTime: "",
        total: null,
        teamId: this.userType === 1 && this.teams.length === 1 ? this.teams[0].id : "",
        userId: "",
      };
      this.memberOptions = [];
      this.memberLoading = false;
      this.dialogMode = "add";
    },
    async showAddWin() {
      this.resetForm();
      if (this.payLogsForm.teamId) {
        await this.loadMemberOptions(this.payLogsForm.teamId);
      }
      this.dialogVisible = true;
    },
    async showUpdWin(row) {
      this.dialogMode = "edit";
      this.payLogsForm = {
        id: row.id,
        createTime: row.createTime,
        total: Number(row.total || 0),
        teamId: row.teamId,
        userId: row.userId,
      };
      await this.loadMemberOptions(row.teamId);
      this.dialogVisible = true;
    },
    async handleTeamChange(teamId) {
      this.payLogsForm.userId = "";
      await this.loadMemberOptions(teamId);
    },
    submitForm() {
      if (!this.payLogsForm.total || this.payLogsForm.total <= 0) {
        this.$message.warning("请输入正确的缴费金额");
        return;
      }
      if (!this.payLogsForm.teamId) {
        this.$message.warning("请选择所属社团");
        return;
      }
      if (!this.payLogsForm.userId) {
        this.$message.warning("请选择缴费成员");
        return;
      }

      const payload = {
        ...this.payLogsForm,
        token: this.$store.state.token,
      };

      const request = this.dialogMode === "add" ? addPayLogs(payload) : updatePayLogs(payload);
      request.then((resp) => {
        this.$message({
          message: resp.msg,
          type: resp.code === 0 ? "success" : "warning",
        });
        if (resp.code === 0) {
          this.getPageInfo(1, this.pageSize);
          this.dialogVisible = false;
          this.resetForm();
        }
      });
    },
    delInfo(id) {
      this.$confirm("删除缴费记录后将无法恢复，是否继续？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        deletePayLogs(this.$store.state.token, id).then((resp) => {
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
  getSummary() {
    const teamId = this.expenseQryForm.teamId || this.qryForm.teamId;
    getPaySummary(this.$store.state.token, teamId).then((resp) => {
      if (resp.code === 0 && resp.data) {
        this.summaryIncome = Number(resp.data.income || 0).toFixed(2);
        this.summaryExpense = Number(resp.data.expense || 0).toFixed(2);
        this.summaryBalance = Number(resp.data.balance || 0).toFixed(2);
      }
    });
  },
  getExpensePageInfo(pageIndex, pageSize) {
    this.expenseLoading = true;
    getPagePayExpenses(pageIndex, pageSize, this.expenseQryForm.token, this.expenseQryForm.teamId, this.expenseQryForm.title).then((resp) => {
      this.expensePageInfos = resp.data.data || [];
      this.expensePageIndex = resp.data.pageIndex;
      this.expensePageSize = resp.data.pageSize;
      this.expensePageTotal = resp.data.pageTotal;
      this.expenseTotalInfo = resp.data.count;
      this.expenseLoading = false;
    });
  },
  getExpensePageLikeInfo() {
    this.expenseLoading = true;
    getPagePayExpenses(1, this.expensePageSize, this.expenseQryForm.token, this.expenseQryForm.teamId, this.expenseQryForm.title).then((resp) => {
      this.expensePageInfos = resp.data.data || [];
      this.expensePageIndex = resp.data.pageIndex;
      this.expensePageSize = resp.data.pageSize;
      this.expensePageTotal = resp.data.pageTotal;
      this.expenseTotalInfo = resp.data.count;
      this.expenseLoading = false;
      this.getSummary();
    });
  },
  handleExpenseSizeChange(pageSize) {
    this.getExpensePageInfo(this.expensePageIndex, pageSize);
  },
  handleExpenseCurrentChange(pageIndex) {
    this.getExpensePageInfo(pageIndex, this.expensePageSize);
  },
  resetExpenseForm() {
    this.expenseForm = {
      id: "",
      createTime: "",
      total: null,
      title: "",
      detail: "",
      teamId: this.userType === 1 && this.teams.length === 1 ? this.teams[0].id : "",
    };
    this.expenseDialogMode = "add";
  },
  showExpenseAddWin() {
    this.resetExpenseForm();
    this.expenseDialogVisible = true;
  },
  showExpenseUpdWin(row) {
    this.expenseDialogMode = "edit";
    this.expenseForm = {
      id: row.id,
      createTime: row.createTime,
      total: Number(row.total || 0),
      title: row.title,
      detail: row.detail,
      teamId: row.teamId,
    };
    this.expenseDialogVisible = true;
  },
  submitExpenseForm() {
    if (!this.expenseForm.title) {
      this.$message.warning("请输入消费标题");
      return;
    }
    if (!this.expenseForm.total || this.expenseForm.total <= 0) {
      this.$message.warning("请输入正确的消费金额");
      return;
    }
    if (!this.expenseForm.teamId) {
      this.$message.warning("请选择所属社团");
      return;
    }

    const payload = {
      ...this.expenseForm,
      token: this.$store.state.token,
    };
    const request = this.expenseDialogMode === "add" ? addPayExpenses(payload) : updatePayExpenses(payload);
    request.then((resp) => {
      this.$message({
        message: resp.msg,
        type: resp.code === 0 ? "success" : "warning",
      });
      if (resp.code === 0) {
        this.getExpensePageInfo(1, this.expensePageSize);
        this.getSummary();
        this.expenseDialogVisible = false;
        this.resetExpenseForm();
      }
    });
  },
  delExpense(id) {
    this.$confirm("删除消费明细后将无法恢复，是否继续？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }).then(() => {
      deletePayExpenses(this.$store.state.token, id).then((resp) => {
        this.$message({
          message: resp.msg,
          type: resp.code === 0 ? "success" : "warning",
        });
        if (resp.code === 0) {
          this.getExpensePageInfo(1, this.expensePageSize);
          this.getSummary();
        }
      });
    });
  },
  },
  mounted() {
    getLoginUser(this.$store.state.token).then(async (resp) => {
      this.userType = resp.data.type;

      if (resp.data.type === 1) {
        const teamResp = await getManTeamList(resp.data.id);
        this.teams = teamResp.data || [];
      } else {
        const teamResp = await getAllTeamList();
        this.teams = teamResp.data || [];
      }

      if (this.userType === 1 && this.teams.length === 1) {
        this.qryForm.teamId = this.teams[0].id;
        this.expenseQryForm.teamId = this.teams[0].id;
      }

      this.resetForm();
      this.getPageInfo(1, this.pageSize);
      this.getExpensePageInfo(1, this.expensePageSize);
      this.getSummary();
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

.insight-card {
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.04), rgba(14, 116, 144, 0.08));
}

.insight-head {
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 12px;
}

.insight-body {
  display: grid;
  gap: 12px;
}

.ratio-bar {
  height: 12px;
  border-radius: 999px;
  background: #e2e8f0;
  overflow: hidden;
  display: flex;
}

.ratio-income {
  background: linear-gradient(90deg, #0ea5e9, #22c55e);
}

.ratio-expense {
  background: linear-gradient(90deg, #f97316, #ef4444);
}

.ratio-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #64748b;
  font-size: 13px;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.list-title__main {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.list-title__sub {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}

.list-header .el-alert {
  flex: 1;
}

.money-text {
  color: #0f766e;
  font-weight: 700;
}

.expense-text {
  color: #ef4444;
}

.danger-text {
  color: #ef4444;
}

.member-card {
  padding: 12px 14px;
  border-radius: 16px;
  background: #f8fbff;
  border: 1px solid #e2e8f0;
}

.member-card__name {
  color: #0f172a;
  font-size: 15px;
  font-weight: 700;
}

.member-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 6px;
  color: #64748b;
}

.page-bar {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .list-header {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
