<template>
  <el-container>
    <el-main>
      <el-card class="box-card" shadow="always">

        <div>
          <el-form ref="loginForm" :model="loginForm" :rules="loginFormRules" :status-icon="true" label-width="100px">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="账号" prefix-icon="el-icon-user"></el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input v-model="loginForm.password" placeholder="密码" prefix-icon="el-icon-lock"
                        show-password></el-input>
            </el-form-item>

            <el-form-item prop="code">
              <el-input v-model="loginForm.code" class="code" placeholder="验证码"
                        prefix-icon="el-icon-chat-line-round"></el-input>
              <img id="code" alt="验证码" src="http://localhost:8888/util/getCodeImg"
                   style="float: right;margin-top: 4px;cursor: pointer" title="看不清,点击刷新"
                   @click="changeCode"/>
            </el-form-item>

            <el-form-item>
              <el-button icon="el-icon el-icon-s-promotion" type="primary" @click="submitForm('loginForm')">登录
              </el-button>
              <el-button icon="el-icon el-icon-circle-plus" type="warning" @click="toRegisterPage">学员注册</el-button>
            </el-form-item>

          </el-form>
        </div>
      </el-card>
    </el-main>

  </el-container>
</template>

<script>

export default {
  name: 'Login',
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
    return {
      //登录表单数据信息
      loginForm: {
        username: '',
        password: '',
        //验证码
        code: ''
      },
      //登录表单的校验规则
      loginFormRules: {
        username: [
          {
            required: true,
            message: '请输入账号',
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
            message: '密码不能小于6个字符',
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
  mounted() {
    this.changeCode()
    //检验用户是否存在token,存在直接跳转主页
    this.checkToken()
  },
  methods: {
    //表单信息提交
    submitForm() {
      this.$refs['loginForm'].validate((valid) => {
        if (valid) {//验证通过
          const data = new FormData()
          data.append('username', this.loginForm.username)
          data.append('password', this.loginForm.password)
          //发送登录请求
          this.$http.post(this.API.login, data).then((resp) => {
            if (resp.data.code === 200) {
              localStorage.setItem('authorization', resp.data.data)
              this.$notify({
                title: 'Tips',
                message: '登陆成功^_^',
                type: 'success',
                duration: 2000
              })
              this.$router.push('/index')
            } else {//请求出错
              this.changeCode()
              this.getCode()
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'error',
                duration: 2000
              })
            }
          })
        } else {//验证不通过
          if (this.code !== this.loginForm.code) {
            this.$notify({
              title: 'Tips',
              message: '验证码输入有误',
              type: 'error',
              duration: 2000
            })
          }
          return false
        }
      })
    },
    //注册页面跳转
    toRegisterPage() {
      this.$router.push('/register')
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
    //检验token
    async checkToken() {
      if (window.localStorage.getItem('authorization') !== null) {
        const resp = await this.$http.get(this.API.checkToken)
        if (resp.data.code === 200) {//如果token合法自动跳转主页
          await this.$router.push('/index')
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.el-container {
  min-width: 417px;
  height: 100%;
  background: url("../assets/imgs/bg.png");
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

/deep/ .el-input__inner {
  height: 47px;
  background-color: transparent;
}
/*  表单的左侧margin清楚 */
/deep/ .el-form-item__content {
  margin: 0 !important;
}

/deep/ .el-form-item {
  background-color: unset !important;
}

/*  按钮样式 */
.el-button {
  width: 48%;
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
