package com.zy.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="查询课程列表请求对象",description = "查询课程列表请求对象")
public class QueryCourseParamsDto {

    @ApiModelProperty("审核状态")
    private String auditStatus; // 审核状态

    @ApiModelProperty("课程名称")
    private String courseName; // 课程名称

    @ApiModelProperty("发布状态")
    private String publishStatus;  // 发布状态
}
