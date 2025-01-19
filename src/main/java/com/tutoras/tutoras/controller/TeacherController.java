package com.tutoras.tutoras.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tutoras.tutoras.model.TeacherRequest;
import com.tutoras.tutoras.model.TeacherResponse;
import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.TeacherService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/teacher/students")
    public TeacherResponse getTeachersStudents(@AuthenticationPrincipal UserPrincipal principal) {
        return teacherService.getTeachersStudents(principal.getUserId());
    }

    @PostMapping("/teacher/add/student")
    public TeacherResponse addStudent(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Validated TeacherRequest request) {        
        return teacherService.addStudent(principal.getUserId(),request.getEmail());
    }

    @GetMapping("teacher/get/student/{student_id}")
    public ResponseEntity<?> getStudent(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("student_id") Long studentId) {
        return teacherService.getStudentById(principal.getUserId(), studentId);
    }

    @DeleteMapping("teacher/delete/student/{student_id}")
    public ResponseEntity<?> deleteStudent(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("student_id") Long studentId) {
        return teacherService.deleteStudentById(principal.getUserId(), studentId);
    }

    // @PutMapping("path/{id}")
    // public ResponseEntity<?> updateSubjects(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Validated TeacherRequest request) {        
    //     return teacherService.updateSubjects(principal.getUserId(), request.getSubjects());
    // }
    
    
}
