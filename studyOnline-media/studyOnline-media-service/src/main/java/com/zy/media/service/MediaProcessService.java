package com.zy.media.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.media.po.MediaProcess;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangyu
 * @since 2024-06-03
 */
public interface MediaProcessService extends IService<MediaProcess> {


    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    public boolean startTask(long id);

    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
