package com.senior.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senior.entity.QuestionBank;
import com.senior.mapper.QuestionBankMapper;
import com.senior.service.QuestionBankService;

@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank>
        implements QuestionBankService {
}
