package com.zy.media.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.media.mapper.MqMessageMapper;
import com.zy.media.po.MqMessage;
import com.zy.media.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangyu
 */
@Slf4j
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {

}
