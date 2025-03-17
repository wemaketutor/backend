package com.tutoras.tutoras.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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

import com.tutoras.tutoras.entity.CommentEntity;
import com.tutoras.tutoras.entity.HomeworkCommentEntity;
import com.tutoras.tutoras.entity.HomeworkEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.CommentRequest;
import com.tutoras.tutoras.model.CommentResponse;
import com.tutoras.tutoras.repository.CommentRepository;
import com.tutoras.tutoras.repository.HomeworkCommentRepository;
import com.tutoras.tutoras.repository.HomeworkRepository;
import com.tutoras.tutoras.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private HomeworkRepository homeworkRepository;
    
    @Mock
    private HomeworkCommentRepository homeworkCommentRepository;
    
    @InjectMocks
    private CommentService commentService;
    
    private CommentEntity testComment;
    private UserEntity testUser;
    private HomeworkEntity testHomework;
    private HomeworkCommentEntity testHomeworkComment;
    
    @BeforeEach
    void setUp() {
        testUser = new UserEntity("user@example.com", "password", "teacher");
        testUser.setId(1L);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        
        testComment = mock(CommentEntity.class);
        when(testComment.getId()).thenReturn(1L);
        when(testComment.getBody()).thenReturn("This is a test comment");
        when(testComment.getUser()).thenReturn(testUser);
        when(testComment.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(testComment.getUpdatedAt()).thenReturn(LocalDateTime.now());
        
        testHomework = mock(HomeworkEntity.class);
        when(testHomework.getId()).thenReturn(1L);
        
        testHomeworkComment = mock(HomeworkCommentEntity.class);
        when(testHomeworkComment.getHomework()).thenReturn(testHomework);
        when(testHomeworkComment.getComment()).thenReturn(testComment);
    }
    
    @Test
    void getCommentsByHomework_ShouldReturnComments() {
        List<HomeworkCommentEntity> homeworkComments = Arrays.asList(testHomeworkComment);
        when(homeworkRepository.findById(1L)).thenReturn(Optional.of(testHomework));
        when(homeworkCommentRepository.findByHomework(testHomework)).thenReturn(homeworkComments);
        
        List<CommentResponse> responses = commentService.getCommentsByHomework(1L);
        
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("This is a test comment", responses.get(0).getBody());
        assertEquals(1L, responses.get(0).getUserId());
        assertEquals("Test User", responses.get(0).getUserName());
        
        verify(homeworkRepository, times(1)).findById(1L);
        verify(homeworkCommentRepository, times(1)).findByHomework(testHomework);
    }
    
    @Test
    void getCommentsByHomework_ShouldThrowException_WhenHomeworkDoesNotExist() {
        when(homeworkRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, 
                () -> commentService.getCommentsByHomework(999L));
        
        verify(homeworkRepository, times(1)).findById(999L);
    }
    
    @Test
    void addCommentToHomework_ShouldAddAndReturnComment() {
        CommentRequest request = new CommentRequest();
        request.setBody("New comment");
        request.setHomeworkId(1L);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(homeworkRepository.findById(1L)).thenReturn(Optional.of(testHomework));
        
        CommentEntity newComment = mock(CommentEntity.class);
        when(newComment.getId()).thenReturn(2L);
        when(newComment.getBody()).thenReturn("New comment");
        when(newComment.getUser()).thenReturn(testUser);
        when(newComment.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(newComment.getUpdatedAt()).thenReturn(LocalDateTime.now());
        
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(newComment);
        
        CommentResponse response = commentService.addCommentToHomework(request, 1L);
        
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("New comment", response.getBody());
        assertEquals(1L, response.getUserId());
        assertEquals("Test User", response.getUserName());
        
        verify(userRepository, times(1)).findById(1L);
        verify(homeworkRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(CommentEntity.class));
        verify(homeworkCommentRepository, times(1)).save(any(HomeworkCommentEntity.class));
    }
    
    @Test
    void updateComment_ShouldThrowException_WhenUserIsNotCommentAuthor() {
        CommentRequest request = new CommentRequest();
        request.setBody("Updated comment");
        
        UserEntity anotherUser = new UserEntity("another@example.com", "password", "student");
        anotherUser.setId(2L);
        
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        
        assertThrows(IllegalArgumentException.class, 
                () -> commentService.updateComment(1L, request, 2L));
        
        verify(commentRepository, times(1)).findById(1L);
    }
    
    @Test
    void deleteComment_ShouldDeleteComment_WhenUserIsCommentAuthor() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(homeworkCommentRepository.findByComment(testComment)).thenReturn(new ArrayList<>());
        
        commentService.deleteComment(1L, 1L);
        
        verify(commentRepository, times(1)).findById(1L);
        verify(homeworkCommentRepository, times(1)).findByComment(testComment);
        verify(homeworkCommentRepository, times(1)).deleteAll(anyList());
        verify(commentRepository, times(1)).delete(testComment);
    }
} 