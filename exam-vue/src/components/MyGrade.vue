<template xmlns="http://www.w3.org/1999/html">
  <el-container
    v-loading="loadingCertificate"
    element-loading-spinner="el-icon-loading"
    element-loading-text="拼命加载证书中">
    <el-header height="210">
      <!--操作的下拉框-->
      <el-select v-model="queryInfo.examId" clearable placeholder="请选择考试"
                 style="margin-bottom: 25px;" @change="operation">
        <el-option v-for="(item,index) in allExamInfo" :key="index" :label="item.examName"
                   :value="parseInt(item.examId)">
          <span style="float: left">{{ item.examName }}</span>
        </el-option>
      </el-select>

    </el-header>

    <el-main>
      <el-table
        ref="questionTable"
        v-loading="loading"
        :border="true"
        :data="grade"
        highlight-current-row
        style="width: 100%;"
        tooltip-effect="dark">

        <el-table-column align="center" label="考试名称" prop="examName"></el-table-column>
        <el-table-column align="center" label="考试时间" prop="examTime"></el-table-column>
        <el-table-column align="center" label="创建人" prop="createMan"></el-table-column>
        <el-table-column align="center" label="逻辑得分" prop="logicScore"></el-table-column>
        <el-table-column align="center" label="及格线" prop="passScore"></el-table-column>
        <el-table-column align="center" label="总得分">
          <template slot-scope="scope">
            {{ scope.row.totalScore === null ? '暂未批阅' : scope.row.totalScore }}
          </template>
        </el-table-column>

        <el-table-column align="center" label="是否通过">
          <template slot-scope="scope">

            <div v-if="isOrNotPassExam(scope.row)">
              <span style="color: limegreen">通过</span>
            </div>

            <span v-if="!isOrNotPassExam(scope.row)" style="color: red">未通过</span>
          </template>
        </el-table-column>

        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-info" size="small" type="primary"
                       @click="$router.push('/examResult/'+scope.row.recordId)">详情
            </el-button>
            <br>
            <el-button icon="el-icon-error" size="small" style="margin-top: 5px" type="danger"
                       @click="showErrorQuestion(scope.row)">错题
            </el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog :visible.sync="errorQuestionDialog" center title="错题详情">
      <el-card>
        <!--题目信息-->
        <div v-for="(item,index) in questionInfo" :key="index" style="margin-top: 15px">
          <div>
            <i class="num">{{ index + 1 }}</i>
            <span v-if="item.questionType === 1">【单选题】</span>
            <span v-else-if="item.questionType === 2">【多选题】</span>
            <span v-else-if="item.questionType === 3">【判断题】</span>
            <span>{{ item.questionContent }}</span>
          </div>
          <!--题目中的配图-->
          <img v-for="url in item.images" :src="url" alt="题目图片" style="width: 100px;height: 100px;cursor: pointer"
               title="点击查看大图" @click="showBigImg(url)">

          <!--单选 和 判断 的答案列表-->
          <div v-show="item.questionType === 1 || item.questionType === 3"
               style="margin-top: 25px">
            <div class="el-radio-group">
              <label v-for="(i2,index2) in item.answer"
                     :class="String(index2) === userAnswer[index] && i2.isTrue === 'true' ?
                      'activeAndTrue' : String(index2) === userAnswer[index] ? 'active' :
                       i2.isTrue === 'true' ? 'true' : ''">
                <span>{{ optionName[index2] + '、' + i2.answer }}</span>
                <img v-for="i3 in i2.images" v-if="i2.images !== null"
                     :src="i3"
                     alt="" style="position: absolute;left:100%;top:50%;transform: translateY(-50%);
                  width: 40px;height: 40px;float: right;cursor: pointer;" title="点击查看大图" @mouseover="showBigImg(i3)">
              </label>
            </div>
          </div>

          <!--多选的答案列表-->
          <div v-show="item.questionType === 2" style="margin-top: 25px">
            <div class="el-radio-group">
              <label v-for="(i2,index2) in item.answer"
                     :class="(userAnswer[index]+'').indexOf(index2+'') !== -1 && i2.isTrue === 'true'
                     ? 'activeAndTrue' : (userAnswer[index]+'').indexOf(index2+'') !== -1 ? 'active' :
                       i2.isTrue === 'true' ? 'true' : ''">
                <span>{{ optionName[index] + '、' + i2.answer }}</span>
                <img v-for="i3 in i2.images" v-if="i2.images !== null"
                     :src="i3"
                     alt="" style="position: absolute;left:100%;top:50%;transform: translateY(-50%);
                  width: 40px;height: 40px;float: right;cursor: pointer;" title="点击查看大图" @mouseover="showBigImg(i3)">
              </label>
            </div>
          </div>

        </div>

      </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button @click="errorQuestionDialog = false">取 消</el-button>
        <el-button type="primary" @click="errorQuestionDialog = false">确 定</el-button>
      </div>
    </el-dialog>
    <!--图片回显-->
    <el-dialog :visible.sync="bigImgDialog" @close="bigImgDialog = false">
      <img :src="bigImgUrl" style="width: 100%">
    </el-dialog>
  </el-container>
</template>

<script>
export default {
  name: 'MyGrade',
  data() {
    return {
      queryInfo: {
        pageNo: 1,
        pageSize: 10,
        username: localStorage.getItem('username')
      },
      //我的成绩
      grade: [],
      //所有的考试信息
      allExamInfo: [],
      //成绩总数
      total: 0,
      //表格数据加载
      loading: true,
      //错题的详细信息
      errorQuestionDialog: false,
      //当前考试的题目
      questionInfo: [],
      //当前用户的答案
      userAnswer: [],
      //答案的选项名abcd数据
      optionName: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'],
      //大图的对话图片地址
      bigImgUrl: '',
      bigImgDialog: false,
      //加载证书的数据加载动画
      loadingCertificate: false
    }
  },
  created() {
    this.getMyGrade()
  },
  methods: {
    getMyGrade() {
      this.$http.get(this.API.getMyGrade, {params: this.queryInfo}).then((resp) => {
        if (resp.data.code === 200) {
          this.grade = resp.data.data.examRecords
          this.total = resp.data.data.total
          this.getAllExamInfo()
          this.loading = false
        }
      })
    },
    setExamName() {
      this.grade.forEach(item => {
        this.allExamInfo.forEach(i2 => {
          if (item.examId === i2.examId) {
            this.$set(item, 'examName', i2.examName)
            this.$set(item, 'passScore', i2.passScore)
          }
        })
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
      this.getMyGrade()
    },
    //分页页面大小改变
    handleSizeChange(val) {
      this.queryInfo.pageSize = val
      this.getMyGrade()
    },
    //分页插件的页数
    handleCurrentChange(val) {
      this.queryInfo.pageNo = val
      this.getMyGrade()
    },
    //根据id查询题目信息
    async getQuestionInfoById(questionId) {
      await this.$http.get(this.API.getQuestionById + '/' + questionId).then((resp) => {
        if (resp.data.code === 200) {
          this.questionInfo.push(resp.data.data)
          //重置问题的顺序 单选 多选 判断
          this.questionInfo = this.questionInfo.sort(function (a, b) {
            return a.questionType - b.questionType
          })
        }
      })
    },
    showErrorQuestion(row) {
      if (row.errorQuestionIds === null) {
        this.$message.warning('当前考试没有逻辑错题O(∩_∩)O~')
      } else {
        this.userAnswer = row.userAnswers.split('-')
        row.errorQuestionIds.split(',').forEach(item => {
          this.getQuestionInfoById(item)
        })
        this.errorQuestionDialog = true
      }
    },
    //点击展示高清大图
    showBigImg(url) {
      this.bigImgUrl = url
      this.bigImgDialog = true
    },
  },
  computed: {
    //是否通过考试
    isOrNotPassExam(row) {
      return (row) => {
        let flag = false
        this.allExamInfo.forEach(item => {
          if (item.examId === row.examId) {
            flag = row.totalScore >= item.passScore
          }
        })
        return flag
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

.examName {
  color: #160f58;
  border-bottom: 4px solid #ffd550;
  font-size: 18px;
  font-weight: 700;
  padding-bottom: 10px
}

.examTime {
  font-size: 16px;
  color: #cbcacf;
  margin-left: 20px;
  font-weight: 700;
}

.el-radio-group label {
  display: block;
  width: 400px;
  padding: 48px 20px 10px 20px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  margin-bottom: 10px;
  position: relative;

  span {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    font-size: 16px;
  }
}

.num {
  display: inline-block;
  background: url('../assets/imgs/examTitle.png') no-repeat 100% 100%;
  background-size: contain;
  height: 37px;
  width: 37px;
  line-height: 30px;
  color: #fff;
  font-size: 20px;
  text-align: center;
  margin-right: 15px;
}

/*选中的答案*/
.active {
  border: 1px solid #1f90ff !important;
  opacity: .5;
}

/*  选中的答案且是正确答案*/
.activeAndTrue {
  border: 1px solid #1f90ff !important;
  opacity: .5;
  height: 15px;
  width: 15px;
  background-size: contain;
  background: url('../assets/imgs/true.png') no-repeat 95%;
  position: absolute;
  top: 0;
  left: 0;
}

.true {
  height: 15px;
  width: 15px;
  background-size: contain;
  background: url('../assets/imgs/true.png') no-repeat 95%;
  position: absolute;
  top: 0;
  left: 0;
}

.ques-analysis {
  padding: 30px 40px;
  background: #f6f6f8;
  margin-bottom: 70px;
}
</style>
