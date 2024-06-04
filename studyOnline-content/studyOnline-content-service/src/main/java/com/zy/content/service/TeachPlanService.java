package com.zy.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.content.model.dto.BindTeachPlanDto;
import com.zy.content.model.dto.SaveTeachPlanDto;
import com.zy.content.model.dto.TeachPlanDto;
import com.zy.content.model.po.Teachplan;
import com.zy.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachPlanService extends IService<Teachplan> {

    List<TeachPlanDto> findTeachPlanTreeNodes(Long courseId);

    void addOrUpdateTeachPlan(SaveTeachPlanDto dto);

    void deleteTeachPlan(Long teachplanId);

    void move(String moveType,Long teachplanId);

    public TeachplanMedia associationMedia(BindTeachPlanDto dto);

    void deleteAssociationMedia(Long teachplanId,String mediaId);
}
