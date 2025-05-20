package com.tutoras.tutoras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MaterialController extends BaseController {
    
    @Autowired
    private MaterialService materialService;
    
    @Autowired
    private UserService userService;
    
    @Value("${spring.web.resources.static-locations[0]}")
    private String uploadDir;
    
    @GetMapping("/materials")
    public ResponseEntity<MaterialsResponse> getAllMaterials(
            @RequestParam(name = "teacherId", required = false) Long teacherId,
            @RequestParam(name = "studentId", required = false) Long studentId,
            @RequestParam(name = "isPublic", required = false) Boolean isPublic,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int per_page,
            @RequestParam(name = "sort_by", defaultValue = "title") String sort_by,
            @RequestParam(name = "sort_order", defaultValue = "desc") String sort_order) {
        
        if (teacherId != null) {
            return ResponseEntity.ok(materialService.getMaterialsByTeacher(teacherId, page, per_page));
        } else if (studentId != null) {
            return ResponseEntity.ok(materialService.getMaterialsForStudent(studentId, page, per_page));
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
    
    @GetMapping(value = "/download/{relativePath}/{fileName:.+}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String relativePath,
            @PathVariable String fileName,
            HttpServletResponse response) {
        
        String basePath = uploadDir.replace("file:", "");
        Path filePath = Paths.get(basePath, relativePath, fileName);
        
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new FileSystemResource(filePath.toFile());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
    
    @GetMapping(value = "/materials/{id}/download")
    public ResponseEntity<Resource> downloadMaterialFile(@PathVariable("id") Long id) {
        log.info("Запрос на скачивание файла для материала с ID: {}", id);
        
        MaterialResponse material = materialService.getMaterialById(id);
        
        if (material == null) {
            log.warn("Материал с ID {} не найден", id);
            return ResponseEntity.notFound().build();
        }
        
        if (material.getFileUrl() == null || material.getFileUrl().trim().isEmpty()) {
            log.warn("У материала {} отсутствует URL файла", id);
            return ResponseEntity.notFound().build();
        }
        
        String fileUrl = material.getFileUrl();
        log.info("URL файла материала {}: {}", id, fileUrl);
        
        String basePath = uploadDir.replace("file:", "");
        Path filePath = Paths.get(basePath, fileUrl);
        log.info("Полный путь к файлу: {}", filePath.toAbsolutePath());
        
        if (!Files.exists(filePath)) {
            log.warn("Файл не найден по пути: {}", filePath.toAbsolutePath());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        
        String contentType = determineContentType(filePath.toString());
        log.info("Определен тип контента: {}", contentType);
        
        Resource resource = new FileSystemResource(filePath.toFile());
        
        try {
            log.info("Файл найден, размер: {} байт", Files.size(filePath));
        } catch (IOException e) {
            log.error("Ошибка при получении размера файла: {}", e.getMessage());
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + material.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") + getFileExtension(fileUrl) + "\"")
                .body(resource);
    }
    

    private String determineContentType(String filePath) {
        if (filePath.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF_VALUE;
        } else if (filePath.endsWith(".txt")) {
            return MediaType.TEXT_PLAIN_VALUE;
        } else if (filePath.endsWith(".html") || filePath.endsWith(".htm")) {
            return MediaType.TEXT_HTML_VALUE;
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        } else if (filePath.endsWith(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }
    
    private String getFileExtension(String fileUrl) {
        int lastDotIndex = fileUrl.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileUrl.substring(lastDotIndex);
        }
        return ".pdf";
    }
} 