package com.zy.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.content.model.dto.TeachPlanDto;
import com.zy.content.model.po.Teachplan;

import java.util.List;

public interface TeachPlanService extends IService<Teachplan> {

    List<TeachPlanDto> findTeachPlanTreeNodes(Long courseId);
}
