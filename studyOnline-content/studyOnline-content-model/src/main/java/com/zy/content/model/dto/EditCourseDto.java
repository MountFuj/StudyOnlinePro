package com.zy.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value="修改课程", description="修改课程")
@Data
public class EditCourseDto extends AddCourseDto{

    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
}
