package com.senior.controller;

import java.util.*;

import com.senior.entity.Exam;
import com.senior.service.impl.ExamServiceImpl;
import com.senior.util.TokenUtils;
import com.senior.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senior.entity.ExamRecord;
import com.senior.entity.User;
import com.senior.service.impl.ExamRecordServiceImpl;
import com.senior.service.impl.NoticeServiceImpl;
import com.senior.service.impl.UserServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/student")
@Slf4j
@Api(tags = "学生权限相关的接口")
public class StudentController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ExamServiceImpl examService;
    @Autowired
    private ExamRecordServiceImpl examRecordService;

    @Autowired
    private NoticeServiceImpl noticeService;

    /**
     * @param username 系统登录用户名
     * @param pageNo 页面大小
     * @param pageSize 页面大小
     * @param examId 考试id
     * @return
     */
    @GetMapping("/getMyGrade")
    @ApiOperation("获取个人成绩(分页 根据考试名查询)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "系统唯一用户名", required = true, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "当前页面数", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "当前页面大小", required = true, dataType = "int",
                    paramType = "query"),
            @ApiImplicitParam(name = "examId", value = "考试唯一id", required = false, dataType = "int",
                    paramType = "query")
    })
    public CommonResult<Object> getMyGrade(String username, Integer pageNo, Integer pageSize,
            @RequestParam(required = false) Integer examId) {
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        // 参数一是当前页，参数二是每页个数
        IPage<ExamRecord> examRecordPage = new Page<>(pageNo, pageSize);
        // 查询条件(可选)
        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getId());
        if (examId != null) {
            wrapper.eq("exam_id", examId);
        }

        IPage<ExamRecord> page = examRecordService.page(examRecordPage, wrapper);
        List<ExamRecord> examRecords = page.getRecords();
        // 创建分页结果集
        Map<Object, Object> result = new HashMap<>();
        result.put("examRecords", examRecords);
        result.put("total", examRecordPage.getTotal());
        return new CommonResult<>(200, "查询成绩成功", result);
    }

    /**
     * @param examQueryVo 考试信息查询vo对象,可以返回当前考试是否考过
     * @return
     */
    @PostMapping("/getExamInfo2")
    @ApiOperation("根据信息查询考试的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examQueryVo", value = "考试信息查询vo对象", required = true, dataType = "examQueryVo",
                    paramType = "body")
    })
    public CommonResult<List<ExamState>> getExamInfo2(@RequestBody ExamQueryVo examQueryVo, HttpServletRequest request) {

        String token = request.getHeader("authorization");
        // 当前用户对象的信息
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));


        log.info("执行了===>TeacherController中的getExamInfo2方法");
        // 参数一是当前页，参数二是每页个数
        IPage<Exam> examIPage = new Page<>(examQueryVo.getPageNo(), examQueryVo.getPageSize());
        // 查询条件(可选)
        QueryWrapper<Exam> wrapper = new QueryWrapper<>();

        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {
            wrapper.like("create_man_id", user.getId() );
        }
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 1 + ""))) {
            wrapper.and(QueryWrapper -> QueryWrapper.eq("major_id", user.getMajorId() ).or().eq("major_id", 0));

            wrapper.and(QueryWrapper -> QueryWrapper.like("class_name", user.getClassName() ).or().like("class_name", "全部" ));

        }
        if (examQueryVo.getClassName()  != null) {

            wrapper.like("class_name", examQueryVo.getClassName() );
        }
        if (examQueryVo.getMajorId()  != null) {
            wrapper.eq("major_id", examQueryVo.getMajorId() );
        }

        if (examQueryVo.getExamType() != null) {
            wrapper.eq("type", examQueryVo.getExamType());
        }
        if (examQueryVo.getExamName() != null) {
            wrapper.like("exam_name", examQueryVo.getExamName());
        }
        if (examQueryVo.getStartTime() != null) {
            wrapper.gt("start_time", examQueryVo.getStartTime());
        }
        if (examQueryVo.getEndTime() != null) {
            wrapper.lt("end_time", examQueryVo.getEndTime());
        }
        // 查询exam数据库表
        IPage<Exam> page = examService.page(examIPage, wrapper);

        List<Exam> exams = page.getRecords();
        List<ExamState> examStates = getRecords(user, exams);
        return new CommonResult<>(200, "查询考试信息成功", examStates);
    }



    public List<ExamState> getRecords(User user, List<Exam> exams) {
        System.out.println("f========================================");
        System.out.println("===========================================");
        System.out.println(exams.size());
        System.out.println("f========================================");
        System.out.println("===========================================");
        List<ExamState> examStates = new ArrayList<>();
        // 查询与当前用户有关的考试记录
        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getId());
        List<ExamRecord> records = examRecordService.list(wrapper); // 与当前用户有关的考试记录
        List<Integer> ExamIds = new ArrayList<>(); // 与用户有关的考试id(exam_id)
        if(records != null) {
            for(int i = 0; i < records.size(); i++) {
                ExamIds.add(records.get(i).getExamId());
            }
        }

        if(exams == null || exams.size() == 0) return examStates;
        // 把Exams复制给
        for(int i = 0; i < exams.size(); i++) {
            ExamState states = new ExamState();
            states.setExamId(exams.get(i).getExamId());
            states.setExamName(exams.get(i).getExamName());
            states.setDuration(exams.get(i).getDuration());
            states.setType(exams.get(i).getType());
            states.setPassword(exams.get(i).getPassword());
            states.setExamDesc(exams.get(i).getExamDesc());
            states.setMajorId(exams.get(i).getMajorId());
            states.setMajorName(exams.get(i).getMajorName());
            states.setClassName(exams.get(i).getClassName());
            states.setCreateMan(exams.get(i).getCreateMan());
            states.setCreateManId(exams.get(i).getCreateManId());
            states.setStartTime(exams.get(i).getStartTime());
            states.setEndTime(exams.get(i).getEndTime());
            states.setTotalScore(exams.get(i).getTotalScore());
            states.setPassScore(exams.get(i).getPassScore());
            if(ExamIds.contains(exams.get(i).getExamId())) { // 这个考试是考过的
                states.setFlag(true);
            }else   states.setFlag(false);
            examStates.add(states);
        }
        return examStates;
    }

}
