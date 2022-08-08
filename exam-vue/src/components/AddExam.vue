<template>
  <el-container>
    <el-header height="220">
      <el-steps :active="curStep" simple>
        <el-step icon="el-icon-edit" title="组卷配置"></el-step>
        <el-step icon="el-icon-lock" title="考试权限"></el-step>
        <el-step icon="el-icon-setting" title="补充配置"></el-step>
      </el-steps>

      <el-button v-show="curStep !== 1" style="margin-top: 10px" @click="curStep--">上一步</el-button>

      <el-button v-show="curStep !== 3" style="float:right;margin-top: 10px" type="primary" @click="curStep++">下一步
      </el-button>
      <el-button v-show="curStep === 3" style="float:right;margin-top: 10px" type="primary" @click="addExam">提交
      </el-button>
    </el-header>

    <el-main>
      <!--设置试题信息-->
      <el-card v-show="curStep === 1">

        <el-radio-group v-model="makeModel"
                        size="medium"
                        @change="makeModelChange">
          <el-radio :label="1" border>题库组卷</el-radio>
          <el-radio :label="2" border>自由组卷</el-radio>
        </el-radio-group>

        <span v-show="makeModel === 2" style="float: right;color: red;font-weight: bold">
          {{ '试卷总分：' + sumTotalScore }}</span>

        <!-- 题库组卷内容-->
        <div v-show="makeModel === 1" style="margin-top: 25px">
          <el-button icon="el-icon-plus" size="mini" @click="addBank">添加题库</el-button>

          <!--存放题目的信息-->
          <el-table :data="addExamQuestion" border style="margin-top: 10px">

            <el-table-column align="center" label="题库" width="155">
              <template slot-scope="scope">
                <el-select v-model="scope.row.bankName" clearable placeholder="请选择题库"
                           style="margin-left: 5px">
                  <el-option
                    v-for="item in allBank"
                    :key="item.questionBank.bankId"
                    :label="item.questionBank.bankName"
                    :value="item.questionBank.bankName">
                  </el-option>
                </el-select>
              </template>
            </el-table-column>

            <el-table-column align="center" label="单选题分数">
              <template slot-scope="scope">
                <el-input v-model="scope.row.singleScore" style="margin-left: 5px"></el-input>
              </template>
            </el-table-column>

            <el-table-column align="center" label="多选题分数">
              <template slot-scope="scope">
                <el-input v-model="scope.row.multipleScore" style="margin-left: 5px"></el-input>
              </template>
            </el-table-column>

            <el-table-column align="center" label="判断题分数">
              <template slot-scope="scope">
                <el-input v-model="scope.row.judgeScore" style="margin-left: 5px"></el-input>
              </template>
            </el-table-column>

            <el-table-column align="center" label="简答题分数">
              <template slot-scope="scope">
                <el-input v-model="scope.row.shortScore" style="margin-left: 5px"></el-input>
              </template>
            </el-table-column>

            <el-table-column align="center" label="操作" width="80">
              <template slot-scope="scope">
                <el-button circle icon="el-icon-delete" type="danger" @click="delBank(scope.row.bankId)"></el-button>
              </template>
            </el-table-column>

          </el-table>
        </div>

        <!-- 自由组卷内容-->
        <div v-show="makeModel === 2">
          <el-card>
            <h1>题目列表</h1>
            <el-button icon="el-icon-plus" size="small" type="primary" @click="showAddDialog">添加试题</el-button>

            <el-table :data="addExamQuestion2" border style="margin-top: 10px">

              <el-table-column
                label="序号"
                type="index"
                width="50">
              </el-table-column>

              <el-table-column align="center" label="题目内容">
                <template slot-scope="scope">
                  {{ scope.row.questionContent }}
                </template>
              </el-table-column>

              <el-table-column align="center"
                               label="题目类型">
                <template slot-scope="scope">
                  <span v-if="scope.row.questionType === 1">单选题</span>
                  <span v-else-if="scope.row.questionType === 2">多选题</span>
                  <span v-else-if="scope.row.questionType === 3">判断题</span>
                  <span v-else-if="scope.row.questionType === 4">简答题</span>
                </template>
              </el-table-column>

              <el-table-column align="center" label="单题分数">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.score" style="margin-left: 5px"></el-input>
                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="80">
                <template slot-scope="scope">
                  <el-button circle icon="el-icon-delete" type="danger"
                             @click="delQuestion(scope.row.questionId)"></el-button>
                </template>
              </el-table-column>
            </el-table>

          </el-card>
        </div>
      </el-card>

      <!--设置考试权限-->
      <el-card v-show="curStep === 2">
        <el-radio-group v-model="examAuthority" size="medium">
          <el-radio :label="1" border>完全公开</el-radio>
          <el-radio :label="2" border>需要密码</el-radio>
        </el-radio-group>

        <el-alert :title="examAuthority === 1? '开放的，任何人都可以进行考试！' : '半开放的，知道密码的人员才可以考试！'"
                  style="margin-top: 15px"
                  type="warning">
        </el-alert>

        <el-input v-show="examAuthority === 2" v-model="examPassword" placeholder="输入考试密码"
                  show-password style="margin-top: 15px;width: 20%" type="password"></el-input>
      </el-card>

      <!--设置考试信息-->
      <el-card v-show="curStep === 3">

        <el-form ref="examInfoForm" :model="examInfo" :rules="examInfoRules" label-width="100px">

          <el-form-item label="考试名称" prop="examName">
            <el-input v-model="examInfo.examName"></el-input>
          </el-form-item>

          <el-form-item label="考试描述" prop="examDesc">
            <el-input v-model="examInfo.examDesc"></el-input>
          </el-form-item>

          <el-form-item v-show="makeModel === 2" label="总分数" prop="totalScore">
            <el-input-number :disabled="true" :value="sumTotalScore"></el-input-number>
          </el-form-item>

          <el-form-item label="及格分数" prop="passScore">
            <el-input-number v-model="examInfo.passScore" :min="1"></el-input-number>
          </el-form-item>

          <el-form-item label="考试时长(分钟)" prop="examDuration">
            <el-input-number v-model="examInfo.examDuration" :min="1"></el-input-number>
          </el-form-item>

          <el-form-item label="考试开始时间" prop="startTime">
            <el-date-picker v-model="examInfo.startTime"
                            placeholder="考试开始时间"
                            value-format="yyyy-MM-dd HH:mm:SS"
                            style="margin-left: 5px" type="datetime">
            </el-date-picker>
          </el-form-item>

          <el-form-item label="考试结束时间" prop="endTime">
            <el-date-picker v-model="examInfo.endTime"
                            placeholder="考试结束时间"
                            value-format="yyyy-MM-dd HH:mm:SS"
                            style="margin-left: 5px" type="datetime">
            </el-date-picker>
          </el-form-item>


<!--
          <el-form-item label="专业" label-width="120px" prop="majorId"  v-if="majorShow" >
            <el-select v-model="examInfo.majorId"  placeholder="请选择">
              <el-option v-for="item in allBankMajor" :key="item.id"
                         :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
-->


          <el-form-item label="班级"  prop="className"  v-if="classNameShow" >
            <el-select v-model="className" placeholder="请选择" multiple >
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
          </el-form-item>


        </el-form>

      </el-card>
    </el-main>

    <el-dialog :visible.sync="showQuestionDialog" center title="添加题目" width="80%">
      <el-row>
        <el-select v-model="queryInfo.questionType" clearable placeholder="请选择题目类型" @change="typeChange">
          <el-option
            v-for="item in questionType"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>

        <el-select v-model="queryInfo.bankId" clearable placeholder="请选择题库" style="margin-left: 5px"
                   @change="bankChange">
          <el-option
            v-for="item in allBank"
            :key="item.questionBank.bankId"
            :label="item.questionBank.bankName"
            :value="item.questionBank.bankId">
          </el-option>
        </el-select>

        <el-input v-model="queryInfo.questionContent" placeholder="题目内容" prefix-icon="el-icon-search"
                  style="margin-left: 5px;width: 220px"
                  @blur="getQuestionInfo"></el-input>
        <el-button style="float: right" type="primary" @click="addQuToFree">确认{{ selectedTable.length }}项</el-button>
      </el-row>

      <el-table
        ref="questionTable"
        v-loading="loading"
        :border="true"
        :data="questionInfo"
        highlight-current-row
        style="width: 100%;margin-top: 25px;"
        tooltip-effect="dark" @selection-change="handleTableSectionChange">

        <el-table-column align="center"
                         type="selection"
                         width="55">
        </el-table-column>

        <el-table-column align="center"
                         label="题目类型">
          <template slot-scope="scope">
            <span v-if="scope.row.quType === 1">单选题</span>
            <span v-else-if="scope.row.quType === 2">多选题</span>
            <span v-else-if="scope.row.quType === 3">判断题</span>
            <span v-else-if="scope.row.quType === 4">简答题</span>
          </template>
        </el-table-column>

        <el-table-column align="center" label="题目内容">
          <template slot-scope="scope">
            <span class="quContent">{{ scope.row.quContent }}</span>
          </template>
        </el-table-column>

        <el-table-column align="center"
                         label="难度">
          <template slot-scope="scope">
            <span v-if="scope.row.level === 1">简单</span>
            <span v-if="scope.row.level === 2">中等</span>
            <span v-if="scope.row.level === 3">困难</span>
          </template>
        </el-table-column>

        <el-table-column align="center"
                         label="所属题库"
                         prop="quBankName">
        </el-table-column>

        <el-table-column align="center"
                         label="创建人"
                         prop="createPerson">
        </el-table-column>

        <el-table-column align="center"
                         label="创建时间">
          <template slot-scope="scope">
            {{ scope.row.createTime }}
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
    </el-dialog>

  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AddExam',
  data() {
    return {
      classNameShow: false,
      //查询题目的参数
      queryInfo: {
        //题目类型下拉款所选的内容
        'questionType': '',
        'bankId': '',
        'questionContent': '',
        'pageNo': 1,
        'pageSize': 10
      },
      allBankMajor: [],
      //题目类型
      questionType: [
        {
          id: 1,
          name: '单选题',
        },
        {
          id: 2,
          name: '多选题',
        },
        {
          id: 3,
          name: '判断题',
        },
        {
          id: 4,
          name: '简答题',
        },
      ],
      //当前的步骤
      curStep: 1,
      //组卷模式
      makeModel: 1,
      //添加考试题目信息(makeModel = 1的时候)
      addExamQuestion: [],
      //所有题库信息
      allBank: [],
      //添加考试题目信息(makeModel = 2 的时候)
      addExamQuestion2: [],
      //所有题目的对话框
      showQuestionDialog: false,
      //对话框中题目表格的加载
      loading: true,
      //所有题目的信息
      questionInfo: [],
      //所有题目的对话框中表格被选中
      selectedTable: [],
      //所有题目总数
      total: 0,
      //考试权限(1公开, 2密码)
      examAuthority: 1,
      //考试密码(权限为2时的密码)
      examPassword: '',
      className: '',
      //补充的考试信息
      examInfo: {
        className: '',
        'examId': '',
        'majorId': '',
        'examDesc': '',
        'passScore': 0,
        'examDuration': '',
        'startTime': '',
        'endTime': ''
      },
      //补充的考试信息的表单验证
      examInfoRules: {
        examName: [
          {
            required: true,
            message: '请输入考试名称',
            trigger: 'blur'
          }
        ],
        passScore: [
          {
            required: true,
            message: '请输入通过分数',
            trigger: 'blur'
          }
        ],
        examDuration: [
          {
            required: true,
            message: '请输入考试时长',
            trigger: 'blur'
          }
        ],
      },
    }
  },
  props: ['tagInfo'],
  created() {
    axios.get('/common/checkToken').then((resp) => {
      if (resp.data.code === 200 && resp.data.data.roleId === '2') {//当前用户携带的token信息正确并且是管理员

        this.classNameShow = true;
      } else {

      }
    })


    this.getMajorBankInfo()

    //一创建就改变头部的面包屑
    this.$emit('giveChildChangeBreakInfo', '添加考试', '添加考试')
    this.createTagsInParent()
    this.getBankInfo()
  },
  methods: {

    //向父组件中添加头部的tags标签
    createTagsInParent() {
      let flag = false
      this.tagInfo.map(item => {
        //如果tags全部符合
        if (item.name === '添加考试' && item.url === this.$route.path) {
          flag = true
        } else if (item.name === '添加考试' && item.url !== this.$route.path) {
          this.$emit('updateTagInfo', '添加考试', this.$route.path)
          flag = true
        }
      })
      if (!flag) this.$emit('giveChildAddTag', '添加考试', this.$route.path)
    },
    //获取所有的题库信息
    getBankInfo() {
      this.$http.get(this.API.getBankHaveQuestionSumByType, {
        params: {
          'pageNo': 1,
          'pageSize': 9999
        }
      }).then((resp) => {
        if (resp.data.code === 200) {
          this.allBank = resp.data.data.bankHaveQuestionSums
        } else {
          this.$notify({
            title: 'Tips',
            message: resp.data.message,
            type: 'error',
            duration: 2000
          })
        }
      })
    },
    //删除当前需要去除的题库
    delBank(bankId) {
      this.addExamQuestion.forEach((item, index) => {
        if (item.bankId === bankId) this.addExamQuestion.splice(index, 1)
      })
    },
    //添加题库组卷中的题库
    addBank() {
      this.addExamQuestion.push(
        {
          'bankName': '',
          'singleScore': 1,
          'multipleScore': 1,
          'judgeScore': 1,
          'shortScore': 1
        })
    },
    //自由组卷时添加试题
    showAddDialog() {
      this.showQuestionDialog = true
      this.getQuestionInfo()
    },
    //自由组卷时删除试题
    delQuestion(questionId) {
      this.addExamQuestion2.forEach((item, index) => {
        if (item.questionId === questionId) this.addExamQuestion2.splice(index, 1)
      })
    },
    //题目类型变化
    typeChange(val) {
      this.queryInfo.questionType = val
      this.getQuestionInfo()
    },
    //题库变化
    bankChange(val) {
      this.queryInfo.bankId = val
      this.getQuestionInfo()
    },
    //获取题目信息
    getQuestionInfo() {
      this.$http.get(this.API.getQuestion2, {params: this.queryInfo}).then((resp) => {
        if (resp.data.code === 200) {
          this.questionInfo = resp.data.data.questions;
          this.total = resp.data.data.total;
          this.loading = false
        } else {
          this.$notify({
            title: 'Tips',
            message: '获取题库信息失败',
            type: 'error',
            duration: 2000
          })
        }
      })
    },
    //处理表格被选中
    handleTableSectionChange(val) {
      this.selectedTable = val
    },
    //分页页面大小改变
    handleSizeChange(val) {
      this.queryInfo.pageSize = val
      this.getQuestionInfo()
    },
    //分页插件的页数
    handleCurrentChange(val) {
      this.queryInfo.pageNo = val
      this.getQuestionInfo()
    },
    //自由组卷中选中的题目添加进去
    addQuToFree() {
      this.selectedTable.forEach(item => {
        if (!this.addExamQuestion2.some(i2 => {
          return i2.questionId === item.id
        })) {//不存在有当前题目
          this.addExamQuestion2.push({
            'questionId': item.id,
            'questionContent': item.quContent,
            'questionType': item.quType,
            'score': 1
          })
        }
      })
      this.showQuestionDialog = false
    },
    //组卷模式变化
    makeModelChange() {
      this.$confirm('此操作将丢失当前组卷数据, 是否继续?', 'Tips', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.makeModel === 1 ? this.addExamQuestion2 = [] : this.addExamQuestion = []
      }).catch(() => {
      })
    },
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
    //添加考试
    addExam() {
      let className = this.className
      this.$refs['examInfoForm'].validate((valid) => {
        if (valid && (this.addExamQuestion.length !== 0 || this.addExamQuestion2.length !== 0)) {
          //构造数据对象(考试信息)
          let exam = this.examInfo
          // console.log(this.examInfo.endTime)

          if(className != null && className !=""){
            exam.className = className.join(',')
          }
          exam.totalScore = this.sumTotalScore
          exam.status = 1
          //权限id设置
          exam.type = this.examAuthority
          if (this.examAuthority === 2) {//考试密码
            if (this.examPassword === '') {// 当前用户选择了需要密码权限,但是密码为空
              this.$message.error('当前权限为需要密码,但是密码为空');
              return false;
            }
            exam.password = this.examPassword
          }
          //题库组卷模式
          if (this.makeModel === 1 && !this.addExamQuestion.some(item => item.bankId === '')) {
            console.log(this.addExamQuestion)
            let bankNames = []
            this.addExamQuestion.forEach(item => bankNames.push(item.bankName))
            exam.bankNames = bankNames.join(',')
            exam.singleScore = this.addExamQuestion[0].singleScore
            exam.multipleScore = this.addExamQuestion[0].multipleScore
            exam.judgeScore = this.addExamQuestion[0].judgeScore
            exam.shortScore = this.addExamQuestion[0].shortScore
            this.$http.post(this.API.addExamByBank, exam).then((resp) => {
              if (resp.data.code === 200) this.$router.push('/examManage')
            })
          } else if (this.makeModel === 2) {//自由组卷模式
            //题目id数组
            let questionIds = []
            //题目成绩数组
            let scores = []
            this.addExamQuestion2.forEach(item => {
              questionIds.push(item.questionId)
              scores.push(item.score)
            })
            exam.questionIds = questionIds.join(',')
            exam.scores = scores.join(',')
            console.log(exam)
            this.$http.post(this.API.addExamByQuestionList, exam).then((resp) => {
              if (resp.data.code === 200) this.$router.push('/examManage')
            })
          } else {
            this.$message.error('请检查考试规则设置是否完整')
          }
        } else {
          this.$message.error('请检查考试规则设置是否完整')
          return false
        }
      })
    }
  },
  computed: {
    //计算总分
    sumTotalScore() {
      if (this.makeModel === 2) {
        let score = 0
        this.addExamQuestion2.forEach(item => {
          score += parseInt(item.score)
        })
        return score
      }
    }
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
</style>
