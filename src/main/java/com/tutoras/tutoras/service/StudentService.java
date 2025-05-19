package com.tutoras.tutoras.service;

import org.springframework.stereotype.Service;

import com.tutoras.tutoras.entity.StudentEntity;
import com.tutoras.tutoras.model.StudentResponse;
import com.tutoras.tutoras.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentResponse getStudentsTeachers(Long studentId) {
        StudentEntity student = studentRepository.findById(studentId).orElseThrow();
        return StudentResponse.builder()
            .teachers(student.getUser().getTeachers())
            .build();
    }
}
