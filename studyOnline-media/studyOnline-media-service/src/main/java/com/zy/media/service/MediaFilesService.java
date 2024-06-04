package com.zy.media.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.base.model.RestResponse;
import com.zy.media.dto.QueryMediaDto;
import com.zy.media.dto.UploadFileParamDto;
import com.zy.media.dto.UploadFileResultDto;
import com.zy.media.po.MediaFiles;

/**
 * <p>
 * 媒资信息 服务类
 * </p>
 *
 * @author zhangyu
 * @since 2024-06-03
 */
public interface MediaFilesService extends IService<MediaFiles> {
    public UploadFileResultDto upload(Long companyId, UploadFileParamDto dto,String localFilepath);

    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamDto uploadFileParamDto,String bucket,String objectName);

    public PageResult<MediaFiles> pageList(Long companyId,PageParams pageParams, QueryMediaDto dto);

    public RestResponse<Boolean> checkFile(String fileMd5);

    public RestResponse<Boolean> checkChunk(String fileMd5,int chunkIndex);

    public RestResponse uploadChunk(String fileMd5,int chunk,String localChunkFilePath);

    public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamDto dto);
}
