package com.tutoras.tutoras.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tutoras.tutoras.model.LessonsResponse;
import com.tutoras.tutoras.model.TeacherResponse;
import com.tutoras.tutoras.model.TeachersResponse;
import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.TeacherService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
public class TeacherController extends BaseController {

    private final TeacherService teacherService;

    @GetMapping("/teachers")
    public ResponseEntity<List<TeachersResponse>> getTeachers() {
        return ResponseEntity.status(HttpStatus.OK).body(teacherService.getTeachers());
    }

    @GetMapping("/teachers/{teacher_id}")
    public ResponseEntity<TeachersResponse> getTeacher(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("teacher_id") Long teacherId) {
        return ResponseEntity.status(HttpStatus.OK).body(teacherService.getTeacher(teacherId));
    }

    @GetMapping("/teacher/students")
    public TeacherResponse getTeachersStudents(@AuthenticationPrincipal UserPrincipal principal) {
        return teacherService.getTeachersStudents(principal.getUserId());
    }

    @GetMapping("/teacher/{teacher_id}/lessons/")
    public ResponseEntity<LessonsResponse> getTeacherLessons(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("teacher_id") Long teacherId) {
        return ResponseEntity.status(HttpStatus.OK).body(teacherService.getTeacherLessons(teacherId));
    }
    
    

    // @PutMapping("path/{id}")
    // public ResponseEntity<?> updateSubjects(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Validated TeacherRequest request) {        
    //     return teacherService.updateSubjects(principal.getUserId(), request.getSubjects());
    // }
    
    
}
