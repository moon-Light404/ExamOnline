let API = {
  //应用表
  api: {
    // 查询应用列表
    register: '/common/register',
    //查询当前的验证码
    getCode: '/util/getCode',
    //校验用户名
    checkUsername: '/common/check',
    //登陆接口
    login: '/common/login',
    //获取主页面的左侧menu数据
    getMenuInfo: '/common/getMenu',
    //检验token合法(返回当前对象)
    checkToken: '/common/checkToken',
    //用户退出登录
    logout: '/common/logout',
    //获取用户信息(可附带查询条件,可分页,n功能合一接口)
    getUserInfo: '/admin/getUser',
    //管理员操作用户
    handleUser: '/admin/handleUser',
    //管理员新增用户
    addUser: '/admin/addUser',
    //获取角色信息
    getRoleInfo: '/admin/getRole',
    //获取所有题库信息
    getQuestionBank: '/teacher/getQuestionBank',
    //获取题目信息(可附带查询条件,可分页,n功能合一接口)
    getQuestion: '/teacher/getQuestion',
    getQuestion2: '/teacher/getQuestion2',
    //批量删除题目
    deleteQuestion: '/teacher/deleteQuestion',
    //将题目加入进题库
    addBankQuestion: '/teacher/addBankQuestion',
    //将题目从题库中移除
    removeBankQuestion: '/teacher/removeBankQuestion',
    //填加题目中的上传图片
    uploadQuestionImage: '/common/image/upload',
    //添加题目
    addQuestion: '/teacher/addQuestion',
    //根据题目id查询题目信息
    getQuestionById: '/teacher/getQuestionById',
    getQuestionByIds: '/teacher/getQuestionByIds',
    //根据题目id更新题目信息
    updateQuestion: '/teacher/updateQuestion',
    //查询所有题库信息和里面含有的题目类型的数量
    getBankHaveQuestionSumByType: '/teacher/getBankHaveQuestionSumByType',
    //删除题库及去除所有包含题库信息的题目
    deleteQuestionBank: '/teacher/deleteQuestionBank',
    //添加题库信息
    addQuestionBank: '/teacher/addQuestionBank',
    //根据题库id查询题库信息
    getBankById: '/teacher/getBankById',
    //根据题库id查询题库中所有单选 多选 判断题
    getQuestionByBank: '/teacher/getQuestionByBank',
    //根据题库id查询各种类型的题目
    getQuestionByBankIdAndType: '/teacher/getQuestionByBankIdAndType',
    //查询考试信息
    getExamInfo: '/teacher/getExamInfo',
    // 查询考试加是否已考信息
    getExamInfo2: 'student/getExamInfo2',
    //对考试信息操作
    operationExam: '/teacher/operationExam',
    //根据题库添加考试
    addExamByBank: '/teacher/addExamByBank',
    //根据题目列表添加考试
    addExamByQuestionList: '/teacher/addExamByQuestionList',
    //根据考试id查询考试信息和题目信息
    getExamInfoById: '/teacher/getExamInfoById',
    //更新考试的规则信息
    updateExamInfo: '/teacher/updateExamInfo',
    //新增考试记录
    addExamRecord: '/teacher/addExamRecord',
    //根据考试记录的id查询考试记录信息
    getExamRecordById: '/teacher/getExamRecordById',
    //根据考试id查询考试中的每一道题目id和分值
    getExamQuestionByExamId: '/teacher/getExamQuestionByExamId',
    //根据考试记录id查询考试的信息
    getExamRecord: '/teacher/getExamRecord',
    //根据考试id单纯的查询考试对象信息
    getExamById: '/teacher/getExamById',
    //教师用户根据用户id查询用户
    getUserById: '/teacher/getUserById',
    //查询所有的考试信息
    allExamInfo: '/teacher/allExamInfo',
    //批阅试卷的客观题
    setObjectQuestionScore: '/teacher/setObjectQuestionScore',
    //查询当前用户的信息
    getCurrentUser: '/common/getCurrentUser',
    //更新当前用户的信息
    updateCurrentUser: '/common/updateCurrentUser',
    //获取我的成绩(根据username)
    getMyGrade: '/student/getMyGrade',
    //获取考试通过率(echarts绘图)
    getExamPassRate: '/teacher/getExamPassRate',
    //获取考试的次数(echarts绘图)
    getExamNumbers: '/teacher/getExamNumbers',
    //获取所有系统公告信息
    getAllNotice: '/admin/getAllNotice',
    //获取最新的系统公告
    getCurrentNewNotice: '/common/getCurrentNewNotice',
    //发布公告
    publishNotice: '/admin/publishNotice',
    //批量删除公告
    deleteNotice: '/admin/deleteNotice',
    //更新公告
    updateNotice: '/admin/updateNotice',
    //获取专业信息
    getMajorInfo: '/admin/getMajor',
    //添加专业信息
    addMajor: '/admin/addMajor',
    //获取所有专业信息
    getMajorBank: '/common/getMajorBank',
    //更改题库名称
    updateBankName: '/common/updateBankName',
    // 重置用户密码
    resetPwd: '/admin/updatePwd',
    // 校验题库
    checkBank: '/common/checkBank',
    //根据用户id和考试id查询考试
    checkExam: 'student/getExamRecordByUser',
  },
}

export default {
  API: API,
}
