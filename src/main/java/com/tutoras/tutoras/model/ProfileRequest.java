package com.tutoras.tutoras.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ProfileRequest {
    private String firstName;
    private String lastName;
    private String extraInfo;
    private MultipartFile avatar;
}
