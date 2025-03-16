package com.tutoras.tutoras.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HomeworkResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String status;
    private Integer grade;
    private Integer assessmentScale;
    private Long studentId;
    private String studentName;
    private Long lessonId;
    private String lessonName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 