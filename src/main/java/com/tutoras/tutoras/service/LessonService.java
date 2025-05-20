package com.tutoras.tutoras.service;

import com.tutoras.tutoras.entity.TeacherEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.entity.LessonEntity;
import com.tutoras.tutoras.entity.LessonStudentEntity;
import com.tutoras.tutoras.entity.Role;
import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.exception.ConflictException;
import com.tutoras.tutoras.exception.NotFindedSuchElementException;
import com.tutoras.tutoras.model.LessonResponse;
import com.tutoras.tutoras.model.LessonsRequest;
import com.tutoras.tutoras.model.LessonsResponse;
import com.tutoras.tutoras.repository.LessonRepository;
import com.tutoras.tutoras.repository.LessonStudentRepository;
import com.tutoras.tutoras.repository.StudentRepository;
import com.tutoras.tutoras.repository.TeacherRepository;
import com.tutoras.tutoras.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;
    private final LessonStudentRepository lessonStudentRepository;

    public LessonsResponse getLessons(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));

        List<LessonEntity> ownerLessons = new ArrayList<>();
        List<LessonEntity> studentLessons = new ArrayList<>();

        if (user.getTeacherId() != null) {
            TeacherEntity teacher = teacherRepository.findById(user.getTeacherId())
                .orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + user.getTeacherId()));
            ownerLessons = teacher.getLessons();
        }
    
        if (user.getStudentId() != null) {
            StudentEntity student = studentRepository.findById(user.getStudentId())
                .orElseThrow(() -> new NotFindedSuchElementException("Student not found with id: " + user.getStudentId()));
            for (LessonStudentEntity lessonStudent : student.getLessonStudent()){
                studentLessons.add(lessonStudent.getLesson());
            }
        }
    
        return LessonsResponse.builder()
            .ownerLessons(ownerLessons)
            .followerLessons(studentLessons)
            .build();
    }

    public LessonResponse getLessonById(Long id) {
        LessonEntity lesson = lessonRepository.findById(id)
            .orElseThrow(() -> new NotFindedSuchElementException("Lesson not found with id: " + id));
        
        return buildLessonResponse(lesson);
    }

    public LessonResponse createLesson(Long userId, String lessonName, OffsetDateTime date, OffsetDateTime duration, Long followedUserId) {
        if (userId == null || followedUserId == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));
        UserEntity followedUser = userRepository.findById(followedUserId).orElseThrow(() -> new NotFindedSuchElementException("followedUser not found with id: " + followedUserId));
        if (!Role.TEACHER.equals(user.getRole()) || !Role.STUDENT.equals(followedUser.getRole())) {
            throw new ConflictException("Roles ERROR in creating lesson for " + followedUserId + " by " + userId);
        }
        TeacherEntity userAsTeacher = teacherRepository.findById(user.getTeacherId()).orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + user.getTeacherId()));
        LessonEntity newLesson = new LessonEntity(lessonName, date, duration, List.of(followedUserId), userAsTeacher);
        lessonRepository.save(newLesson);
        
        // Создание связи с учеником
        StudentEntity student = studentRepository.findById(followedUser.getStudentId())
            .orElseThrow(() -> new NotFindedSuchElementException("Student not found with id: " + followedUser.getStudentId()));
        LessonStudentEntity lessonStudent = new LessonStudentEntity(newLesson, student);
        lessonStudentRepository.save(lessonStudent);
        
        return buildLessonResponse(newLesson);
    }
    
    @Transactional
    public LessonResponse updateLesson(Long userId, Long lessonId, LessonsRequest request) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));
        
        if (!Role.TEACHER.equals(user.getRole())) {
            throw new ConflictException("Only teachers can update lessons");
        }
        
        LessonEntity lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new NotFindedSuchElementException("Lesson not found with id: " + lessonId));
        
        TeacherEntity teacher = teacherRepository.findById(user.getTeacherId())
            .orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + user.getTeacherId()));
        
        if (!lesson.getTeacher().getId().equals(teacher.getId())) {
            throw new ConflictException("You can only update your own lessons");
        }
        
        lesson.setName(request.getName());
        lesson.setStartTime(request.getDate());
        lesson.setEndTime(request.getDuration());
        lesson.setUpdateAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        
        lessonRepository.save(lesson);
        
        return buildLessonResponse(lesson);
    }
    
    @Transactional
    public void deleteLesson(Long userId, Long lessonId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));
        
        if (!Role.TEACHER.equals(user.getRole())) {
            throw new ConflictException("Only teachers can delete lessons");
        }
        
        LessonEntity lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new NotFindedSuchElementException("Lesson not found with id: " + lessonId));
        
        TeacherEntity teacher = teacherRepository.findById(user.getTeacherId())
            .orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + user.getTeacherId()));
        
        if (!lesson.getTeacher().getId().equals(teacher.getId())) {
            throw new ConflictException("You can only delete your own lessons");
        }
        
        // Удаляем связи со студентами
        lessonStudentRepository.deleteByLessonId(lessonId);
        
        // Удаляем сам урок
        lessonRepository.delete(lesson);
    }
    
    public LessonsResponse getTeacherLessons(Long teacherId) {
        TeacherEntity teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + teacherId));
        
        List<LessonEntity> lessons = teacher.getLessons();
        
        return LessonsResponse.builder()
            .ownerLessons(lessons)
            .followerLessons(new ArrayList<>())
            .build();
    }
    
    @Transactional
    public LessonResponse takeLesson(Long lessonId, Long studentId) {
        LessonEntity lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new NotFindedSuchElementException("Lesson not found with id: " + lessonId));
        
        StudentEntity student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFindedSuchElementException("Student not found with id: " + studentId));
        
        // Проверяем, не записан ли студент уже на этот урок
        boolean alreadyTaken = lessonStudentRepository.existsByLessonIdAndStudentId(lessonId, studentId);
        if (alreadyTaken) {
            throw new ConflictException("Student is already taking this lesson");
        }
        
        // Создаем запись о том, что студент берет урок
        LessonStudentEntity lessonStudent = new LessonStudentEntity(lesson, student);
        lessonStudentRepository.save(lessonStudent);
        
        return buildLessonResponse(lesson);
    }
    
    @Transactional
    public LessonResponse switchLesson(Long lessonId, Long studentId, Long otherStudentId) {
        LessonEntity lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new NotFindedSuchElementException("Lesson not found with id: " + lessonId));
        
        StudentEntity student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFindedSuchElementException("Student not found with id: " + studentId));
        
        StudentEntity otherStudent = studentRepository.findById(otherStudentId)
            .orElseThrow(() -> new NotFindedSuchElementException("Other student not found with id: " + otherStudentId));
        
        // Проверяем, что студент записан на урок
        boolean studentTakesLesson = lessonStudentRepository.existsByLessonIdAndStudentId(lessonId, studentId);
        if (!studentTakesLesson) {
            throw new ConflictException("Student is not taking this lesson");
        }
        
        // Устанавливаем флаг запроса на смену в связи студент-урок
        // Примечание: тут должна быть реализация логики для запросов на смену уроков
        // Для полной реализации, необходимо расширить модель данных
        
        // В данной реализации просто возвращаем текущий урок
        return buildLessonResponse(lesson);
    }
    
    @Transactional
    public LessonResponse approveSwitchLesson(Long lessonId, Long studentId, Boolean approve) {
        LessonEntity lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new NotFindedSuchElementException("Lesson not found with id: " + lessonId));
        
        StudentEntity student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFindedSuchElementException("Student not found with id: " + studentId));
        
        // Проверяем, что студент записан на урок
        boolean studentTakesLesson = lessonStudentRepository.existsByLessonIdAndStudentId(lessonId, studentId);
        if (!studentTakesLesson) {
            throw new ConflictException("Student is not taking this lesson");
        }
        
        // Тут должна быть реализация логики для подтверждения смены уроков
        // Для полной реализации, необходимо расширить модель данных
        
        // В данной реализации просто возвращаем текущий урок
        return buildLessonResponse(lesson);
    }
    
    private LessonResponse buildLessonResponse(LessonEntity lesson) {
        return LessonResponse.builder()
            .id(lesson.getId())
            .name(lesson.getName())
            .description(lesson.getDescription())
            .subject(lesson.getSubject())
            .startTime(lesson.getStartTime())
            .endTime(lesson.getEndTime())
            .studentIds(lesson.getStudentIds())
            .homeworkLink(lesson.getHomeworkLink())
            .date_created(lesson.getDateCreated())
            .update_at(lesson.getUpdateAt())
            .build();
    }
}
