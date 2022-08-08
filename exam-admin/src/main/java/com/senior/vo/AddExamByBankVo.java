package com.senior.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddExamByBankVo {
    private String bankNames;

    private Integer examDuration;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING ,pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING ,pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String examDesc;
    private String examName;
    private Integer judgeScore;
    private Integer multipleScore;
    private Integer passScore;
    private Integer shortScore;
    private Integer singleScore;
    private Integer status;
    private Integer type;
    private String password;


    private Integer majorId;

    private String majorName;

    private String className;
}
