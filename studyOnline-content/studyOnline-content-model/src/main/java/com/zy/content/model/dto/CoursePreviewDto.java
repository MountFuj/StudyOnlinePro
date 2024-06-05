package com.zy.content.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoursePreviewDto {

    CourseBaseInfoDto courseBase;

    List<TeachPlanDto> teachplans;
}
