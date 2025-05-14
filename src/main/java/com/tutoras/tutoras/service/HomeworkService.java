package com.tutoras.tutoras.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tutoras.tutoras.entity.EventEntity;
import com.tutoras.tutoras.entity.HomeworkEntity;
import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.entity.HomeworkEntity.HomeworkStatus;
import com.tutoras.tutoras.model.HomeworkRequest;
import com.tutoras.tutoras.model.HomeworkResponse;
import com.tutoras.tutoras.model.HomeworksResponse;
import com.tutoras.tutoras.repository.EventRepository;
import com.tutoras.tutoras.repository.HomeworkRepository;
import com.tutoras.tutoras.repository.StudentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class HomeworkService {
    
    @Autowired
    private HomeworkRepository homeworkRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    public ResponseEntity<?> getHomeworksForStudentFromTeacher(Long studentId, Long teacherId, int page, int perPage, String sort_by, String sort_order) {
        if (!studentRepository.findById(teacherId).isPresent()){
            
        }
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));
        
        List<HomeworkEntity> homeworks = homeworkRepository.findByStudent(student);
        
        int totalCount = homeworks.size();
        int fromIndex = (page - 1) * perPage;
        int toIndex = Math.min(fromIndex + perPage, totalCount);
        
        List<HomeworkEntity> pagedHomeworks = homeworks.subList(fromIndex, toIndex);
        
        HomeworksResponse response = new HomeworksResponse();
        response.setHomeworks(pagedHomeworks.stream()
                .map(this::mapToHomeworkResponse)
                .collect(Collectors.toList()));
        response.setTotalCount(totalCount);
        response.setPage(page);
        response.setPerPage(perPage);
        
        return response;
    }
    
    public HomeworksResponse getHomeworksByStatus(String status, int page, int perPage) {
        HomeworkStatus homeworkStatus = HomeworkStatus.valueOf(status.toUpperCase());
        
        List<HomeworkEntity> homeworks = homeworkRepository.findByStatus(homeworkStatus);
        
        int totalCount = homeworks.size();
        int fromIndex = (page - 1) * perPage;
        int toIndex = Math.min(fromIndex + perPage, totalCount);
        
        List<HomeworkEntity> pagedHomeworks = homeworks.subList(fromIndex, toIndex);
        
        HomeworksResponse response = new HomeworksResponse();
        response.setHomeworks(pagedHomeworks.stream()
                .map(this::mapToHomeworkResponse)
                .collect(Collectors.toList()));
        response.setTotalCount(totalCount);
        response.setPage(page);
        response.setPerPage(perPage);
        
        return response;
    }
    
    public HomeworkResponse getHomeworkById(Long id) {
        HomeworkEntity homework = homeworkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Homework not found with id: " + id));
        
        return mapToHomeworkResponse(homework);
    }
    
    @Transactional
    public HomeworkResponse createHomework(HomeworkRequest request) {
        StudentEntity student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + request.getStudentId()));
        
        EventEntity lesson = eventRepository.findById(request.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + request.getLessonId()));
        
        HomeworkStatus status = HomeworkStatus.valueOf(request.getStatus().toUpperCase());
        
        HomeworkEntity homework = new HomeworkEntity(
                request.getTitle(),
                request.getDescription(),
                request.getDueDate(),
                status,
                request.getAssessmentScale(),
                student,
                lesson
        );
        
        if (request.getGrade() != null) {
            homework.setGrade(request.getGrade());
        }
        
        HomeworkEntity savedHomework = homeworkRepository.save(homework);
        
        return mapToHomeworkResponse(savedHomework);
    }
    
    @Transactional
    public HomeworkResponse updateHomework(Long id, HomeworkRequest request) {
        HomeworkEntity homework = homeworkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Homework not found with id: " + id));
        
        if (request.getTitle() != null) {
            homework.setTitle(request.getTitle());
        }
        
        if (request.getDescription() != null) {
            homework.setDescription(request.getDescription());
        }
        
        if (request.getDueDate() != null) {
            homework.setDueDate(request.getDueDate());
        }
        
        if (request.getStatus() != null) {
            HomeworkStatus status = HomeworkStatus.valueOf(request.getStatus().toUpperCase());
            homework.setStatus(status);
        }
        
        if (request.getGrade() != null) {
            homework.setGrade(request.getGrade());
        }
        
        if (request.getAssessmentScale() != null) {
            homework.setAssessmentScale(request.getAssessmentScale());
        }
        
        if (request.getStudentId() != null) {
            StudentEntity student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + request.getStudentId()));
            homework.setStudent(student);
        }
        
        if (request.getLessonId() != null) {
            EventEntity lesson = eventRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + request.getLessonId()));
            homework.setLesson(lesson);
        }
        
        homework.setUpdatedAt(LocalDateTime.now());
        
        HomeworkEntity updatedHomework = homeworkRepository.save(homework);
        
        return mapToHomeworkResponse(updatedHomework);
    }
    
    @Transactional
    public void deleteHomework(Long id) {
        if (!homeworkRepository.existsById(id)) {
            throw new EntityNotFoundException("Homework not found with id: " + id);
        }
        
        homeworkRepository.deleteById(id);
    }
    
    @Transactional
    public HomeworkResponse updateHomeworkStatus(Long id, String status) {
        HomeworkEntity homework = homeworkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Homework not found with id: " + id));
        
        HomeworkStatus homeworkStatus = HomeworkStatus.valueOf(status.toUpperCase());
        homework.setStatus(homeworkStatus);
        homework.setUpdatedAt(LocalDateTime.now());
        
        HomeworkEntity updatedHomework = homeworkRepository.save(homework);
        
        return mapToHomeworkResponse(updatedHomework);
    }
    
    private HomeworkResponse mapToHomeworkResponse(HomeworkEntity homework) {
        HomeworkResponse response = new HomeworkResponse();
        response.setId(homework.getId());
        response.setTitle(homework.getTitle());
        response.setDescription(homework.getDescription());
        response.setDueDate(homework.getDueDate());
        response.setStatus(homework.getStatus().name());
        response.setGrade(homework.getGrade());
        response.setAssessmentScale(homework.getAssessmentScale());
        
        if (homework.getStudent() != null) {
            response.setStudentId(homework.getStudent().getStudentId());
            response.setStudentName(homework.getStudent().getUser().getFirstName() + " " + 
                                   homework.getStudent().getUser().getLastName());
        }
        
        if (homework.getLesson() != null) {
            response.setLessonId(homework.getLesson().getId());
            response.setLessonName(homework.getLesson().getName());
        }
        
        response.setCreatedAt(homework.getCreatedAt());
        response.setUpdatedAt(homework.getUpdatedAt());
        
        return response;
    }
} 