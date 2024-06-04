package com.zy.media.jobhandler;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SampleJob {

    @XxlJob("testJob")
    public void testJob(){
        System.out.println("-------testJob-------");
    }
}
