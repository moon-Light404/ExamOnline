<template>
  <el-container>
    <el-main>

      <el-table
        ref="roleForm"
        v-loading="loading"
        :border="true"
        :data="roleInfo"
        highlight-current-row
        style="width: 100%"
        tooltip-effect="dark">

        <el-table-column label="角色ID" prop="roleId">
        </el-table-column>

        <el-table-column label="角色名称" prop="roleName">
        </el-table-column>
      </el-table>

    </el-main>


  </el-container>
</template>

<script>
export default {
  name: 'RoleManage',
  data() {
    return {
      //角色信息
      roleInfo: [],
      //表格数据加载
      loading: true,
    }
  },
  created() {
    this.getRoleInfo()
  },
  methods: {
    //获取用户角色信息
    getRoleInfo() {
      this.$http.get(this.API.getRoleInfo).then((resp) => {
        if (resp.data.code === 200) {
          this.roleInfo = resp.data.data
          this.loading = false;
        } else {
          this.$notify({
            title: 'Tips',
            type: 'error',
            message: '获取信息失败',
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
