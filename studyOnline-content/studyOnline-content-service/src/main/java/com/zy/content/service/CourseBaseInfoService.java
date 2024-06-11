package com.zy.content.service;

import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.content.model.dto.AddCourseDto;
import com.zy.content.model.dto.CourseBaseInfoDto;
import com.zy.content.model.dto.EditCourseDto;
import com.zy.content.model.dto.QueryCourseParamsDto;
import com.zy.content.model.po.CourseBase;

public interface CourseBaseInfoService {

    /**
     * 根据条件查询课程信息
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 动态查询条件
     * @return 分页课程信息
     */
    PageResult<CourseBase> queryCourseBaseList(Long companyId,PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);


    CourseBaseInfoDto createCourse(Long companyId,AddCourseDto addCourseDto);

    CourseBaseInfoDto getCourseById(Long courseId);

    CourseBaseInfoDto updateCourse(Long companyId,EditCourseDto dto);

    void deleteCourse(Long courseId);
}
