package com.tutoras.tutoras.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentResponse {
    private Long id;
    private String body;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 