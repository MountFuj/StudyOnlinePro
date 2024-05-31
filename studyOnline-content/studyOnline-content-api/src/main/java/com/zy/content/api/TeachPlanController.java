package com.zy.content.api;

import com.zy.content.model.dto.TeachPlanDto;
import com.zy.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "课程计划接口", tags = "课程计划接口")
@RestController
public class TeachPlanController {

    @Autowired
    private TeachPlanService teachPlanService;

    @GetMapping("/teachplan/{courseId}/tree-nodes")
    @ApiOperation("查询课程计划树形结构")
    public List<TeachPlanDto> getTreeNodes(@PathVariable Long courseId){
        return teachPlanService.findTeachPlanTreeNodes(courseId);
    }
}
