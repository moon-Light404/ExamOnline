<template>
  <el-container>
    <el-header>
      <el-input v-model="queryInfo.bankName"  placeholder="题库名称" prefix-icon="el-icon-search"
                style="width: 220px"
                @blur="contentChange"></el-input>

      <el-select  v-if="majorShow"   clearable v-model="queryInfo.majorId"  placeholder="请选择专业" @change="contentChange" >
        <el-option v-for="item in allBank" :key="item.id"
                   :label="item.name" :value="item.id"></el-option>
      </el-select>

      <br>
      <el-button icon="el-icon-plus" style="margin-top: 10px" type="primary" @click="addTableVisible = true">添加
      </el-button>

    </el-header>

    <el-main style="margin-top: 20px">

      <!--操作的下拉框-->
      <el-select v-if="selectedTable.length !== 0" v-model="operation" :placeholder="'已选择' + selectedTable.length + '项'"
                 clearable
                 style="margin-bottom: 25px;" @change="operationChange">

        <el-option value="delete">
          <span style="float: left">删除</span>
          <span style="float: right; color: #8492a6; font-size: 13px">delete</span>
        </el-option>

      </el-select>

      <el-table
        ref="questionTable"
        v-loading="loading"
        :border="true"
        :data="questionBankInfo"
        highlight-current-row
        style="width: 100%;"
        tooltip-effect="dark" @selection-change="handleTableSectionChange">

        <el-table-column align="center"
                         type="selection"
                         width="55">
        </el-table-column>

<!--
        <el-table-column align="center"
                         label="题库名称"
                         prop="questionBank.bankName">
        </el-table-column>
-->

        <el-table-column align="center" label="题库名称">
          <template slot-scope="scope">
            <span class="quContent" @click="updateTk(scope.row.questionBank.bankName,scope.row.questionBank.bankId)">{{ scope.row.questionBank.bankName }}</span>
          </template>
        </el-table-column>


        <el-table-column align="center"
                         label="所属专业"
                         prop="questionBank.majorName">
        </el-table-column>

        <el-table-column align="center"
                         label="单选题数量"
                         prop="singleChoice">
        </el-table-column>

        <el-table-column align="center"
                         label="多选题数量"
                         prop="multipleChoice">
        </el-table-column>

        <el-table-column align="center"
                         label="判断题数量"
                         prop="judge">
        </el-table-column>

        <el-table-column align="center"
                         label="简答题数量"
                         prop="shortAnswer">
        </el-table-column>

<!--        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
            <el-button
                       :icon=" 'el-icon-caret-right' "
                       :type="'primary'"
                       size="small"
                       @click="gg(scope.row)">
             更改题库名
            </el-button>
          </template>
        </el-table-column>-->

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

      <!--添加题库信息-->
      <el-dialog :visible.sync="addTableVisible" center title="添加题库" width="30%"
                 @close="$refs['addForm'].resetFields()">

        <el-form ref="addForm" :model="addForm" :rules="addFormRules">

          <el-form-item label="题库名称" label-width="120px" prop="bankName">
            <el-input v-model="addForm.bankName"></el-input>
          </el-form-item>

          <el-form-item label="专业" label-width="120px" prop="majorId"  v-if="majorShow"  >
            <el-select v-model="addForm.majorId"  placeholder="请选择">
              <el-option v-for="item in allBank" :key="item.id"
                         :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>

        </el-form>

        <div slot="footer" class="dialog-footer">
          <el-button @click="addTableVisible = false">取 消</el-button>
          <el-button type="primary" @click="addQuestionBank">确 定</el-button>
        </div>
      </el-dialog>

    </el-main>
  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'QuestionBankManage',
  data() {
    var validateBankname = (rule, value, callback) => {
      this.$http.get(this.API.checkBank + '/' + this.addForm.bankName).then((resp) => {
        if (resp.data.code === 200) {
          callback()
        } else {
          callback(new Error('题库不能重名哦，谢谢配合<==>'))
        }
      })
    }
    return {
      majorShow: true,
      queryInfo: {
        bankName: '',
        pageNo: 1,
        pageSize: 10
      },
      //被选中的表格的信息
      selectedTable: [],
      //所有题库信息
      questionBankInfo: [],
      //当前被选中的操作
      operation: '',
      loading: true,
      //所有的题库条数
      total: 0,
      //添加题库的对话框
      addTableVisible: false,
      //添加题库的表单信息
      addForm: {
        bankName: '',
        majorId: ''
      },
      //添加表单的数据校验规则
      addFormRules: {
        bankName: [
          {
            required: true,
            message: '请输入题库名称',
            trigger: 'blur'
          },
          {
            validator: validateBankname,
            trigger: 'blur'
          }
        ]
      },

    }
  },
  created() {
    axios.get('/common/checkToken').then((resp) => {
      if (resp.data.code === 200 && resp.data.data.roleId === '2') {//当前用户携带的token信息正确并且是管理员

        this.majorShow = false;
      } else {

      }
    })

    this.getMajorBankInfo()
    this.getBankInfo()
  },
  methods: {
    gg(e){
      console.log(e.questionBank)
      var bankId = e.questionBank.bankName;
      this.$prompt(
        '请输入题库名称：',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type:"warning",            // 图标样式 "warning"|"error"...
          inputValue: e.questionBank.bankName,
          inputErrorMessage: '输入不能为空',
          inputValidator: (value) => {       // 点击按钮时，对文本框里面的值进行验证
            if(!value) {
              return '输入不能为空';
            }


          },
        }).then(({value}) => {
        console.log(value);

        this.$http.get(this.API.updateBankName,{params: {newName: value,bankId:e.questionBank.bankId}}).then((resp) => {
          if (resp.data.code === 200) {
            this.getBankInfo();
          } else {
            this.$notify({
              title: 'Tips',
              message: '失败',
              type: 'error',
              duration: 2000
            })
          }
        })



        // TO DO DO ...
      }).catch((err) => {
        console.log(err);
      });

    },
    updateTk(bankName,bankId){

      //   this.$router.push('/examResult/' + resp.data.data)
      this.$router.push('/questionTk/' + bankName + '/' + bankId)
    },
    //获取所有的专业
    getMajorBankInfo() {
      this.$http.get(this.API.getMajorBank).then((resp) => {
        if (resp.data.code === 200) {
          this.allBank = resp.data.data
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
    //获取所有的题库信息
    getBankInfo() {
      this.$http.get(this.API.getBankHaveQuestionSumByType, {params: this.queryInfo}).then((resp) => {
        if (resp.data.code === 200) {
          this.questionBankInfo = resp.data.data.bankHaveQuestionSums
          this.total = resp.data.data.total
          this.loading = false
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
    //查询内容变化
    contentChange() {
      this.getBankInfo()
    },
    //操作选项的被触发
    operationChange(val) {
      if (val === 'delete') {
        this.$confirm('此操作将永久删除该题库, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let ids = []
          this.selectedTable.map(item => {
            ids.push(item.questionBank.bankId)
          })
          //发起删除请求
          this.$http.get(this.API.deleteQuestionBank, {params: {'ids': ids.join(',')}}).then((resp) => {
            if (resp.data.code === 200) {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'success',
                duration: 2000
              })
              this.getBankInfo()
            } else {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'error',
                duration: 2000
              })
            }
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
      }
    },
    //表格部分行被选中
    handleTableSectionChange(row) {
      this.selectedTable = row
    },
    //分页插件的大小改变
    handleSizeChange(val) {
      this.queryInfo.pageSize = val
      this.getBankInfo()
    },
    //分页插件的页数
    handleCurrentChange(val) {
      this.queryInfo.pageNo = val
      this.getBankInfo()
    },
    //添加题库
    addQuestionBank() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          let majorId = this.addForm.majorId
          let bankName = this.addForm.bankName

          this.$http.get(this.API.addQuestionBank,
          {
            params: {
              'bankName': bankName,
              'majorId': majorId
            }
          }).then((resp) => {
            if (resp.data.code === 200) {
              this.getBankInfo()
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'success',
                duration: 2000
              })
            } else {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'error',
                duration: 2000
              })
            }
            this.addTableVisible = false
          })
        } else {
          this.$message.error('请检查您所填写的信息是否有误')
          return false
        }
      })
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
.quContent {
  color: #4d99de;
  cursor: pointer;
}
</style>
