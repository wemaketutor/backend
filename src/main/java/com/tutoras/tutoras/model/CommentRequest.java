package com.tutoras.tutoras.model;

import lombok.Data;

@Data
public class CommentRequest {
    private String body;
    private Long homeworkId;
} 