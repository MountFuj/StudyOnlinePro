package com.zy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.base.exception.StudyOnlineException;
import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.content.mapper.*;
import com.zy.content.model.dto.AddCourseDto;
import com.zy.content.model.dto.CourseBaseInfoDto;
import com.zy.content.model.dto.EditCourseDto;
import com.zy.content.model.dto.QueryCourseParamsDto;
import com.zy.content.model.po.*;
import com.zy.content.service.CourseBaseInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        // 构建查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNoneEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNoneEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNoneEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        // 分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);
        // 数据列表
        List<CourseBase> records = courseBasePage.getRecords();
        // 总记录数
        long total = courseBasePage.getTotal();
        return new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourse(Long companyId, AddCourseDto dto) {
        //合法性校验
        if (StringUtils.isBlank(dto.getName())) {
//            throw new RuntimeException("课程名称为空");
            throw new StudyOnlineException("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
//            throw new RuntimeException("课程分类为空");
            throw new StudyOnlineException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
//            throw new RuntimeException("课程分类为空");
            throw new StudyOnlineException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
//            throw new RuntimeException("课程等级为空");
            throw new StudyOnlineException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
//            throw new RuntimeException("教育模式为空");
            throw new StudyOnlineException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
//            throw new RuntimeException("适应人群为空");
            throw new StudyOnlineException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
//            throw new RuntimeException("收费规则为空");
            throw new StudyOnlineException("收费规则为空");
        }
        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(dto,courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        if(insert<=0){
            throw new RuntimeException("新增课程基本信息失败");
        }
        //向课程营销表保存课程营销信息
        //课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        Long courseId = courseBaseNew.getId();
        BeanUtils.copyProperties(dto,courseMarketNew);
        courseMarketNew.setId(courseId);
        int i = saveCourseMarket(courseMarketNew);
        if(i<=0){
            throw new RuntimeException("保存课程营销信息失败");
        }
        //查询课程基本信息及营销信息并返回
        return getCourseBaseInfo(courseId);
    }

    @Override
    public CourseBaseInfoDto getCourseById(Long courseId) {
        // 查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null) return null;
        // 查营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        // 分类名称
        CourseCategory courseCategory = courseCategoryMapper.selectById(courseBase.getMt());
        CourseCategory courseCategory1 = courseCategoryMapper.selectById(courseBase.getSt());
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket!=null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        courseBaseInfoDto.setMtName(courseCategory.getName());
        courseBaseInfoDto.setStName(courseCategory1.getName());
        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto updateCourse(Long companyId,EditCourseDto dto) {
        //课程id
        Long courseId = dto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            StudyOnlineException.cast("课程不存在");
        }

        //校验本机构只能修改本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            StudyOnlineException.cast("本机构只能修改本机构的课程");
        }

        //封装基本信息的数据
        BeanUtils.copyProperties(dto,courseBase);
        courseBase.setChangeDate(LocalDateTime.now());

        //更新课程基本信息
        int i = courseBaseMapper.updateById(courseBase);

        //封装营销信息的数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarket);
        saveCourseMarket(courseMarket);
        //查询课程信息
        return this.getCourseBaseInfo(courseId);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        // 删除课程下营销信息 师资信息 课程计划信息 以及课程计划对应的媒资信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            StudyOnlineException.cast("课程不存在");
        }
        if (!courseBase.getAuditStatus().equals("202002")) {
            StudyOnlineException.cast("课程状态不是未提交审核状态，不能删除");
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        // 营销信息
        if(courseMarket != null){
            courseMarketMapper.deleteById(courseMarket);
        }
        // 课程计划
        List<Teachplan> teachplanList = teachplanMapper.selectList(new QueryWrapper<Teachplan>().eq("course_id", courseId));
        // 循环遍历课程计划，删除绑定的媒资和计划
        for(Teachplan teachplan : teachplanList){
            TeachplanMedia teachplanMedia = teachplanMediaMapper.selectOne(new QueryWrapper<TeachplanMedia>()
                    .eq("teachplan_id", teachplan.getId())
                    .eq("course_id", teachplan.getCourseId()));
            teachplanMediaMapper.deleteById(teachplanMedia);
            teachplanMapper.deleteById(teachplan);
        }
        // 师资信息
        List<CourseTeacher> teacherList = courseTeacherMapper.selectList(new QueryWrapper<CourseTeacher>()
                .eq("course_id", courseId));
        courseTeacherMapper.deleteBatchIds(teacherList.stream().map(CourseTeacher::getId).collect(Collectors.toList()));
        // 课程本身
        courseBaseMapper.deleteById(courseBase);
    }

    public int saveCourseMarket(CourseMarket courseMarketNew){
        //收费规则
        String charge = courseMarketNew.getCharge();
        if(StringUtils.isBlank(charge)){
//            throw new RuntimeException("收费规则没有选择");
            throw new StudyOnlineException("收费规则没有选择");
        }
        //收费规则为收费
        if(charge.equals("201001")){
            if(courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue()<=0){
//                throw new RuntimeException("课程为收费价格不能为空且必须大于0");
                throw new StudyOnlineException("课程为收费价格不能为空且必须大于0");
            }
        }
        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarketNew.getId());
        if(courseMarketObj == null){
            return courseMarketMapper.insert(courseMarketNew);
        }else{
            BeanUtils.copyProperties(courseMarketNew,courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }

    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        return courseBaseInfoDto;
    }
}
