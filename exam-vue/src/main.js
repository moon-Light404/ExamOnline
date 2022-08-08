import Vue from 'vue'
import App from './App.vue'
import router from './router'
import './plugins/element.js'
import axios from 'axios'
import api from './api/api'
// 引入echarts
import echarts from 'echarts'
//解决路径跳转的报错
import Router from 'vue-router'
import ElementUI from 'element-ui'

Vue.prototype.$echarts = echarts
Vue.use(ElementUI)


//配置请求根路径
axios.defaults.baseURL = 'http://localhost:8888/'
//axios拦截器拦截每一个请求,有token就配置头信息的token
axios.interceptors.request.use(config => {
  let token = window.localStorage.getItem('authorization')
  if (token) {  // 判断是否存在token，如果存在的话，则每个http header都加上token
    config.headers.authorization = token
  }
  return config
}, error => {
  return Promise.reject(error)
})

//接口地址统一管理
Vue.prototype.API = api.API.api
/**
 * 原型链挂载
 * @type {AxiosStatic}
 */
Vue.prototype.$http = axios
Vue.config.productionTip = false

//全局过滤器(秒数转化为分钟)
Vue.filter('timeFormat', function (time) {
  //分钟
  var minute = time / 60;
  var minutes = parseInt(minute);

  if (minutes < 10) {
    minutes = "0" + minutes;
  }

  //秒
  var second = time % 60;
  var seconds = Math.round(second);
  if (seconds < 10) {
    seconds = "0" + seconds;
  }
  return `${minutes}:${seconds}`;
})

const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
