# ExamOnline
## 基于SpringBoot、Vue、Redis的在线考试系统
### 主要性能
1、本项目采用了 Springboot + vue2.0 前后端分离模式设计，后端提供获取和显 示数据库数据的接口，前端采用 Vue 框架 Element-UI 前端组件库布局，使用 http 和 axios 向后端发送数据请求获得数据集，然后在前端的组件内部进行展示，前 后端分离的项目模式程序代码层次分明，便于维护和修改。同时还用到了 Echarts 图表工具对考试结果进行展示。

2、因为本项目已经部署到服务器上，为了提升服务器性能，使用了 Redis 保存 已经访问过的信息，使请求的数据信息得以持久化，提升高并发性能和访问速度。 

3、在用户登录验证方面，采用了自生成 token 令牌，每次用户发送请求时都需 要检验该用户的 token 是否与当前登录用户匹配且是否超时等，提升系统安全性能。

4、在用户信息安全方面，采取非明文数据存储数据库的方式，使用的是 MD5 信息摘要算法将密码转成完全不同的 128 位信息摘要。这样即使数据库信息泄露，用户的密码也是安全的。

5、接口设计采用 restful 风格，接口文档基于 swagger； 6、基于 slf4j 和 Log4j2 实现系统运行日志采集，基于切面实现系统操作日志采集


### 功能模块
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808180456.png)

### 后端项目模块
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808180537.png)

### 前端项目模块
![image](https://user-images.githubusercontent.com/78211709/183393860-5287869c-51d9-44b9-9afe-b0a326764b0e.png)

### 整体调用关系
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808180632.png)

### 部分界面设计
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181348.png)
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181420.png)
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181453.png)
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181511.png)
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181533.png)
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181550.png)
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181605.png)
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181632.png)
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181657.png)
在线考试页面：
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181722.png)
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181738.png)
成绩可视化
![](https://cdn.jsdelivr.net/gh/moon-Light404/my_picgo@master/img/20220808181802.png)
