package com.zy.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zy.content.model.dto.CourseCategoryTreeDto;
import com.zy.content.model.po.CourseCategory;

import java.util.List;


/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author zhangYu
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
