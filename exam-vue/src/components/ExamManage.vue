<template>
  <el-container>
    <el-header height="220">
      <el-select clearable v-model="queryInfo.examType"  placeholder="请选择考试类型" @change="typeChange">
        <el-option
          v-for="item in examType"
          :key="item.type"
          :label="item.info"
          :value="item.type">
        </el-option>
      </el-select>

      <el-date-picker v-model="queryInfo.startTime"
                      placeholder="考试开始时间" style="margin-left: 5px"
                      format = "yyyy-MM-dd HH:mm:SS"
                      value-format="yyyy-MM-dd HH:mm:SS"
                      type="datetime"
                      @change="getExamInfo">
      </el-date-picker>

      <el-date-picker v-model="queryInfo.endTime" placeholder="考试结束时间"
                      style="margin-left: 5px"
                      format = "yyyy-MM-dd HH:mm:SS"
                      value-format="yyyy-MM-dd HH:mm:SS"
                      type="datetime"
                      @change="getExamInfo">
      </el-date-picker>

      <el-input v-model="queryInfo.examName" placeholder="考试名称" prefix-icon="el-icon-search"
                style="margin-left: 5px;width: 220px"
                @blur="getExamInfo"></el-input>


      <el-select clearable v-model="queryInfo.majorId"  placeholder="请选择专业" @change="getExamInfo" v-if="Show" >
        <el-option :value="0" label="全部"></el-option>
        <el-option v-for="item in allBankMajor" :key="item.id"
                   :label="item.name" :value="item.id"></el-option>
      </el-select>
<!--

      <el-select v-model="queryInfo.className" placeholder="班级" prefix-icon="el-icon-search" @change="getExamInfo" >
        <el-option :value="1" label="1班"></el-option>
        <el-option :value="2" label="2班"></el-option>
        <el-option :value="3" label="3班"></el-option>
        <el-option :value="4" label="4班"></el-option>
        <el-option :value="5" label="5班"></el-option>
        <el-option :value="6" label="6班"></el-option>
        <el-option :value="7" label="7班"></el-option>
        <el-option :value="8" label="8班"></el-option>
        <el-option :value="9" label="9班"></el-option>
        <el-option :value="10" label="10班"></el-option>
      </el-select>
-->



      <br>
      <el-button icon="el-icon-plus" style="margin-top: 10px" type="primary"
                 @click="$router.push('/addExam')">添加
      </el-button>
    </el-header>

    <el-main>
      <!--操作的下拉框-->
      <el-select v-if="selectionTable.length !== 0" v-model="operation"
                 :placeholder="'已选择' + selectionTable.length + '项'" clearable
                 style="margin-bottom: 25px;" @change="operationChange">
        <el-option v-for="(item,index) in operationInfo" :key="index" :value="item.desc">
          <span style="float: left">{{ item.label }}</span>
          <span style="float: right; color: #8492a6; font-size: 13px">{{ item.desc }}</span>
        </el-option>
      </el-select>

      <el-table
        ref="questionTable"
        v-loading="loading"
        :border="true"
        :data="examInfo"
        highlight-current-row
        style="width: 100%;"
        tooltip-effect="dark" @selection-change="handleTableSectionChange">

        <el-table-column align="center"
                         type="selection"
                         width="55">
        </el-table-column>

        <el-table-column align="center" label="考试名称">
          <template slot-scope="scope">
            <span style="cursor:pointer;color: rgb(24,144,255)" @click="$router.push('/updateExam/'+ scope.row.examId)">{{
                scope.row.examName
              }}</span>
          </template>
        </el-table-column>

        <el-table-column align="center" label="考试类型">
          <template slot-scope="scope">
            {{ scope.row.type === 1 ? '完全公开' : '需要密码' }}
          </template>
        </el-table-column>

        <el-table-column align="center" label="考试时间">
          <template slot-scope="scope">
            {{
              scope.row.startTime !== 'null' && scope.row.endTime !== 'null' ?
                scope.row.startTime + ' ~' + scope.row.endTime : '不限时'
            }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="专业" prop="majorName"></el-table-column>
        <el-table-column align="center" label="班级" prop="className"></el-table-column>

        <el-table-column align="center" label="考试总分" prop="totalScore"></el-table-column>

        <el-table-column align="center" label="及格分数" prop="passScore"></el-table-column>

        <el-table-column align="center" label="创建人" prop="createMan"></el-table-column>


        <el-table-column align="center" label="状态">
          <template slot-scope="scope">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
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
  name: 'ExamManage',
  data() {
    return {
      Show: false,
      queryInfo: {

        'examType': null,
        'startTime': null,
        'endTime': null,
        'examName': null,
        'className': null,
        'majorId': '',
        'pageNo': 0,
        'pageSize': 10
      },
      //表格是否在加载
      loading: true,
      //考试类型信息
      examType: [
        {
          info: '公开考试',
          type: 1
        },
        {
          info: '需要密码',
          type: 2
        }
      ],
      //被选择的考试的行
      selectionTable: [],
      allBankMajor: [],
      //表格多行选中的操作信息
      operation: '',
      //支持操作的信息
      operationInfo: [
        {
          'label': '启用',
          'desc': 'on'
        },
        {
          'label': '禁用',
          'desc': 'off'
        },
        {
          'label': '删除',
          'desc': 'delete'
        }
      ],
      //考试信息
      examInfo: [],
      //查询到的考试总数
      total: 0,

    }
  },
  created() {
    axios.get('/common/checkToken').then((resp) => {
      if (resp.data.code === 200 && resp.data.data.roleId === '3') {//当前用户携带的token信息正确并且是管理员

        this.Show = true;
      } else {

      }
    })

    this.getMajorBankInfo()
    this.getExamInfo()
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
    //考试类型搜索
    typeChange(val) {
      this.queryInfo.examType = val
      this.getExamInfo()
    },
    //操作多行表格信息
    operationChange(val) {
      let examIds = []
      this.selectionTable.forEach(item => {
        examIds.push(item.examId)
      })
      if (val === 'on') {
        this.$http.get(this.API.operationExam + '/1', {params: {'ids': examIds.join(',')}}).then((resp) => {
          if (resp.data.code === 200) {
            this.getExamInfo()
            this.$notify({
              'type': 'success',
              'title': 'Tips',
              'message': '操作成功',
              'duration': 2000
            })
          }
        })
      } else if (val === 'off') {
        this.$http.get(this.API.operationExam + '/2', {params: {'ids': examIds.join(',')}}).then((resp) => {
          if (resp.data.code === 200) {
            this.getExamInfo()
            this.$notify({
              'type': 'success',
              'title': 'Tips',
              'message': '操作成功',
              'duration': 2000
            })
          }
        })
      } else if (val === 'delete') {
        this.$http.get(this.API.operationExam + '/3', {params: {'ids': examIds.join(',')}}).then((resp) => {
          if (resp.data.code === 200) {
            this.getExamInfo()
            this.$notify({
              'type': 'success',
              'title': 'Tips',
              'message': '操作成功',
              'duration': 2000
            })
          }
        })
      }
    },
    //查询考试信息
    getExamInfo() {
      this.$http.post(this.API.getExamInfo, this.queryInfo).then((resp) => {
        if (resp.data.code === 200) {
          resp.data.data.forEach(item => {
            item.startTime = String(item.startTime)
            item.endTime = String(item.endTime)
          })
          this.examInfo = resp.data.data
          this.getExamTotal()
          this.loading = false
        }
      })
    },
    //查询考试信息
    getExamTotal() {
      let data = JSON.parse(JSON.stringify(this.queryInfo))
      data.pageNo = 1
      data.pageSize = 9999
      this.$http.post(this.API.getExamInfo, data).then((resp) => {
        if (resp.data.code === 200) {
          this.total = resp.data.data.length
        }
      })
    },
    //处理表格被选中
    handleTableSectionChange(val) {
      this.selectionTable = val
    },
    //分页页面大小改变
    handleSizeChange(val) {
      this.queryInfo.pageSize = val
      this.getExamInfo()
    },
    //分页插件的页数
    handleCurrentChange(val) {
      this.queryInfo.pageNo = val
      this.getExamInfo()
    },
  }
}
</script>

<style lang="scss" scoped>
.el-container {
  width: 100%;
  height: 100%;
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
</style>
