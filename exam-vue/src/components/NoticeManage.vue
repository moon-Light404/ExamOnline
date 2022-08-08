<template>
  <el-container>

    <el-header>
      <el-input v-model="queryInfo.noticeContent" placeholder="搜索公告" prefix-icon="el-icon-search"
                @blur="getNoticeInfo"></el-input>

      <el-button icon="el-icon-plus" style="margin-left: 5px" type="primary" @click="showPublishNoticeDialog">发布新公告
      </el-button>
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
        :data="noticeInfo"
        highlight-current-row
        style="width: 100%"
        tooltip-effect="dark"
        @selection-change="handleSelectionChange">

        <el-table-column align="center"
                         type="selection"
                         width="55">
        </el-table-column>


        <el-table-column label="公告内容" width="600px;">
          <template slot-scope="scope">
            <p v-html="scope.row.content"></p>
          </template>
        </el-table-column>

        <el-table-column align="center"
                         label="发布时间"
                         prop="createTime">
        </el-table-column>

        <el-table-column align="center"
                         label="更新时间">

          <template slot-scope="scope">
            {{ scope.row.updateTime === null ? '暂无更新' : scope.row.updateTime }}
          </template>
        </el-table-column>

        <el-table-column align="center"
                         label="状态">
          <template slot-scope="scope">
            <p v-if="scope.row.status === 1" style="color: limegreen">当前系统公告</p>
            <p v-if="scope.row.status === 0" style="color: #ffd550">历史公告</p>
          </template>
        </el-table-column>

        <el-table-column align="center"
                         label="操作">
          <template slot-scope="scope">
            <el-button type="warning" @click="showUpdateNoticeDialog(scope.row)">更新公告</el-button>
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

    <el-dialog :visible.sync="publishTableVisible" center title="发布公告"
               width="70%"
               @close="resetPublishForm">

      <el-form ref="publishForm" :model="publishForm" :rules="publishFormRules">

        <el-form-item label="公告内容" label-width="120px" prop="content">
          <div id="publishEditor"></div>
        </el-form-item>


        <el-form-item label="当前最新公告?" label-width="120px" prop="status">
          <el-select v-model="publishForm.status" placeholder="是否设置为当前最新公告">
            <el-option label="是" value="1"></el-option>
            <el-option label="否" value="0"></el-option>
          </el-select>
        </el-form-item>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="publishTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="publishNotice">发 布</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="updateTableVisible" center title="更新公告"
               width="70%"
               @close="$refs['updateForm'].resetFields()">

      <el-form ref="updateForm" :model="updateForm" :rules="updateFormRules">

        <el-form-item label="公告内容" label-width="120px" prop="content">
          <div id="updateEditor"></div>
        </el-form-item>


        <el-form-item label="当前最新公告?" label-width="120px" prop="status">
          <el-select v-model="updateForm.status" :disabled="currentUpdateNoticeStatus === '1'"
                     placeholder="是否设置为当前最新公告">
            <el-option label="是" value="1"></el-option>
            <el-option label="否" value="0"></el-option>
          </el-select>
          <span v-show="currentUpdateNoticeStatus === '1'" style="color: red">
            请将其他公告设置为当前使用公告后再操作正在使用的公告
          </span>
        </el-form-item>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="updateTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateNotice">更 新</el-button>
      </div>
    </el-dialog>

  </el-container>

</template>

<script>
// 导入富文本编辑器
import E from 'wangeditor'

export default {
  name: 'NoticeManage',
  data() {
    return {
      //查询公告的参数
      queryInfo: {
        'noticeContent': '',
        'pageNo': 1,
        'pageSize': 10
      },
      //下拉选择框的数据
      optionInfo: [
        {
          'label': '删除',
          'desc': 'delete'
        }
      ],
      //下拉框所选择的数据
      selected: '',
      // 所有公告信息
      noticeInfo: [],
      // 表格数据预加载
      loading: true,
      // 公告总数
      total: 0,
      // 被选中的表格行
      selectedInTable: [],
      // 发布公告对话框
      publishTableVisible: false,
      // 发布的表单
      publishForm: {
        content: '',
        status: '1'
      },
      publishFormRules: {
        content: [
          {
            required: true,
            message: '请输入公告内容',
            trigger: 'blur'
          }
        ]
      },
      // 发布公告的富文本编辑器
      publishEditor: new E('#publishEditor'),
      // 更新公告的富文本编辑器
      updateEditor: new E('#updateEditor'),
      // 更新公告的对话框是否展示
      updateTableVisible: false,
      // 更新的表单
      updateForm: {
        nid: '',
        content: '',
        status: ''
      },
      updateFormRules: {
        content: [
          {
            required: true,
            message: '请输入公告内容',
            trigger: 'blur'
          }
        ]
      },
      // 当前更新的公告的信息
      currentUpdateNoticeStatus: ''
    }
  },
  created() {
    this.getNoticeInfo()
  },
  methods: { //获取用户信息
    getNoticeInfo() {
      this.$http.get(this.API.getAllNotice, {params: this.queryInfo}).then((resp) => {
        if (resp.data.code === 200) {
          this.noticeInfo = resp.data.data.notices;
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
    //分页插件的大小改变
    handleSizeChange(val) {
      this.queryInfo.pageSize = val
      this.getNoticeInfo()
    },
    //分页插件的页数
    handleCurrentChange(val) {
      this.queryInfo.pageNo = val
      this.getNoticeInfo()
    },
    //功能下拉框被选择
    selectChange(val) {
      //清空上一次的操作
      this.selected = ''
      //表格中所选中的公告的id
      let ids = []
      this.selectedInTable.map(item => {
        // if (item.status !== 1) {// **防止删除当前使用公告
        // }
        ids.push(item.nid)
      })
      if (val === 'delete') {//删除
        if (ids.length === 0) {
          this.$message.error('不允许删除当前使用公告')
        } else {
          this.$http.get(this.API.deleteNotice, {params: {'ids': ids.join(',')}}).then((resp) => {
            if (resp.data.code === 200) {
              //删除成功后,回调更新数据
              this.getNoticeInfo()
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
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
      }
    },
    // 显示发布新闻对话框
    showPublishNoticeDialog() {
      this.publishTableVisible = true
      window.setTimeout(() => {
        this.createPublishWangEditor()
      }, 200)
    },
    // 发布公告的表单信息重置
    resetPublishForm() {
      //清空表格数据
      this.$refs['publishForm'].resetFields()
    },
    // 创建新增新闻的富文本编辑器
    createPublishWangEditor() {
      // 设置编辑区域高度为 300px
      this.publishEditor.config.height = 300
      this.publishEditor.config.onchange = (html) => {
        this.publishForm.content = html
      }
      this.publishEditor.txt.html(this.publishForm.content)
      this.publishEditor.create()
    },
    // 创建更新新闻的富文本编辑器
    createUpdateWangEditor() {
      // 设置编辑区域高度为 300px
      this.updateEditor.config.height = 300
      this.updateEditor.config.onchange = (html) => {
        this.updateForm.content = html
      }
      this.updateEditor.create()
      this.updateEditor.txt.html(this.updateForm.content)
    },
    // 发布公告
    publishNotice() {
      this.$refs['publishForm'].validate((valid) => {
        if (valid) {
          this.$http.post(this.API.publishNotice, this.publishForm).then((resp) => {
            if (resp.data.code === 200) {
              this.getNoticeInfo()
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
            this.publishTableVisible = false
          })
        } else {
          this.$message.error('请检查您所填写的信息是否有误')
          return false
        }
      })
    },
    // 显示更新公告的对话框
    showUpdateNoticeDialog(row) {
      this.updateForm.content = row.content
      this.updateForm.nid = row.nid
      this.updateForm.status = row.status + ''
      this.currentUpdateNoticeStatus = row.status + ''
      this.updateTableVisible = true
      // 创建富文本编辑器
      window.setTimeout(() => {
        this.createUpdateWangEditor()
      }, 200)
    },
    // 更新公告
    updateNotice() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          this.$http.post(this.API.updateNotice, this.updateForm).then((resp) => {
            if (resp.data.code === 200) {
              this.getNoticeInfo()
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
            this.updateTableVisible = false
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
</style>
