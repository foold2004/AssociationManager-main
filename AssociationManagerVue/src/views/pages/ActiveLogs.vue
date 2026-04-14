<template>
  <div class="module-page">
    <el-card shadow="never">
      <div slot="header" class="page-title">
        <i class="iconfont icon-r-find"></i>
        活动报名
      </div>
      <el-form :inline="true" :model="qryForm">
        <el-form-item>
          <el-input v-model="qryForm.teamName" placeholder="输入社团名称" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="qryForm.activeName" placeholder="输入活动名称" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item v-if="userType !== 2">
          <el-input v-model="qryForm.userName" placeholder="输入报名学生姓名" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="qryForm.status" clearable placeholder="选择审核状态">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getPageLikeInfo">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div slot="header" class="header-row">
        <span class="header-title">{{ headerTitle }}</span>
      </div>

      <el-table
        v-loading="loading"
        element-loading-text="正在加载活动报名数据"
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(124, 124, 124, 0.18)"
        :data="pageInfos"
        border
      >
        <el-table-column align="center" type="index" width="60"></el-table-column>
        <el-table-column align="center" prop="activeName" label="活动名称" min-width="170"></el-table-column>
        <el-table-column align="center" prop="teamName" label="所属社团" min-width="130"></el-table-column>
        <el-table-column v-if="userType !== 2" align="center" prop="userName" label="报名学生" min-width="110"></el-table-column>
        <el-table-column v-if="userType !== 2" align="center" prop="userPhone" label="联系电话" min-width="130"></el-table-column>
        <el-table-column align="center" prop="createTime" label="报名时间" min-width="160"></el-table-column>
        <el-table-column align="center" label="审核状态" min-width="120">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)" effect="plain">
              {{ statusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="reviewTime" label="审核时间" min-width="160">
          <template slot-scope="scope">
            {{ scope.row.reviewTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="审核备注" min-width="180">
          <template slot-scope="scope">
            {{ scope.row.reviewRemark || '-' }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" fixed="right" :width="userType === 2 ? 120 : 240">
          <template slot-scope="scope">
            <template v-if="canReview">
              <el-button
                v-if="scope.row.status !== 1"
                type="primary"
                size="mini"
                @click="submitReview(scope.row, 1)"
              >
                通过
              </el-button>
              <el-button
                v-if="scope.row.status !== 2"
                type="warning"
                size="mini"
                @click="submitReview(scope.row, 2)"
              >
                驳回
              </el-button>
              <el-button type="danger" size="mini" @click="delInfo(scope.row)">
                删除
              </el-button>
            </template>
            <template v-else>
              <el-button
                v-if="scope.row.status !== 2"
                type="danger"
                size="mini"
                @click="delInfo(scope.row)"
              >
                取消报名
              </el-button>
              <span v-else class="table-muted"></span>
            </template>
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

    <el-dialog title="审核报名" width="520px" :visible.sync="reviewVisible">
      <el-form label-width="90px" :model="reviewForm">
        <el-form-item label="活动名称">
          <div class="dialog-value">{{ reviewTarget.activeName || '-' }}</div>
        </el-form-item>
        <el-form-item label="报名学生">
          <div class="dialog-value">{{ reviewTarget.userName || '-' }}</div>
        </el-form-item>
        <el-form-item label="审核结果">
          <el-tag :type="statusTagType(reviewForm.status)" effect="plain">
            {{ statusLabel(reviewForm.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input
            v-model="reviewForm.reviewRemark"
            type="textarea"
            :rows="4"
            placeholder=""
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReview">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { deleteActiveLogs, getLoginUser, getPageActiveLogs, updateActiveLogs } from "../../api";

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
      reviewVisible: false,
      reviewTarget: {},
      reviewForm: {
        id: "",
        status: 1,
        reviewRemark: "",
        token: "",
      },
      qryForm: {
        token: this.$store.state.token,
        teamName: "",
        activeName: "",
        userName: "",
        status: null,
      },
      statusOptions: [
        { value: 0, label: "待审核" },
        { value: 1, label: "已通过" },
        { value: 2, label: "已驳回" },
      ],
    };
  },
  computed: {
    canReview() {
      return this.userType === 0 || this.userType === 1;
    },
    headerTitle() {
      if (this.userType === 0) return "全站活动报名审核";
      if (this.userType === 1) return "本社团活动报名审核";
      return "我的活动报名记录";
    },
  },
  methods: {
    statusLabel(status) {
      if (status === 1) return "已通过";
      if (status === 2) return "已驳回";
      return "待审核";
    },
    statusTagType(status) {
      if (status === 1) return "success";
      if (status === 2) return "danger";
      return "warning";
    },
    getPageInfo(pageIndex, pageSize) {
      this.loading = true;
      getPageActiveLogs(
        pageIndex,
        pageSize,
        this.qryForm.token,
        this.qryForm.teamName,
        this.qryForm.activeName,
        this.qryForm.userName,
        this.qryForm.status
      ).then((resp) => {
        this.pageInfos = (resp.data && resp.data.data) || [];
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
    submitReview(row, status) {
      this.reviewTarget = row;
      this.reviewForm = {
        id: row.id,
        status,
        reviewRemark: status === 1 ? "审核通过" : "",
        token: this.$store.state.token,
      };
      this.reviewVisible = true;
    },
    confirmReview() {
      updateActiveLogs(this.reviewForm).then((resp) => {
        this.$message({
          message: resp.msg,
          type: resp.code === 0 ? "success" : "warning",
        });
        if (resp.code === 0) {
          this.reviewVisible = false;
          this.getPageInfo(this.pageIndex, this.pageSize);
        }
      });
    },
    delInfo(row) {
      const message = this.canReview ? "确定删除这条活动报名记录吗？" : "确定取消这次活动报名吗？";
      this.$confirm(message, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        deleteActiveLogs(this.$store.state.token, row.id).then((resp) => {
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
    getLoginUser(this.$store.state.token).then((resp) => {
      this.userType = resp.data.type;
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

.header-title {
  font-size: 18px;
  font-weight: 700;
  color: #203152;
}

.header-tip {
  color: #7b879e;
  font-size: 14px;
}

.dialog-value {
  min-height: 40px;
  padding: 10px 12px;
  background: #f5f7fb;
  border-radius: 10px;
  color: #203152;
}

.table-muted {
  color: #a0a9ba;
}
</style>
