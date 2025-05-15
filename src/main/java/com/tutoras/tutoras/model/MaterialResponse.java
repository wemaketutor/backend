package com.tutoras.tutoras.model;

import java.util.List;

import com.tutoras.tutoras.model.UserResponse.UserData;

import lombok.Data;

@Data
public class MaterialResponse {
    private Long id;
    private String title;
    private String subject;
    private String description;
    private String fileUrl;
    private Boolean isPublic;
    private Long teacherId;
    private String teacherName;
    private List<UserData> visibleToUsers;
} 