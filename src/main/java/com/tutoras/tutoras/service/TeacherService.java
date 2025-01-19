package com.tutoras.tutoras.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.entity.TeacherEntity;
import com.tutoras.tutoras.entity.UserEntity;
import com.tutoras.tutoras.model.ErrorResponse;
import com.tutoras.tutoras.model.TeacherResponse;
import com.tutoras.tutoras.repository.StudentRepository;
import com.tutoras.tutoras.repository.TeacherRepository;
import com.tutoras.tutoras.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public TeacherResponse getTeachersStudents(Long teacherId) {
        TeacherEntity teacher = teacherRepository.findById(teacherId).orElseThrow();
        return TeacherResponse.builder()
            .students(teacher.getStudents())
            .build();
    }

    public TeacherResponse addStudent(Long teacherId, String email) {
        UserEntity studentUser = userRepository.findByEmail(email).orElseThrow();
        TeacherEntity teacher = teacherRepository.findById(teacherId).orElseThrow();
        StudentEntity newStudent = studentRepository.findById(studentUser.getId()).orElseThrow();
        if (!teacher.getStudents().contains(newStudent)) {
            newStudent.getTeachers().add(teacher);
            teacher.getStudents().add(newStudent);
            teacherRepository.save(teacher);
            studentRepository.save(newStudent);
        }
        return TeacherResponse.builder()
            .students(List.of(newStudent))
            .build();
    }

    public ResponseEntity<?> getStudentById(Long teacherId, Long studentId) {
        TeacherEntity teacher = teacherRepository.findById(teacherId).orElseThrow();
        List<StudentEntity> students = teacher.getStudents();
        boolean found = false;
        List<StudentEntity> foundedStudent = new ArrayList<>();
        for (StudentEntity student : students) {
            if (student.getStudentId().equals(studentId)){
                found = true;
                foundedStudent = List.of(student);
                break;
            }
        }
        if (found) {
            return ResponseEntity.ok(TeacherResponse.builder()
                .students(foundedStudent)
                .build());
        }
        else {
            ErrorResponse errorResponse = new ErrorResponse(404L,"Студент не найден");
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    public ResponseEntity<?> deleteStudentById(Long teacherId, Long studentId) {
        TeacherEntity teacher = teacherRepository.findById(teacherId).orElseThrow();
        List<StudentEntity> students = teacher.getStudents();
        boolean found = false;
        List<StudentEntity> foundedStudent = new ArrayList<>();
        for (int i = 0;i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(studentId)){
                found = true;
                UserEntity userStudent = students.get(i).getUser();
                List<TeacherEntity> teachers = students.get(i).getTeachers();
                teachers.remove(teacher);
                students.remove(i);
                TeacherEntity newTeacher = new TeacherEntity(teacherId, teacher.getUser(), students);
                StudentEntity newStudent = new StudentEntity(studentId, userStudent, teachers);
                teacherRepository.save(newTeacher);
                studentRepository.save(newStudent);
                break;
            }
        }
        if (found) {
            return ResponseEntity.ok(TeacherResponse.builder()
                .students(foundedStudent)
                .build());
        }
        else {
            ErrorResponse errorResponse = new ErrorResponse(404L,"Студент не найден");
            return ResponseEntity.status(404).body(errorResponse);
        }

    }

    // public ResponseEntity<?> updateSubjects(Long teacherId, List<String> subjects) {
        
    // }
}
