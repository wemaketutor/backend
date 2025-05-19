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
import com.tutoras.tutoras.model.LessonsResponse;
import com.tutoras.tutoras.repository.LessonRepository;
import com.tutoras.tutoras.repository.StudentRepository;
import com.tutoras.tutoras.repository.TeacherRepository;
import com.tutoras.tutoras.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;

    public LessonsResponse getLessons(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));

        List<LessonEntity> ownerLessons = Collections.emptyList();
        List<LessonEntity> studentLessons = Collections.emptyList();

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

    public LessonResponse createLesson(Long userId, String lessonName, OffsetDateTime date, OffsetDateTime duration, Long followedUserId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));
        UserEntity followedUser = userRepository.findById(followedUserId).orElseThrow(() -> new NotFindedSuchElementException("followedUser not found with id: " + followedUserId));
        if (!Role.TEACHER.equals(user.getRole()) || !Role.STUDENT.equals(followedUser.getRole())) {
            throw new ConflictException("Roles ERROR in creating lesson for " + followedUserId + " by " + userId);
        }
        TeacherEntity userAsTeacher = teacherRepository.findById(user.getTeacherId()).orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + user.getTeacherId()));
        LessonEntity newLesson = new LessonEntity(lessonName, date, duration, List.of(followedUserId), userAsTeacher);
        lessonRepository.save(newLesson);
        return LessonResponse.builder()
            .id(newLesson.getId())
            .name(newLesson.getName())
            .description(newLesson.getDescription())
            .subject(newLesson.getSubject())
            .starTime(newLesson.getStarTime())
            .endTime(newLesson.getEndTime())
            .studentIds(newLesson.getStudentIds())
            .homeworkLink(newLesson.getHomeworkLink())
            .date_created(newLesson.getDateCreated())
            .update_at(newLesson.getUpdateAt())
            .build();
    }

}
