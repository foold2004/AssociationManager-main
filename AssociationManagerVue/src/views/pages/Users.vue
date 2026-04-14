<template>
  <div class="module-page">
    <el-card shadow="never">
      <div slot="header" class="page-title">
        <i class="iconfont icon-r-find"></i>
        用户管理
      </div>
      <el-form :inline="true" :model="qryForm" class="filter-form">
        <el-form-item>
          <el-input v-model="qryForm.userName" placeholder="输入登录账号" clearable autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="qryForm.name" placeholder="输入姓名" clearable autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="qryForm.phone" placeholder="输入手机号" clearable autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="qryForm.type" clearable placeholder="筛选身份类型">
            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
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
          <div class="summary-label">用户总览</div>
          <div class="summary-value">{{ totalInfo }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :md="8">
        <div class="summary-card">
          <div class="summary-label">当前页角色分布</div>
          <div class="summary-value">{{ roleSummaryText }}</div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <div slot="header" class="list-header">
        <el-button v-if="userType === 0" type="primary" @click="showAddWin">新增用户</el-button>
      </div>

      <el-table
        v-loading="loading"
        element-loading-text="正在加载用户数据"
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(124, 124, 124, 0.18)"
        :data="pageInfos"
        border
      >
        <el-table-column align="center" type="index" width="60"></el-table-column>
        <el-table-column align="center" prop="userName" label="登录账号" min-width="140"></el-table-column>
        <el-table-column align="center" prop="name" label="姓名" min-width="120"></el-table-column>
        <el-table-column align="center" prop="gender" label="性别" width="90"></el-table-column>
        <el-table-column align="center" prop="age" label="年龄" width="90"></el-table-column>
        <el-table-column align="center" prop="phone" label="联系电话" min-width="140"></el-table-column>
        <el-table-column align="center" prop="address" label="联系地址" min-width="220"></el-table-column>
        <el-table-column align="center" label="状态" width="110">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? "启用" : "停用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="身份类型" min-width="130">
          <template slot-scope="scope">
            <el-tag :type="roleTagType(scope.row.type)">{{ roleLabel(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="createTime" label="创建时间" min-width="170"></el-table-column>
        <el-table-column v-if="userType === 0" align="center" label="操作" fixed="right" width="180">
          <template slot-scope="scope">
            <el-button type="text" @click="showUpdWin(scope.row)">编辑</el-button>
            <el-button type="text" class="danger-text" @click="delInfo(scope.row.id)">删除</el-button>
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

    <el-dialog :title="dialogMode === 'add' ? '新增用户' : '编辑用户'" width="640px" :visible.sync="dialogVisible" @close="initForm">
      <el-form label-width="100px" :model="usersForm">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="身份类型">
              <el-select v-model="usersForm.type" style="width: 100%" placeholder="请选择身份类型">
                <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录账号">
              <el-input v-model="usersForm.userName" placeholder="请输入登录账号" autocomplete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="登录密码">
              <el-input v-model="usersForm.passWord" type="password" placeholder="请输入登录密码" autocomplete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="usersForm.name" placeholder="请输入姓名" autocomplete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="usersForm.gender">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input-number v-model="usersForm.age" :min="16" :max="60" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="usersForm.phone" placeholder="请输入联系电话" autocomplete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="usersForm.status" style="width: 100%">
                <el-option label="启用" :value="1"></el-option>
                <el-option label="停用" :value="0"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="联系地址">
          <el-input v-model="usersForm.address" type="textarea" :rows="3" placeholder="请输入联系地址"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">{{ dialogMode === 'add' ? '确认新增' : '保存修改' }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addUsers, deleteUsers, getLoginUser, getPageUsers, updateUsers } from "../../api";

export default {
  data() {
    return {
      userType: "",
      pageInfos: [],
      pageIndex: 1,
      pageSize: 10,
      pageTotal: 0,
      totalInfo: 0,
      loading: true,
      dialogVisible: false,
      dialogMode: "add",
      qryForm: {
        userName: "",
        name: "",
        phone: "",
        type: "",
      },
      usersForm: {
        id: "",
        userName: "",
        passWord: "",
        type: 2,
        name: "",
        gender: "男",
        age: 20,
        phone: "",
        address: "",
        status: 1,
      },
    };
  },
  computed: {
    roleOptions() {
      return [
        { label: "系统管理员", value: 0 },
        { label: "社团管理员", value: 1 },
        { label: "学生", value: 2 },
      ];
    },
    roleSummaryText() {
      const counter = { 0: 0, 1: 0, 2: 0 };
      this.pageInfos.forEach((item) => {
        if (counter[item.type] !== undefined) {
          counter[item.type] += 1;
        }
      });
      return `系管 ${counter[0]} / 社团 ${counter[1]} / 学生 ${counter[2]}`;
    },
  },
  methods: {
    roleLabel(type) {
      const found = this.roleOptions.find((item) => item.value === type);
      return found ? found.label : "未知身份";
    },
    roleTagType(type) {
      if (type === 0) return "danger";
      if (type === 1) return "warning";
      return "success";
    },
    getPageInfo(pageIndex, pageSize) {
      getPageUsers(pageIndex, pageSize, this.qryForm.userName, this.qryForm.name, this.qryForm.phone, this.qryForm.type).then((resp) => {
        this.pageInfos = resp.data.data || [];
        this.pageIndex = resp.data.pageIndex;
        this.pageSize = resp.data.pageSize;
        this.pageTotal = resp.data.pageTotal;
        this.totalInfo = resp.data.count;
        this.loading = false;
      });
    },
    getPageLikeInfo() {
      getPageUsers(1, this.pageSize, this.qryForm.userName, this.qryForm.name, this.qryForm.phone, this.qryForm.type).then((resp) => {
        this.pageInfos = resp.data.data || [];
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
      this.usersForm = {
        id: "",
        userName: "",
        passWord: "",
        type: 2,
        name: "",
        gender: "男",
        age: 20,
        phone: "",
        address: "",
        status: 1,
      };
      this.dialogMode = "add";
    },
    showAddWin() {
      this.initForm();
      this.dialogVisible = true;
    },
    showUpdWin(row) {
      this.dialogMode = "edit";
      this.usersForm = {
        id: row.id,
        userName: row.userName,
        passWord: row.passWord,
        type: row.type,
        name: row.name,
        gender: row.gender,
        age: row.age,
        phone: row.phone,
        address: row.address,
        status: row.status,
      };
      this.dialogVisible = true;
    },
    submitForm() {
      if (!this.usersForm.userName || !this.usersForm.passWord || !this.usersForm.name) {
        this.$message.warning("请完整填写账号、密码和姓名");
        return;
      }
      const request = this.dialogMode === "add" ? addUsers(this.usersForm) : updateUsers(this.usersForm);
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
    delInfo(id) {
      this.$confirm("删除用户前请确认该账号没有关联社团成员关系，是否继续？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        deleteUsers(id).then((resp) => {
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
    this.getPageInfo(1, this.pageSize);
    getLoginUser(this.$store.state.token).then((resp) => {
      this.userType = resp.data.type;
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
