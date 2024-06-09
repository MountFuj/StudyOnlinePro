package com.zy.content.feignclient;

import com.alibaba.nacos.common.http.param.MediaType;
import com.zy.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "media-api",configuration = MultipartSupportConfig.class)
public interface MediaClient {

    @RequestMapping(value = "/media/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA)
    String upload(@RequestPart("filedata") MultipartFile filedata, @RequestParam(value = "objectName",required = false)String objectName);
}
