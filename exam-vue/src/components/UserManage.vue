<template>
  <el-container>

    <el-header>
      <el-select v-model="queryInfo.roleId" placeholder="角色" prefix-icon="el-icon-search" style="width:180px">
        <el-option :value="1" label="学生"></el-option>
        <el-option :value="2" label="教师"></el-option>
        <el-option :value="3" label="超级管理员"></el-option>
      </el-select>


      <el-input v-model="queryInfo.loginName" placeholder="用户名" prefix-icon="el-icon-search"></el-input>
      <el-input v-model="queryInfo.trueName" placeholder="姓名" prefix-icon="el-icon-search"
                style="margin-left: 5px"></el-input>

      <el-select v-model="queryInfo.majorId"  placeholder="请选择专业" style="width:180px" clearable>
        <el-option v-for="item in allBank" :key="item.id"
                   :label="item.name" :value="item.id"></el-option>
      </el-select>


      <el-select v-model="queryInfo.className" placeholder="班级" prefix-icon="el-icon-search" style="width:180px" clearable>
        <el-option :value="1" label="1班"></el-option>
        <el-option :value="2" label="2班"></el-option>
        <el-option :value="3" label="3班"></el-option>
        <el-option :value="4" label="4班"></el-option>
        <el-option :value="5" label="5班"></el-option>
        <el-option :value="6" label="6班"></el-option>
        <el-option :value="7" label="7班"></el-option>
        <el-option :value="8" label="8班"></el-option>
        <el-option :value="9" label="9班"></el-option>

      </el-select>




      <el-button icon="el-icon-search" style="margin-left: 5px" type="primary" @click="getUserInfo">查询</el-button>
      <el-button icon="el-icon-plus" style="margin-left: 5px" type="primary" @click="showAddDialog">添加</el-button>
    </el-header>

    <el-main>
      <!--操作的下拉框-->
      <el-select v-if="selectedInTable.length !== 0" v-model="selected"
                 :placeholder="'已选择' + selectedInTable.length + '项'" clearable
                 style="margin-bottom: 25px;" @change="selectChange">

        <el-option v-for="(item,index) in optionInfo" :key="index" :value="item.desc">
          <span style="float: left">{{ item.label }}</span>
          <span style="float: right; color: #8492a6; font-size: 13px">{{ item.desc }}</span>
        </el-option>

      </el-select>

      <el-table
        ref="multipleTable"
        v-loading="loading"
        :border="true"
        :data="userInfo"
        highlight-current-row
        style="width: 100%"
        tooltip-effect="dark"
        @selection-change="handleSelectionChange">

        <el-table-column align="center"
                         type="selection"
                         width="55">
        </el-table-column>

        <el-table-column align="center" label="用户名">
          <template slot-scope="scope">
            <span class = "userContent" @click="SelectUser(scope.row.username)">{{scope.row.username}}</span>
          </template>
        </el-table-column>

        <el-table-column align="center"
                         label="姓名"
                         prop="trueName">
        </el-table-column>

        <el-table-column align="center"
                         label="角色">
          <template slot-scope="scope">
            <span v-show="scope.row.roleId === 3" class="role">超级管理员</span>
            <span v-show="scope.row.roleId === 2" class="role">教师</span>
            <span v-show="scope.row.roleId === 1" class="role">学生</span>
          </template>
        </el-table-column>



        <el-table-column align="center"
                         label="专业"
                         prop="majorName">
        </el-table-column>

        <el-table-column align="center"
                         label="班级"
                         prop="className">
        </el-table-column>



        <el-table-column align="center"
                         label="创建时间">
          <template slot-scope="scope">
            {{ scope.row.createDate }}
          </template>
        </el-table-column>

        <el-table-column align="center"
                         label="状态">
          <template slot-scope="scope">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
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

<!--    重置密码操作-->
    <el-dialog :visible.sync="updatePwdTableVisible" center title="重置密码" width="50%">
        <el-form ref="resetPwdForm" :model="resetPwdForm" :rules="resetPwdFormRules">
            <el-form-item label="用户名" prop="username">
              <el-input class="pwd" v-model="resetPwdForm.username" disabled>

              </el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input class="pwd" v-model="resetPwdForm.password"  placeholder="密码" prefix-icon="el-icon-lock"
                          show-password>
                </el-input>
            </el-form-item>
        </el-form>


        <el-button class="quxiao" @click="updatePwdTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="ResetUser">确 定</el-button>

    </el-dialog>



    <el-dialog :visible.sync="addTableVisible" center title="添加用户" width="30%"
               @close="resetAddForm">

      <el-form ref="addForm" :model="addForm" :rules="addFormRules">

        <el-form-item label="用户名" label-width="100px" prop="username">
          <el-input v-model="addForm.username"></el-input>
        </el-form-item>

        <el-form-item label="密码" label-width="100px" prop="password">
          <el-input v-model="addForm.password" show-password type="password"></el-input>
        </el-form-item>

        <el-form-item label="角色" label-width="100px" prop="roleId">
          <el-select v-model="addForm.roleId" placeholder="请选择用户权限" @change="operation">
            <el-option label="学生" value="1"></el-option>
            <el-option label="教师" value="2"></el-option>
            <el-option label="超级管理员" value="3"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="真实姓名" label-width="100px" prop="trueName">
          <el-input v-model="addForm.trueName"></el-input>
        </el-form-item>


        <el-form-item label="专业" label-width="100px" prop="majorId" v-if="majorShow" >
          <el-select v-model="addForm.majorId"  placeholder="请选择">
            <el-option v-for="item in allBank" :key="item.id"
                       :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="班级" label-width="100px" prop="className" v-if="classNameShow">
          <el-select v-model="addForm.className" placeholder="请选择">
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

<!--
        <el-form-item label="班级" label-width="120px" prop="majorId">
          <el-select v-model="addForm.majorId" multiple placeholder="请选择">
            <el-option v-for="item in allBank" :key="item.majorId"
                       :label="item.bankName" :value="item.majorId"></el-option>
          </el-select>
        </el-form-item>

        -->


      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="addTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="addUser">确 定</el-button>
      </div>
    </el-dialog>

  </el-container>

</template>

<script>
export default {
  name: 'UserManage',
  data() {
    // 校验重置密码
    var validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'))
      } else if (value.length < 6) {
        callback(new Error('新密码少于6位数!'))
      } else {
        callback()
      }
    }
    //自定义用户名校验规则
    var validateUsername = (rule, value, callback) => {
      this.$http.get(this.API.checkUsername + '/' + this.addForm.username).then((resp) => {
        if (resp.data.code === 200) {
          callback()
        } else {
          callback(new Error('用户名已存在'))
        }
      })
    }
    return {
      //查询用户的参数
      queryInfo: {
        'loginName': '',
        'trueName': '',
        'roleId': '',
        'className': '',
        'majorId': '',
        'pageNo': 1,
        'pageSize': 10
      },
      //用户信息
      userInfo: [],
      majorShow: false,
      classNameShow: false,
      allBank: [],
      //下拉选择框的数据
      optionInfo: [
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

      //下拉框所选择的数据
      selected: '',
      //被选择的表格中的行数据
      selectedInTable: [],
      //所有用户的条数
      total: 0,
      //添加用户的对话框是否显示
      addTableVisible: false,
      //添加用户的表单信息
      addForm: {
        'username': '',
        'password': '',
        'roleId': '',
        'className': '',
        'majorId': '',
        'trueName': ''
      },
      //添加用户表单的验证规则
      addFormRules: {
        username: [
          {
            required: true,
            message: '请输入登录用户名',
            trigger: 'blur'
          },
          {
            validator: validateUsername,
            trigger: 'blur'
          }
        ],
        password: [
          {
            required: true,
            message: '请输入密码',
            trigger: 'blur'
          },
          {
            min: 5,
            message: '密码必须5位以上',
            trigger: 'blur'
          }
        ],
        trueName: [
          {
            required: true,
            message: '请输入用户真实姓名',
            trigger: 'blur'
          },
        ],
        roleId: [
          {
            required: true,
            message: '请选择用户权限',
            trigger: 'blur'
          },
        ],
      },

      resetPwdForm: {
        'username': '', // 用户名
        'password': '', // 新密码
      },

      // 更新密码的规则
      resetPwdFormRules: {
        password: [
          {
            required: true,
            validator: validatePassword,
            trigger: 'blur',
          }
        ]
      },
      updatePwdTableVisible: false, // 不显示重置密码框
      //表格信息加载
      loading: true
    }
  },
  created() {
    this.getMajorBankInfo()
    this.getUserInfo()
  },
  methods: {
    operation(v) {
      if (v === '1') {
        this.majorShow = true;
        this.classNameShow = true;
        this.addForm.className = '';
        this.addForm.majorId = '';
      }
      if (v === '2') {
        this.majorShow = true;
        this.classNameShow = false;
        this.addForm.className = '';
        this.addForm.majorId = '';
      }
      if (v === '3') {
        this.majorShow = false;
        this.classNameShow = false;
        this.addForm.className = '';
        this.addForm.majorId = '';
      }
    },
    //获取用户信息
    getUserInfo() {
      this.$http.get(this.API.getUserInfo, {params: this.queryInfo}).then((resp) => {
        if (resp.data.code === 200) {
          this.userInfo = resp.data.data.users;
          this.total = resp.data.data.total;
          this.loading = false;
        } else {
          this.$notify({
            title: 'Tips',
            message: '获取信息失败',
            type: 'error',
            duration: 2000
          })
        }
      })
    },
    //表格某一行被选中
    handleSelectionChange(val) {
      this.selectedInTable = val
    },
    //功能下拉框被选择
    selectChange(val) {
      //清空上一次的操作
      this.selected = ''
      //表格中所选中的用户的id
      let userIds = []
      this.selectedInTable.map(item => {
        userIds.push(item.id)
      })
      if (val === 'on') {//状态设置为正常
        this.$http.get(this.API.handleUser + '/' + 1, {params: {'userIds': userIds.join(',')}}).then((resp) => {
          if (resp.data.code === 200) {
            //删除成功后,回调更新用户数据
            this.getUserInfo()
            this.$notify({
              title: 'Tips',
              message: '操作成功',
              type: 'success',
              duration: 2000
            })
          } else {
            this.$notify({
              title: 'Tips',
              message: '操作失败',
              type: 'error',
              duration: 2000
            })
          }
        })
      } else if (val === 'off') {//禁用用户
        this.$http.get(this.API.handleUser + '/' + 2, {params: {'userIds': userIds.join(',')}}).then((resp) => {
          if (resp.data.code === 200) {
            //删除成功后,回调更新用户数据
            this.getUserInfo()
            this.$notify({
              title: 'Tips',
              message: '操作成功',
              type: 'success',
              duration: 2000
            })
          } else {
            this.$notify({
              title: 'Tips',
              message: '操作失败',
              type: 'error',
              duration: 2000
            })
          }
        })
      } else if (val === 'delete') {//删除用户
        this.$http.get(this.API.handleUser + '/' + 3, {params: {'userIds': userIds.join(',')}}).then((resp) => {
          if (resp.data.code === 200) {
            //删除成功后,回调更新用户数据
            this.getUserInfo()
            this.$notify({
              title: 'Tips',
              message: '操作成功',
              type: 'success',
              duration: 2000
            })
          } else {
            this.$notify({
              title: 'Tips',
              message: '操作失败',
              type: 'error',
              duration: 2000
            })
          }
        })
      }
    },
    //分页插件的大小改变
    handleSizeChange(val) {
      this.queryInfo.pageSize = val
      this.getUserInfo()
    },
    //分页插件的页数
    handleCurrentChange(val) {
      this.queryInfo.pageNo = val
      this.getUserInfo()
    },
    //点击添加按钮
    showAddDialog() {
      this.addTableVisible = true
    },
    //添加用户
    addUser() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.$http.post(this.API.addUser, this.addForm).then((resp) => {
            if (resp.data.code === 200) {
              this.getUserInfo()
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
    //表单信息重置
    resetAddForm() {
      //清空表格数据
      this.$refs['addForm'].resetFields()
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

    // 显示重置密码操作框
    SelectUser(usrname) {
      this.resetPwdForm.username = usrname // 记录这个被选中的用户名
      this.resetPwdForm.password = ''
      this.updatePwdTableVisible = true // 显示重置密码对话框
    },

    // 退出登录
    async logout() {
      const resp = await this.$http.get(this.API.logout)
      if (resp.data.code === 200) {//退出成功
        window.localStorage.removeItem('authorization')
        //右侧提示通知
        this.$notify({
          title: 'Tips',
          message: '注销成功',
          type: 'success',
          duration: 2000
        })
        await this.$router.push('/')
      } else {//异常
        this.$notify({
          title: 'Tips',
          message: '注销失败,服务器异常',
          type: 'error',
          duration: 2000
        })
      }
    },
    // 重置用户密码
    ResetUser() {
      this.$refs['resetPwdForm'].validate((valid) => {
        if (valid) {
          this.$http.post(this.API.resetPwd, null, {params: this.resetPwdForm}).then((resp) => {
            if (resp.data.code === 210) {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'success',
                duration: 2000
              })
              this.logout() // 注销重新登录
            } else if(resp.data.code == 200) {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'success',
                duration: 2000
              })
            }
          })
          this.updatePwdTableVisible = false;
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.el-container {
  width: 100%;
  height: 100%;
}

.el-input {
  width: 180px;
}

.el-container {
  animation: leftMoveIn .7s ease-in;
}
.userContent {
  color: #4d99de;
  cursor: pointer;
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
//重置密码框
.pwd{
  width: 300px;
  margin-right: 140px;
  margin-left: 100px;
}

.quxiao {
  margin-right: 25px;
}
</style>
