package com.zy.media.api;

import com.zy.base.exception.StudyOnlineException;
import com.zy.base.model.RestResponse;
import com.zy.media.po.MediaFiles;
import com.zy.media.service.MediaFilesService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
public class MediaOpenController {

    @Autowired
    MediaFilesService mediaFileService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){

        MediaFiles mediaFiles = mediaFileService.getById(mediaId);
        if(mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())){
            StudyOnlineException.cast("视频还没有转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }
}
