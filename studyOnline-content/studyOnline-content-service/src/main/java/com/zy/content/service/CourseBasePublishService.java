package com.zy.content.service;

import com.zy.content.model.dto.CoursePreviewDto;

public interface CourseBasePublishService {
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);
}
