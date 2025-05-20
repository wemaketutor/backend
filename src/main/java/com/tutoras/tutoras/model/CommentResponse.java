package com.tutoras.tutoras.model;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private Long homeworkId;
    private Long userId;
    private String userFullName;
    private String userRole;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
} 