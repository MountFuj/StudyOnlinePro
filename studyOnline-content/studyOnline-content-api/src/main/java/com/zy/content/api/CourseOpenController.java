package com.zy.content.api;

import com.zy.content.model.dto.CoursePreviewDto;
import com.zy.content.service.CourseBaseInfoService;
import com.zy.content.service.CourseBasePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
public class CourseOpenController {

    @Autowired
    private CourseBasePublishService coursePublishService;


    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        return coursePublishService.getCoursePreviewInfo(courseId);
    }
}
