<template>
  <el-container>
    <!--用户头部菜单-->
    <el-aside id="aside" width="210px">
      <el-menu :collapse="isCollapse" :default-active="activeMenu" :router="true"
               active-text-color="rgb(64,158,255)"
               background-color="rgb(48,65,86)"
               text-color="rgb(191,203,217)"
               @select="handleSelect">
        <el-menu-item disabled index="/index" style="text-align: center">
          <i class="el-icon-sunny"></i>
          <span slot="title">
            在线考试系统
          </span>
        </el-menu-item>

        <!-- 单独的导航 -->
        <el-menu-item v-if="!menuInfo[0].submenu"
                      index="/dashboard"
                      @click="changeBreadInfo(menuInfo[0].topMenuName,menuInfo[0].topMenuName,menuInfo[0].url)">
          <i :class="menuInfo[0].topIcon"></i>
          <span slot="title">{{ menuInfo[0].topMenuName }}</span>
        </el-menu-item>

        <!--具有子导航的-->
        <el-submenu v-for="(menu,index) in menuInfo" v-if="menu.submenu" :key="index" :index="index+''">
          <template slot="title">
            <i :class="menu.topIcon"></i>
            <span slot="title">{{ menu.topMenuName }}</span>
          </template>

          <!--子导航的分组-->
          <el-menu-item-group>
            <el-menu-item v-for="(sub,index) in menu.submenu" :key="index"
                          :index="sub.url" @click="changeBreadInfo(menu.topMenuName,sub.name,sub.url)">
              <i :class="sub.icon"></i>
              <span slot="title">{{ sub.name }}</span>
            </el-menu-item>
          </el-menu-item-group>

        </el-submenu>
      </el-menu>
    </el-aside>

    <!--右侧的面板-->
    <el-main>

      <el-container>

        <el-header height="100px">
          <el-card class="box-card">
            <div slot="header">
              <!--缩小图标-->
              <el-tooltip class="item" content="缩小侧边栏" effect="dark" placement="top-start">
                <i class="el-icon-s-fold" style="cursor:pointer;font-size: 25px;font-weight: 100"
                   @click="changeIsCollapse"></i>
              </el-tooltip>

              <!--面包屑-->
              <el-breadcrumb style="margin-left: 15px">
                <el-breadcrumb-item>{{ breadInfo.top }}</el-breadcrumb-item>
                <el-breadcrumb-item>{{ breadInfo.sub }}</el-breadcrumb-item>
              </el-breadcrumb>

              <!--右侧的个人信息下拉框-->
              <el-dropdown style="float: right;color: black;cursor:pointer;" trigger="click" @command="handleCommand">
                <!--                <span class="el-dropdown-link">-->
                <!--&lt;!&ndash;                  {{ currentUserInfo.username }}&ndash;&gt;-->
                <!--                </span>-->
                <el-avatar v-if="this.currentUserInfo.avatar" :size="40" :src="this.currentUserInfo.avatar"/>
                <el-avatar v-else="this.currentUserInfo.avatar" :size="40" :src="require('@/assets/imgs/favicon.png')"/>
                <i class="el-icon-caret-bottom"></i>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="personInfo">个人资料</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>


              <!--右侧的放大图标-->
              <el-tooltip content="全屏预览" effect="dark" placement="top-start">
                <i id="full" class="el-icon-full-screen" style="float: right;margin-right:10px;
              margin-bottom:5px;cursor:pointer;font-size: 25px;font-weight: 100" @click="fullShow"></i>
              </el-tooltip>

              <!--右侧的查看公告图标-->
              <el-tooltip content="查看公告" effect="dark" placement="top-start">
                <i class="el-icon-bell" style="float: right;margin-right:10px;
              margin-bottom:5px;cursor:pointer;font-size: 25px;font-weight: 100" @click="showSystemNotice"></i>
              </el-tooltip>
            </div>

            <!--卡片面板的主内容-->
            <div>
              <el-tag v-for="(item,index) in tags" :key="index"
                      :class="item.highlight ? 'active' : ''" :closable="item.name !== '首页'" effect="plain"
                      size="small"
                      type="info" @click="changeHighlightTag(item.name)"
                      @close="handleClose(index)">
                <i v-if="item.highlight" class="el-icon-s-opportunity"
                   style="margin-right: 2px"></i>
                {{ item.name }}
              </el-tag>
            </div>
          </el-card>
        </el-header>

        <el-main style="margin-top: 25px;">
          <router-view :tagInfo="tags" @giveChildAddTag="giveChildAddTag"
                       @giveChildChangeBreakInfo="giveChildChangeBreakInfo" @showSystemNotice="showSystemNotice"
                       @updateTagInfo="updateTagInfo"></router-view>
        </el-main>

      </el-container>

      <el-dialog :visible.sync="updateCurrentUserDialog" center title="个人信息" style="text-align: center">

        <el-form ref="updateUserForm" :model="currentUserInfo2" :rules="updateUserFormRules" >
          <div align="center">
            <el-upload
              :on-success="updateAvatar"
              :show-file-list="false"
              action="http://localhost:8888/common/image/upload"
              style="width: 100px"
            >
              <img v-if="currentUserInfo2.avatar"
                   :src="currentUserInfo2.avatar"
                   class="avatar"/>
              <img v-else
                   class="avatar"
                   src="../assets/imgs/favicon.png"/>
              <!--              <img-->
              <!--                class="avatar"-->
              <!--                src="../assets/imgs/favicon.png"/>-->
            </el-upload>
          </div>

          <el-form-item label="用户名" >
            <el-input v-model="currentUserInfo2.username" disabled></el-input>
          </el-form-item>

          <el-form-item label="实名" prop="trueName" >
            <el-input v-model="currentUserInfo2.trueName" disabled></el-input>
          </el-form-item>

          <el-form-item label="角色" v-if="this.currentUserInfo.roleId==1">
            <el-input v-model="this.student" disabled></el-input>
          </el-form-item>

          <el-form-item label="角色" v-if="this.currentUserInfo.roleId==2">
            <el-input v-model="this.teacher" disabled></el-input>
          </el-form-item>

          <el-form-item label="角色" v-if="this.currentUserInfo.roleId==3">
            <el-input v-model="this.admin" disabled></el-input>
          </el-form-item>

          <el-form-item v-if="this.currentUserInfo.roleId == 1 || this.currentUserInfo.roleId == 2" label="专业">
            <el-input v-model="currentUserInfo2.majorName" disabled></el-input>
          </el-form-item>

          <el-form-item v-if="this.currentUserInfo.roleId == 1" label="班级" >
            <el-input v-model="currentUserInfo2.className" disabled></el-input>
          </el-form-item>

          <el-form-item label="密码" prop="password" >
            <el-input v-model="currentUserInfo2.password" placeholder="不更改请留空"></el-input>
          </el-form-item>
        </el-form>

        <div slot="footer" class="dialog-footer">
          <el-button @click="updateCurrentUserDialog = false">返回</el-button>
          <el-button type="primary" @click="updateCurrentUser">修改密码</el-button>
        </div>
      </el-dialog>

    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'Main',
  data() {
    var validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'))
      } else if (value.length < 6) {
        callback(new Error('新密码少于6位数!'))
      } else {
        callback()
      }
    }
    return {
      student: "学生",
      teacher: "教师",
      admin: "管理员",
      allBank: [],
      //菜单信息
      menuInfo: [
        {
          'topIcon': '',
          'url': '',
          'children': [
            {
              'url': ''
            }
          ]
        }
      ],
      //面板是否收缩
      isCollapse: false,
      //当前是否全屏显示
      isFullScreen: false,
      //当前登录的用户信息
      currentUserInfo: {
        'username': '',
        'avatar': ''
      },
      //当前激活的菜单
      activeMenu: '',
      //面包屑信息
      breadInfo: {
        'top': '首页',//顶级菜单信息
        'sub': '首页'//当前的菜单信息
      },
      //面包屑下的标签数据
      tags: [
        {
          'name': '首页',
          'url': '/dashboard',
          'highlight': true
        }
      ],
      //跟新当前用户的信息的对话框
      updateCurrentUserDialog: false,
      //当前用户的信息
      currentUserInfo2: {},
      //更新信息表单信息
      updateUserFormRules: {
        trueName: [
          {
            required: true,
            message: '请输入真实姓名',
            trigger: 'blur'
          }
        ],
        password: [
          {
            validator: validatePassword,
            trigger: 'blur'
          }
        ]
      }
    }
  },
  created() {
    this.getMenu()
    //获取登录用户信息
    this.getUserInfoByCheckToken()
    this.getMajorBankInfo()
  },
  mounted() {
    //根据当前链接的hash设置对应高亮的菜单
    this.activeMenu = window.location.hash.substring(1)
    document.querySelector('.el-container').style.maxHeight = screen.height + 'px'
    // 根据设备大小调整侧边栏
    if (screen.width <= 1080) {
      this.isCollapse = !this.isCollapse
      document.querySelector('#aside').style.width = 65 + 'px'
      document.querySelector('.el-container').style.minWidth = 1080 + 'px'
    }
  },
  watch: {
    //监察路径变化,改变菜单的高亮
    '$route.path': function (o, n) {
      this.activeMenu = o
      //如果没有该标签就创建改标签
      let flag = false
      //判断是否含有改标签
      this.tags.map(item => {
        if (item.url === this.activeMenu) {//如果有含有该标签
          flag = true
        }
      })
      if (!flag) {//对应链接的标签不存在
        //先找到该标签的名字
        this.createHighlightTag()
      } else {//改标签存在,则高亮
        this.tags.map(item => {
          //取消高亮别的标签
          item.highlight = false
          //高亮当前标签
          if (item.url === this.activeMenu) {
            item.highlight = true
          }
        })
      }
    }
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
    updateAvatar(response) {
      if (response.code === 200) {
        this.currentUserInfo2.avatar = response.data;
        this.$message.success(response.message);
      } else {
        this.$message.error(response.message);
      }
    },
    //查看系统公告
    showSystemNotice() {
      this.$http.get("/common/getCurrentNewNotice").then((resp) => {
        if (resp.data.code === 200) {
          if (resp.data.data !== null) {
            this.$alert(resp.data.data, '最新公告', {
              dangerouslyUseHTMLString: true,
              closeOnPressEscape: true,
              lockScroll: false
            })
          }
        } else {
          this.$notify({
            title: 'Tips',
            message: '公告获取失败',
            type: 'error',
            duration: 2000
          })
        }
      })
    },
    //根据token后台判断用户权限,传递相对应的菜单
    getMenu() {
      this.$http.get(this.API.getMenuInfo).then((resp) => {
        if (resp.data.code === 200) {
          this.menuInfo = JSON.parse(resp.data.data)
          //根据链接创建不存在的tag标签并高亮
          this.createHighlightTag()
        } else {//后台认证失败,跳转登录页面
          this.$message.error('权限认证失败')
          this.$router.push('/')
        }
      })
    },
    //放大缩小侧边栏
    changeIsCollapse() {
      const aside = document.querySelector('#aside')
      if (this.isCollapse) {
        aside.style.width = 210 + 'px'
      } else {
        aside.style.width = 65 + 'px'
      }
      this.isCollapse = !this.isCollapse
    },
    //是否全屏显示
    fullShow() {
      var docElm = document.documentElement
      const full = document.querySelector('#full')
      if (this.isFullScreen) {//退出全屏模式
        //切换图标样式
        full.className = 'el-icon-full-screen'
        //W3C
        if (document.exitFullscreen) {
          document.exitFullscreen()
        }
        //FireFox
        else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen()
        }
        //Chrome等
        else if (document.webkitCancelFullScreen) {
          document.webkitCancelFullScreen()
        }
        //IE11
        else if (document.msExitFullscreen) {
          document.msExitFullscreen()
        }
      } else {//进入全屏模式
        //W3C
        //切换图标样式
        full.className = 'el-icon-switch-button'
        if (docElm.requestFullscreen) {
          docElm.requestFullscreen()
        }
        //FireFox
        else if (docElm.mozRequestFullScreen) {
          docElm.mozRequestFullScreen()
        }
        //Chrome等
        else if (docElm.webkitRequestFullScreen) {
          docElm.webkitRequestFullScreen()
        }
        //IE11
        else if (docElm.msRequestFullscreen) {
          docElm.msRequestFullscreen()
        }
      }
      //改变标志位
      this.isFullScreen = !this.isFullScreen
    },
    //处理右上角下拉菜单的处理事件
    handleCommand(command) {
      if (command === 'logout') {//退出
        this.logout()
      } else if (command === 'personInfo') {
        this.updateCurrentUserDialog = true
        this.$http.get(this.API.getCurrentUser).then((resp) => {
          if (resp.data.code === 200) {
            resp.data.data.password = ''
            this.currentUserInfo2 = resp.data.data
            this.currentUserInfo2.className = resp.data.data.className * 1;
          }
        })
      }
    },
    //退出登录
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
    //检查token获取其中的用户信息
    async getUserInfoByCheckToken() {
      const resp = await this.$http.get(this.API.checkToken)
      this.currentUserInfo = resp.data.data
      localStorage.setItem('username', this.currentUserInfo.username)
      localStorage.setItem('avatar', this.currentUserInfo.avatar)
      localStorage.setItem('roleId', this.currentUserInfo.roleId)
    },
    //关闭tag标签
    handleClose(index) {//当前点击的tag的下标
      if (this.tags[index].highlight) {
        this.tags[index - 1].highlight = true
        //关闭之后,路由调跳转,高亮菜单和标签
        this.$router.push(this.tags[index - 1].url)
        this.handleSelect(this.tags[index - 1].url)
      }
      this.tags.splice(index, 1)
    },
    //菜单的高亮变化
    handleSelect(currentMenu) {
      this.activeMenu = currentMenu
    },
    //处理面包屑信息和面包屑下的标签信息
    changeBreadInfo(curTopMenuName, curMenuName, url) {
      //面包屑信息
      this.breadInfo.top = curTopMenuName
      this.breadInfo.sub = curMenuName
      //标签信息
      let flag = false//当前是否有此菜单信息(防止无限点击,无线生成)
      this.tags.map(item => {
        if (item.name === curMenuName) flag = true
      })
      if (!flag) {//不存在当前点击的菜单
        this.tags.push({
          'name': curMenuName,
          'url': url,
          'highlight': true
        })
      } //高亮菜单tag
      this.changeHighlightTag(curMenuName)
    },
    //处理高亮的tag
    changeHighlightTag(curMenuName) {//当前需要高亮的名字
      let curMenu
      this.tags.map((item, i) => {
        if (item.name === curMenuName) curMenu = item
        item.highlight = item.name === curMenuName
      })
      //调用改变面包屑的方法
      this.changeTopBreakInfo(curMenu.name)
      this.$router.push(curMenu.url)
    },
    //创建当前高亮的tags
    createHighlightTag() {
      //根据链接创建不存在的tag标签并高亮
      let menuName
      this.menuInfo.map(item => {
        if (item.submenu !== undefined) {
          item.submenu.map(subItem => {
            if (subItem.url === this.activeMenu) menuName = subItem.name
          })
        }
      })
      if (menuName !== undefined && this.tags.indexOf(menuName) === -1) {
        this.tags.push({
          'name': menuName,
          'url': this.activeMenu,
          'highlight': true
        })
        //高亮对应的标签
        this.tags.map(item => {
          if (item.url === window.location.hash.substring(1)) this.changeHighlightTag(item.name)
        })
      }
    },
    //改变头部的面包屑信息
    changeTopBreakInfo(subMenuName) {
      let topMenuName
      this.menuInfo.map(item => {
        if (item.submenu !== undefined) {
          item.submenu.map(i2 => {
            if (i2.name === subMenuName) topMenuName = item.topMenuName
          })
        }
      })
      this.breadInfo.top = topMenuName
      this.breadInfo.sub = subMenuName
    },
    //提供给子组件改变面包屑最后的信息
    giveChildChangeBreakInfo(subMenuName, topMenuName) {
      this.breadInfo.sub = subMenuName
      this.breadInfo.top = topMenuName
    },
    //提供给子组件创建tag标签使用
    giveChildAddTag(menuName, url) {
      this.tags.map(item => {
        item.highlight = false
      })
      this.tags.push({
        'name': menuName,
        'url': url,
        'highlight': true
      })
    },
    //提供给子组件修改tag标签使用
    updateTagInfo(menuName, url) {
      this.tags.map((item, index) => {
        item.highlight = false
        if (item.name === menuName) {
          this.tags.splice(index, 1)
        }
      })
      this.tags.push({
        'name': menuName,
        'url': url,
        'highlight': true
      })
    },
    //更新当前用户
    updateCurrentUser() {
      this.$refs['updateUserForm'].validate((valid) => {
        if (valid) {
          this.$http.post(this.API.updateCurrentUser, this.currentUserInfo2).then((resp) => {
            if (resp.data.code === 200) {
              this.$notify({
                title: 'Tips',
                message: resp.data.message,
                type: 'success',
                duration: 2000
              })
              this.logout()
            }
          })
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

  .el-menu {
    height: 100% !important;
  }
}

.el-main, .el-header {
  padding: 0;
}

.el-menu-item:hover {
  background-color: rgb(38, 52, 69) !important;
}

/deep/ .el-submenu__title:hover {
  background-color: rgb(38, 52, 69) !important;
}

/deep/ .el-menu-item-group__title {
  padding: 0 !important;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}

/*  右侧面板根据左侧的宽度变化而变化,侧边栏缩小,右侧面板变大,反之同理*/
#aside {
  transition: width .3s;
}

.el-breadcrumb {
  display: inline-block;
}

/*右上角的个人信息字体*/
.el-dropdown-link {
  font-weight: 600;
  font-size: 18px;
}

.el-tag {
  border: none;
  border-radius: 0;
  box-shadow: 0 0 .5px .5px gray;
  color: black;
  font-weight: 400;
  text-align: center;
  margin-left: 10px;
  cursor: pointer;
}

/deep/ .el-card__body {
  padding: 10px;
}

.el-menu-item.is-disabled {
  opacity: 1;
  cursor: pointer;
  background-color: rgb(38, 52, 69) !important;
}

/*  tag的高亮*/
.active {
  background-color: rgb(66, 185, 131);
  color: white;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar {
  width: 150px;
  height: 150px;
  display: block;
}
</style>
