package com.tutoras.tutoras.model;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonResponse {
    private Long id;

    private String name;

    private String description;

    private String subject;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private List<Long> studentIds;

    private String homeworkLink;

    private OffsetDateTime date_created;
    
    private OffsetDateTime update_at;
}
