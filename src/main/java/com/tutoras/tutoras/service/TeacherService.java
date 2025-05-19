package com.tutoras.tutoras.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tutoras.tutoras.entity.Role;
import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.entity.TeacherEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.exception.ConflictException;
import com.tutoras.tutoras.exception.NotFindedSuchElementException;
import com.tutoras.tutoras.model.LessonsResponse;
import com.tutoras.tutoras.model.TeacherResponse;
import com.tutoras.tutoras.model.TeachersResponse;
import com.tutoras.tutoras.repository.StudentRepository;
import com.tutoras.tutoras.repository.TeacherRepository;
import com.tutoras.tutoras.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final LessonService lessonService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public TeacherResponse getTeachersStudents(Long teacherId) {
        TeacherEntity teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + teacherId));
        UserEntity teacherAsUser = teacher.getUser();
        return TeacherResponse.builder()
            .students(teacherAsUser.getStudents())
            .build();
    }

    public List<TeachersResponse> getTeachers() {
        List<TeacherEntity> teachers = teacherRepository.findAll();
        List<TeachersResponse> responseList = new ArrayList<>();
        for (TeacherEntity teacher : teachers) {
            UserEntity teacherAsUser = teacher.getUser();
            TeachersResponse response = new TeachersResponse();
            response.setId(teacher.getId());
            response.setEmail(teacherAsUser.getEmail());
            response.setFirstName(teacherAsUser.getFirstName());
            response.setLastName(teacherAsUser.getLastName());
            responseList.add(response);
        }
        return responseList;
    } 

    public TeachersResponse getTeacher(Long teacherId) {
        TeacherEntity teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + teacherId));
        UserEntity teacherAsUser = teacher.getUser();
        TeachersResponse response = new TeachersResponse();
        response.setId(teacherId);
        response.setEmail(teacherAsUser.getEmail());
        response.setFirstName(teacherAsUser.getFirstName());
        response.setLastName(teacherAsUser.getLastName());
        return response;
    }

    public LessonsResponse getTeacherLessons(Long teacherId) {
        UserEntity teacherAsUser = userRepository.findById(teacherId).orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + teacherId));
        return lessonService.getLessons(teacherAsUser.getTeacherId());
    }

    public void addStudentForTeacher(Long userId, Long teacherId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFindedSuchElementException("User not found with id: " + userId));
        UserEntity teacher = userRepository.findById(teacherId).orElseThrow(() -> new NotFindedSuchElementException("Teacher not found with id: " + teacherId));
        
        if (!Role.STUDENT.equals(user.getRole())) {
            throw new ConflictException("Only students can be added to a teacher");
        }

        if (!Role.TEACHER.equals(teacher.getRole())) {
            throw new ConflictException("Target user is not a teacher");
        }

        StudentEntity userAsStudent = studentRepository.findById(user.getStudentId()).orElseThrow();
        List<StudentEntity> existedStudents = teacher.getStudents();
        if (!existedStudents.contains(userAsStudent)) {
            existedStudents.add(userAsStudent);
            userRepository.save(teacher);
        }
    }
}
