<template>
  <el-scrollbar :native="false" style="height: 100%">
    <div>
      <img height="120%" src="../assets/imgs/wellcome.5bb22a17.jpg" width="100%">
    </div>

  </el-scrollbar>
</template>

<script>
export default {
  name: 'Dashboard',
  created() {
    // 调用父组件Main的展示系统公告方法
    this.$emit('showSystemNotice')
  },
}
</script>

<style lang="scss" scoped>
div {
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

.title {
  text-align: center;
  font-size: 25px;
}


p {
  text-indent: 2em;
}

a {
  text-decoration: none
}
</style>
