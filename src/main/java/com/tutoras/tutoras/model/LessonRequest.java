package com.tutoras.tutoras.model;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;

@Data
public class LessonRequest {
    private Long teacherId;
    private String name;
    private String description;
    private String subject;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private List<Long> studentIds;
    private String homeworkLink;
} 