package com.tutoras.tutoras.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.UserResponse;
import com.tutoras.tutoras.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    private UserEntity testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new UserEntity("test@example.com", "password", "teacher");
        testUser.setId(1L);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
    }
    
    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        Optional<UserEntity> result = userService.findByEmail("test@example.com");
        
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }
    
    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        Optional<UserEntity> result = userService.findByEmail("nonexistent@example.com");
        
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }
    
    @Test
    void getProfile_ShouldReturnUserResponse_WhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        ResponseEntity<?> response = userService.getProfile("test@example.com");
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserResponse);
        UserResponse userResponse = (UserResponse) response.getBody();
        assertEquals(1L, userResponse.getId());
        assertEquals("test@example.com", userResponse.getEmail());
        assertEquals("Test", userResponse.getFirstName());
        assertEquals("User", userResponse.getLastName());
        
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }
    
    @Test
    void getUserByEmail_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, 
                () -> userService.getUserByEmail("nonexistent@example.com"));
        
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }
    
    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        UserEntity anotherUser = new UserEntity("another@example.com", "password", "student");
        anotherUser.setId(2L);
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, anotherUser));
        
        List<UserResponse> users = userService.getAllUsers();
        
        assertEquals(2, users.size());
        assertEquals("test@example.com", users.get(0).getEmail());
        assertEquals("another@example.com", users.get(1).getEmail());
        
        verify(userRepository, times(1)).findAll();
    }
} 