package com.zy.content.api;

import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.base.validated.ValidatedGroups;
import com.zy.content.model.dto.AddCourseDto;
import com.zy.content.model.dto.CourseBaseInfoDto;
import com.zy.content.model.dto.EditCourseDto;
import com.zy.content.model.dto.QueryCourseParamsDto;
import com.zy.content.model.po.CourseBase;
import com.zy.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口")
public class CourseBaseInfoController {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto dto){
        return courseBaseInfoService.queryCourseBaseList(pageParams,dto);
    }

    @ApiOperation("课程添加接口")
    @PostMapping("/course")
//    @Validated({ValidatedGroups.Insert.class})  可以指定校验分组
    public CourseBaseInfoDto addCourse(@RequestBody @Validated AddCourseDto addCourseDto){
        return courseBaseInfoService.createCourse(1232141425L,addCourseDto);
    }

    @ApiOperation("根据课程id查询课程")
    @GetMapping("/course/{id}")
    public CourseBaseInfoDto getCourseById(@PathVariable Long id){
        return courseBaseInfoService.getCourseById(id);
    }

    @ApiOperation("修改课程信息")
    @PutMapping("/course")
    public CourseBaseInfoDto updateCourse(@RequestBody EditCourseDto dto){
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourse(companyId,dto);
    }
}
