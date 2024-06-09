package com.xuecheng.search.controller;

import com.xuecheng.search.po.CourseIndex;
import com.xuecheng.search.service.IndexService;
import com.zy.base.exception.StudyOnlineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class CourseIndexController {

    @Value("${elasticsearch.course.index}")
    private String courseIndex;

    @Autowired
    IndexService indexService;
    @PostMapping("/course")
    public Boolean add(@RequestBody CourseIndex index){
        if(index.getId()==null){
            StudyOnlineException.cast("课程id为空");
        }
        Boolean result = indexService.addCourseIndex(courseIndex, String.valueOf(index.getId()), index);
        if(!result){
            StudyOnlineException.cast("添加课程索引失败");
        }
        return result;
    }
}
