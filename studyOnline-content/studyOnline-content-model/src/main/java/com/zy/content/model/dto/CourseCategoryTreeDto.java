package com.zy.content.model.dto;

import com.zy.content.model.po.CourseCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CourseCategoryTreeDto extends CourseCategory {

    List<CourseCategoryTreeDto> childrenTreeNodes;

}
