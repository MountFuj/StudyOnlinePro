package com.zy.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.zy.base.exception.StudyOnlineException;
import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.base.model.RestResponse;
import com.zy.media.dto.QueryMediaDto;
import com.zy.media.dto.UploadFileParamDto;
import com.zy.media.dto.UploadFileResultDto;
import com.zy.media.mapper.MediaFilesMapper;
import com.zy.media.mapper.MediaProcessMapper;
import com.zy.media.po.MediaFiles;
import com.zy.media.po.MediaProcess;
import com.zy.media.service.MediaFilesService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 媒资信息 服务实现类
 * </p>
 *
 * @author zhangyu
 */
@Slf4j
@Service
public class MediaFilesServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFilesService {

    @Autowired
    MinioClient minioClient;

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MediaProcessMapper mediaProcessMapper;

    @Autowired
    MediaFilesService proxy;

    @Value("${minio.bucket.files}")
    private String bucket_files;

    @Value("${minio.bucket.videofiles}")
    private String video_files;

    @Override
    @Transactional
    public UploadFileResultDto upload(Long companyId, UploadFileParamDto dto, String localFilepath) {
        File file = new File(localFilepath);
        if(!file.exists()){
            StudyOnlineException.cast("文件不存在");
        }
        // 文件名称
        String filename = dto.getFilename();
        // 文件拓展名
        String extension = filename.substring(filename.lastIndexOf("."));
        // 文件mimeType
        String mimeType = getMimeType(extension);
        // 文件md5
        String fileMd5 = getFileMd5(file);
        // 文件默认目录
        String defaultFolderPath = getDefaultFolderPath();
        // objectName
        String objectName = defaultFolderPath+fileMd5+extension;
        // 将文件上传到Minio
        boolean b = addMediaFilesToMinio(localFilepath, mimeType, bucket_files, objectName);
        // 文件大小
        dto.setFileSize(file.length());
        // 存数据库
        MediaFiles mediaFiles = proxy.addMediaFilesToDb(companyId, fileMd5, dto, bucket_files, objectName);
        // 返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles,uploadFileResultDto);
        return uploadFileResultDto;
    }

    // 获取文件默认存储路径 年/月/日
    private String getDefaultFolderPath(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date()).replace("-","/")+"/";
    }

    // 获取文件的md5
    private String getFileMd5(File file){
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            return DigestUtils.md5Hex(fileInputStream);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String getMimeType(String extension){
        if(extension == null) extension = "";
        // 根据拓展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        // 通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }
    /*
    添加文件到minio
     */
    public boolean addMediaFilesToMinio(String localFilePath,String mimeType,String bucket,String objectName){
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传到minio出错,bucket:{},objectName:{}",bucket,objectName);
            StudyOnlineException.cast("上传到文件系统失败");
        }
        return false;
    }

    /*
    上传文件到数据库
     */
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamDto uploadFileParamDto,String bucket,String objectName){
        // 从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles == null){
            mediaFiles = new MediaFiles();
            // 拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamDto,mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/"+bucket+"/"+objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            // 保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if(insert<0){
                log.error("保存文件信息到数据库失败,{}",mediaFiles);
                StudyOnlineException.cast("保存文件信息失败");
            }
            // 添加到待处理任务表
            addAwaitingTask(mediaFiles);
            log.debug("保存文件信息到数据库成功，{}",mediaFiles);
        }
        return mediaFiles;
    }

    private void addAwaitingTask(MediaFiles mediaFiles) {
        // 文件名称
        String filename = mediaFiles.getFilename();
        // 拓展名
        String extension = filename.substring(filename.lastIndexOf("."));
        // 文件mimeType
        String mimeType = getMimeType(extension);
        // avi视频加入待处理任务表
        if(mimeType.equals("video/x-msvideo")){
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles,mediaProcess);
            mediaProcess.setStatus("1"); // 未处理
            mediaProcess.setFailCount(0); //失败次数默认为0
            mediaProcessMapper.insert(mediaProcess);
        }
    }

    @Override
    public PageResult<MediaFiles> pageList(Long companyId,PageParams pageParams, QueryMediaDto dto) {
        Page<MediaFiles> mediaPage = new Page<>(pageParams.getPageNo(),pageParams.getPageSize());
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaFiles::getCompanyId,companyId);
        queryWrapper.eq(StringUtils.isNoneEmpty(dto.getFileType()),MediaFiles::getFileType,dto.getFileType());
        queryWrapper.like(StringUtils.isNotEmpty(dto.getFilename()),MediaFiles::getFilename,dto.getFilename());
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(mediaPage, queryWrapper);
        List<MediaFiles> items = pageResult.getRecords();
        long total = pageResult.getTotal();
        return new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        // 检查文件是否在minio存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles!=null){
            String bucket = mediaFiles.getBucket();
            String filePath = mediaFiles.getFilePath();
            InputStream stream = null;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build()
                );
                if(stream!=null){
                    // 文件存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {

            }
        }
        // 文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        // 得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        // 分块文件路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        // 文件流
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(video_files)
                            .object(chunkFilePath)
                            .build());
            if(fileInputStream!=null){
                // 分块存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {

        }
        // 分块不存在
        return RestResponse.success(false);
    }

    // 上传分块
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
        // 分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        // 分块路径
        String chunkFilePath = chunkFileFolderPath + chunk;
        // mimeType
        String mimeType = getMimeType(null);
        // 存储minio
        boolean b = addMediaFilesToMinio(localChunkFilePath, mimeType,video_files,chunkFilePath);
        if(!b){
            log.debug("上传分块文件失败,{}",chunkFilePath);
            return RestResponse.validfail(false,"上传分块文件失败");
        }
        log.debug("上传分块文件成功,{}",chunkFilePath);
        System.out.println("上传分块文件成功"+chunkFilePath);
        return RestResponse.success(true);
    }

    /*
    合并分块
     */
    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamDto dto) {
        // 获取分块文件路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        // 将分块文件路径组成 List<ComposeSource>
        List<ComposeSource> sourceList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(video_files)
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        // 合并
        // 文件名称
        String filename = dto.getFilename();
        // 文件拓展名
        String extension = filename.substring(filename.lastIndexOf("."));
        // 合并文件路径
        String merageFilePath = getFilePathByMd5(fileMd5,extension);
        // 合并文件
        try {
            minioClient.composeObject(ComposeObjectArgs.builder()
                            .bucket(video_files)
                            .object(merageFilePath)
                            .sources(sourceList)
                    .build());
            log.debug("合并文件成功,{}",merageFilePath);
        } catch (Exception e) {
            log.error("合并文件失败,{}",merageFilePath);
            return RestResponse.validfail(false,"合并文件失败");
        }
        // -- 验证md5 --
        // 下载合并后的文件
        File miniofile = downloadFileFromMinio(video_files,merageFilePath);
        if(miniofile == null){
            log.debug("下载合并后的文件失败,{}",merageFilePath);
            return RestResponse.validfail(false,"下载合并后的文件失败");
        }
        try(InputStream newFileInputStream = new FileInputStream(miniofile)) {
            // minio上文件的md5值
            String md5Hex = DigestUtils.md5Hex(newFileInputStream);
            // 比较md5 不一致说明文件不完整
            if(!fileMd5.equals(md5Hex)){
                return RestResponse.validfail(false,"文件合并校验失败，最终上传失败");
            }
            // 文件大小
            dto.setFileSize(miniofile.length());
        }catch (Exception e){
            log.debug("校验文件失败，fileMd5:{},异常：{}",fileMd5,e.getMessage(),e);
            return RestResponse.validfail(false,"校验文件失败，最终上传失败");
        }finally {
            if(miniofile!=null){
                miniofile.delete();
            }
        }
        // 文件入库
        proxy.addMediaFilesToDb(companyId,fileMd5,dto,video_files,merageFilePath);
        // ===清除分块文件===
        clearChunkFiles(chunkFileFolderPath,chunkTotal);
        return RestResponse.success(true);
    }

    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {
        try {
            List<DeleteObject> deleteObjectList = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());
            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                    .bucket(video_files)
                    .objects(deleteObjectList)
                    .build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r->{
                DeleteError deleteError = null;
                try{
                    deleteError = r.get();
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("删除分块文件失败,{}",deleteError.objectName(),e);
                }
            });
        } catch (Exception e) {
           e.printStackTrace();
           log.error("删除分块文件失败,路径：{}",chunkFileFolderPath,e);
        }
    }

    public File downloadFileFromMinio(String videoFiles, String merageFilePath) {
        // 临时文件
        File miniofile = null;
        FileOutputStream fileOutputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                            .bucket(videoFiles)
                            .object(merageFilePath)
                    .build());
            // 创建临时文件
            miniofile = File.createTempFile("minio", ".merge");
            fileOutputStream = new FileOutputStream(miniofile);
            IOUtils.copy(stream,fileOutputStream);
            return miniofile;
        } catch (Exception e) {
           e.printStackTrace();
        }finally {
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String getFilePathByMd5(String fileMd5, String extension) {
        return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+extension;
    }

    //得到分块文件的目录
    public String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }
}
