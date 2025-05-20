package com.tutoras.tutoras.model;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long homeworkId;
    private Long userId;
} 