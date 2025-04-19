package com.tutoras.tutoras.model;

import java.util.List;

import lombok.Data;

@Data
public class HomeworksResponse {
    private List<HomeworkResponse> homeworks;
    private int totalCount;
    private int page;
    private int perPage;
} 