<template>
  <el-container>
    <el-main>
      <el-card class="box-card" shadow="always">
        <div slot="header" class="card-header">
          <p>在线考试系统</p>
        </div>

        <div>
          <el-form ref="registerForm" :model="registerForm" :rules="registerFormRules" :status-icon="true"
                   label-width="100px">
            <el-form-item prop="username">
              <el-input v-model="registerForm.username" placeholder="账号" prefix-icon="el-icon-user"></el-input>
            </el-form-item>

            <el-form-item prop="trueName">
              <el-input v-model="registerForm.trueName" placeholder="姓名" prefix-icon="el-icon-s-check"></el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input v-model="registerForm.password" placeholder="密码" prefix-icon="el-icon-lock"
                        show-password></el-input>
            </el-form-item>


            <el-form-item prop="majorId">
              <el-select v-model="registerForm.majorId"  placeholder="请选择专业">
                <el-option v-for="item in allBank" :key="item.id"
                           :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>


            <el-form-item prop="className">
              <el-select v-model="registerForm.className" placeholder="请选择班级">
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

            <el-form-item prop="code">
              <el-input v-model="registerForm.code" class="code" placeholder="验证码"
                        prefix-icon="el-icon-chat-line-round"></el-input>
              <img id="code" alt="验证码" src="http://localhost:8888/util/getCodeImg"
                   style="float: right;margin-top: 4px;cursor: pointer" title="看不清,点击刷新"
                   @click="changeCode"/>
            </el-form-item>

            <el-form-item>
              <el-button icon="el-icon el-icon-circle-plus" type="warning" @click="submitForm('registerForm')">注册
              </el-button>
              <el-button icon="el-icon el-icon-s-promotion" @click="toLoginPage">去登陆</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>

export default {
  name: 'Register',
  data() {
    //自定义验证码校验规则
    var validateCode = (rule, value, callback) => {
      //验证码不区分大小写
      if (value.toString().toLocaleLowerCase() !== this.code.toString().toLocaleLowerCase()) {
        callback(new Error('验证码输入错误'))
      } else {
        callback()
      }
    }

    //自定义用户名校验规则
    var validateUsername = (rule, value, callback) => {
      this.$http.get(this.API.checkUsername + '/' + this.registerForm.username).then((resp) => {
        if (resp.data.code === 200) {
          callback()
        } else {
          callback(new Error('用户名已存在'))
        }
      })
    }
    return {
      //登录表单数据信息
      registerForm: {
        majorId: '',
        className: '',
        username: '',
        trueName: '',
        password: '',
        code: ''
      },
      //登录表单的校验规则
      registerFormRules: {
        username: [
          {
            required: true,
            message: '请输入账号',
            trigger: 'blur'
          },
          {
            validator: validateUsername,
            trigger: 'blur'
          }
        ],
        trueName: [
          {
            required: true,
            message: '请输入您的姓名',
            trigger: 'blur'
          },
        ],
        password: [
          {
            required: true,
            message: '请输入密码',
            trigger: 'blur'
          },
          {
            min: 6,
            message: '密码不能小于5个字符',
            trigger: 'blur'
          }
        ],
        code: [
          {
            required: true,
            message: '请输入验证码',
            trigger: 'blur'
          },
          {
            validator: validateCode,
            trigger: 'blur'
          }
        ],
      },
      //后台的验证码
      code: window.onload = () => this.getCode(),
    }
  },
  created() {
    this.getMajorBankInfo()
  },
  mounted() {
    this.changeCode()
  },
  methods: {
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
    //表单信息提交
    submitForm() {
      this.$refs['registerForm'].validate((valid) => {
        if (valid) {//验证通过
          this.$http.post(this.API.register, this.registerForm).then((resp) => {
            if (resp.data.code === 200) {
              localStorage.setItem('authorization', resp.data.data)
              this.$router.push('/index')
            } else {//请求出错
              this.$notify({
                title: 'Tips',
                message: '用户注册失败,请稍后重试',
                type: 'error',
                duration: 2000
              });
            }
          })
        } else {//验证失败
          this.$notify({
            title: 'Tips',
            message: '请检查所填写信息是否正确',
            type: 'error',
            duration: 2000
          });
          return false
        }
      })
    },
    //注册页面跳转
    toLoginPage() {
      this.$router.push('/')
    },
    //点击图片刷新验证码
    changeCode() {
      const code = document.querySelector('#code')
      code.src = 'http://localhost:8888/util/getCodeImg?id=' + Math.random()
      code.onload = () => this.getCode()
    },
    //获取后台验证码
    getCode() {
      this.$http.get(this.API.getCode).then((resp) => {
        this.code = resp.data.message
      })
    },
  }
}
</script>

<style lang="scss" scoped>
.el-container {
  height: 100%;
  min-width: 417px;
  background: url("../assets/imgs/bg.png");
  -moz-background-size: 100% 100%;
  background-size: 100% 100%;
}

a {
  text-decoration: none;
  color: blueviolet;
}

/*  card样式  */
.box-card {
  width: 450px;
}

.el-card {
  position: absolute;
  top: 45%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%);
  border-radius: 15px;
}

.card-header {
  text-align: center;

  p {
    font-size: 20px;
  }
}

/*  表单的左侧margin清楚 */
/deep/ .el-form-item__content {
  margin: 0 !important;
}

/*  按钮样式 */
.el-button:first-child {
  width: 60%;
}

.el-button:nth-child(2) {
  width: 37%;
}

/*  按钮前的小图标样式更改*/
/deep/ .el-icon {
  margin-right: 3px;
}

/*  验证码的输入框*/
.code {
  width: 72%;
}
</style>
