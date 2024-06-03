package com.zy.content.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddOrUpdateCourseTeacher {

    private Long id;

    private Long courseId;

    private String teacherName;

    private String position;

    private String introduction;

    private String photograph;

    private LocalDateTime createDate;
}
