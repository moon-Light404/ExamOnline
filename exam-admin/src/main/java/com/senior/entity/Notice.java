package com.senior.entity;

import java.io.Serializable;
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
@ApiModel("系统公告实体")
@TableName(value = "notice")
public class Notice implements Serializable {

    // 对应数据库的主键(uuid,自增id,雪花算法, redis,zookeeper)
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键 公告id", example = "1")
    private Integer nId;

    @ApiModelProperty(value = "公告内容(Html片段)", example = "1. 修复系统BUG")
    private String content;

    @ApiModelProperty(value = "公告创建时间", example = "2020-10-22 10:35:44")
    private Date createTime;

    @ApiModelProperty(value = "公告修改时间", example = "2020-10-22 10:35:44")
    private Date updateTime;

    @ApiModelProperty(value = "公告状态", example = "0(不是当前系统公告) 1(是当前系统公告)")
    private Integer status;
}
