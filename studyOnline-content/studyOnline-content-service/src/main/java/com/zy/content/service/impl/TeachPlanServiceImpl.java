package com.zy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.base.exception.StudyOnlineException;
import com.zy.content.mapper.TeachplanMapper;
import com.zy.content.mapper.TeachplanMediaMapper;
import com.zy.content.model.dto.SaveTeachPlanDto;
import com.zy.content.model.dto.TeachPlanDto;
import com.zy.content.model.po.Teachplan;
import com.zy.content.model.po.TeachplanMedia;
import com.zy.content.service.TeachPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeachPlanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachPlanService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;
    @Override
    public List<TeachPlanDto> findTeachPlanTreeNodes(Long courseId) {
        // 获取1级目录id
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,0);
        queryWrapper.orderByAsc(Teachplan::getOrderby);
        List<Teachplan> oneList = teachplanMapper.selectList(queryWrapper);
        if(oneList == null) return null;
        // 结果
        List<TeachPlanDto> result = new ArrayList<>();
        // 遍历1级目录
        for(Teachplan one : oneList){
            TeachPlanDto oneDto = new TeachPlanDto();
            BeanUtils.copyProperties(one,oneDto);
            // 取资源
            TeachplanMedia teachplanMedia = teachplanMediaMapper.selectOne(new QueryWrapper<TeachplanMedia>().eq("teachplan_id", one.getId()));
            if(teachplanMedia!=null)  BeanUtils.copyProperties(teachplanMedia,oneDto);
            // 拿到二级目录
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teachplan::getParentid,one.getId());
            wrapper.orderByAsc(Teachplan::getOrderby);
            List<Teachplan> twoList = teachplanMapper.selectList(wrapper);
            List<TeachPlanDto> twoDtoList = new ArrayList<>();
            // 遍历二级目录
            for (Teachplan two : twoList){
                TeachPlanDto twoDto = new TeachPlanDto();
                BeanUtils.copyProperties(two,twoDto);
                // 取资源
                TeachplanMedia media = teachplanMediaMapper.selectOne(new QueryWrapper<TeachplanMedia>().eq("teachplan_id", two.getId()));
                if(media!=null)  BeanUtils.copyProperties(media,twoDto);
                twoDtoList.add(twoDto);
            }
            oneDto.setTeachPlanTreeNodes(twoDtoList);
            result.add(oneDto);
        }
        return result;
    }

    @Override
    public void addOrUpdateTeachPlan(SaveTeachPlanDto dto) {
        Long id = dto.getId();
        Teachplan teachplan = new Teachplan();
        if(id==null){
            // 新增
            // 查询该课程下所有计划
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getCourseId,dto.getCourseId());
            queryWrapper.eq(Teachplan::getParentid,dto.getParentid());
            List<Teachplan> teachplanList = teachplanMapper.selectList(queryWrapper);
            BeanUtils.copyProperties(dto,teachplan);
            teachplan.setOrderby(teachplanList.size()+1);
            // 插入数据库
            teachplanMapper.insert(teachplan);
        }else {
            // 修改
            BeanUtils.copyProperties(dto,teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachPlan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid,teachplan.getId());
        List<Teachplan> teachplanList = teachplanMapper.selectList(queryWrapper);
        if(!teachplanList.isEmpty()){
            //有小章节 不让删
            StudyOnlineException.cast("该章节下有小节不允许删除");
        }
        // 如果是小节，删除关联的媒资
        if(teachplan.getParentid()!='0'){
            TeachplanMedia teachplanMedia = teachplanMediaMapper.selectOne(new QueryWrapper<TeachplanMedia>().eq("teachplan_id", teachplan.getId()));
            teachplanMediaMapper.deleteById(teachplanMedia);
        }
        teachplanMapper.deleteById(teachplan);
    }

    @Override
    public void move(String moveType, Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(moveType.equals("moveup")){
            // 交换上级orderBy
            QueryWrapper<Teachplan> wrapper = new QueryWrapper<Teachplan>()
                    .eq("parentId", teachplan.getParentid())
                    .eq("course_id",teachplan.getCourseId())
                    .eq("orderBy", teachplan.getOrderby() - 1);
            Teachplan selectedOne = teachplanMapper.selectOne(wrapper);
            if(selectedOne==null){
                StudyOnlineException.cast("没有上一级");
            }
            // 交换
            Integer orderby = teachplan.getOrderby();
            teachplan.setOrderby(orderby-1);
            selectedOne.setOrderby(orderby);
            // 更新数据库
            teachplanMapper.updateById(teachplan);
            teachplanMapper.updateById(selectedOne);
        }else{
            // 交换上级orderBy
            QueryWrapper<Teachplan> wrapper = new QueryWrapper<Teachplan>()
                    .eq("parentId", teachplan.getParentid())
                    .eq("course_id",teachplan.getCourseId())
                    .eq("orderBy", teachplan.getOrderby() + 1);
            Teachplan selectedOne = teachplanMapper.selectOne(wrapper);
            if(selectedOne==null){
                StudyOnlineException.cast("没有下一级");
            }
            // 交换
            Integer orderby = teachplan.getOrderby();
            teachplan.setOrderby(orderby+1);
            selectedOne.setOrderby(orderby);
            // 更新数据库
            teachplanMapper.updateById(teachplan);
            teachplanMapper.updateById(selectedOne);
        }
    }
}
