package com.zy.content.service.impl;

import com.zy.content.model.dto.CourseBaseInfoDto;
import com.zy.content.model.dto.CoursePreviewDto;
import com.zy.content.model.dto.TeachPlanDto;
import com.zy.content.service.CourseBaseInfoService;
import com.zy.content.service.CourseBasePublishService;
import com.zy.content.service.TeachPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseBasePublishServiceImpl implements CourseBasePublishService {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachPlanService teachPlanService;
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CourseBaseInfoDto courseById = courseBaseInfoService.getCourseById(courseId);
        List<TeachPlanDto> treeNodes = teachPlanService.findTeachPlanTreeNodes(courseId);
        CoursePreviewDto dto = new CoursePreviewDto();
        dto.setCourseBase(courseById);
        dto.setTeachplans(treeNodes);
        return dto;
    }
}
