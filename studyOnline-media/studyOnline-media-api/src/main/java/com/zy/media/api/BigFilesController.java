package com.zy.media.api;

import com.zy.base.model.RestResponse;
import com.zy.media.dto.UploadFileParamDto;
import com.zy.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@Api(value = "大文件上传接口")
public class BigFilesController {

    @Autowired
    MediaFilesService mediaFilesService;

    @ApiOperation("文件检查")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5) throws Exception{
        return mediaFilesService.checkFile(fileMd5);
    }

    @ApiOperation("分块检查")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5")String fileMd5,
                                            @RequestParam("chunk")int chunk) throws Exception{
        return mediaFilesService.checkChunk(fileMd5,chunk);
    }

    @ApiOperation("上传分块")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file")MultipartFile file,
                                    @RequestParam("fileMd5")String fileMd5,
                                    @RequestParam("chunk")int chunk) throws Exception{
        File tempFile = File.createTempFile("minio", "temp");
        file.transferTo(tempFile);
        String absolutePath = tempFile.getAbsolutePath();
        return mediaFilesService.uploadChunk(fileMd5,chunk,absolutePath);
    }

    @ApiOperation("合并分块")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergeChunks(@RequestParam("fileMd5")String fileMd5,
                                    @RequestParam("fileName")String fileName,
                                    @RequestParam("chunkTotal")int chunkTotal) throws Exception{
        Long companyId = 1232141425L;
        UploadFileParamDto uploadFileParamDto = new UploadFileParamDto();
        uploadFileParamDto.setFileType("001002");
        uploadFileParamDto.setTags("课程视频");
        uploadFileParamDto.setRemark("");
        uploadFileParamDto.setFilename(fileName);
        return mediaFilesService.mergechunks(companyId,fileMd5,chunkTotal,uploadFileParamDto);
    }
}
