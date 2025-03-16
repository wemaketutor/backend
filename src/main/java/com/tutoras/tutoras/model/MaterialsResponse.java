package com.tutoras.tutoras.model;

import java.util.List;

import lombok.Data;

@Data
public class MaterialsResponse {
    private List<MaterialResponse> materials;
    private int totalCount;
    private int page;
    private int perPage;
} 