<template>
  <el-container>
    <el-header height="220">
      <el-select v-model="queryInfo.examType" clearable placeholder="请选择考试类型" @change="typeChange">
        <el-option
          v-for="item in examType"
          :key="item.type"
          :label="item.info"
          :value="item.type">
        </el-option>
      </el-select>

      <el-input v-model="queryInfo.examName" placeholder="考试名称" prefix-icon="el-icon-search"
                style="margin-left: 5px;width: 220px"
                @blur="getExamInfo2"></el-input>
    </el-header>

    <el-main>

      <el-table
        ref="questionTable"
        v-loading="loading"
        :border="true"
        :data="examInfo"
        highlight-current-row
        style="width: 100%;"
        tooltip-effect="dark">

        <el-table-column align="center" label="考试名称">
          <template slot-scope="scope">
            {{ scope.row.examName }}
          </template>
        </el-table-column>

        <el-table-column align="center" label="考试类型">
          <template slot-scope="scope">
            {{ scope.row.type === 1 ? '完全公开' : '需要密码' }}
          </template>
        </el-table-column>


        <el-table-column align="center" label="所属专业">
          <template slot-scope="scope">
            {{ scope.row.majorName }}
          </template>
        </el-table-column>

        <el-table-column align="center" label="班级">
          <template slot-scope="scope">
            {{ scope.row.className }}
          </template>
        </el-table-column>


        <el-table-column align="center" label="考试时间" width="300px">
          <template slot-scope="scope">
            {{
              scope.row.startTime !== 'null' && scope.row.endTime !== 'null' ?
                scope.row.startTime + ' ~' + scope.row.endTime : '不限时'
            }}
          </template>
        </el-table-column>

        <el-table-column align="center" label="考试时长">
          <template slot-scope="scope">
            {{ scope.row.duration + '分钟' }}
          </template>
        </el-table-column>

        <el-table-column align="center" label="考试总分" prop="totalScore"></el-table-column>

        <el-table-column align="center" label="及格分数" prop="passScore"></el-table-column>

        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
<!--            <el-button :disabled="!checkExam(scope.row)"-->
<!--                       :icon="checkExam(scope.row) ? 'el-icon-caret-right' : 'el-icon-close'"-->
<!--                       :type="checkExam(scope.row) ? 'primary' : 'info'"-->
<!--                       size="small"-->
<!--                       @click="toStartExam(scope.row)">-->
<!--              {{ checkExam(scope.row) ? '去考试' : '未开始' }}-->
<!--          1未开始 2已结束 3已考过  4进入考试  </el-button>-->
<!--            这里应该是已经考过的-->
            <template v-if="scope.row.flag === true">
              <el-button type="warning" disabled>已考过</el-button>
            </template>
            <template v-else>
              <el-button type="danger" v-if="checkExam(scope.row)==1" disabled>未开始</el-button>
              <el-button type="danger" v-if="checkExam(scope.row)==2" disabled>已结束</el-button>
              <el-button type="warning" v-if="checkExam(scope.row)==3" disabled>已考过</el-button>
              <el-button type="primary" :icon="'el-icon-caret-right'" @click="toStartExam(scope.row)" v-if="checkExam(scope.row)==4" >进入考试 </el-button>
            </template>
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
    <el-dialog
      :visible.sync="startExamDialog"
      center title="考试提示"
      width="50%">

      <el-card style="margin-top: 25px">
        <span>考试名称：</span>{{ currentSelectedExam.examName }}
        <br>
        <span>考试描述：</span>{{ currentSelectedExam.examDesc }}
        <br>
        <span>考试时长：</span>{{ currentSelectedExam.duration + '分钟' }}
        <br>
        <span>试卷总分：</span>{{ currentSelectedExam.totalScore }}分
        <br>
        <span>及格分数：</span>{{ currentSelectedExam.passScore }}分
        <br>
        <span>开放类型：</span> {{ currentSelectedExam.type === 2 ? '需要密码' : '完全公开' }}
        <br>
      </el-card>


      <span slot="footer" class="dialog-footer">
    <el-button @click="startExamDialog = false">返 回</el-button>
    <el-button type="primary" @click="$router.push('/exam/'+ currentSelectedExam.examId)">开始考试</el-button>
  </span>
    </el-dialog>
  </el-container>
</template>

<script>
export default {
  name: 'ExamOnline',
  data() {
    return {
      queryInfo: {

        'examType': '',
        'startTime': null,
        'endTime': null,
        'examName': '',
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
      //考试信息
      examInfo: [],
      //查询到的考试总数
      total: 0,
      //开始考试的提示框
      startExamDialog: false,
      //当前选中的考试的信息
      currentSelectedExam: {},
    }
  },
  created() {
    this.getExamInfo2()
  },
  methods: {
    //考试类型搜索
    typeChange(val) {
      this.queryInfo.examType = val
      this.getExamInfo2()
    },
    //查询考试信息
    getExamInfo2() {
      this.$http.post(this.API.getExamInfo2, this.queryInfo).then((resp) => {
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
      this.$http.post(this.API.getExamInfo2, data).then((resp) => {
        if (resp.data.code === 200) {
          this.total = resp.data.data.length
        }
      })
    },
    //分页页面大小改变
    handleSizeChange(val) {
      this.queryInfo.pageSize = val
      this.getExamInfo2()
    },
    //分页插件的页数
    handleCurrentChange(val) {
      this.queryInfo.pageNo = val
      this.getExamInfo2()
    },
    //去考试准备页面
    toStartExam(row) {
      if (row.type === 2) {
        this.$prompt('请提供考试密码', 'Tips', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(({value}) => {
          if (value === row.password) {
            this.startExamDialog = true
            this.currentSelectedExam = row
          } else {
            this.$message.error('密码错误o(╥﹏╥)o')
          }
        }).catch(() => {
        })
      } else {
        this.startExamDialog = true
        this.currentSelectedExam = row
      }
    },
    checkExam(row) {
      //1未开始 2已结束 3已考过  4进入考试
      if (row.status == 2) return 1; // states=2也表示未开始
      let nowDate = new Date();
      //进入考试
      //开始空 结束空
      if(row.startTime==''&&row.endTime=='')
      {
        return 4;
      }
      //开始不空 结束空
      if(row.startTime&&!row.endTime&&new Date(row.startTime)>nowDate)
      {
        return 4;
      }
      //开始空 结束不空
      if(!row.startTime&&row.endTime&&new Date(row.endTime)>nowDate)
      {
        return 4;
      }
      //开始不空 结束不空
      if(row.startTime&&row.endTime)
      {
        if(new Date(row.startTime)>nowDate)
        {
          return 1;
        }
        if(new Date(row.startTime)<nowDate&&new Date(row.endTime)>nowDate)
        {
          return 4;
        }
        if (new Date(row.endTime)<nowDate)
        {
          return 2;
        }
      };
      return 0;
    },
    async  checkExamRecord(row) {
      let res=false;
      //return row.examId%2==0;//随机假设 测试按钮合法
      await this.$http.get(this.API.checkExam + '/' + row.examId).then((resp) => {
              console.log(resp.data);
              res = resp.data.code == 210;//这里是已考过
             //tmpcode = resp.data.code
        console.log(res)
      });
      return res;
      //这里假设成立
      // return (row) => {
      //   let tmpcode = 0
      //   this.$http.get(this.API.checkExam + '/' + row.examId).then((resp) => {
      //     tmpcode = resp.data.code
      //   })
      //   return tmpcode === 200;
      // }
    },
  },
  computed: {
      //检查考试的合法性
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

span {
  font-weight: 500;
  display: inline-block;
  font-size: 16px;
  padding: 10px !important;
}
</style>
