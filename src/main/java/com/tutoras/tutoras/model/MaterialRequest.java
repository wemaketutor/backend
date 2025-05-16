package com.tutoras.tutoras.model;

import java.util.List;

import lombok.Data;

@Data
public class MaterialRequest {
    private String title;
    private String description;
    private String fileUrl;
    private Boolean isPublic;
    private List<Long> studentIds;
} 