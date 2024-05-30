package com.zy.content.model.dto;

import com.zy.base.validated.ValidatedGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(value="AddCourseDto", description="新增课程基本信息")
public class AddCourseDto {

    @ApiModelProperty(value = "大分类",required = true)
    @NotEmpty(message = "大分类不能为空")
    private String mt;

    @ApiModelProperty(value = "小分类",required = true)
    @NotEmpty(message = "小分类不能为空")
    private String st;

    @ApiModelProperty(value = "课程名称", required = true)
    @NotEmpty(message = "课程名称不能为空")
//    @NotEmpty(groups = {ValidatedGroups.Insert.class},message = "添加课程名称不能为空")
//    @NotEmpty(groups = {ValidatedGroups.Update.class},message = "修改课程名称不能为空")
    private String name;

    @ApiModelProperty(value = "课程图片",required = true)
    private String pic;

    @ApiModelProperty(value = "教学模式（普通，录播，直播等）",required = true)
    private String teachmode;

    @ApiModelProperty(value = "适用人群",required = true)
    @NotEmpty(message = "适用人群不能为空")
    private String users;

    @ApiModelProperty(value = "课程标签")
    private String tags;

    @ApiModelProperty(value = "课程等级",required = true)
    @NotEmpty(message = "课程等级不能为空")
    private String grade;

    @ApiModelProperty(value = "课程介绍")
    private String description;

    @ApiModelProperty(value = "收费规则，对应数据字典",required = true)
    @NotEmpty(message = "收费规则不能为空")
    private String charge;

    @ApiModelProperty(value = "价格")
    private Float price;

    @ApiModelProperty(value = "原价")
    private Float originalPrice;

    @ApiModelProperty(value = "qq")
    private String qq;

    @ApiModelProperty(value = "微信")
    private String wechat;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "有效期")
    private Integer validDays;
}
