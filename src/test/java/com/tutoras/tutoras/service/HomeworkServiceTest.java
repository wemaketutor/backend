package com.tutoras.tutoras.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.tutoras.tutoras.entity.EventEntity;
import com.tutoras.tutoras.entity.HomeworkEntity;
import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.entity.HomeworkEntity.HomeworkStatus;
import com.tutoras.tutoras.model.HomeworkRequest;
import com.tutoras.tutoras.model.HomeworkResponse;
import com.tutoras.tutoras.repository.EventRepository;
import com.tutoras.tutoras.repository.HomeworkRepository;
import com.tutoras.tutoras.repository.StudentRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HomeworkServiceTest {

    @Mock
    private HomeworkRepository homeworkRepository;
    
    @Mock
    private StudentRepository studentRepository;
    
    @Mock
    private EventRepository eventRepository;
    
    @InjectMocks
    private HomeworkService homeworkService;
    
    private HomeworkEntity testHomework;
    private StudentEntity testStudent;
    private UserEntity testStudentUser;
    private EventEntity testLesson;
    private UserEntity testTeacherUser;
    
    @BeforeEach
    void setUp() {
        testStudentUser = new UserEntity("student@example.com", "password", "student");
        testStudentUser.setId(1L);
        testStudentUser.setFirstName("Student");
        testStudentUser.setLastName("Test");
        
        testStudent = mock(StudentEntity.class);
        when(testStudent.getStudentId()).thenReturn(1L);
        when(testStudent.getUser()).thenReturn(testStudentUser);
        
        testTeacherUser = new UserEntity("teacher@example.com", "password", "teacher");
        testTeacherUser.setId(2L);
        testTeacherUser.setFirstName("Teacher");
        testTeacherUser.setLastName("Test");
        
        testLesson = mock(EventEntity.class);
        when(testLesson.getId()).thenReturn(1L);
        when(testLesson.getName()).thenReturn("Test Lesson");
        when(testLesson.getUser()).thenReturn(testTeacherUser);
        
        testHomework = new HomeworkEntity(
                "Test Homework",
                "This is a test homework description",
                LocalDateTime.now().plusDays(7),
                HomeworkStatus.NOT_DONE,
                10,
                testStudent,
                testLesson
        );
        testHomework.setId(1L);
    }
    
    @Test
    void getHomeworkById_ShouldReturnHomework_WhenHomeworkExists() {
        when(homeworkRepository.findById(1L)).thenReturn(Optional.of(testHomework));
        
        HomeworkResponse response = homeworkService.getHomeworkById(1L);
        
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Homework", response.getTitle());
        assertEquals("This is a test homework description", response.getDescription());
        assertEquals(HomeworkStatus.NOT_DONE.name(), response.getStatus());
        assertEquals(10, response.getAssessmentScale());
        assertEquals(1L, response.getStudentId());
        assertEquals("Student Test", response.getStudentName());
        assertEquals(1L, response.getLessonId());
        assertEquals("Test Lesson", response.getLessonName());
        
        verify(homeworkRepository, times(1)).findById(1L);
    }
    
    @Test
    void getHomeworkById_ShouldThrowException_WhenHomeworkDoesNotExist() {
        when(homeworkRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, 
                () -> homeworkService.getHomeworkById(999L));
        
        verify(homeworkRepository, times(1)).findById(999L);
    }
    
    @Test
    void createHomework_ShouldCreateAndReturnHomework() {
        HomeworkRequest request = new HomeworkRequest();
        request.setTitle("New Homework");
        request.setDescription("New homework description");
        request.setDueDate(LocalDateTime.now().plusDays(7));
        request.setStatus("NOT_DONE");
        request.setAssessmentScale(10);
        request.setStudentId(1L);
        request.setLessonId(1L);
        
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testLesson));
        
        HomeworkEntity newHomework = new HomeworkEntity(
                request.getTitle(),
                request.getDescription(),
                request.getDueDate(),
                HomeworkStatus.NOT_DONE,
                request.getAssessmentScale(),
                testStudent,
                testLesson
        );
        newHomework.setId(2L);
        
        when(homeworkRepository.save(any(HomeworkEntity.class))).thenReturn(newHomework);
        
        HomeworkResponse response = homeworkService.createHomework(request);
        
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("New Homework", response.getTitle());
        assertEquals("New homework description", response.getDescription());
        assertEquals("NOT_DONE", response.getStatus());
        assertEquals(10, response.getAssessmentScale());
        assertEquals(1L, response.getStudentId());
        assertEquals(1L, response.getLessonId());
        
        verify(studentRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).findById(1L);
        verify(homeworkRepository, times(1)).save(any(HomeworkEntity.class));
    }
    
    @Test
    void updateHomeworkStatus_ShouldUpdateAndReturnHomework() {
        HomeworkEntity homeworkToUpdate = testHomework;
        
        when(homeworkRepository.findById(1L)).thenReturn(Optional.of(homeworkToUpdate));
        when(homeworkRepository.save(any(HomeworkEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        HomeworkResponse response = homeworkService.updateHomeworkStatus(1L, "PENDING");
        
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("PENDING", response.getStatus());
        
        verify(homeworkRepository, times(1)).findById(1L);
        verify(homeworkRepository, times(1)).save(any(HomeworkEntity.class));
    }
} 