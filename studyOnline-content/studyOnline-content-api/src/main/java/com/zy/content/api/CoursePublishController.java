package com.zy.content.api;

import com.zy.content.model.dto.CoursePreviewDto;
import com.zy.content.service.CourseBasePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CoursePublishController {

    @Autowired
    CourseBasePublishService courseBasePublishService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId")Long courseId){
        ModelAndView modelAndView = new ModelAndView();
        CoursePreviewDto coursePreviewInfo = courseBasePublishService.getCoursePreviewInfo(courseId);
        modelAndView.addObject("model",coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }
}
