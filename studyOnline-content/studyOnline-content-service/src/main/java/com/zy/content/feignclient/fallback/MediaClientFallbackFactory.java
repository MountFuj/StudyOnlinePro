package com.zy.content.feignclient.fallback;

import com.zy.content.feignclient.MediaClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class MediaClientFallbackFactory implements FallbackFactory<MediaClient>{
    @Override
    public MediaClient create(Throwable throwable) {
        return new MediaClient() {
            @Override
            public String upload(MultipartFile filedata, String objectName) {
                // 降级方法
                log.debug("调用媒资管理服务上传文件时发生熔断，返回空");
                return null;
            }
        };
    }
}
