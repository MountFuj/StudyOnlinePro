package com.zy.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.content.model.dto.AddOrUpdateCourseTeacher;
import com.zy.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService extends IService<CourseTeacher> {

    List<CourseTeacher> getCourseTeacherList(Long courseId);


    CourseTeacher addCourseTeacher(Long companyId, AddOrUpdateCourseTeacher dto);


    void deleteCourseTeacher(Long courseId, Long teacherId);
}
