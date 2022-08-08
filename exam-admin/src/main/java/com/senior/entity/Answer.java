package com.senior.entity;

import java.io.Serializable;

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
@ApiModel("答案表实体")
@TableName(value = "answer")
public class Answer implements Serializable {

    // 对应数据库的主键(uuid,自增id,雪花算法, redis,zookeeper)
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键 答案id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "所有的选项信息", example = "2,3,4,5(代表ABCD四个选项)")
    private String allOption;

    @ApiModelProperty(value = "答案的图片列表", example = "img1URl, img2URl")
    private String images;

    @ApiModelProperty(value = "答案解析", example = "1+1=2")
    private String analysis;

    @ApiModelProperty(value = "问题id", example = "1")
    private Integer questionId;

    @ApiModelProperty(value = "正确的答案的索引", example = "allOption[index]")
    private String trueOption;

}
