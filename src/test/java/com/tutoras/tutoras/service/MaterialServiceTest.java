package com.tutoras.tutoras.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.tutoras.tutoras.entity.MaterialEntity;
import com.tutoras.tutoras.entity.MaterialVisibleByUserEntity;
import com.tutoras.tutoras.entity.Role;
import com.tutoras.tutoras.entity.TeacherEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.MaterialRequest;
import com.tutoras.tutoras.model.MaterialResponse;
import com.tutoras.tutoras.model.MaterialsResponse;
import com.tutoras.tutoras.repository.MaterialRepository;
import com.tutoras.tutoras.repository.MaterialVisibleByUserRepository;
import com.tutoras.tutoras.repository.TeacherRepository;
import com.tutoras.tutoras.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;
    
    @Mock
    private TeacherRepository teacherRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private MaterialVisibleByUserRepository materialVisibleByUserRepository;
    
    @InjectMocks
    private MaterialService materialService;
    
    private MaterialEntity testMaterial;
    private TeacherEntity testTeacher;
    private UserEntity testTeacherUser;
    
    @BeforeEach
    void setUp() {
        testTeacherUser = new UserEntity("teacher@example.com", "password", Role.fromString("teacher"));
        testTeacherUser.setId(1L);
        testTeacherUser.setFirstName("Teacher");
        testTeacherUser.setLastName("Test");
        
        testTeacher = mock(TeacherEntity.class);
        when(testTeacher.getId()).thenReturn(1L);
        when(testTeacher.getUser()).thenReturn(testTeacherUser);
        
        testMaterial = mock(MaterialEntity.class);
        when(testMaterial.getId()).thenReturn(1L);
        when(testMaterial.getTitle()).thenReturn("Test Material");
        when(testMaterial.getDescription()).thenReturn("This is a test material description");
        when(testMaterial.getFileUrl()).thenReturn("http://example.com/test-material.pdf");
        when(testMaterial.getIsPublic()).thenReturn(true);
        when(testMaterial.getTeacher()).thenReturn(testTeacher);
    }
    
    @Test
    void getMaterialById_ShouldReturnMaterial_WhenMaterialExists() {
        when(materialRepository.findById(1L)).thenReturn(Optional.of(testMaterial));
        when(materialVisibleByUserRepository.findByMaterial(testMaterial)).thenReturn(new ArrayList<>());
        
        MaterialResponse response = materialService.getMaterialById(1L);
        
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Material", response.getTitle());
        assertEquals("This is a test material description", response.getDescription());
        assertEquals("http://example.com/test-material.pdf", response.getFileUrl());
        assertTrue(response.getIsPublic());
        assertEquals(1L, response.getTeacherId());
        
        verify(materialRepository, times(1)).findById(1L);
        verify(materialVisibleByUserRepository, times(1)).findByMaterial(testMaterial);
    }
    
    @Test
    void getMaterialById_ShouldThrowException_WhenMaterialDoesNotExist() {
        when(materialRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, 
                () -> materialService.getMaterialById(999L));
        
        verify(materialRepository, times(1)).findById(999L);
    }
    
    @Test
    void getPublicMaterials_ShouldReturnPublicMaterials() {
        List<MaterialEntity> publicMaterials = Arrays.asList(testMaterial);
        when(materialRepository.findByIsPublic(true)).thenReturn(publicMaterials);
        when(materialVisibleByUserRepository.findByMaterial(testMaterial)).thenReturn(new ArrayList<>());
        
        MaterialsResponse response = materialService.getPublicMaterials(1, 10);
        
        assertNotNull(response);
        assertEquals(1, response.getTotalCount());
        assertEquals(1, response.getPage());
        assertEquals(10, response.getPerPage());
        assertNotNull(response.getMaterials());
        assertEquals(1, response.getMaterials().size());
        assertEquals("Test Material", response.getMaterials().get(0).getTitle());
        
        verify(materialRepository, times(1)).findByIsPublic(true);
    }
    
    @Test
    void createMaterial_ShouldCreateAndReturnMaterial() {
        MaterialRequest request = new MaterialRequest();
        request.setTitle("New Material");
        request.setDescription("New material description");
        request.setFileUrl("http://example.com/new-material.pdf");
        request.setIsPublic(false);
        List<Long> visibleToUserIds = Arrays.asList(2L, 3L);
        request.setStudentIds(visibleToUserIds);
        
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        
        MaterialEntity newMaterial = mock(MaterialEntity.class);
        when(newMaterial.getId()).thenReturn(2L);
        when(newMaterial.getTitle()).thenReturn("New Material");
        when(newMaterial.getDescription()).thenReturn("New material description");
        when(newMaterial.getFileUrl()).thenReturn("http://example.com/new-material.pdf");
        when(newMaterial.getIsPublic()).thenReturn(false);
        when(newMaterial.getTeacher()).thenReturn(testTeacher);
        
        when(materialRepository.save(any(MaterialEntity.class))).thenReturn(newMaterial);
        
        UserEntity user2 = new UserEntity("user2@example.com", "password", Role.fromString("student"));
        user2.setId(2L);
        UserEntity user3 = new UserEntity("user3@example.com", "password", Role.fromString("student"));
        user3.setId(3L);
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));
        
        when(materialVisibleByUserRepository.findByMaterial(newMaterial)).thenReturn(new ArrayList<>());
        
        MaterialResponse response = materialService.createMaterial(request, 1L);
        
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("New Material", response.getTitle());
        assertEquals("New material description", response.getDescription());
        assertEquals("http://example.com/new-material.pdf", response.getFileUrl());
        assertFalse(response.getIsPublic());
        assertEquals(1L, response.getTeacherId());
        
        verify(teacherRepository, times(1)).findById(1L);
        verify(materialRepository, times(1)).save(any(MaterialEntity.class));
        verify(userRepository, times(2)).findById(anyLong());
        verify(materialVisibleByUserRepository, times(2)).save(any(MaterialVisibleByUserEntity.class));
    }
} 