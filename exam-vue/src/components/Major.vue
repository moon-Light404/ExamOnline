<template>
  <el-container>
    <el-main>

      <el-header>
        <el-button icon="el-icon-plus" style="margin-left: 5px" type="primary" @click="showAddDialog">添加</el-button>
      </el-header>

      <el-table
        ref="majorForm"
        v-loading="loading"
        :border="true"
        :data="majorInfo"
        highlight-current-row
        style="width: 100%"
        tooltip-effect="dark">

<!--
        <el-table-column label="专业ID" prop="id">
        </el-table-column>
-->

        <el-table-column label="名称" prop="name">
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

    </el-main>


    <el-dialog :visible.sync="addTableVisible" center title="添加专业" width="30%"
               @close="resetAddForm">

      <el-form ref="addForm" :model="addForm" :rules="addFormRules">

        <el-form-item label="专业名称" label-width="120px" prop="name">
          <el-input v-model="addForm.name"></el-input>
        </el-form-item>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="addTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="addMajor">确 定</el-button>
      </div>
    </el-dialog>




  </el-container>
</template>

<script>
export default {
  name: 'Major',
  data() {
    return {
      addForm: {
        'name': ''
      },
      queryInfo: {
        'pageNo': 1,
        'pageSize': 10
      },
      //添加用户表单的验证规则
      addFormRules: {
        username: [
          {
            required: true,
            message: '请输入登录用户名',
            trigger: 'blur'
          }
        ],
      },
      //角色信息
      majorInfo: [],
      addTableVisible: false,
      total: 0,
      //表格数据加载
      loading: true
    }
  },
  created() {
    this.getMajorInfo()
  },
  methods: {
    //分页插件的大小改变
    handleSizeChange(val) {
      this.queryInfo.pageSize = val
      this.getMajorInfo()
    },
    //分页插件的页数
    handleCurrentChange(val) {
      this.queryInfo.pageNo = val
      this.getMajorInfo()
    },
    //点击添加按钮
    showAddDialog() {
      this.addTableVisible = true
    },
    //表单信息重置
    resetAddForm() {
      //清空表格数据
      this.$refs['addForm'].resetFields()
    },
    addMajor() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.$http.post(this.API.addMajor, this.addForm).then((resp) => {
            if (resp.data.code === 200) {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'success',
                duration: 2000
              })
              this.getMajorInfo()
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
    getMajorInfo() {



      this.$http.get(this.API.getMajorInfo, {params: this.queryInfo}).then((resp) => {
        if (resp.data.code === 200) {
          console.log(resp.data)
          this.majorInfo = resp.data.data.majors;
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


    }
  }
}
</script>

<style lang="scss" scoped>
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
