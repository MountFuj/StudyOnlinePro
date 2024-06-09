package com.zy.media.api;

import com.alibaba.nacos.common.http.param.MediaType;
import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.media.dto.QueryMediaDto;
import com.zy.media.dto.UploadFileParamDto;
import com.zy.media.dto.UploadFileResultDto;
import com.zy.media.po.MediaFiles;
import com.zy.media.service.MediaFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class MediaFilesController {

    @Autowired
    MediaFilesService mediaFilesService;

    @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA)
    public UploadFileResultDto upload(@RequestPart("filedata")MultipartFile filedata,@RequestParam(value = "objectName",required = true)String objectName) throws IOException{
        Long companyId = 1232141425L;
        UploadFileParamDto dto = new UploadFileParamDto();
        dto.setFilename(filedata.getOriginalFilename());
        dto.setFileSize(filedata.getSize());
        // 图片
        dto.setFileType("001001");
        // 创建临时文件
        File tempFile = File.createTempFile("minio", "temp");
        filedata.transferTo(tempFile);
        // 文件路径
        String absolutePath = tempFile.getAbsolutePath();
        // 上传文件
        return mediaFilesService.upload(companyId, dto, absolutePath,objectName);
    }

    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaDto dto){
        Long companyId = 1232141425L;
        if(companyId == null) return null;
        return mediaFilesService.pageList(companyId,pageParams,dto);
    }
}
