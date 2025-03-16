package com.tutoras.tutoras.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HomeworkRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String status;
    private Integer grade;
    private Integer assessmentScale;
    private Long studentId;
    private Long lessonId;
} 