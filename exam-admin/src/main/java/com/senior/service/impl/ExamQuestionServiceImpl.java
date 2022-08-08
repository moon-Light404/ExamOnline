package com.senior.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senior.entity.ExamQuestion;
import com.senior.mapper.ExamQuestionMapper;
import com.senior.service.ExamQuestionService;

@Service
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion>
        implements ExamQuestionService {
}
