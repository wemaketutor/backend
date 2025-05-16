package com.tutoras.tutoras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.*;
import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.MaterialService;
import com.tutoras.tutoras.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MaterialController extends BaseController {
    
    @Autowired
    private MaterialService materialService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/materials")
    public ResponseEntity<MaterialsResponse> getAllMaterials(
            @RequestParam(name = "teacherId", required = false) Long teacherId,
            @RequestParam(name = "isPublic", required = false) Boolean isPublic,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int per_page,
            @RequestParam(name = "sort_by", defaultValue = "title") String sort_by,
            @RequestParam(name = "sort_order", defaultValue = "desc") String sort_order) {
        
        if (teacherId != null) {
            return ResponseEntity.ok(materialService.getMaterialsByTeacher(teacherId, page, per_page));
        } else if (isPublic != null && isPublic) {
            return ResponseEntity.ok(materialService.getPublicMaterials(page, per_page));
        } else {
            return ResponseEntity.ok(materialService.getAllMaterials(page, per_page));
        }
    }
    
    @GetMapping("/material/{materialId}")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable("materialId") Long materialId) {
        return ResponseEntity.ok(materialService.getMaterialById(materialId));
    }
    
    @PostMapping("/materials")
    public ResponseEntity<MaterialResponse> createMaterial(@RequestBody MaterialRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materialService.createMaterial(request, principal.getUserId()));
    }
    
    @PutMapping("/materials/{materialId}")
    public ResponseEntity<MaterialResponse> updateMaterial(
            @PathVariable("materialId") Long materialId,
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
            @PathVariable("materialId") Long materialId,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        MaterialResponse material = materialService.getMaterialById(materialId);
        if (!material.getTeacherId().equals(principal.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        materialService.deleteMaterial(materialId);
        return ResponseEntity.noContent().build();
    }
} 