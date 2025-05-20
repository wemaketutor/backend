package com.tutoras.tutoras.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tutoras.tutoras.entity.MaterialEntity;
import com.tutoras.tutoras.entity.MaterialVisibleByUserEntity;
import com.tutoras.tutoras.entity.SourceEntity;
import com.tutoras.tutoras.entity.TeacherEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.exception.NotFindedSuchElementException;
import com.tutoras.tutoras.model.MaterialRequest;
import com.tutoras.tutoras.model.MaterialResponse;
import com.tutoras.tutoras.model.MaterialsResponse;
import com.tutoras.tutoras.repository.MaterialRepository;
import com.tutoras.tutoras.repository.MaterialVisibleByUserRepository;
import com.tutoras.tutoras.repository.TeacherRepository;
import com.tutoras.tutoras.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MaterialService {
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MaterialVisibleByUserRepository materialVisibleByUserRepository;
    
    @Autowired
    private PdfGeneratorService pdfGeneratorService;
    
    public MaterialsResponse getAllMaterials(int page, int perPage) {
        List<MaterialEntity> materials = materialRepository.findAll();
        
        int totalCount = materials.size();
        int fromIndex = (page - 1) * perPage;
        int toIndex = Math.min(fromIndex + perPage, totalCount);
        
        List<MaterialEntity> pagedMaterials = materials.subList(fromIndex, toIndex);
        
        MaterialsResponse response = new MaterialsResponse();
        response.setMaterials(pagedMaterials.stream()
                .map(this::mapToMaterialResponse)
                .collect(Collectors.toList()));
        response.setTotalCount(totalCount);
        response.setPage(page);
        response.setPerPage(perPage);
        
        return response;
    }
    
    public MaterialsResponse getMaterialsByTeacher(Long teacherId, int page, int perPage) {
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + teacherId));
        
        List<MaterialEntity> materials = materialRepository.findByTeacher(teacher);
        
        int totalCount = materials.size();
        int fromIndex = (page - 1) * perPage;
        int toIndex = Math.min(fromIndex + perPage, totalCount);
        
        List<MaterialEntity> pagedMaterials = materials.subList(fromIndex, toIndex);
        
        MaterialsResponse response = new MaterialsResponse();
        response.setMaterials(pagedMaterials.stream()
                .map(this::mapToMaterialResponse)
                .collect(Collectors.toList()));
        response.setTotalCount(totalCount);
        response.setPage(page);
        response.setPerPage(perPage);
        
        return response;
    }
    

    @Transactional(readOnly = true)
    public MaterialsResponse getMaterialsForStudent(Long studentId, int page, int perPage) {
        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));
        
        List<MaterialEntity> publicMaterials = materialRepository.findByIsPublic(true);
        
        List<MaterialVisibleByUserEntity> visibilities = materialVisibleByUserRepository.findByUser(student);
        List<MaterialEntity> privateMaterials = visibilities.stream()
                .map(MaterialVisibleByUserEntity::getMaterial)
                .collect(Collectors.toList());
        
        List<MaterialEntity> allMaterials = new ArrayList<>(publicMaterials);
        for (MaterialEntity material : privateMaterials) {
            if (!allMaterials.contains(material)) {
                allMaterials.add(material);
            }
        }
        
        int totalCount = allMaterials.size();
        int fromIndex = (page - 1) * perPage;
        int toIndex = Math.min(fromIndex + perPage, totalCount);
        
        List<MaterialEntity> pagedMaterials = 
            (fromIndex < allMaterials.size()) ? allMaterials.subList(fromIndex, toIndex) : new ArrayList<>();
        
        MaterialsResponse response = new MaterialsResponse();
        response.setMaterials(pagedMaterials.stream()
                .map(this::mapToMaterialResponse)
                .collect(Collectors.toList()));
        response.setTotalCount(totalCount);
        response.setPage(page);
        response.setPerPage(perPage);
        
        return response;
    }
    
    public MaterialsResponse getPublicMaterials(int page, int perPage) {
        List<MaterialEntity> materials = materialRepository.findByIsPublic(true);
        
        int totalCount = materials.size();
        int fromIndex = (page - 1) * perPage;
        int toIndex = Math.min(fromIndex + perPage, totalCount);
        
        List<MaterialEntity> pagedMaterials = materials.subList(fromIndex, toIndex);
        
        MaterialsResponse response = new MaterialsResponse();
        response.setMaterials(pagedMaterials.stream()
                .map(this::mapToMaterialResponse)
                .collect(Collectors.toList()));
        response.setTotalCount(totalCount);
        response.setPage(page);
        response.setPerPage(perPage);
        
        return response;
    }
    
    public MaterialResponse getMaterialById(Long id) {
        MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new NotFindedSuchElementException("Material not found with id: " + id));
        
        return mapToMaterialResponse(material);
    }
    
    @Transactional
    public MaterialResponse createMaterial(MaterialRequest request, Long teacherId) {
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + teacherId));
        
        String fileUrl = request.getFileUrl();
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            SourceEntity source = new SourceEntity(
                request.getTitle(),
                request.getDescription(),
                "Автоматически сгенерированный материал"
            );
            
            try {
                fileUrl = pdfGeneratorService.generatePdf(source);
                
                if (fileUrl == null || fileUrl.trim().isEmpty()) {
                    fileUrl = "/files/pdf/default.pdf";
                }
            } catch (Exception e) {
                fileUrl = "/files/pdf/default.pdf";
            }
        }
        
        MaterialEntity material = new MaterialEntity(
                request.getTitle(),
                request.getDescription(),
                fileUrl,
                request.getIsPublic(),
                teacher
        );
        
        MaterialEntity savedMaterial = materialRepository.save(material);
        
        if (!request.getIsPublic() && request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
            for (Long userId : request.getStudentIds()) {
                UserEntity user = userRepository.findById(userId)
                        .orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));
                
                MaterialVisibleByUserEntity visibility = new MaterialVisibleByUserEntity(savedMaterial, user);
                materialVisibleByUserRepository.save(visibility);
            }
        }
        
        return mapToMaterialResponse(savedMaterial);
    }
    
    @Transactional
    public MaterialResponse updateMaterial(Long id, MaterialRequest request) {
        MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Material not found with id: " + id));
        
        if (request.getTitle() != null) {
            material.setTitle(request.getTitle());
        }
        
        if (request.getDescription() != null) {
            material.setDescription(request.getDescription());
        }
        
        if (request.getFileUrl() != null) {
            material.setFileUrl(request.getFileUrl());
        }
        
        if (request.getIsPublic() != null) {
            material.setIsPublic(request.getIsPublic());
        }
        
        MaterialEntity updatedMaterial = materialRepository.save(material);
        
        if (request.getStudentIds() != null) {
            List<MaterialVisibleByUserEntity> currentVisibilities = 
                    materialVisibleByUserRepository.findByMaterial(updatedMaterial);
            materialVisibleByUserRepository.deleteAll(currentVisibilities);
            
            for (Long userId : request.getStudentIds()) {
                UserEntity user = userRepository.findById(userId)
                        .orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));
                
                MaterialVisibleByUserEntity visibility = new MaterialVisibleByUserEntity(updatedMaterial, user);
                materialVisibleByUserRepository.save(visibility);
            }
        }
        
        return mapToMaterialResponse(updatedMaterial);
    }
    
    @Transactional
    public void deleteMaterial(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new NotFindedSuchElementException("Material not found with id: " + id);
        }
        

        MaterialEntity material = materialRepository.findById(id).get();
        List<MaterialVisibleByUserEntity> visibilities = materialVisibleByUserRepository.findByMaterial(material);
        materialVisibleByUserRepository.deleteAll(visibilities);

        materialRepository.deleteById(id);
    }
    
    private MaterialResponse mapToMaterialResponse(MaterialEntity material) {
        MaterialResponse response = new MaterialResponse();
        response.setId(material.getId());
        response.setTitle(material.getTitle());
        response.setDescription(material.getDescription());
        response.setFileUrl(material.getFileUrl());
        response.setIsPublic(material.getIsPublic());
        
        if (material.getTeacher() != null) {
            response.setTeacherId(material.getTeacher().getId());
        }
        
        List<MaterialVisibleByUserEntity> visibilities = 
                materialVisibleByUserRepository.findByMaterial(material);
        
        List<Long> visibleToUsers = new ArrayList<>();
        for (MaterialVisibleByUserEntity visibility : visibilities) {
            UserEntity user = visibility.getUser();           
            visibleToUsers.add(user.getId());
        }
        
        response.setStudentIds(visibleToUsers);
        
        return response;
    }
} 