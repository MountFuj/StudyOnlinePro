package com.zy.content.model.dto;

import com.zy.content.model.po.Teachplan;
import com.zy.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeachPlanDto extends Teachplan {

    // 课程计划关联的媒资信息
    TeachplanMedia teachplanMedia;

    // 子节点
    List<TeachPlanDto> teachPlanTreeNodes;
}
