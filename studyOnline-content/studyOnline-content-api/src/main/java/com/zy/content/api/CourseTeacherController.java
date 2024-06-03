package com.zy.content.api;

import com.zy.content.model.dto.AddOrUpdateCourseTeacher;
import com.zy.content.model.po.CourseTeacher;
import com.zy.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "课程教师接口", tags = "课程教师接口")
@RestController
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> courseTeacherList(@PathVariable Long courseId){
        return courseTeacherService.getCourseTeacherList(courseId);
    }

    @PostMapping("/courseTeacher")
    public CourseTeacher addCourseTeacher(@RequestBody AddOrUpdateCourseTeacher dto){
        Long companyId = 1232141425L;
        return courseTeacherService.addCourseTeacher(companyId,dto);
    }


    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void delete(@PathVariable Long courseId,@PathVariable Long teacherId){
        courseTeacherService.deleteCourseTeacher(courseId,teacherId);
    }
}
