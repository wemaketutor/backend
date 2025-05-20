package com.tutoras.tutoras.model;

import java.util.List;

import lombok.Data;

@Data
public class CommentsResponse {
    private List<CommentResponse> comments;
    private int totalCount;
} 