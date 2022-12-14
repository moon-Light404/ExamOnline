package com.senior.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;

import com.senior.service.impl.*;
import com.senior.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.senior.constant.ExamConstant;
import com.senior.entity.Answer;
import com.senior.entity.Exam;
import com.senior.entity.ExamQuestion;
import com.senior.entity.ExamRecord;
import com.senior.entity.Question;
import com.senior.entity.QuestionBank;
import com.senior.entity.User;
import com.senior.util.RedisUtil;
import com.senior.util.SaltEncryption;
import com.senior.util.TokenUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/teacher")
@Slf4j
@Api(tags = "???????????????????????????")
public class TeacherController {

    // jackson
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private ExamServiceImpl examService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private QuestionServiceImpl questionService;
    @Autowired
    private ExamQuestionServiceImpl examQuestionService;
    @Autowired
    private ExamRecordServiceImpl examRecordService;
    @Autowired
    private QuestionBankServiceImpl questionBankService;
    @Autowired
    private AnswerServiceImpl answerService;
    // ???????????????redis?????????
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MajorServiceImpl majorService;
    @GetMapping("/getQuestionBank")
    @ApiOperation("????????????????????????")
    public CommonResult<Object> getQuestionBank(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        QueryWrapper queryWrapper = new QueryWrapper<Question>();
        // ????????????
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 3 + ""))) {

        }

        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {
            queryWrapper.eq("major_name",user.getMajorName());
        }
        log.info("?????????===>TeacherController??????getQuestionBank??????");
        List<QuestionBank> questionBanks = questionBankService.list(queryWrapper);
        // ????????????????????????(10??????) + ??????????????????(0-5?????? ) ??????????????????????????????
        return new CommonResult<>(200, "success", questionBanks);
    }

    /**
     * @param questionType ????????????
     * @param questionBank ??????????????????
     * @param questionContent ????????????
     * @param pageNo ?????????
     * @param pageSize ????????????
     * @return
     */
    @GetMapping("/getQuestion") // ?????????????????????????????????????????????????????????
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionType", value = "????????????", required = false, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "questionBank", value = "??????????????????", required = false, dataType = "Integer",
                    paramType = "query"),
            @ApiImplicitParam(name = "questionContent", value = "????????????", required = false, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "?????????", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", required = true, dataType = "int", paramType = "query")
    })
    @ApiOperation("??????????????????,????????? ----> ????????????(??????)(questionType,questionBank,questionContent),????????????(pageNo,pageSize)")
    public CommonResult<Object> getQuestion(@RequestParam(required = false) String questionType,
            @RequestParam(required = false) String questionBank,
            @RequestParam(required = false) String questionContent,HttpServletRequest request,
            Integer pageNo, Integer pageSize) {

        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));

        log.info("?????????===>TeacherController??????getQuestion??????");
        // ????????????????????????????????????????????????
        IPage<Question> questionPage = new Page<>(pageNo, pageSize);
        // ????????????(??????)
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        // ????????????????????????????????????
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {

        int majorId =  user.getMajorId();
        QueryWrapper queryWrapper2 =  new QueryWrapper<Question>();
        queryWrapper2.ne("major_id",majorId);
        List<QuestionBank> questionBanks = questionBankService.list(queryWrapper2);
        if(questionBanks !=null){
            for (int i = 0; i < questionBanks.size(); i++) {
                System.out.println(questionBanks.get(i).getBankName());
                wrapper.notLike("qu_bank_name", questionBanks.get(i).getBankName());
            }
        }
    }


        if (!Objects.equals(questionType, "")) {
            wrapper.eq("qu_type", questionType);
        }
        if (!Objects.equals(questionBank, "")) {
            wrapper.like("qu_bank_id", questionBank);
        }
        if (!Objects.equals(questionContent, "")) {
            wrapper.like("qu_content", questionContent);
        }

        questionPage = questionService.page(questionPage, wrapper);
        List<Question> questions = questionPage.getRecords();
        // ?????????????????????
        Map<Object, Object> result = new HashMap<>();
        result.put("questions", questions);
        result.put("total", questionPage.getTotal());
        return new CommonResult<>(200, "success", result);
    }



    @GetMapping("/getQuestion2")  // ????????????????????????????????????
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionType", value = "????????????", required = false, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "bankId", value = "??????????????????Id", required = false, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "questionContent", value = "????????????", required = false, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "?????????", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", required = true, dataType = "int", paramType = "query")
    })
    @ApiOperation("??????????????????,????????? ----> ????????????(??????)(questionType,questionBank,questionContent),????????????(pageNo,pageSize)")
    public CommonResult<Object> getQuestion2(@RequestParam(required = false) String questionType,
                                            @RequestParam(required = false) String bankId,
                                            @RequestParam(required = false) String questionContent,HttpServletRequest request,
                                            Integer pageNo, Integer pageSize) {

        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        // user.getMajorId() && user.roleId == 2 ??????????????????????????????
        List<Integer> BankIds = new ArrayList<>(); // ????????????????????????????????????
        if(user.getRoleId() == 2) {
            List<QuestionBank> questionBanks = questionBankService.list(new QueryWrapper<QuestionBank>().eq("major_id", user.getMajorId()));
            if(questionBanks != null) {
                for(QuestionBank q : questionBanks) {
                    BankIds.add(q.getBankId());
                }
            }
        }

        log.info("?????????===>TeacherController??????getQuestion2??????");
        // ????????????????????????????????????????????????
        IPage<Question> questionPage = new Page<>(pageNo, pageSize);
        // ????????????(??????)
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        // ?????????????????????
        if(BankIds != null && BankIds.size() != 0) {
            wrapper.and(w -> {
                for(int i = 0;i < BankIds.size(); i++) {
                    if(i == BankIds.size() - 1) {
                        w.like("qu_bank_id", BankIds.get(i));
                    }
                    w.like("qu_bank_id", BankIds.get(i)).or();
                }
                return w;
            });
        }

        if (!Objects.equals(questionType, "")) {
            wrapper.eq("qu_type", questionType);
        }
        if (!Objects.equals(bankId, "")) {
            wrapper.like("qu_bank_id", bankId);
        }
        if (!Objects.equals(questionContent, "")) {
            wrapper.like("qu_content", questionContent);
        }

        questionPage = questionService.page(questionPage, wrapper);
        List<Question> questions = questionPage.getRecords();
        // ?????????????????????
        Map<Object, Object> result = new HashMap<>();
        result.put("questions", questions);
        result.put("total", questionPage.getTotal());
        return new CommonResult<>(200, "success", result);
    }

    /**
     * @param questionIds ?????????????????????id????????????,????????????
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/deleteQuestion")
    @ApiOperation("??????id????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionIds", value = "??????id???????????????????????????", required = true, dataType = "string",
                    paramType = "query")
    })
    @Transactional
    public CommonResult<String> deleteQuestion(String questionIds) throws InterruptedException {
        log.info("?????????===>TeacherController??????deleteQuestion??????");
        String[] ids = questionIds.split(",");
        Map<String, Object> map = new HashMap<>();
        for (String id : ids) {
            map.clear();
            map.put("question_id", id);
            // 1. ??????????????????????????????
            questionService.removeById(Integer.parseInt(id));
            // 2. ?????????????????????????????????id?????????
            answerService.removeByMap(map);
            // 2. ??????redis??????
            redisUtil.del("questionVo:" + id);
        }
        // ?????????????????????
        redisUtil.del("questionBanks");
        return new CommonResult<>(200, "????????????");
    }

    /**
     * @param questionIds ??????id?????????,????????????
     * @param banks ??????id?????????,????????????
     * @return
     */
    @GetMapping("/addBankQuestion")
    @ApiOperation("?????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionIds", value = "??????id???????????????????????????", required = true, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "banks", value = "??????id???????????????????????????", required = true, dataType = "string",
                    paramType = "query"),
    })
    public CommonResult<String> addBankQuestion(String questionIds, String banks) {
        log.info("?????????===>TeacherController??????addBankQuestion??????");
        boolean flag = false;
        // ?????????????????????
        String[] quIds = questionIds.split(",");
        // ?????????????????????id
        String[] bankIds = banks.split(",");

        // ??????????????????????????????????????????
        for (String quId : quIds) {
            // ?????????????????????
            Question question = questionService.getById(Integer.parseInt(quId));
            String quBankId = question.getQuBankId();
            // ????????????????????????id
            String[] qid = quBankId.split(",");
            // ????????????????????????id
            Set<Integer> allId = new HashSet<>();
            if (!quBankId.equals("")) {// ????????????????????????
                for (String s : qid) {
                    allId.add(Integer.parseInt(s));
                }
            }
            // ??????????????????id??????
            for (String bankId : bankIds) {
                allId.add(Integer.parseInt(bankId));
            }
            // ????????????id????????? ??????(1,2,3)
            String handleHaveBankIds = allId.toString().replaceAll(" ", "");
            handleHaveBankIds = handleHaveBankIds.substring(1, handleHaveBankIds.length() - 1);
            // ???????????????????????????id???
            question.setQuBankId(handleHaveBankIds);

            // ?????????????????????set????????????,?????????????????????????????????
            StringBuilder bankNames = new StringBuilder();
            for (Integer id : allId) {
                bankNames.append(questionBankService.getById(id).getBankName()).append(",");
            }
            // ???????????????????????????
            question.setQuBankName(bankNames.toString().substring(0, bankNames.toString().length() - 1));
            // ??????????????????
            flag = questionService.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
        }
        return flag ? new CommonResult<>(200, "??????????????????") : new CommonResult<>(233, "??????????????????");
    }

    /**
     * @param questionIds ??????id?????????,????????????
     * @param banks ??????id?????????,????????????
     * @return
     */
    @GetMapping("/removeBankQuestion")
    @ApiOperation("????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionIds", value = "??????id???????????????????????????", required = true, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "banks", value = "??????id???????????????????????????", required = true, dataType = "string",
                    paramType = "query"),
    })
    @Transactional
    public CommonResult<String> removeBankQuestion(String questionIds, String banks) {
        log.info("?????????===>TeacherController??????removeBankQuestion??????");
        boolean flag = false;
        // ?????????????????????
        String[] quIds = questionIds.split(",");
        // ?????????????????????id
        String[] bankIds = banks.split(",");
        // ?????????????????????????????????
        for (String quId : quIds) {
            Question question = questionService.getById(Integer.parseInt(quId));
            String quBankId = question.getQuBankId();
            // ???????????????????????????id
            String[] curHaveId = quBankId.split(",");
            // ??????????????????id
            Set<Integer> handleId = new HashSet<>();
            if (!quBankId.equals("")) {
                for (String s : curHaveId) {
                    handleId.add(Integer.parseInt(s));
                }
            }
            // ????????????set????????????????????????????????????id
            for (String bankId : bankIds) {
                handleId.remove(Integer.parseInt(bankId));
            }
            // ????????????id????????? ??????(1,2,3)
            String handleHaveBankIds = handleId.toString().replaceAll(" ", "");
            handleHaveBankIds = handleHaveBankIds.substring(1, handleHaveBankIds.length() - 1);
            // ???????????????????????????id???
            question.setQuBankId(handleHaveBankIds);

            if (!handleHaveBankIds.equals("")) {// ?????????????????????????????????
                // ?????????????????????set????????????,?????????????????????????????????
                StringBuilder bankNames = new StringBuilder();
                for (Integer id : handleId) {
                    bankNames.append(questionBankService.getById(id).getBankName()).append(",");
                }
                // ???????????????????????????
                question.setQuBankName(bankNames.toString().substring(0, bankNames.toString().length() - 1));
            } else {// ???????????????
                question.setQuBankName("");
            }
            // ??????????????????
            flag = questionService.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
        }
        return flag ? new CommonResult<>(200, "??????????????????") : new CommonResult<>(233, "??????????????????");
    }

    /**
     * @param questionVo ??????Vo??????
     * @return
     */
    @PostMapping("/addQuestion")
    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionVo", value = "?????????vo????????????", required = true, dataType = "questionVo",
                    paramType = "body"),
    })
    public CommonResult<String> addQuestion(@RequestBody QuestionVo questionVo, HttpServletRequest request) {
        log.info("?????????===>TeacherController??????addQuestion??????");

        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));


        // ?????????????????????,????????????????????????????????????id???
        // List<Question> qus = questionService.list(new QueryWrapper<>());
        // Integer currentQuId = qus.get(qus.size() - 1).getId() + 1;
        Question question = new Question();
        question.setCreateMan(user.getTrueName());
        // ??????????????????
        question.setQuType(questionVo.getQuestionType());
        // question.setId(currentQuId);
        question.setCreateTime(new Date());
        question.setLevel(questionVo.getQuestionLevel());
        question.setAnalysis(questionVo.getAnalysis());
        question.setQuContent(questionVo.getQuestionContent());
        question.setCreatePerson(questionVo.getCreatePerson());
        // ??????????????????
        String bankIds = Arrays.toString(questionVo.getBankId());
        question.setQuBankId(bankIds.substring(1, bankIds.length() - 1).replaceAll(" ", ""));
        // ??????????????????
        if (ArrayUtils.isNotEmpty(questionVo.getImages())) {
            String images = String.join(ExamConstant.SEPARATOR, questionVo.getImages());
            question.setImage(images);
        }
        StringBuilder bankNames = new StringBuilder();
        for (Integer integer : questionVo.getBankId()) {
            bankNames.append(questionBankService.getById(integer).getBankName()).append(",");
        }
        String names = bankNames.toString();
        names = names.substring(0, names.length() - 1);
        question.setQuBankName(names);

        questionService.save(question);
        // ??????????????????
        StringBuilder multipleChoice = new StringBuilder();
        if (questionVo.getQuestionType() != 4) {// ???????????????
            Answer answer = new Answer();
            answer.setQuestionId(question.getId());
            StringBuilder imgs = new StringBuilder();
            StringBuilder answers = new StringBuilder();
            for (int i = 0; i < questionVo.getAnswer().length; i++) {
                if (questionVo.getAnswer()[i].getImages().length > 0) {// ????????????????????????????????????
                    imgs.append(questionVo.getAnswer()[i].getImages()[0]).append(ExamConstant.SEPARATOR);
                }
                answers.append(questionVo.getAnswer()[i].getAnswer()).append(ExamConstant.SEPARATOR);
                // ??????????????????????????????
                if (questionVo.getQuestionType() == 2) {// ??????
                    if (questionVo.getAnswer()[i].getIsTrue().equals("true")) {
                        multipleChoice.append(i).append(ExamConstant.SEPARATOR);
                    }
                } else {// ??????????????? ????????????????????????
                    if (questionVo.getAnswer()[i].getIsTrue().equals("true")) {
                        answer.setTrueOption(i + "");
                        answer.setAnalysis(questionVo.getAnswer()[i].getAnalysis());
                    }
                }
            }
            if (questionVo.getQuestionType() == 2) {
                answer.setTrueOption(multipleChoice.toString().substring(0, multipleChoice.toString().length() - ExamConstant.SEPARATOR.length()));
            }
            String handleImgs = imgs.toString();
            String handleAnswers = answers.toString();
            if (handleImgs.length() != 0) {
                handleImgs = handleImgs.substring(0, handleImgs.length() - ExamConstant.SEPARATOR.length());
            }
            if (handleAnswers.length() != 0) {
                handleAnswers = handleAnswers.substring(0, handleAnswers.length() - ExamConstant.SEPARATOR.length());
            }

            // ?????????????????????
            answer.setImages(handleImgs);
            // ?????????????????????
            answer.setAllOption(handleAnswers);
            answerService.save(answer);
            // ?????????????????????
            redisUtil.del("questionBanks");
        }
        return new CommonResult<>(200, "??????????????????");
    }

    /**
     * @param ids ??????id
     * @return
     */
    @GetMapping("/getQuestionByIds")
    @ApiOperation("??????id??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "??????id", required = true, dataType = "int", paramType = "path"),
    })
    public CommonResult<Object> getQuestionByIds(String ids) {
        log.info("?????????===>TeacherController??????getQuestionByIds??????");
        String[] idList = ids.split(",");
        if (idList.length == 0) {
            return new CommonResult<>(200, "????????????", Lists.newArrayList());
        }
        List<QuestionVo> questionVos = Lists.newArrayList();
        for (String idStr : idList) {
            Integer id = Integer.parseInt(idStr);
            QuestionVo questionVo = getById(id);
            questionVos.add(questionVo);
        }
        questionVos = questionVos.stream().sorted(Comparator.comparingInt(QuestionVo::getQuestionType))
                .collect(Collectors.toList());
        return new CommonResult<>(200, "????????????", questionVos);
    }

    @GetMapping("/getQuestionById/{id}")
    @ApiOperation("??????id??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "??????id", required = true, dataType = "int", paramType = "path"),
    })
    public CommonResult<Object> getQuestionById(@PathVariable("id") Integer id) {
        log.info("?????????===>TeacherController??????getQuestionById??????");
        QuestionVo questionVo = getById(id);
        return new CommonResult<>(200, "????????????", questionVo);
    }

    private QuestionVo getById(Integer id) {
        Question question = questionService.getById(id);
        Answer answer = answerService.getOne(new QueryWrapper<Answer>().eq("question_id", id));
        QuestionVo questionVo = new QuestionVo();
        // ????????????
        questionVo.setQuestionContent(question.getQuContent());
        questionVo.setAnalysis(question.getAnalysis());
        questionVo.setQuestionType(question.getQuType());
        questionVo.setQuestionLevel(question.getLevel());
        questionVo.setQuestionId(question.getId());
        if (StringUtils.isNotBlank(question.getImage())) {
            questionVo.setImages(StringUtils.splitByWholeSeparator(question.getImage(), ExamConstant.SEPARATOR));
        }
        questionVo.setCreatePerson(question.getCreatePerson());
        // ??????????????????
        if (!Objects.equals(question.getQuBankId(), "")) {
            String[] bids = question.getQuBankId().split(",");
            Integer[] bankIds = new Integer[bids.length];
            for (int i = 0; i < bids.length; i++) {
                bankIds[i] = Integer.parseInt(bids[i]);
            }
            questionVo.setBankId(bankIds);
        }
        if (answer != null) {
            if (question.getQuType() != 2) {
                String[] allOption = StringUtils.splitByWholeSeparator(answer.getAllOption(),
                        ExamConstant.SEPARATOR);
                String[] imgs = StringUtils.splitByWholeSeparator(answer.getImages(), ExamConstant.SEPARATOR);
                QuestionVo.Answer[] qa = new QuestionVo.Answer[allOption.length];
                for (int i = 0; i < allOption.length; i++) {
                    QuestionVo.Answer answer1 = new QuestionVo.Answer();
                    answer1.setId(i);
                    answer1.setAnswer(allOption[i]);
                    if (i <= imgs.length - 1 && !Objects.equals(imgs[i], "")) {
                        answer1.setImages(new String[] {imgs[i]});
                    }
                    if (i == Integer.parseInt(answer.getTrueOption())) {
                        answer1.setIsTrue("true");
                        answer1.setAnalysis(answer.getAnalysis());
                    }
                    qa[i] = answer1;
                }
                questionVo.setAnswer(qa);
            } else {// ??????
                String[] allOption = StringUtils.splitByWholeSeparator(answer.getAllOption(),
                        ExamConstant.SEPARATOR);
                String[] imgs = StringUtils.splitByWholeSeparator(answer.getImages(),
                        ExamConstant.SEPARATOR);
                QuestionVo.Answer[] qa = new QuestionVo.Answer[allOption.length];
                for (int i = 0; i < allOption.length; i++) {
                    QuestionVo.Answer answer1 = new QuestionVo.Answer();
                    answer1.setId(i);
                    answer1.setAnswer(allOption[i]);
                    answer1.setImages(imgs);
                    if (i < StringUtils.splitByWholeSeparator(answer.getTrueOption(), ExamConstant.SEPARATOR).length
                            && i == Integer
                                    .parseInt(StringUtils.splitByWholeSeparator(answer.getTrueOption(),
                                            ExamConstant.SEPARATOR)[i])) {
                        answer1.setIsTrue("true");
                        answer1.setAnalysis(answer.getAnalysis());
                    }
                    qa[i] = answer1;
                }
                questionVo.setAnswer(qa);
            }
        }
        return questionVo;
    }

    /**
     * @param questionVo ??????vo??????
     * @return
     */
    @PostMapping("/updateQuestion")
    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionVo", value = "?????????vo????????????", required = true, dataType = "questionVo",
                    paramType = "body"),
    })
    public CommonResult<String> updateQuestion(@RequestBody QuestionVo questionVo) {
        log.info("?????????===>TeacherController??????updateQuestion??????");
        Question question = new Question();
        // ??????????????????
        question.setQuType(questionVo.getQuestionType());
        question.setId(questionVo.getQuestionId());
        question.setCreateTime(new Date());
        question.setLevel(questionVo.getQuestionLevel());
        question.setAnalysis(questionVo.getAnalysis());
        question.setQuContent(questionVo.getQuestionContent());
        question.setCreatePerson(questionVo.getCreatePerson());
        // ??????????????????
        String bankIds = Arrays.toString(questionVo.getBankId());
        question.setQuBankId(bankIds.substring(1, bankIds.length() - 1).replaceAll(" ", ""));
        // ??????????????????
        if (questionVo.getImages() != null && questionVo.getImages().length != 0) {
            String QuImages = Arrays.toString(questionVo.getImages());
            question.setImage(QuImages.substring(1, QuImages.length() - 1).replaceAll(" ", ""));
        }
        StringBuilder bankNames = new StringBuilder();
        for (Integer integer : questionVo.getBankId()) {
            bankNames.append(questionBankService.getById(integer).getBankName()).append(",");
        }
        String names = bankNames.toString();
        names = names.substring(0, names.length() - 1);
        question.setQuBankName(names);
        // ??????
        questionService.update(question, new UpdateWrapper<Question>().eq("id", questionVo.getQuestionId()));
        // ??????????????????
        StringBuffer multipleChoice = new StringBuffer();
        if (questionVo.getQuestionType() != 4) {// ???????????????
            Answer answer = new Answer();
            answer.setQuestionId(questionVo.getQuestionId());
            StringBuffer imgs = new StringBuffer();
            StringBuffer answers = new StringBuffer();
            for (int i = 0; i < questionVo.getAnswer().length; i++) {
                if (questionVo.getAnswer()[i].getImages() != null && questionVo.getAnswer()[i].getImages().length > 0) {// ????????????????????????????????????
                    imgs.append(questionVo.getAnswer()[i].getImages()[0]).append(ExamConstant.SEPARATOR);
                }
                answers.append(questionVo.getAnswer()[i].getAnswer()).append(ExamConstant.SEPARATOR);
                // ??????????????????????????????
                if (questionVo.getQuestionType() == 2) {// ??????
                    if (questionVo.getAnswer()[i].getIsTrue().equals("true")) {
                        multipleChoice.append(i).append(ExamConstant.SEPARATOR);
                    }
                } else {// ??????????????? ????????????????????????
                    if (questionVo.getAnswer()[i].getIsTrue().equals("true")) {
                        answer.setTrueOption(i + "");
                        answer.setAnalysis(questionVo.getAnswer()[i].getAnalysis());
                    }
                }
            }
            if (questionVo.getQuestionType() == 2) {
                answer.setTrueOption(multipleChoice.toString().substring(0, multipleChoice.toString().length() - ExamConstant.SEPARATOR.length()));
            }
            String handleImgs = imgs.toString();
            String handleAnswers = answers.toString();
            if (handleImgs.length() != 0) {
                handleImgs = handleImgs.substring(0, handleImgs.length() - 1);
            }
            if (handleAnswers.length() != 0) {
                handleAnswers = handleAnswers.substring(0, handleAnswers.length() - ExamConstant.SEPARATOR.length());
            }

            // ?????????????????????
            answer.setImages(handleImgs);
            // ?????????????????????
            answer.setAllOption(handleAnswers);
            answerService.update(answer, new UpdateWrapper<Answer>().eq("question_id", questionVo.getQuestionId()));
            redisUtil.del("questionVo:" + questionVo.getQuestionId());
        }
        return new CommonResult<>(200, "??????????????????");
    }

    /**
     * @param bankName ????????????
     * @param pageNo ?????????
     * @param pageSize ????????????
     * @return
     */
    @GetMapping("/getBankHaveQuestionSumByType")
    @ApiOperation("??????????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankName", value = "????????????", required = false, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "majorId", value = "????????????", required = false, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "?????????", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", required = true, dataType = "int",
                    paramType = "query"),
    })
    public CommonResult<Object> getBankHaveQuestionSumByType(@RequestParam(required = false) String bankName,
                                                             @RequestParam(required = false) String majorId,HttpServletRequest request,
            Integer pageNo, Integer pageSize) {


            String token = request.getHeader("authorization");
            // ???????????????????????????
            TokenVo tokenVo = TokenUtils.verifyToken(token);
            User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));


        log.info("?????????===>TeacherController??????getBankHaveQuestionSumByType??????");

        // ????????????????????????????????????????????????
        IPage<QuestionBank> questionBankIPage = new Page<>(pageNo, pageSize);
        // ????????????(??????)
        QueryWrapper<QuestionBank> wrapper = new QueryWrapper<>();
        if (!Objects.equals(majorId, "") && majorId != null) {
            wrapper.eq("major_id", majorId);
        }
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {

            wrapper.eq("major_id", user.getMajorId());
        }
        if (!Objects.equals(bankName, "") && bankName != null) {
            wrapper.like("bank_name", bankName);
        }
        IPage<QuestionBank> iPage = questionBankService.page(questionBankIPage, wrapper);
        List<QuestionBank> questionBanks = iPage.getRecords();

        // ????????????????????????????????????
        List<BankHaveQuestionSum> bankHaveQuestionSums = new ArrayList<>();
        for (QuestionBank questionBank : questionBanks) {
            // ??????vo??????
            BankHaveQuestionSum bankHaveQuestionSum = new BankHaveQuestionSum();
            // ????????????
            bankHaveQuestionSum.setQuestionBank(questionBank);
            // ????????????????????????
            List<Question> singleQuestions = questionService.list(
                    new QueryWrapper<Question>().eq("qu_type", 1).like("qu_bank_id", questionBank.getBankId()));
            bankHaveQuestionSum.setSingleChoice(singleQuestions.size());
            // ????????????????????????
            List<Question> multipleQuestions = questionService.list(
                    new QueryWrapper<Question>().eq("qu_type", 2).like("qu_bank_id", questionBank.getBankId()));
            bankHaveQuestionSum.setMultipleChoice(multipleQuestions.size());
            // ????????????????????????
            List<Question> judgeQuestions = questionService.list(
                    new QueryWrapper<Question>().eq("qu_type", 3).like("qu_bank_id", questionBank.getBankId()));
            bankHaveQuestionSum.setJudge(judgeQuestions.size());
            // ????????????????????????
            List<Question> shortAnswerQuestions = questionService.list(
                    new QueryWrapper<Question>().eq("qu_type", 4).like("qu_bank_id", questionBank.getBankId()));
            bankHaveQuestionSum.setShortAnswer(shortAnswerQuestions.size());
            // ??????list???
            bankHaveQuestionSums.add(bankHaveQuestionSum);
        }
        // ?????????????????????
        Map<Object, Object> result = new HashMap<>();
        result.put("bankHaveQuestionSums", bankHaveQuestionSums);
        result.put("total", iPage.getTotal());
        return new CommonResult<>(200, "???????????????????????????????????????", result);
    }

    /**
     * @param ids
     * @return
     */
    @GetMapping("/deleteQuestionBank")
    @ApiOperation("???????????????????????????????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "???????????????id?????????????????????", required = true, dataType = "string",
                    paramType = "query")
    })
    public CommonResult<String> deleteQuestionBank(String ids) {
        log.info("?????????===>TeacherController??????deleteQuestionBank??????");
        String[] bankId = ids.split(",");
        for (String s : bankId) {
            // ????????????
            QuestionBank questionBank = questionBankService.getById(s);
            // ?????????????????????????????????????????????
            List<Question> questions = questionService
                    .list(new QueryWrapper<Question>().like("qu_bank_name", questionBank.getBankName()));
            // ?????????????????????????????????
            for (Question question : questions) {
                String quBankName = question.getQuBankName();
                String quBankId = question.getQuBankId();
                String[] name = quBankName.split(",");
                String[] id = quBankId.split(",");
                // ???????????????
                String[] newName = new String[name.length - 1];
                // ????????????id??????
                String[] newId = new String[id.length - 1];

                for (int i = 0, j = 0; i < name.length; i++) {
                    if (!name[i].equals(questionBank.getBankName())) {
                        newName[j] = name[i];
                        j++;
                    }
                }
                for (int i = 0, j = 0; i < id.length; i++) {
                    if (!id[i].equals(String.valueOf(questionBank.getBankId()))) {
                        newId[j] = id[i];
                        j++;
                    }
                }
                String handleName = Arrays.toString(newName)
                        .replaceAll(" ", "")
                        .replaceAll("]", "")
                        .replace("[", "");
                String handleId = Arrays.toString(newId).replaceAll(" ", "")
                        .replaceAll("]", "")
                        .replace("[", "");
                // ?????????????????????????????????
                question.setQuBankName(handleName);
                question.setQuBankId(handleId);
                // ????????????
                questionService.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
            }
            // ????????????
            questionBankService.removeById(Integer.parseInt(s));
            // ?????????????????????
            redisUtil.del("questionBanks");
        }
        return new CommonResult<>(200, "??????????????????");
    }

    /**
     * @param
     * @return
     */
    @GetMapping("/addQuestionBank")
    @ApiOperation("??????????????????")
    @ApiImplicitParams({
/*
            @ApiImplicitParam(name = "questionBank", value = "?????????????????????", required = true, dataType = "questionBank",
                    paramType = "body")
*/

            @ApiImplicitParam(name = "majorId", value = "????????????", required = true, dataType = "string",
                    paramType = "query"),

            @ApiImplicitParam(name = "bankName", value = "?????????", required = true, dataType = "string",
                    paramType = "query"),
    })
    @Transactional
    public CommonResult<String> addQuestionBank( String majorId, String bankName, HttpServletRequest request) {
        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        log.info("?????????===>TeacherController??????addQuestionBank??????");
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {
            majorId = user.getMajorId().toString();
        }

        QuestionBank questionBank = new QuestionBank();

        questionBank.setMajorId(majorId);
        questionBank.setBankName(bankName);
        String majorName = majorService.getById(Integer.valueOf(majorId)).getName() ;
        questionBank.setMajorName(majorName);
        boolean flag = questionBankService.save(questionBank);
        return flag ? new CommonResult<>(200, "??????????????????") : new CommonResult<>(200, "??????????????????");
    }

    /**
     * @param id ??????id
     * @return
     */
    @GetMapping("/getBankById")
    @ApiOperation("????????????id??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "??????id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<QuestionBank> getBankById(Integer id) {
        log.info("?????????===>TeacherController??????getBankById??????");
        return new CommonResult<>(200, "????????????????????????", questionBankService.getById(id));
    }

    /**
     * @param bankId ??????id
     * @return
     */
    @GetMapping("/getQuestionByBank")
    @ApiOperation("???????????????????????????????????????(??????,??????,?????????)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankId", value = "??????id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<Object> getQuestionByBank(Integer bankId) {
        log.info("?????????===>TeacherController??????getQuestionByBank??????");
        QuestionBank bank = questionBankService.getById(bankId);
        // ???????????????(??????,??????,?????????)??????
        List<Question> questions = questionService
                .list(new QueryWrapper<Question>().like("qu_bank_id", bank.getBankId()).in("qu_type", 1, 2, 3));
        // ?????????????????????vo??????
        List<QuestionVo> questionVos = new ArrayList<>();
        for (Question question : questions) {
            if(question.getQuType() == 4)   continue;;
            QuestionVo questionVo = new QuestionVo();

            questionVo.setQuestionId(question.getId());
            questionVo.setQuestionLevel(question.getLevel());
            if (question.getImage() != null && !question.getImage().equals("")) // ????????????????????????
            {
                questionVo
                        .setImages(StringUtils.splitByWholeSeparator(question.getImage(), ExamConstant.SEPARATOR));
            }
            questionVo.setCreatePerson(question.getCreatePerson());
            questionVo.setAnalysis(question.getAnalysis());
            questionVo.setQuestionContent(question.getQuContent());
            questionVo.setQuestionType(question.getQuType());

            Answer answer = answerService.getOne(new QueryWrapper<Answer>().eq("question_id", question.getId()));
            // ????????????
            String[] options = StringUtils.splitByWholeSeparator(answer.getAllOption(), ExamConstant.SEPARATOR);
            String[] images = StringUtils.splitByWholeSeparator(answer.getImages(), ExamConstant.SEPARATOR);
            // ??????????????????
            QuestionVo.Answer[] handleAnswer = new QuestionVo.Answer[options.length];
            // ????????????
            for (int i = 0; i < options.length; i++) {
                QuestionVo.Answer answer1 = new QuestionVo.Answer();
                if (images.length - 1 >= i && images[i] != null && !images[i].equals("")) {
                    answer1.setImages(new String[] {images[i]});
                }
                answer1.setAnswer(options[i]);
                answer1.setId(i);
                answer1.setIsTrue("false");
                handleAnswer[i] = answer1;
            }
            if (question.getQuType() == 1 || question.getQuType() == 3) {// ???????????????
                int trueOption = Integer.parseInt(answer.getTrueOption());
                handleAnswer[trueOption].setIsTrue("true");
                handleAnswer[trueOption].setAnalysis(answer.getAnalysis());
            } else if(question.getQuType() == 2) {// ??????
                String[] trueOptions = StringUtils.splitByWholeSeparator(answer.getTrueOption(),
                        ExamConstant.SEPARATOR);
                for (String trueOption : trueOptions) {
                    handleAnswer[Integer.parseInt(trueOption)].setIsTrue("true");
                    handleAnswer[Integer.parseInt(trueOption)].setAnalysis(answer.getAnalysis());
                }
            }
            questionVo.setAnswer(handleAnswer);
            questionVos.add(questionVo);
        }
        redisUtil.set("questionBankQuestion:" + bankId, questionVos, 60 * 5 + new Random().nextInt(2));
        return new CommonResult<>(200, "??????????????????????????????", questionVos);
    }

    /**
     * @param bankId ??????id
     * @param type ????????????
     * @return
     */
    @GetMapping("/getQuestionByBankIdAndType")
    @ApiOperation("????????????id????????????????????????????????? type(1?????? 2?????? 3??????)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankId", value = "??????id", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "????????????", required = true, dataType = "int", paramType = "query"),
    })
    public CommonResult<List<QuestionVo>> getQuestionByBankIdAndType(Integer bankId, Integer type) {
        log.info("?????????===>TeacherController??????getQuestionByBankIdAndType??????");
        // ???????????????????????????????????????????????????
        CommonResult<Object> questionByBank = getQuestionByBank(bankId);
        List<QuestionVo> questionVos = (List<QuestionVo>) questionByBank.getData();
        // ??????????????????????????????
        questionVos.removeIf(questionVo -> !Objects.equals(questionVo.getQuestionType(), type));
        return new CommonResult<>(200, "??????????????????????????????", questionVos);
    }

    /**
     * @param examQueryVo ??????????????????vo??????
     * @return
     */
    @PostMapping("/getExamInfo")
    @ApiOperation("?????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examQueryVo", value = "??????????????????vo??????", required = true, dataType = "examQueryVo",
                    paramType = "body")
    })
    public CommonResult<List<Exam>> getExamInfo(@RequestBody ExamQueryVo examQueryVo, HttpServletRequest request) {

        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));


        log.info("?????????===>TeacherController??????getExamInfo??????");
        // ????????????????????????????????????????????????
        IPage<Exam> examIPage = new Page<>(examQueryVo.getPageNo(), examQueryVo.getPageSize());
        // ????????????(??????)
        QueryWrapper<Exam> wrapper = new QueryWrapper<>();

        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {
            wrapper.like("create_man_id", user.getId() );
        }
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 1 + ""))) {
            wrapper.and(QueryWrapper -> QueryWrapper.eq("major_id", user.getMajorId() ).or().eq("major_id", 0));

            wrapper.and(QueryWrapper -> QueryWrapper.like("class_name", user.getClassName() ).or().like("class_name", "??????" ));

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
        // ??????exam????????????
        IPage<Exam> page = examService.page(examIPage, wrapper);

        List<Exam> exams = page.getRecords();

        return new CommonResult<>(200, "????????????????????????", exams);
    }


    /**
     * @param type ????????????
     * @param ids ???????????????id??????
     * @return
     */
    @GetMapping("/operationExam/{type}")
    @ApiOperation("????????????????????????(type 1?????? 2?????? 3??????)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "????????????", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "ids", value = "???????????????id??????", required = true, dataType = "string",
                    paramType = "query"),
    })
    public CommonResult<String> operationExam(@PathVariable("type") Integer type, String ids) {
        log.info("?????????===>TeacherController??????operationExam??????");
        String[] id = ids.split(",");
        if (type == 1) {
            for (String s : id) {
                Exam exam = examService.getOne(new QueryWrapper<Exam>().eq("exam_id", Integer.parseInt(s)));
                exam.setStatus(1);
                examService.update(exam, new UpdateWrapper<Exam>().eq("exam_id", s));
            }
        } else if (type == 2) {
            for (String s : id) {
                Exam exam = examService.getOne(new QueryWrapper<Exam>().eq("exam_id", Integer.parseInt(s)));
                exam.setStatus(2);
                examService.update(exam, new UpdateWrapper<Exam>().eq("exam_id", s));
            }
        } else if (type == 3) {
            Map<String, Object> map = new HashMap<>();
            for (String s : id) {
                map.clear();
                map.put("exam_id", Integer.parseInt(s));
                // ??????exam?????????
                examService.removeByMap(map);
                examQuestionService.removeByMap(map); // ??????exam_question?????????
                examRecordService.removeByMap(map); // ??????exam_record?????????
            }
        }
        return new CommonResult<>(200, "????????????");
    }

    /**
     * @param addExamByBankVo ????????????????????????vo??????
     * @return
     */
    @PostMapping("/addExamByBank")
    @ApiOperation("????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addExamByBankVo", value = "????????????????????????vo??????", required = true,
                    dataType = "addExamByBankVo", paramType = "body")
    })
    public CommonResult<String> addExamByBank(@RequestBody AddExamByBankVo addExamByBankVo, HttpServletRequest request) {

        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        // ????????????
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 3 + ""))) {
            addExamByBankVo.setMajorId(0);
            addExamByBankVo.setMajorName("??????");
            addExamByBankVo.setClassName("??????");
        }

        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {
            addExamByBankVo.setMajorId(user.getMajorId());
            addExamByBankVo.setMajorName(user.getMajorName());
        }



        log.info("?????????===>TeacherController??????addExamByBank??????");
        Exam exam = new Exam();

        exam.setCreateMan(user.getTrueName());
        exam.setCreateManId(user.getId());

        exam.setMajorId(addExamByBankVo.getMajorId());
        exam.setClassName(addExamByBankVo.getClassName());
        if(addExamByBankVo.getMajorId() != 0){
            exam.setMajorName(addExamByBankVo.getMajorName());

        }else {
            exam.setMajorName("??????");
        }

        exam.setStatus(addExamByBankVo.getStatus());
        exam.setDuration(addExamByBankVo.getExamDuration());
        if (addExamByBankVo.getEndTime() != null) {
            exam.setEndTime(addExamByBankVo.getEndTime());
        }
        if (addExamByBankVo.getStartTime() != null) {
            exam.setStartTime(addExamByBankVo.getStartTime());
        }
        exam.setExamDesc(addExamByBankVo.getExamDesc());
        exam.setExamName(addExamByBankVo.getExamName());
        exam.setPassScore(addExamByBankVo.getPassScore());
        exam.setType(addExamByBankVo.getType());
        // ?????????????????????
        if (addExamByBankVo.getPassword() != null) {
            exam.setPassword(addExamByBankVo.getPassword());
        }

        // ????????????????????????????????????
        ExamQuestion examQuestion = new ExamQuestion();
        // ????????????id?????????
        HashSet<Integer> set = new HashSet<>();
        String[] bankNames = addExamByBankVo.getBankNames().split(",");

        for (String bankName : bankNames) {
            List<Question> questions = questionService
                    .list(new QueryWrapper<Question>().like("qu_bank_name", bankName));
            for (Question question : questions) {
                set.add(question.getId());
            }
        }
        String quIds = set.toString().substring(1, set.toString().length() - 1)
                .replaceAll(" ", "");
        examQuestion.setQuestionIds(quIds);
        // ????????????????????????
        String[] s = quIds.split(",");
        // ??????
        int totalScore = 0;
        StringBuilder sf = new StringBuilder();
        for (String s1 : s) {
            Question question = questionService.getById(Integer.parseInt(s1));
            if (question.getQuType() == 1) {
                sf.append(addExamByBankVo.getSingleScore()).append(",");
                totalScore += addExamByBankVo.getSingleScore();
            } else if (question.getQuType() == 2) {
                sf.append(addExamByBankVo.getMultipleScore()).append(",");
                totalScore += addExamByBankVo.getMultipleScore();
            } else if (question.getQuType() == 3) {
                sf.append(addExamByBankVo.getJudgeScore()).append(",");
                totalScore += addExamByBankVo.getJudgeScore();
            } else if (question.getQuType() == 4) {
                sf.append(addExamByBankVo.getShortScore()).append(",");
                totalScore += addExamByBankVo.getShortScore();
            }
        }
        examQuestion.setScores(sf.substring(0, sf.toString().length() - 1));
        // ???????????????
        exam.setTotalScore(totalScore);

        examService.save(exam);
        examQuestion.setExamId(exam.getExamId());
        examQuestionService.save(examQuestion);
        return new CommonResult<>(200, "??????????????????");
    }

    /**
     * @param addExamByQuestionVo ?????????????????????????????????vo??????
     * @return
     */
    @PostMapping("/addExamByQuestionList")
    @ApiOperation("??????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addExamByQuestionVo", value = "?????????????????????????????????vo??????", required = true,
                    dataType = "addExamByQuestionVo",
                    paramType = "body")
    })
    public CommonResult<String> addExamByQuestionList(@RequestBody AddExamByQuestionVo addExamByQuestionVo, HttpServletRequest request) {
        log.info("?????????===>TeacherController??????addExamByQuestionList??????");

        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        // ????????????
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 3 + ""))) {
            addExamByQuestionVo.setMajorId(0);
            addExamByQuestionVo.setMajorName("??????");
            addExamByQuestionVo.setClassName("??????");
        }

        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {
            addExamByQuestionVo.setMajorId(user.getMajorId());
            addExamByQuestionVo.setMajorName(user.getMajorName());
        }


        Exam exam = new Exam();

        exam.setCreateMan(user.getTrueName());
        exam.setCreateManId(user.getId());

        exam.setMajorId(addExamByQuestionVo.getMajorId());
        exam.setClassName(addExamByQuestionVo.getClassName());
        if(addExamByQuestionVo.getMajorId() != 0){
            exam.setMajorName(addExamByQuestionVo.getMajorName());

        }else {
            exam.setMajorName("??????");
        }



        exam.setTotalScore(addExamByQuestionVo.getTotalScore());
        exam.setType(addExamByQuestionVo.getType());
        exam.setPassScore(addExamByQuestionVo.getPassScore());
        if (addExamByQuestionVo.getEndTime() != null) {
            exam.setEndTime(addExamByQuestionVo.getEndTime());
        }
        if (addExamByQuestionVo.getStartTime() != null) {
            exam.setStartTime(addExamByQuestionVo.getStartTime());
        }
        exam.setExamDesc(addExamByQuestionVo.getExamDesc());
        exam.setExamName(addExamByQuestionVo.getExamName());
        exam.setDuration(addExamByQuestionVo.getExamDuration());
        // ?????????????????????
        if (addExamByQuestionVo.getPassword() != null) {
            exam.setPassword(addExamByQuestionVo.getPassword());
        }
        exam.setStatus(addExamByQuestionVo.getStatus());
        // ????????????????????????????????????
        ExamQuestion examQuestion = new ExamQuestion();

        examQuestion.setScores(addExamByQuestionVo.getScores());
        examQuestion.setQuestionIds(addExamByQuestionVo.getQuestionIds());

        examService.save(exam);
        examQuestion.setExamId(exam.getExamId());
        examQuestionService.save(examQuestion);
        return new CommonResult<>(200, "??????????????????");
    }

    /**
     * @param examId ??????id
     * @return
     */
    @GetMapping("/getExamInfoById")
    @ApiOperation("????????????id????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "??????id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<Object> getExamInfoById(@RequestParam Integer examId) {
        log.info("?????????===>TeacherController??????getExamInfoById??????");
        // ??????????????????????????????????????????
        AddExamByQuestionVo addExamByQuestionVo = new AddExamByQuestionVo();
        Exam exam = examService.getOne(new QueryWrapper<Exam>().eq("exam_id", examId));
        addExamByQuestionVo.setExamDesc(exam.getExamDesc());
        addExamByQuestionVo.setExamDuration(exam.getDuration());
        addExamByQuestionVo.setExamId(examId);
        addExamByQuestionVo.setExamName(exam.getExamName());
        addExamByQuestionVo.setPassScore(exam.getPassScore());
        addExamByQuestionVo.setTotalScore(exam.getTotalScore());
        addExamByQuestionVo.setEndTime(exam.getEndTime());
        addExamByQuestionVo.setStartTime(exam.getStartTime());
        addExamByQuestionVo.setType(exam.getType());
        addExamByQuestionVo.setPassword(exam.getPassword());
        addExamByQuestionVo.setStatus(exam.getStatus());
        addExamByQuestionVo.setClassName(exam.getClassName());
        // ????????????????????????
        ExamQuestion examQuestion = examQuestionService
                .getOne(new QueryWrapper<ExamQuestion>().eq("exam_id", examId));
        addExamByQuestionVo.setQuestionIds(examQuestion.getQuestionIds());
        addExamByQuestionVo.setScores(examQuestion.getScores());
        return new CommonResult<>(200, "????????????", addExamByQuestionVo);
    }

    /**
     * @param addExamByQuestionVo
     * @return
     */
    @PostMapping("/updateExamInfo")
    @ApiOperation("?????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addExamByQuestionVo", value = "?????????????????????????????????vo??????", required = true,
                    dataType = "addExamByQuestionVo",
                    paramType = "body")
    })
    public CommonResult<String> updateExamInfo(@RequestBody AddExamByQuestionVo addExamByQuestionVo) {
        log.info("?????????===>TeacherController??????updateExamInfo??????");
        Exam exam = new Exam();
        exam.setClassName(addExamByQuestionVo.getClassName());
        exam.setTotalScore(addExamByQuestionVo.getTotalScore());
        exam.setType(addExamByQuestionVo.getType());
        exam.setPassScore(addExamByQuestionVo.getPassScore());
        exam.setEndTime(addExamByQuestionVo.getEndTime());
        exam.setStartTime(addExamByQuestionVo.getStartTime());
        exam.setExamDesc(addExamByQuestionVo.getExamDesc());
        exam.setExamName(addExamByQuestionVo.getExamName());
        exam.setDuration(addExamByQuestionVo.getExamDuration());
        // ?????????????????????
        if (addExamByQuestionVo.getPassword() != null) {
            exam.setPassword(addExamByQuestionVo.getPassword());
        } else {
            exam.setPassword(null);
        }
        exam.setStatus(addExamByQuestionVo.getStatus());
        exam.setExamId(addExamByQuestionVo.getExamId());
        // ????????????????????????????????????
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExamId(addExamByQuestionVo.getExamId());
        examQuestion.setScores(addExamByQuestionVo.getScores());
        examQuestion.setQuestionIds(addExamByQuestionVo.getQuestionIds());

        examService.update(exam, new UpdateWrapper<Exam>().eq("exam_id", exam.getExamId()));
        examQuestionService.update(examQuestion, new UpdateWrapper<ExamQuestion>().eq("exam_id", exam.getExamId()));
        // ????????????
        redisUtil.del("examInfo:" + exam.getExamId());
        return new CommonResult<>(200, "????????????");
    }

    /**
     * @param examRecord
     * @param request
     * @return
     */
    @PostMapping("/addExamRecord")
    @ApiOperation("????????????????????????,?????????????????????id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examRecord", value = "????????????????????????", required = true, dataType = "examRecord",
                    paramType = "body")
    })
    public CommonResult<Integer> addExamRecord(@RequestBody ExamRecord examRecord, HttpServletRequest request) {
        log.info("?????????===>TeacherController??????addExamRecord??????");
        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        // ???????????????????????????
        examRecord.setUserId(user.getId());
        examRecord.setClassName(user.getClassName());

        Exam exam = examService.getOne(new QueryWrapper<Exam>().eq("exam_id", examRecord.getExamId()));

        examRecord.setCreateMan(exam.getCreateMan());
        examRecord.setCreateManId(exam.getCreateManId());

        // ???????????????????????????
        // ?????????????????????????????????
        List<Answer> answers = answerService.list(
                new QueryWrapper<Answer>().in("question_id",
                        Arrays.asList(examRecord.getQuestionIds().split(","))));
        // ??????????????????????????????
        HashMap<String, String> map = new HashMap<>();// key????????????id value???????????????
        ExamQuestion examQuestion = examQuestionService
                .getOne(new QueryWrapper<ExamQuestion>().eq("exam_id", examRecord.getExamId()));
        // ?????????id
        String[] ids = examQuestion.getQuestionIds().split(",");
        // ?????????????????????????????????
        String[] scores = examQuestion.getScores().split(",");
        for (int i = 0; i < ids.length; i++) {
            map.put(ids[i], scores[i]);
        }
        // ????????????
        int logicScore = 0;
        // ?????????id
        StringBuffer sf = new StringBuffer();
        // ???????????????
        String[] userAnswers = examRecord.getUserAnswers().split("-");
        for (int i = 0; i < examRecord.getQuestionIds().split(",").length; i++) {
            int index = SaltEncryption.getIndex(answers,
                    Integer.parseInt(examRecord.getQuestionIds().split(",")[i]));
            if (index != -1) {
                if (Objects.equals(userAnswers[i], answers.get(index).getTrueOption())) {
                    logicScore += Integer
                            .parseInt(map.get(examRecord.getQuestionIds().split(",")[i]));
                } else {
                    sf.append(examRecord.getQuestionIds().split(",")[i])
                            .append(",");
                }
            }
        }
        examRecord.setLogicScore(logicScore);
        if (sf.length() > 0) {// ?????????????????????
            examRecord.setErrorQuestionIds(sf.toString().substring(0, sf.toString().length() - 1));
        }

        examRecord.setExamTime(new Date());
        examRecord.setMajorId(user.getMajorId());
        examRecord.setMajorName(user.getMajorName());
        examRecordService.save(examRecord);
        return new CommonResult<>(200, "????????????????????????", examRecord.getRecordId());
    }

    /**
     * @param recordId ????????????id
     * @return
     */
    @GetMapping("/getExamRecordById/{recordId}")
    @ApiOperation("?????????????????????id???????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "recordId", value = "????????????id", required = true, dataType = "int",
                    paramType = "query")
    })
    public CommonResult<Object> getExamRecordById(@PathVariable Integer recordId) {
        log.info("?????????===>TeacherController??????getExamRecordById??????");
        ExamRecord examRecord = examRecordService.getOne(new QueryWrapper<ExamRecord>().eq("record_id", recordId));
        redisUtil.set("examRecord:" + recordId, examRecord, 60 * 5 + new Random().nextInt(2) * 60);
        return new CommonResult<>(200, "????????????????????????", examRecord);
    }

    /**
     * @param examId ??????id
     * @return
     */
    @GetMapping("/getExamQuestionByExamId/{examId}")
    @ApiOperation("????????????id?????????????????????????????????id?????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "??????id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<Object> getExamQuestionByExamId(@PathVariable Integer examId) {
        log.info("?????????===>TeacherController??????getExamQuestionByExamId??????");
        ExamQuestion examQuestion = examQuestionService
                .getOne(new QueryWrapper<ExamQuestion>().eq("exam_id", examId));
        return new CommonResult<>(200, "????????????????????????????????????", examQuestion);
    }

    /**
     * @param examId ??????id
     * @param pageNo ??????
     * @param pageSize ????????????
     * @return
     */
    @GetMapping("/getExamRecord")
    @ApiOperation("????????????????????????,(pageNo,pageSize)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "??????id", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "majorId", value = "majorId", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "className", value = "className", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "?????????", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", required = true, dataType = "int",
                    paramType = "query"),
    })
    public CommonResult<Object> getExamRecord(@RequestParam(required = false) Integer examId,@RequestParam(required = false) Integer majorId,@RequestParam(required = false) String className,HttpServletRequest request,
            Integer pageNo, Integer pageSize) {



        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));

        log.info("?????????===>TeacherController??????getExamRecords??????");
        // ????????????????????????????????????????????????
        IPage<ExamRecord> examRecordPage = new Page<>(pageNo, pageSize);
        // ????????????(??????)
        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();



        if (examId != null) {
            wrapper.eq("exam_id", examId);
        }


        if (className != null && className != "") {
            wrapper.eq("class_name", className);
        }


        if (majorId != null) {
            wrapper.eq("major_id", majorId);
        }


        wrapper.eq("create_man_id", user.getId());

        IPage<ExamRecord> page = examRecordService.page(examRecordPage, wrapper);

        List<ExamRecord> examRecords = page.getRecords();
        // ???????????????
        Map<Object, Object> result = new HashMap<>();
        result.put("examRecords", examRecords);
        result.put("total", examRecordPage.getTotal());
        return new CommonResult<>(200, "success", result);
    }

    /**
     * @param userId ??????id
     * @return
     */
    @GetMapping("/getUserById/{userId}")
    @ApiOperation("????????????id??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "??????id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<Object> getUserById(@PathVariable Integer userId) {
        log.info("?????????===>TeacherController??????getUserById??????");
        User user = userService.getOne(new QueryWrapper<User>().eq("id", userId));
        return new CommonResult<>(200, "????????????????????????", user);
    }

    @GetMapping("/allExamInfo")
    @ApiOperation("????????????????????????")
    public CommonResult<List<Exam>> allExamInfo(HttpServletRequest request) {


        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
/*        QueryWrapper queryWrapper =      new QueryWrapper<Question>();*/
        QueryWrapper<Exam> wrapper = new QueryWrapper<>();

        // ????????????
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 3 + ""))) {
            wrapper.eq("create_man_id",user.getId());
        }

        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {
            wrapper.eq("create_man_id",user.getId());
        }

        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 1 + ""))) {
/*
            queryWrapper.like("class_name",user.getClassName());
            queryWrapper.like("major_name",user.getMajorName());
*/
            wrapper.and(QueryWrapper -> QueryWrapper.eq("major_id", user.getMajorId() ).or().eq("major_id", 0));

            wrapper.and(QueryWrapper2 -> QueryWrapper2.like("class_name", user.getClassName() ).or().like("class_name", "??????" ));


        }


        log.info("?????????===>TeacherController??????allExamInfo??????");
        List<Exam> exams = examService.list(wrapper);
        return new CommonResult<>(200, "??????????????????????????????", exams);
    }

    /**
     * @param totalScore ?????????
     * @param examRecordId ????????????id
     * @return
     */
    @GetMapping("/setObjectQuestionScore")
    @ApiOperation("????????????????????????????????????,???????????????????????????+?????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "totalScore", value = "?????????", required = true, dataType = "int",
                    paramType = "query"),
            @ApiImplicitParam(name = "examRecordId", value = "????????????id", required = true, dataType = "int",
                    paramType = "query")
    })
    public CommonResult<String> setObjectQuestionScore(Integer totalScore, Integer examRecordId) {
        ExamRecord examRecord = examRecordService.getOne(new QueryWrapper<ExamRecord>().eq("record_id", examRecordId));
        examRecord.setTotalScore(totalScore);
        boolean flag = examRecordService.update(examRecord,
                new UpdateWrapper<ExamRecord>().eq("record_id", examRecordId));
        return flag ? new CommonResult<>(200, "????????????") : new CommonResult<>(233, "????????????");
    }

    @GetMapping("/getExamPassRate")
    @ApiOperation("???????????????????????????????????????(echarts??????)")
    public CommonResult<List<String>> getExamPassRate(HttpServletRequest request) {

        String token = request.getHeader("authorization");
        // ???????????????????????????
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        QueryWrapper queryWrapper =      new QueryWrapper<Question>();
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {
            queryWrapper.eq("create_man_id",user.getId());
        }


        List<Exam> exams = examService.list(queryWrapper);
        List<ExamRecord> examRecords = examRecordService.list(new QueryWrapper<ExamRecord>().isNotNull("total_score"));
        // ???????????????
        String[] examNames = new String[exams.size()];
        // ???????????????
        double[] passRates = new double[exams.size()];

        double total = 0;
        double pass = 0;
        for (int i = 0; i < exams.size(); i++) {
            examNames[i] = exams.get(i).getExamName();
            total = 0;
            pass = 0;
            for (ExamRecord examRecord : examRecords) {
                if (Objects.equals(examRecord.getExamId(), exams.get(i).getExamId())) {
                    total++;
                    if (examRecord.getTotalScore() >= exams.get(i).getPassScore()) {
                        pass++;
                    }
                }
            }
            passRates[i] = pass / total;
        }
        for (int i = 0; i < passRates.length; i++) {
            if (Double.isNaN(passRates[i])) {
                passRates[i] = 0;
            }
        }
        List<String> list = new ArrayList<>();
        String res1 = Arrays.toString(examNames);
        String res2 = Arrays.toString(passRates);
        list.add(res1.substring(1, res1.length() - 1).replaceAll(" ", ""));
        list.add(res2.substring(1, res2.length() - 1).replaceAll(" ", ""));
        return new CommonResult<>(200, "???????????????????????????", list);
    }

    @GetMapping("/getExamNumbers")
    @ApiOperation("????????????????????????????????????(echarts??????)")
    public CommonResult<List<String>> getExamNumbers(HttpServletRequest request) {

        String token = request.getHeader("authorization");
        TokenVo tokenVo = TokenUtils.verifyToken(token);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        QueryWrapper queryWrapper =      new QueryWrapper<Question>();
        if (tokenVo != null && (Objects.equals(tokenVo.getRoleId(), 2 + ""))) {
            queryWrapper.eq("create_man_id",user.getId());
        }


        List<Exam> exams = examService.list(queryWrapper);
        List<ExamRecord> examRecords = examRecordService.list(new QueryWrapper<ExamRecord>());
        // ???????????????
        String[] examNames = new String[exams.size()];
        // ?????????????????????
        String[] examNumbers = new String[exams.size()];

        int total = 0;
        int cur = 0;
        for (int i = 0; i < exams.size(); i++) {
            examNames[i] = exams.get(i).getExamName();
            total = 0;
            cur = 0;
            for (ExamRecord examRecord : examRecords) {
                total++;
                if (Objects.equals(examRecord.getExamId(), exams.get(i).getExamId())) {
                    cur++;
                }
            }
            examNumbers[i] = cur + "";
        }
        List<String> list = new ArrayList<>();
        String res1 = Arrays.toString(examNames);
        String res2 = Arrays.toString(examNumbers);
        list.add(res1.substring(1, res1.length() - 1).replaceAll(" ", ""));
        list.add(res2.substring(1, res2.length() - 1).replaceAll(" ", ""));
        return new CommonResult<>(200, "????????????????????????", list);
    }
}
