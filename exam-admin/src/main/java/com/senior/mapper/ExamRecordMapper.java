package com.senior.mapper;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senior.entity.ExamRecord;

// 在对应的mapper上面实现基本的接口
@Repository // 代表持久层
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
}
