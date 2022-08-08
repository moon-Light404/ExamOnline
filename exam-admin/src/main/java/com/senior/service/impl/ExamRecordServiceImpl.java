package com.senior.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senior.entity.ExamRecord;
import com.senior.mapper.ExamRecordMapper;
import com.senior.service.ExamRecordService;

@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {
}
