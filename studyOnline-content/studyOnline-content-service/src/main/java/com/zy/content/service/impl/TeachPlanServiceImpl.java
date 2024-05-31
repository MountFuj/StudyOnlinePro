package com.zy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.content.mapper.TeachplanMapper;
import com.zy.content.mapper.TeachplanMediaMapper;
import com.zy.content.model.dto.TeachPlanDto;
import com.zy.content.model.po.Teachplan;
import com.zy.content.model.po.TeachplanMedia;
import com.zy.content.service.TeachPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
