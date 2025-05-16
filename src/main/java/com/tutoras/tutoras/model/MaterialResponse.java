package com.tutoras.tutoras.model;

import java.util.List;

import lombok.Data;

@Data
public class MaterialResponse {
    private Long id;
    private String title;   
    private String description;
    private String fileUrl;
    private Boolean isPublic;
    private Long teacherId;
    private List<Long> studentIds;
} 