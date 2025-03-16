package com.tutoras.tutoras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.*;
import com.tutoras.tutoras.service.MaterialService;
import com.tutoras.tutoras.service.UserService;

@RestController
public class MaterialController {
    
    @Autowired
    private MaterialService materialService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/materials")
    public ResponseEntity<MaterialsResponse> getAllMaterials(
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int per_page) {
        
        if (teacherId != null) {
            return ResponseEntity.ok(materialService.getMaterialsByTeacher(teacherId, page, per_page));
        } else if (isPublic != null && isPublic) {
            return ResponseEntity.ok(materialService.getPublicMaterials(page, per_page));
        } else {
            return ResponseEntity.ok(materialService.getAllMaterials(page, per_page));
        }
    }
    
    @GetMapping("/materials/{materialId}")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable Long materialId) {
        return ResponseEntity.ok(materialService.getMaterialById(materialId));
    }
    
    @PostMapping("/materials")
    public ResponseEntity<MaterialResponse> createMaterial(
            @RequestBody MaterialRequest request,
            Authentication authentication) {
        UserEntity user = userService.getUserByEmail(authentication.getName());
        
        if (!"teacher".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Long teacherId = user.getId();
        
        MaterialResponse response = materialService.createMaterial(request, teacherId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/materials/{materialId}")
    public ResponseEntity<MaterialResponse> updateMaterial(
            @PathVariable Long materialId,
            @RequestBody MaterialRequest request,
            Authentication authentication) {
        UserEntity user = userService.getUserByEmail(authentication.getName());
        
        if (!"teacher".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        MaterialResponse material = materialService.getMaterialById(materialId);
        if (!material.getTeacherId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(materialService.updateMaterial(materialId, request));
    }
    
    @DeleteMapping("/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable Long materialId,
            Authentication authentication) {
        UserEntity user = userService.getUserByEmail(authentication.getName());
        
        if (!"teacher".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        MaterialResponse material = materialService.getMaterialById(materialId);
        if (!material.getTeacherId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        materialService.deleteMaterial(materialId);
        return ResponseEntity.noContent().build();
    }
} 