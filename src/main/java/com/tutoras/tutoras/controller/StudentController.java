package com.tutoras.tutoras.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutoras.tutoras.model.StudentResponse;
import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.StudentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/student/teachers")
    public StudentResponse getTeachersStudents(@AuthenticationPrincipal UserPrincipal principal) {
        return studentService.getStudentsTeachers(principal.getUserId());
    }
}
