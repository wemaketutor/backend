package com.tutoras.tutoras.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HomeworksResponse {
    private List<HomeworkResponse> homeworks;
    
    // Добавляем поле для обратной совместимости с фронтендом
    @JsonProperty("students")
    public List<HomeworkResponse> getStudents() {
        return homeworks;
    }
    
    private int totalCount;
    private int page;
    private int perPage;
} 