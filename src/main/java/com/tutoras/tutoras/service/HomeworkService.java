package com.tutoras.tutoras.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tutoras.tutoras.entity.EventEntity;
import com.tutoras.tutoras.entity.HomeworkEntity;
import com.tutoras.tutoras.entity.LessonEntity;
import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.entity.HomeworkEntity.HomeworkStatus;
import com.tutoras.tutoras.model.HomeworkRequest;
import com.tutoras.tutoras.model.HomeworkResponse;
import com.tutoras.tutoras.model.HomeworksResponse;
import com.tutoras.tutoras.repository.EventRepository;
import com.tutoras.tutoras.repository.HomeworkRepository;
import com.tutoras.tutoras.repository.LessonRepository;
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
    
    @Autowired
    private LessonRepository lessonRepository;
    
    public HomeworksResponse getHomeworksForStudentFromTeacher(Long studentId, Long teacherId, int page, int perPage, String sort_by, String sort_order) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));
        
        List<HomeworkEntity> homeworks = homeworkRepository.findByStudent(student);
        
        if (teacherId != null) {
            homeworks = homeworks.stream()
                    .filter(hw -> hw.getLesson() != null && 
                           hw.getLesson().getUser() != null && 
                           hw.getLesson().getUser().getId().equals(teacherId))
                    .collect(Collectors.toList());
        }
        
        if (sort_by != null && sort_order != null) {
            boolean ascending = "asc".equalsIgnoreCase(sort_order);
            homeworks = sortHomeworks(homeworks, sort_by, ascending);
        }
        
        int totalCount = homeworks.size();
        int fromIndex = (page - 1) * perPage;
        int toIndex = Math.min(fromIndex + perPage, totalCount);
        
        if (fromIndex < totalCount) {
            List<HomeworkEntity> pagedHomeworks = homeworks.subList(fromIndex, toIndex);
            
            HomeworksResponse response = new HomeworksResponse();
            response.setHomeworks(pagedHomeworks.stream()
                    .map(this::mapToHomeworkResponse)
                    .collect(Collectors.toList()));
            response.setTotalCount(totalCount);
            response.setPage(page);
            response.setPerPage(perPage);
            
            return response;
        } else {
            HomeworksResponse response = new HomeworksResponse();
            response.setHomeworks(List.of());
            response.setTotalCount(totalCount);
            response.setPage(page);
            response.setPerPage(perPage);
            
            return response;
        }
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
        
        LessonEntity lesson = null;
        EventEntity event = null;
        HomeworkEntity homework = null;
        
        try {
            lesson = lessonRepository.findById(request.getLessonId())
                    .orElse(null);
        } catch (Exception e) {
        }
        
        HomeworkStatus status = HomeworkStatus.valueOf(request.getStatus().toUpperCase());
        
        if (lesson != null) {
            homework = new HomeworkEntity(
                    request.getTitle(),
                    request.getDescription(),
                    request.getDueDate(),
                    status,
                    request.getAssessmentScale(),
                    student,
                    lesson
            );
        } else {
            try {
                event = eventRepository.findById(request.getLessonId())
                        .orElseThrow(() -> new EntityNotFoundException("Lesson/Event not found with id: " + request.getLessonId()));
                
                homework = new HomeworkEntity(
                        request.getTitle(),
                        request.getDescription(),
                        request.getDueDate(),
                        status,
                        request.getAssessmentScale(),
                        student,
                        event
                );
            } catch (Exception e) {
                throw new EntityNotFoundException("Lesson/Event not found with id: " + request.getLessonId());
            }
        }
        
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
            LessonEntity lesson = null;
            try {
                lesson = lessonRepository.findById(request.getLessonId())
                        .orElse(null);
            } catch (Exception e) {
            }
            
            if (lesson != null) {
                homework.setLessonEntity(lesson);
                homework.setLessonEntityId(lesson.getId());
                homework.setLesson(null);
            } else {
                try {
                    EventEntity event = eventRepository.findById(request.getLessonId())
                            .orElseThrow(() -> new EntityNotFoundException("Lesson/Event not found with id: " + request.getLessonId()));
                    homework.setLesson(event);
                    homework.setLessonEntity(null);
                    homework.setLessonEntityId(null);
                } catch (Exception e) {
                    throw new EntityNotFoundException("Lesson/Event not found with id: " + request.getLessonId());
                }
            }
        }
        
        homework.setUpdatedAt(OffsetDateTime.now());
        
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
        homework.setUpdatedAt(OffsetDateTime.now());
        
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
            response.setStudentId(homework.getStudent().getId());
            response.setStudentName(homework.getStudent().getUser().getFirstName() + " " + 
                                   homework.getStudent().getUser().getLastName());
        }
        
        if (homework.getLessonEntity() != null) {
            response.setLessonId(homework.getLessonEntity().getId());
            response.setLessonName(homework.getLessonEntity().getName());
        } 
        else if (homework.getLesson() != null) {
            response.setLessonId(homework.getLesson().getId());
            response.setLessonName(homework.getLesson().getName());
        }

        else if (homework.getLessonEntityId() != null) {
            response.setLessonId(homework.getLessonEntityId());
            try {
                LessonEntity lessonEntity = lessonRepository.findById(homework.getLessonEntityId()).orElse(null);
                if (lessonEntity != null) {
                    response.setLessonName(lessonEntity.getName());
                } else {
                    response.setLessonName("Unknown Lesson");
                }
            } catch (Exception e) {
                response.setLessonName("Unknown Lesson");
            }
        }
        
        response.setCreatedAt(homework.getCreatedAt());
        response.setUpdatedAt(homework.getUpdatedAt());
        
        return response;
    }
    
    private List<HomeworkEntity> sortHomeworks(List<HomeworkEntity> homeworks, String sortBy, boolean ascending) {
        switch (sortBy.toLowerCase()) {
            case "duedate":
                homeworks.sort((h1, h2) -> ascending 
                        ? h1.getDueDate().compareTo(h2.getDueDate())
                        : h2.getDueDate().compareTo(h1.getDueDate()));
                break;
            case "title":
                homeworks.sort((h1, h2) -> ascending 
                        ? h1.getTitle().compareTo(h2.getTitle())
                        : h2.getTitle().compareTo(h1.getTitle()));
                break;
            case "status":
                homeworks.sort((h1, h2) -> ascending 
                        ? h1.getStatus().name().compareTo(h2.getStatus().name())
                        : h2.getStatus().name().compareTo(h1.getStatus().name()));
                break;
            case "createdat":
                homeworks.sort((h1, h2) -> ascending 
                        ? h1.getCreatedAt().compareTo(h2.getCreatedAt())
                        : h2.getCreatedAt().compareTo(h1.getCreatedAt()));
                break;
            case "updatedat":
                homeworks.sort((h1, h2) -> ascending 
                        ? h1.getUpdatedAt().compareTo(h2.getUpdatedAt())
                        : h2.getUpdatedAt().compareTo(h1.getUpdatedAt()));
                break;
            default:
                homeworks.sort((h1, h2) -> ascending 
                        ? h1.getCreatedAt().compareTo(h2.getCreatedAt())
                        : h2.getCreatedAt().compareTo(h1.getCreatedAt()));
        }
        return homeworks;
    }
    
    /**
     * Получить все домашние задания с пагинацией и сортировкой
     */
    public HomeworksResponse getAllHomeworks(int page, int perPage, String sortBy, String sortOrder) {
        List<HomeworkEntity> homeworks = homeworkRepository.findAll();
        
        // Сортировка
        if (sortBy != null && sortOrder != null) {
            boolean ascending = "asc".equalsIgnoreCase(sortOrder);
            homeworks = sortHomeworks(homeworks, sortBy, ascending);
        }
        
        return createPaginatedResponse(homeworks, page, perPage);
    }
    
    /**
     * Получить домашние задания конкретного студента с пагинацией и сортировкой
     */
    public HomeworksResponse getHomeworksForStudent(Long studentId, int page, int perPage, String sortBy, String sortOrder) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));
        
        List<HomeworkEntity> homeworks = homeworkRepository.findByStudent(student);
        
        // Сортировка
        if (sortBy != null && sortOrder != null) {
            boolean ascending = "asc".equalsIgnoreCase(sortOrder);
            homeworks = sortHomeworks(homeworks, sortBy, ascending);
        }
        
        return createPaginatedResponse(homeworks, page, perPage);
    }
    
    /**
     * Получить домашние задания, созданные конкретным учителем, с пагинацией и сортировкой
     */
    public HomeworksResponse getHomeworksForTeacher(Long teacherId, int page, int perPage, String sortBy, String sortOrder) {
        // Получаем все домашние задания
        List<HomeworkEntity> allHomeworks = homeworkRepository.findAll();
        
        // Фильтруем по учителю
        List<HomeworkEntity> teacherHomeworks = allHomeworks.stream()
                .filter(hw -> {
                    // Проверяем сначала через EventEntity
                    if (hw.getLesson() != null && hw.getLesson().getUser() != null) {
                        return hw.getLesson().getUser().getId().equals(teacherId);
                    }
                    // Затем через LessonEntity, если есть
                    else if (hw.getLessonEntity() != null && hw.getLessonEntity().getTeacher() != null) {
                        return hw.getLessonEntity().getTeacher().getId().equals(teacherId);
                    }
                    return false;
                })
                .collect(Collectors.toList());
        
        // Сортировка
        if (sortBy != null && sortOrder != null) {
            boolean ascending = "asc".equalsIgnoreCase(sortOrder);
            teacherHomeworks = sortHomeworks(teacherHomeworks, sortBy, ascending);
        }
        
        return createPaginatedResponse(teacherHomeworks, page, perPage);
    }
    
    /**
     * Вспомогательный метод для создания пагинированного ответа
     */
    private HomeworksResponse createPaginatedResponse(List<HomeworkEntity> homeworks, int page, int perPage) {
        int totalCount = homeworks.size();
        int fromIndex = (page - 1) * perPage;
        int toIndex = Math.min(fromIndex + perPage, totalCount);
        
        HomeworksResponse response = new HomeworksResponse();
        
        if (fromIndex < totalCount) {
            List<HomeworkEntity> pagedHomeworks = homeworks.subList(fromIndex, toIndex);
            response.setHomeworks(pagedHomeworks.stream()
                    .map(this::mapToHomeworkResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setHomeworks(List.of());
        }
        
        response.setTotalCount(totalCount);
        response.setPage(page);
        response.setPerPage(perPage);
        
        return response;
    }
} 