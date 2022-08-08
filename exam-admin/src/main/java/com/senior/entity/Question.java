package com.senior.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("题目实体")
@TableName(value = "question")
public class Question {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键 题目id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "问题内容", example = "1+1等于几")
    private String quContent;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建人的username")
    private String createPerson;

    @ApiModelProperty(value = "问题类型", example = " 1单选 2多选 3判断 4简答")
    private Integer quType;

    @ApiModelProperty(value = "问题难度", example = "1")
    private Integer level;

    @ApiModelProperty(value = "问题相关的图片", example = "imageUrl")
    private String image;

    @ApiModelProperty(value = "所属题库的id", example = "1,2")
    private String quBankId;

    @ApiModelProperty(value = "所属题库的名称", example = "小学题库")
    private String quBankName;

    @ApiModelProperty(value = "题目解析", example = "题目解析")
    private String analysis;
    private String createMan;

}
