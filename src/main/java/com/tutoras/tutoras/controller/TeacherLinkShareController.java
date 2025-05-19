package com.tutoras.tutoras.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.TeacherService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
public class TeacherLinkShareController extends BaseController {

    private final TeacherService teacherService;

    @PostMapping("/teachers/{teacherId}/add-student")
    public ResponseEntity<?> addStudentToTeacher(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("teacherId") Long teacherId) {
        teacherService.addStudentForTeacher(principal.getUserId(), teacherId);
        return ResponseEntity.ok().build();
    }
    
}
