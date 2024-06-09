package com.zy.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.content.model.po.CoursePublish;

import java.io.File;

/**
 * <p>
 * 课程发布 服务类
 * </p>
 *
 * @author zhangYu
 * @since 2024-05-20
 */
public interface CoursePublishService extends IService<CoursePublish> {
    public void commitAudit(Long companyId,Long courseId);

    public void publish(Long companyId,Long courseId);

    public File generateCourseHtml(Long courseId);

    public void  uploadCourseHtml(Long courseId,File file);
}
