package com.zy.media.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zy.base.utils.Mp4VideoUtil;
import com.zy.media.po.MediaProcess;
import com.zy.media.service.MediaFilesService;
import com.zy.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class VideoTask {

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    MediaProcessService mediaProcessService;

    @Value("${{videoprocess.ffmpegpath}")
    private String ffmpegPath;

    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception{
        System.out.println("=======开始执行=====");
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        List<MediaProcess> mediaProcessList = null;
        int size = 0;
        try {
            // 取出cpu核心数作为一次处理数据的条数
            int processors = Runtime.getRuntime().availableProcessors();
            // 一次处理视频数量不要超过cpu核心数
            mediaProcessList = mediaProcessService.getMediaProcessList(shardIndex,shardTotal,processors);
            size = mediaProcessList.size();
            log.debug("取到待处理视频{}条记录",size);
            if(size <= 0){
                return ;
            }
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        // 启动size个线程的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        // 将处理任务加入线程池
        mediaProcessList.forEach(mediaProcess -> {
            executorService.execute(()->{
                try {
                    // 任务id
                    Long taskId = mediaProcess.getId();
                    // 抢占任务
                    boolean b = mediaProcessService.startTask(taskId);
                    if(!b) return;
                    log.debug("开始处理视频任务，任务:{},任务id:{}",mediaProcess,taskId);
                    // 处理逻辑
                    String bucket = mediaProcess.getBucket();
                    String filePath = mediaProcess.getFilePath();
                    String fileId = mediaProcess.getFileId();
                    String filename = mediaProcess.getFilename();
                    // 将要处理的文件下载到服务器
                    File orignalFile = mediaFilesService.downloadFileFromMinio(mediaProcess.getBucket(), mediaProcess.getFilePath());
                    if(orignalFile == null){
                        log.debug("下载视频文件失败，originalFile:{}",mediaProcess.getBucket().concat(mediaProcess.getFilePath()));
                        return;
                    }
                    // 处理下载的视频文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("mp4",".mp4");
                    }catch (Exception e){
                        log.error("创建临时文件失败");
                        mediaProcessService.saveProcessFinishStatus(mediaProcess.getId(),"3",fileId,null,"创建临时文件失败");
                        return;
                    }
                    // 视频处理结果
                    String result = "";
                    try {
                        // 开始处理视频
                        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegPath, orignalFile.getAbsolutePath(), mp4File.getName(), mp4File.getAbsolutePath());
                        // 开始视频转换，成功返回success
                        result = mp4VideoUtil.generateMp4();
                    }catch (Exception e){
                        e.printStackTrace();
                        log.error("视频处理失败:{},出错：{}",mediaProcess.getFilePath(),e.getMessage());
                    }
                    if(!result.equals("success")){
                        // 记录错误信息
                        log.error("视频处理失败:{},出错：{}",mediaProcess.getFilePath(),result);
                        mediaProcessService.saveProcessFinishStatus(mediaProcess.getId(),"3",fileId,null,result);
                        return;
                    }
                    // 将mp4上传到minio
                    String objectName = getFilePath(fileId, ".mp4");
                    String url = "/"+bucket+"/"+objectName;
                    try {
                        mediaFilesService.addMediaFilesToMinio(mp4File.getAbsolutePath(),"video/mp4",bucket,objectName);
                        mediaProcessService.saveProcessFinishStatus(mediaProcess.getId(),"2",fileId,url,null);
                    }catch (Exception e){
                        log.error("上传视频失败:{},出错：{}",mediaProcess.getFilePath(),e.getMessage());
                        // 最终还是失败了
                        mediaProcessService.saveProcessFinishStatus(mediaProcess.getId(),"3",fileId,null,"处理后视频上传或入库失败");
                    }
                }finally {
                    countDownLatch.countDown();
                }
            });
        });
        // 等待，给一个充裕的超时时间，防止无限等待，到达超时时间还没有处理完成则退出
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    private String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }
}
