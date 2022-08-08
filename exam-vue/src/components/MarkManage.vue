<template>
  <el-container>
    <el-header>
      <!--操作的下拉框-->
      <el-select v-model="queryInfo.examId" clearable placeholder="请选择考试"
                 style="margin-bottom: 25px;" @change="operation">
        <el-option v-for="(item,index) in allExamInfo" :key="index" :label="item.examName"
                   :value="parseInt(item.examId)">
          <span style="float: left">{{ item.examName }}</span>
        </el-option>
      </el-select>

<!--      v-if="majorShow"-->

        <el-select v-model="queryInfo.majorId"  placeholder="请选择专业" clearable @change="operation" v-if="Show">
          <el-option v-for="item in allBankMajor" :key="item.id"
                     :label="item.name" :value="item.id"></el-option>
        </el-select>




        <el-select v-model="queryInfo.className" placeholder="请选择班级" clearable @change="operation">
          <el-option label="1班" value="1"></el-option>
          <el-option label="2班" value="2"></el-option>
          <el-option label="3班" value="3"></el-option>
          <el-option label="4班" value="4"></el-option>
          <el-option label="5班" value="5"></el-option>
          <el-option label="6班" value="6"></el-option>
          <el-option label="7班" value="7"></el-option>
          <el-option label="8班" value="8"></el-option>
          <el-option label="9班" value="9"></el-option>

        </el-select>


    </el-header>

    <el-main>
      <el-table
        ref="questionTable"
        v-loading="loading"
        :border="true"
        :data="examRecords"
        highlight-current-row
        style="width: 100%;"
        tooltip-effect="dark">

        <el-table-column align="center" label="考试名称" prop="examName"></el-table-column>

        <el-table-column align="center" label="参考时间" prop="examTime"></el-table-column>

        <el-table-column align="center" label="参考人" prop="trueName"></el-table-column>

        <el-table-column align="center" label="专业" prop="majorName"></el-table-column>

        <el-table-column align="center" label="班级" prop="className"></el-table-column>

        <el-table-column align="center" label="逻辑题得分" prop="logicScore"></el-table-column>

        <el-table-column align="center" label="是否批阅">
          <template slot-scope="scope">
            <span>{{ scope.row.totalScore === null ? '未批阅' : '已批阅' }}</span>
          </template>
        </el-table-column>

        <el-table-column align="center" label="总分">
          <template slot-scope="scope">
            <span>{{ scope.row.totalScore === null ? 0 : scope.row.totalScore }}</span>
          </template>
        </el-table-column>

        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
            <el-button :disabled="scope.row.totalScore !== null"
                       :type="scope.row.totalScore === null ? 'primary' : 'warning'" icon="el-icon-view"
                       size="small"
                       @click="$router.push('/markExam/' + scope.row.recordId)">
              {{ scope.row.totalScore === null ? '批阅' : '已批阅' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!--分页-->
      <el-pagination :current-page="queryInfo.pageNo"
                     :page-size="queryInfo.pageSize"
                     :page-sizes="[10, 20, 30, 50]"
                     :total="total"
                     layout="total, sizes, prev, pager, next, jumper"
                     style="margin-top: 25px"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange">
      </el-pagination>
    </el-main>
  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'MarkManage',
  data() {
    return {
      queryInfo: {
        pageNo: 1,
        pageSize: 10
      },
      //考试记录
      examRecords: [],
      //表格数据加载
      loading: true,
      //所有考试信息
      allExamInfo: [],
      //总数
      total: 0,
      Show: true
    }
  },
  created() {
    axios.get('/common/checkToken').then((resp) => {
      if (resp.data.code === 200 && resp.data.data.roleId === '2') {//当前用户携带的token信息正确并且是管理员
        this.Show = false;
      } else {

      }
    })
    this.getMajorBankInfo()
    this.getExamRecords()
    this.getAllExamInfo()
  },
  methods: {
    //获取所有的专业
    getMajorBankInfo() {
      this.$http.get(this.API.getMajorBank).then((resp) => {
        if (resp.data.code === 200) {
          this.allBankMajor = resp.data.data
        } else {
          this.$notify({
            title: 'Tips',
            message: '获取所有的专业',
            type: 'error',
            duration: 2000
          })
        }
      })
    },
    async getExamRecords() {
      await this.$http.get(this.API.getExamRecord, {params: this.queryInfo}).then((resp) => {
        if (resp.data.code === 200) {
          this.getAllExamInfo()
          resp.data.data.examRecords.forEach(item => {
            this.$http.get(this.API.getUserById + '/' + item.userId).then((r) => {
              item.trueName = r.data.data.trueName
            })
          })
          this.examRecords = resp.data.data.examRecords
          this.total = resp.data.data.total
          this.loading = false
        }
      })
    },
    getAllExamInfo() {
      this.$http.get(this.API.allExamInfo).then((resp) => {
        if (resp.data.code === 200) {
          this.allExamInfo = resp.data.data
          this.setExamName()
        }
      })
    },
    operation(v) {
      if (v === '') this.queryInfo.examId = null
      this.getExamRecords()
    },

    setExamName() {
      this.examRecords.forEach(item => {
        this.allExamInfo.forEach(i2 => {
          if (item.examId === i2.examId) {
            this.$set(item, 'examName', i2.examName)
          }
        })
      })
    },
    //分页页面大小改变
    handleSizeChange(val) {
      this.queryInfo.pageSize = val
      this.getExamRecords()
    },
    //分页插件的页数
    handleCurrentChange(val) {
      this.queryInfo.pageNo = val
      this.getExamRecords()
    },
  }
}
</script>

<style lang="scss" scoped>
.el-container {
  width: 100%;
  height: 100%;
}

.el-input {
  width: 200px;
}

.el-container {
  animation: leftMoveIn .7s ease-in;
}

@keyframes leftMoveIn {
  0% {
    transform: translateX(-100%);
    opacity: 0;
  }
  100% {
    transform: translateX(0%);
    opacity: 1;
  }
}

.role {
  color: #606266;
}

/deep/ .el-table thead {
  color: rgb(85, 85, 85) !important;
}

/*表格的头部样式*/
/deep/ .has-gutter tr th {
  background: rgb(242, 243, 244);
  color: rgb(85, 85, 85);
  font-weight: bold;
  line-height: 32px;
}

.el-table {
  box-shadow: 0 0 1px 1px gainsboro;
}
</style>
