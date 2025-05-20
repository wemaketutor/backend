package com.tutoras.tutoras.model;

import java.util.List;

import lombok.Data;

@Data 
public class HomeworkListResponse {
    private List<HomeworkResponse> homeworks;
}
