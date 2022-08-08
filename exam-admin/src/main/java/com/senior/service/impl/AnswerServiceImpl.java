package com.senior.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senior.entity.Answer;
import com.senior.mapper.AnswerMapper;
import com.senior.service.AnswerService;

@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements AnswerService {
}
