package com.senior.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamQueryVo {
    private Integer examType;
    private String startTime;
    private String endTime;
    private String examName;
    private Integer pageNo;
    private Integer pageSize;

    private Integer majorId;

    private String majorName;

    private String className;
}
