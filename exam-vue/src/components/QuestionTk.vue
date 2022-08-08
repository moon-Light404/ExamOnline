<template>
  <el-container>

    <el-header height="220">
<!--
      <el-row>
        <el-select v-model="queryInfo.questionType" clearable placeholder="请选择题目类型" @change="typeChange">
          <el-option
            v-for="item in questionType"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>

        <el-select v-model="queryInfo.questionBank" clearable placeholder="请选择题库" style="margin-left: 5px"
                   @change="bankChange">
          <el-option
            v-for="item in allBank"
            :key="item.id"
            :label="item.bankName"
            :value="item.bankName">
          </el-option>
        </el-select>

        <el-input v-model="queryInfo.questionContent" placeholder="题目内容" prefix-icon="el-icon-search"
                  style="margin-left: 5px;width: 220px"
                  @blur="contentChange"></el-input>
      </el-row>
-->

      <el-row style="margin-top: 10px">
        <el-button icon="el-icon-plus" type="primary" @click="addQuTableVisible = true">添加</el-button>
      </el-row>
    </el-header>

    <el-main>
      <!--操作的下拉框-->
      <el-select v-if="selectionTable.length !== 0" v-model="operation"
                 :placeholder="'已选择' + selectionTable.length + '项'" clearable
                 style="margin-bottom: 25px;" @change="operationChange">

        <el-option v-for="(item,index) in optionInfo" :key="index" :value="item.desc" v-if="glyShow">
          <span style="float: left">{{ item.label }}</span>
          <span style="float: right; color: #8492a6; font-size: 13px">{{ item.desc }}</span>
        </el-option>

        <el-option v-for="(item,index) in optionInfo2" :key="index" :value="item.desc" v-if="jsShow">
          <span style="float: left">{{ item.label }}</span>
          <span style="float: right; color: #8492a6; font-size: 13px">{{ item.desc }}</span>
        </el-option>

      </el-select>

      <el-table
        ref="questionTable"
        v-loading="loading"
        :border="true"
        :data="questionInfo"
        highlight-current-row
        style="width: 100%;"
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
            <span class="quContent" @click="updateQu(scope.row.id)">{{ scope.row.quContent }}</span>
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

    </el-main>

    <el-dialog :visible.sync="updateQuTableVisible" center title="更新题目" width="50%">
      <el-card>

        <el-form ref="updateQuForm" :model="updateQuForm" :rules="updateQuFormRules">

          <el-form-item label="题目类型" label-width="120px" prop="questionType">
            <el-select v-model="updateQuForm.questionType" disabled placeholder="请选择"
                       @change="updateQuForm.answer =  []" >
              <el-option
                v-for="item in questionType"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="难度等级" label-width="120px" prop="questionLevel">
            <el-select v-model="updateQuForm.questionLevel" placeholder="请选择">
              <el-option :value="parseInt(1)" label="简单"></el-option>
              <el-option :value="parseInt(2)" label="中等"></el-option>
              <el-option :value="parseInt(3)" label="困难"></el-option>
            </el-select>
          </el-form-item>


          <el-form-item label="归属题库" label-width="120px" prop="bankId">
            <el-select v-model="updateQuForm.bankId" multiple placeholder="请选择" disabled="jsShow">
              <el-option v-for="item in allBank" :key="item.bankId"
                         :label="item.bankName" :value="item.bankId"></el-option>
            </el-select>
          </el-form-item>


          <el-form-item label="题目内容" label-width="120px" prop="questionContent">
            <el-input v-model="updateQuForm.questionContent" :rows="2" style="margin-left: 5px"
                      type="textarea"></el-input>
          </el-form-item>

          <el-form-item label="题目图片" label-width="120px">
            <el-upload
              :before-upload="beforeAvatarUpload"
              :headers="headers"
              :on-preview="uploadPreview"
              :on-remove="handleUpdateRemove"
              :on-success="updateUploadImgSuccess"
              action="http://localhost:8888/common/image/upload"
              list-type="picture"
              name="file">
              <el-button size="small" type="primary">点击上传</el-button>
              <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过10M</div>
            </el-upload>
          </el-form-item>

          <el-form-item label="整题解析" label-width="120px" prop="analysis">
            <el-input v-model="updateQuForm.analysis" :rows="2" style="margin-left: 5px"
                      type="textarea"></el-input>
          </el-form-item>

          <el-button v-if="updateQuForm.questionType!==4" icon="el-icon-plus" plain size="small"
                     style="margin-left: 40px"
                     type="primary" @click="addUpdateAnswer">
            添加
          </el-button>

          <!--存放答案表单的信息-->
          <el-form-item v-if="updateQuForm.questionType !== 4" prop="answer">
            <el-table :data="updateQuForm.answer" border style="width: 96%;margin-left: 40px;margin-top: 10px">

              <el-table-column align="center" label="是否答案" width="80">
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.isTrue" @change="checked=>checkUpdateAnswer(checked,scope.row.id)">答案
                  </el-checkbox>
                </template>
              </el-table-column>

              <el-table-column label="答案图片">
                <template slot-scope="scope">
                  <el-upload id="answerUpload" :before-upload="beforeAvatarUpload"
                             :headers="headers"
                             :limit="1"
                             :on-preview="uploadPreview"
                             :on-remove="handleUpdateAnswerRemove"
                             :on-success="(res) => { return uploadUpdateAnswerImgSuccess(res,scope.row.id)}"
                             action="http://localhhost:8888/common/image/upload"
                             list-type="picture"
                             name="file">
                    <el-button size="small" type="primary">点击上传</el-button>
                  </el-upload>
                </template>

              </el-table-column>

              <el-table-column label="答案内容">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.answer" :rows="2" style="margin-left: 5px"
                            type="textarea"></el-input>
                </template>
              </el-table-column>

              <el-table-column label="答案解析">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.analysis" :rows="2" style="margin-left: 5px"
                            type="textarea"></el-input>
                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="80">
                <template slot-scope="scope">
                  <el-button circle icon="el-icon-delete" type="danger"
                             @click="delUpdateAnswer(scope.row.id)"></el-button>
                </template>
              </el-table-column>

            </el-table>
          </el-form-item>

        </el-form>

      </el-card>

      <div slot="footer" class="dialog-footer">
        <el-button @click="updateQuTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateQuestion">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="addQuTableVisible" center title="新增题目" width="50%" @close="resetAddQuForm">

      <el-card>

        <el-form ref="addQuForm" :model="addQuForm" :rules="addQuFormRules">

          <el-form-item label="题目类型" label-width="120px" prop="questionType">
            <el-select v-model="addQuForm.questionType" placeholder="请选择" @change="addQuForm.answer =  []">
              <el-option
                v-for="item in questionType"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="难度等级" label-width="120px" prop="questionLevel">
            <el-select v-model="addQuForm.questionLevel" placeholder="请选择">
              <el-option :value="parseInt(1)" label="简单"></el-option>
              <el-option :value="parseInt(2)" label="中等"></el-option>
              <el-option :value="parseInt(3)" label="困难"></el-option>
            </el-select>
          </el-form-item>


          <el-form-item label="归属题库" label-width="120px" prop="bankId">
            <el-select v-model="addQuForm.bankId" multiple placeholder="请选择">
              <el-option v-for="item in allBank" :key="item.bankId"
                         :label="item.bankName" :value="item.bankId"></el-option>
            </el-select>
          </el-form-item>


          <el-form-item label="题目内容" label-width="120px" prop="questionContent">
            <el-input v-model="addQuForm.questionContent" :rows="2" style="margin-left: 5px"
                      type="textarea"></el-input>
          </el-form-item>

          <el-form-item label="题目图片" label-width="120px" prop="image">
            <el-upload
              :before-upload="beforeAvatarUpload"
              :headers="headers"
              :on-preview="uploadPreview"
              :on-remove="handleRemove"
              :on-success="uploadImgSuccess"
              action="http://localhost:8888/common/image/upload"
              list-type="picture"
              name="file">
              <el-button size="small" type="primary">点击上传</el-button>
              <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过10M</div>
            </el-upload>
          </el-form-item>

          <el-form-item label="整题解析" label-width="120px" prop="analysis">
            <el-input v-model="addQuForm.analysis" :rows="2" style="margin-left: 5px"
                      type="textarea"></el-input>
          </el-form-item>

          <el-button v-if="addQuForm.questionType!==4" icon="el-icon-plus" plain size="small" style="margin-left: 40px"
                     type="primary" @click="addAnswer">
            添加
          </el-button>

          <!--存放答案表单的信息-->
          <el-form-item v-if="addQuForm.questionType!==4" prop="answer">
            <el-table :data="addQuForm.answer" border style="width: 96%;margin-left: 40px;margin-top: 10px">

              <el-table-column align="center" label="是否答案" width="80">
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.isTrue" @change="checked=>checkAnswer(checked,scope.row.id)">答案
                  </el-checkbox>
                </template>
              </el-table-column>

              <el-table-column label="答案图片">
                <template slot-scope="scope">
                  <el-upload :before-upload="beforeAvatarUpload"
                             :headers="headers"
                             :limit="1"
                             :on-preview="uploadPreview"
                             :on-remove="handleAnswerRemove"
                             :on-success="(res) => { return uploadAnswerImgSuccess(res,scope.row.id)}"
                             action="http://localhost:8888/common/image/upload"
                             list-type="picture"
                             name="file">
                    <el-button size="small" type="primary">点击上传</el-button>
                  </el-upload>
                </template>

              </el-table-column>

              <el-table-column label="答案内容">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.answer" :rows="2" style="margin-left: 5px"
                            type="textarea"></el-input>
                </template>
              </el-table-column>

              <el-table-column label="答案解析">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.analysis" :rows="2" style="margin-left: 5px"
                            type="textarea"></el-input>
                </template>
              </el-table-column>

              <el-table-column align="center" label="操作" width="80">
                <template slot-scope="scope">
                  <el-button circle icon="el-icon-delete" type="danger" @click="delAnswer(scope.row.id)"></el-button>
                </template>
              </el-table-column>

            </el-table>
          </el-form-item>

        </el-form>

      </el-card>

      <div slot="footer" class="dialog-footer">
        <el-button @click="addQuTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="addQuestion">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="addTableVisible" center title="加入题库" width="30%"
               @close="resetAddForm">

      <el-form ref="addForm" :model="addForm" :rules="addFormRules">

        <el-form-item label="题库名称" label-width="120px" prop="bankId">

          <el-select v-model="addForm.bankId" multiple placeholder="请选择题库">
            <el-option v-for="item in allBank" :key="item.bankId"
                       :label="item.bankName" :value="item.bankId"></el-option>
          </el-select>

        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="addTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="addBank">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="removeTableVisible" center title="从题库中移除" width="30%"
               @close="resetRemoveForm">

      <el-form ref="removeForm" :model="removeForm" :rules="removeFormRules">

        <el-form-item label="题库名称" label-width="120px" prop="bankId">

          <el-select v-model="removeForm.bankId" multiple placeholder="请选择题库">

            <el-option v-for="item in allBank" :key="item.bankId"
                       :label="item.bankName" :value="item.bankId"></el-option>
          </el-select>

        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="removeTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="removeBank">确 定</el-button>
      </div>
    </el-dialog>

    <!--图片回显-->
    <el-dialog :visible.sync="backShowImgVisible" @close="backShowImgVisible = false">
      <img :src="backShowImgUrl" alt="" style="width: 100%">
    </el-dialog>

  </el-container>

</template>

<script>
import axios from 'axios'

export default {
  name: 'QuestionTk',
  data() {
    return {
      //查询用户的参数
      queryInfo: {
        //题目类型下拉款所选的内容
        'questionType': '',
        'questionBank': '',
        'questionContent': '',
        'pageNo': 1,
        'pageSize': 10
      },
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
      //题库信息
      allBank: [],
      //题目信息
      questionInfo: [],
      //题目信息表格是否加载
      loading: true,
      //表格被选中的所有行
      selectionTable: [],
      //表格行被选中后的所有操作方式的数据
      optionInfo: [
        {
          'label': '删除该题(谨慎操作)',
          'desc': 'delete'
        },
        {
          'label': '加入其他题库',
          'desc': 'add'
        },
        {
          'label': '题库中移除',
          'desc': 'remove'
        }
      ],
      optionInfo2: [
        {
          'label': '加入其他题库',
          'desc': 'add'
        },
        {
          'label': '题库中移除',
          'desc': 'remove'
        }
      ],
      //表格行被选中后的数据
      operation: '',
      //题目总数
      total: 0,
      //是否显示加入题库对话框
      addTableVisible: false,
      //是否显示移除题库对话框
      removeTableVisible: false,
      //是否显示添加题目的对话框
      addQuTableVisible: false,
      //添加题库的表单信息
      addForm: {
        bankId: ''
      },
      removeForm: {
        bankId: ''
      },
      //添加题库表单的验证
      addFormRules: {
        bankId: [
          {
            required: true,
            message: '请选择需要添加进的题库',
            trigger: 'blur'
          }
        ]
      },
      //移除题库表单的验证
      removeFormRules: {
        bankId: [
          {
            required: true,
            message: '请选择需要移除的题库',
            trigger: 'blur'
          }
        ]
      },
      //添加题目的表单信息
      addQuForm: {
        questionType: 1,
        questionLevel: 1,
        bankId: '',
        questionContent: '',
        images: [],
        analysis: '',
        createPerson: localStorage.getItem('username'),
        //答案对象
        answer: []
      },
      //添加题目表单的验证规则
      addQuFormRules: {
        questionType: [
          {
            required: true,
            message: '请选择问题类型',
            trigger: 'blur'
          }
        ],
        questionLevel: [
          {
            required: true,
            message: '请选择问题难度',
            trigger: 'blur'
          }
        ],
        bankId: [
          {
            required: true,
            message: '请选择题库',
            trigger: 'blur'
          }
        ],
        questionContent: [
          {
            required: true,
            message: '请输入题库内容',
            trigger: 'blur'
          }
        ],
      },
      //更新题目表单的验证规则
      updateQuFormRules: {
        questionType: [
          {
            required: true,
            message: '请选择问题类型',
            trigger: 'blur'
          }
        ],
        questionLevel: [
          {
            required: true,
            message: '请选择问题难度',
            trigger: 'blur'
          }
        ],
        bankId: [
          {
            required: true,
            message: '请选择题库',
            trigger: 'blur'
          }
        ],
        questionContent: [
          {
            required: true,
            message: '请输入题库内容',
            trigger: 'blur'
          }
        ],
      },
      //图片回显的样式
      backShowImgVisible: false,
      //图片回显的图片地址
      backShowImgUrl: '',
      //更新题目的数据信息
      updateQuForm: {
        questionId: '',
        questionType: 1,
        questionLevel: 1,
        bankId: '',
        glyShow: false,
        jsShow: false,
        questionContent: '',
        images: [],
        analysis: '',
        createPerson: localStorage.getItem('username'),
        //答案对象
        answer: []
      },
      //是否显示更新题目的对话框
      updateQuTableVisible: false,
    }
  },
  created() {

    axios.get('/common/checkToken').then((resp) => {
      if (resp.data.code === 200 && resp.data.data.roleId === '3') {//当前用户携带的token信息正确并且是管理员

        this.glyShow = true;
      } else {
        this.jsShow = true;
      }
    })

    let bankName = this.$route.params.bankName
    let bankId = this.$route.params.bankId
    this.queryInfo.bankId = bankId;
    this.getQuestionBankInfo()
    this.getQuestionInfo(bankId)
  },
  methods: {
    //获取所有的题库信息
    getQuestionBankInfo() {
      this.$http.get(this.API.getQuestionBank).then((resp) => {
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
    //题目类型变化
    typeChange(val) {
      this.queryInfo.questionType = val
      this.getQuestionInfo()
    },
    //题库变化
    bankChange(val) {
      this.queryInfo.questionBank = val
      this.getQuestionInfo()
    },
    //题目名字筛选
    contentChange() {
      //发送查询题目总数的请求
      this.getQuestionInfo()
    },
    //获取题目信息
    getQuestionInfo(bankId) {
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
      this.selectionTable = val
    },
    //处理操作选择框的变化
    operationChange(val) {
      //清空上一次的选择
      this.operation = ''

      let questionIds = []
      if (val === 'delete') {
        this.selectionTable.map(item => {
          questionIds.push(item.id)
        })
        //发起删除请求
        this.$http.get(this.API.deleteQuestion, {params: {'questionIds': questionIds.join(',')}}).then(resp => {
          if (resp.data.code === 200) {
            this.$notify({
              title: 'Tips',
              message: '删除成功',
              type: 'success',
              duration: 2000
            })
            this.getQuestionInfo()
          } else {
            this.$notify({
              title: 'Tips',
              message: '删除失败',
              type: 'error',
              duration: 2000
            })
          }
        })
      } else if (val === 'add') {
        this.addTableVisible = true
      } else if (val === 'remove') {
        this.removeTableVisible = true
      }
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
    //表单信息重置
    resetAddForm() {
      //清空表格数据
      this.$refs['addForm'].resetFields()
    },
    resetRemoveForm() {
      //清空表格数据
      this.$refs['removeForm'].resetFields()
    },
    resetAddQuForm() {
      this.$refs['addQuForm'].resetFields()
    },
    //提交加入题库的表单信息
    addBank() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          let questionIds = []
          let banks = this.addForm.bankId
          //将表格选中的数据中的问题id加入进去
          this.selectionTable.map(item => {
            questionIds.push(item.id)
          })
          this.$http.get(this.API.addBankQuestion, {
            params: {
              'questionIds': questionIds.join(','),
              'banks': banks.join(',')
            }
          }).then((resp) => {
            if (resp.data.code === 200) {
              this.getQuestionInfo()
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
          this.$message.error('请选择需要加入进的题库')
          return false
        }
      })
    },
    //提交移除题库的表单信息
    removeBank() {
      this.$refs['removeForm'].validate((valid) => {
        if (valid) {
          let questionIds = []
          let banks = this.removeForm.bankId
          //将表格选中的数据中的问题id加入进去
          this.selectionTable.map(item => {
            questionIds.push(item.id)
          })
          //发起移除请求
          this.$http.get(this.API.removeBankQuestion, {
            params: {
              'questionIds': questionIds.join(','),
              'banks': banks.join(',')
            }
          }).then((resp) => {
            if (resp.data.code === 200) {
              this.getQuestionInfo()
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
            this.removeTableVisible = false
          })
        } else {
          this.$message.error('请选择需要移除的题库')
          return false
        }
      })
    },
    //新增题目上传后 点击图片的回显
    uploadPreview(file) {
      this.backShowImgUrl = file.response.data
      this.backShowImgVisible = true
    },
    //新增题目中的上传图片的移除
    handleRemove(file, fileList) {
      this.addQuForm.images.map((item, index) => {//移除图片在表单中的数据
        if (item === file.response.data) this.addQuForm.images.splice(index, 1)
      })
    },
    //更新题目中的上传图片的移除
    handleUpdateRemove(file, fileList) {
      this.updateQuForm.images.map((item, index) => {//移除图片在表单中的数据
        if (item === file.response.data) this.updateQuForm.images.splice(index, 1)
      })
    },
    //新增题目中的上传图片的格式大小限制
    beforeAvatarUpload(file) {
      const isImg = file.type === 'image/jpeg' ||
        file.type === 'image/png' ||
        file.type === 'image/jpg'
      const isLt = file.size / 1024 / 1024 < 10

      if (!isImg) {
        this.$message.error('上传图片只能是 JPG/PNG 格式!')
      }

      if (!isLt) {
        this.$message.error('上传头像图片大小不能超过 10MB!')
      }
      return isImg && isLt
    },
    //新增题目中的上传图片成功后的钩子函数
    uploadImgSuccess(response, file, fileList) {
      this.addQuForm.images.push(response.data)
    },
    //更新题目中的上传图片成功后的钩子函数
    updateUploadImgSuccess(response, file, fileList) {
      this.updateQuForm.images.push(response.data)
    },
    //新增题目中的新增答案填写框
    addAnswer() {
      this.addQuForm.answer.push({
        id: this.addQuForm.answer.length,
        isTrue: false,
        answer: '',
        images: [],
        analysis: ''
      })
    },
    //更新时新增题目中的新增答案填写框
    addUpdateAnswer() {
      this.updateQuForm.answer.push({
        id: this.updateQuForm.answer.length,
        isTrue: false,
        answer: '',
        images: [],
        analysis: ''
      })
    },
    //新增题目中的删除答案填写框
    delAnswer(id) {//当前答案的id
      this.addQuForm.answer.map((item, index) => {
        if (item.id === id) this.addQuForm.answer.splice(index, 1)
      })
    },
    //新增题目中的删除答案填写框
    delUpdateAnswer(id) {//当前答案的id
      this.updateQuForm.answer.map((item, index) => {
        if (item.id === id) this.updateQuForm.answer.splice(index, 1)
      })
    },
    //答案上传照片了
    uploadAnswerImgSuccess(response, id) {
      this.addQuForm.answer[id].images.push(response.data)
    },
    //更新的答案上传图片了
    uploadUpdateAnswerImgSuccess(response, id) {
      this.updateQuForm.answer[id].images.push(response.data)
    },
    //答案上传成功后删除
    handleAnswerRemove(file) {
      this.addQuForm.answer.images.map((item, index) => {//移除图片在表单中的数据
        if (item === file.response.data) this.addQuForm.images.splice(index, 1)
      })
    },
    //更新的时候答案上传成功后删除
    handleUpdateAnswerRemove(file) {
      this.updateQuForm.answer.images.map((item, index) => {//移除图片在表单中的数据
        if (item === file.response.data) this.updateQuForm.images.splice(index, 1)
      })
    },
    //选择正确答案的按钮变化事件
    checkAnswer(checked, id) {
      if (checked) {
        if (this.addQuForm.questionType === 1 || this.addQuForm.questionType === 3) {//单选或者判断
          //当前已经有一个正确的选择了
          this.addQuForm.answer.map(item => {
            item.isTrue = false
          })
          this.addQuForm.answer.map(item => {
            if (item.id === id) item.isTrue = true
          })
        } else {//多选 可以有多个答案
          this.addQuForm.answer.map(item => {
            if (item.id === id) item.isTrue = true
          })
        }
      } else {
        this.addQuForm.answer.map(item => {
          if (item.id === id) item.isTrue = false
        })
      }
    },
    //更新时选择正确答案的按钮变化事件
    checkUpdateAnswer(checked, id) {
      if (checked) {
        if (this.updateQuForm.questionType === 1 || this.updateQuForm.questionType === 3) {//单选或者判断
          //当前已经有一个正确的选择了
          this.updateQuForm.answer.map(item => {
            item.isTrue = false
          })
          this.updateQuForm.answer.map(item => {
            if (item.id === id) item.isTrue = true
          })
        } else {//多选 可以有多个答案
          this.updateQuForm.answer.map(item => {
            if (item.id === id) item.isTrue = true
          })
        }
      } else {
        this.updateQuForm.answer.map(item => {
          if (item.id === id) item.isTrue = false
        })
      }
    },
    //新增题目
    addQuestion() {
      this.$refs['addQuForm'].validate((valid) => {
        if (valid && this.addQuForm.answer.some(item => item.isTrue) && this.addQuForm.questionType !== 4) {//单选或者多选或者判断
          this.$http.post(this.API.addQuestion, this.addQuForm).then((resp) => {
            if (resp.data.code === 200) {
              this.addQuTableVisible = false
              this.getQuestionInfo()
              this.$notify({
                title: 'Tips',
                message: '新增题目成功',
                type: 'success',
                duration: 2000
              })
            } else {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'success',
                duration: 2000
              })
            }
          })
        } else if (valid && !this.addQuForm.answer.some(item => item.isTrue) && this.addQuForm.questionType !== 4) {//无答案
          this.$message.error('必须有一个答案')
          return false
        } else if (valid && this.addQuForm.questionType === 4) {//简答题 无标准答案直接发请求
          //当是简答题的时候需要清除answer
          this.addQuForm.answer = []
          this.$http.post(this.API.addQuestion, this.addQuForm).then((resp) => {
            if (resp.data.code === 200) {
              this.addQuTableVisible = false
              this.getQuestionInfo()
              this.$notify({
                title: 'Tips',
                message: '新增题目成功',
                type: 'success',
                duration: 2000
              })
            } else {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'success',
                duration: 2000
              })
            }
          })
        } else if (!valid) {
          this.$message.error('请填写必要信息')
          return false
        }
      })
    },
    //更新题目
    updateQu(id) {
      this.$http.get(this.API.getQuestionById + '/' + id).then((resp) => {
        if (resp.data.code === 200) {
          if (resp.data.data.questionType !== 4) {
            resp.data.data.answer.map(item => {
              item.isTrue = item.isTrue === 'true'
            })
          }
          this.updateQuForm = resp.data.data
          //处理图片那个参数是个数组
          if (this.updateQuForm.images === null) this.updateQuForm.images = []

          if (resp.data.data.questionType !== 4) {
            this.updateQuForm.answer.map(item => {
              if (item.images === null) {
                item.images = []
              }
            })
          }

        } else {
          this.$notify({
            title: 'Tips',
            type: 'error',
            message: '获取信息失败'
          })
        }
      })
      this.updateQuTableVisible = true
    },
    //提交更新表单
    updateQuestion() {
      this.$refs['updateQuForm'].validate((valid) => {
        if (valid && this.updateQuForm.questionType !== 4 && this.updateQuForm.answer.some(item => item.isTrue)) {//单选或者多选或者判断
          //保证答案的图片只有一张
          this.updateQuForm.answer.map(item => {
            if (item.images.length > 1) {
              item.images.splice(0, item.images.length - 1)
            }
          })
          this.$http.post(this.API.updateQuestion, this.updateQuForm).then((resp) => {
            if (resp.data.code === 200) {
              this.updateQuTableVisible = false
              this.getQuestionInfo()
              this.$notify({
                title: 'Tips',
                message: '更新题目成功',
                type: 'success',
                duration: 2000
              })
            } else {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'success',
                duration: 2000
              })
            }
          })
        } else if (valid && this.updateQuForm.questionType !== 4 && !this.updateQuForm.answer.some(item => item.isTrue)) {//无答案
          this.$message.error('必须有一个答案')
          return false
        } else if (valid && this.updateQuForm.questionType === 4) {//简答题 无标准答案直接发请求
          //当是简答题的时候需要清除answer
          this.addQuForm.answer = []
          this.$http.post(this.API.updateQuestion, this.updateQuForm).then((resp) => {
            if (resp.data.code === 200) {
              this.updateQuTableVisible = false
              this.getQuestionInfo()
              this.$notify({
                title: 'Tips',
                message: '更新题目成功',
                type: 'success',
                duration: 2000
              })
            } else {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'success',
                duration: 2000
              })
            }
          })
        } else if (!valid) {
          this.$message.error('请填写必要信息')
          return false
        }
      })
    },
  },
  computed: {
    //监测头部信息的token变化
    headers() {
      return {
        authorization: localStorage.getItem('authorization') || ''
      }
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
