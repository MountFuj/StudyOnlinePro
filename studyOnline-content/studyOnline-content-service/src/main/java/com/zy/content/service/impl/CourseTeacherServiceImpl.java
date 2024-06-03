package com.zy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.base.exception.StudyOnlineException;
import com.zy.content.mapper.CourseTeacherMapper;
import com.zy.content.model.dto.AddOrUpdateCourseTeacher;
import com.zy.content.model.po.CourseTeacher;
import com.zy.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<CourseTeacher> getCourseTeacherList(Long courseId) {
        QueryWrapper<CourseTeacher> querywrapper = new QueryWrapper<CourseTeacher>()
                .eq("course_id", courseId);
        return courseTeacherMapper.selectList(querywrapper);
    }

    @Override
    public CourseTeacher addCourseTeacher(Long companyId, AddOrUpdateCourseTeacher dto) {
        if(companyId!=1232141425L){
            throw new StudyOnlineException("非本机构不能添加");
        }
        CourseTeacher courseTeacher = new CourseTeacher();
        BeanUtils.copyProperties(dto,courseTeacher);
        if(dto.getId()==null){
            courseTeacherMapper.insert(courseTeacher);
            return courseTeacherMapper.selectOne(new QueryWrapper<CourseTeacher>().eq("course_id", courseTeacher.getCourseId()).eq("teacher_name", courseTeacher.getTeacherName()));
        }
        // 修改
        courseTeacherMapper.updateById(courseTeacher);
        return courseTeacher;
    }


    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        QueryWrapper<CourseTeacher> queryWrapper = new QueryWrapper<CourseTeacher>()
                .eq("course_id", courseId)
                .eq("id", teacherId);
        CourseTeacher courseTeacher = courseTeacherMapper.selectOne(queryWrapper);
        if(courseTeacher!=null){
            courseTeacherMapper.deleteById(courseTeacher);
        }
    }
}
