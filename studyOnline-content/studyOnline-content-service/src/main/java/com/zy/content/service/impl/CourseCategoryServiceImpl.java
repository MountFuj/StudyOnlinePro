package com.zy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.content.mapper.CourseCategoryMapper;
import com.zy.content.model.dto.CourseCategoryTreeDto;
import com.zy.content.model.po.CourseCategory;
import com.zy.content.service.CourseCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    CourseCategoryMapper categoryMapper;

    @Override
    public List<CourseCategoryTreeDto> getCategoryTree(String id) {
        List<CourseCategoryTreeDto> treeNodes = new ArrayList<>();
        // 先查所有二级节点
        LambdaQueryWrapper<CourseCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseCategory::getParentid,id);
        queryWrapper.eq(CourseCategory::getIsShow,1);
        queryWrapper.orderByAsc(CourseCategory::getOrderby);
        List<CourseCategory> categoryList = categoryMapper.selectList(queryWrapper);
        // 遍历二级节点，拿到所有三级节点
        for (CourseCategory category : categoryList) {
            CourseCategoryTreeDto dto = new CourseCategoryTreeDto();
            BeanUtils.copyProperties(category,dto);
            LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseCategory::getParentid,category.getId());
            wrapper.eq(CourseCategory::getIsShow,1);
            wrapper.orderByAsc(CourseCategory::getOrderby);
            List<CourseCategory> selectList = categoryMapper.selectList(wrapper);
            List<CourseCategoryTreeDto> children = new ArrayList<>();
            selectList.forEach(item->{
                CourseCategoryTreeDto treeDto = new CourseCategoryTreeDto();
                BeanUtils.copyProperties(item,treeDto);
                children.add(treeDto);
            });
            dto.setChildrenTreeNodes(children);
            treeNodes.add(dto);
        }
        return treeNodes;
    }
}
