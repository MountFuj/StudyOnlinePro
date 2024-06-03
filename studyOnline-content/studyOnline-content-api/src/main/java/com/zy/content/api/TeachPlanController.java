package com.zy.content.api;

import com.zy.content.model.dto.SaveTeachPlanDto;
import com.zy.content.model.dto.TeachPlanDto;
import com.zy.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/teachplan")
    public void saveOrUpdate(@RequestBody SaveTeachPlanDto dto){
        teachPlanService.addOrUpdateTeachPlan(dto);
    }

    @DeleteMapping("/teachplan/{teachplanId}")
    public void deleteTeachPlan(@PathVariable Long teachplanId){
        teachPlanService.deleteTeachPlan(teachplanId);
    }

    @PostMapping("/teachplan/{moveType}/{teachplanId}")
    public void move(@PathVariable String moveType,@PathVariable Long teachplanId){
        teachPlanService.move(moveType,teachplanId);
    }
}
